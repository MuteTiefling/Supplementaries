package net.mehvahdjukaar.supplementaries.block.tiles;

import net.mehvahdjukaar.supplementaries.block.blocks.ClockBlock;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ClockBlockTile extends BlockEntity {
    public float roll = 0;
    public float prevRoll = 0;
    public float targetRoll = 0;

    public float sRoll = 0;
    public float sPrevRoll = 0;
    public float sTargetRoll = 0;

    public int power = 0;

    public ClockBlockTile(BlockPos pos, BlockState state) {
        super(ModRegistry.CLOCK_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        this.roll = compound.getFloat("MinRoll");
        this.prevRoll = this.roll;
        this.targetRoll = this.roll;

        this.sRoll = compound.getFloat("SecRoll");
        this.sPrevRoll = this.sRoll;
        this.sTargetRoll = this.sRoll;
        this.power = compound.getInt("Power");
    }

    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        super.save(compound);
        compound.putFloat("MinRoll", this.targetRoll);
        compound.putFloat("SecRoll", this.sTargetRoll);
        compound.putInt("Power", this.power);
        return compound;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    public void updateInitialTime(Level level, BlockState state, BlockPos pos) {
        int time = (int) (level.getDayTime() % 24000);
        this.updateTime(time, level, state, pos);
        this.roll = this.targetRoll;
        this.prevRoll = this.targetRoll;
        this.sRoll = this.sTargetRoll;
        this.sPrevRoll = this.sTargetRoll;
    }

    //TODO: rewrite
    public void updateTime(int time, Level level, BlockState state, BlockPos pos) {

        if (level.dimensionType().natural()) {

            //minute here are 1 rl second -> 50m in a minecraft hour
            int minute = Mth.clamp((time % 1000) / 20, 0, 50);
            int hour = Mth.clamp(time / 1000, 0, 24);

            //server
            if (!level.isClientSide) {

                if (hour != state.getValue(ClockBlock.HOUR)) {
                    //if they are sent to the client the animation gets broken. Side effect is that you can't see hour with f3
                    level.setBlock(pos, state.setValue(ClockBlock.HOUR, hour), 3);
                }
                int p = Mth.clamp(time / 1500, 0, 15);
                if (p != this.power) {
                    this.power = p;
                    level.updateNeighbourForOutputSignal(pos, this.getBlockState().getBlock());
                }
                //TODO: add proper sounds
                //this.world.playSound(null, this.pos, SoundEvents.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.BLOCKS,0.03f,time%40==0?2:1.92f);

            }
            //hours
            this.targetRoll = (hour * 30) % 360;
            //minutes
            this.sTargetRoll = (minute * 7.2f + 180) % 360f;

        } else {

            /*
            double d0 = Math.random() - (this.targetRoll/360f);
            d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
            this.rota += d0 * 0.1D;
            this.rota *= 0.9D;
            this.targetRoll = 360*((float) MathHelper.positiveModulo(this.targetRoll/360f + this.rota, 1.0D));

            this.roll = this.targetRoll;
            */

            this.targetRoll = level.random.nextFloat() * 360;
            this.sTargetRoll = level.random.nextFloat() * 360;
            //TODO: make it wobbly
        }
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ClockBlockTile tile) {
        int dayTime = (int) (pLevel.getDayTime() % 24000);
        int time = pLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT) ?
                dayTime : (int) (pLevel.getGameTime() % 24000);
        if (time % 20 == 0) {
            tile.updateTime(dayTime, pLevel, pState, pPos);

        }
        //TODO: fix hour hand
        //hours
        tile.prevRoll = tile.roll;
        if (tile.roll != tile.targetRoll) {
            float r = (tile.roll + 8) % 360;
            if ((r >= tile.targetRoll) && (r <= tile.targetRoll + 8)) {
                r = tile.targetRoll;
            }
            tile.roll = r;
        }
        //minutes
        tile.sPrevRoll = tile.sRoll;
        if (tile.sRoll != tile.sTargetRoll) {
            float r = (tile.sRoll + 8) % 360;
            if ((r >= tile.sTargetRoll) && (r <= tile.sTargetRoll + 8)) {
                r = tile.sTargetRoll;
            }
            tile.sRoll = r;
        }
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(ClockBlock.FACING);
    }
}

