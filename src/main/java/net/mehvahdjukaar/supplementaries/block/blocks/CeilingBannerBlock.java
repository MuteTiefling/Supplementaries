package net.mehvahdjukaar.supplementaries.block.blocks;

import net.mehvahdjukaar.selene.map.ExpandedMapData;
import net.mehvahdjukaar.supplementaries.block.tiles.CeilingBannerBlockTile;
import net.mehvahdjukaar.supplementaries.block.tiles.SignPostBlockTile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class CeilingBannerBlock extends AbstractBannerBlock {
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE_X = Block.box(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_Z = Block.box(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public CeilingBannerBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(color, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACHED, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState above = world.getBlockState(pos.above());
        if (state.getValue(ATTACHED)) {
            return this.canAttach(state, above);
        }
        return above.getMaterial().isSolid();
    }

    private boolean canAttach(BlockState state, BlockState above) {
        Block b = above.getBlock();
        if (b instanceof RopeBlock) {
            if (!above.getValue(RopeBlock.DOWN)) {
                Direction dir = state.getValue(FACING);
                return above.getValue(RopeBlock.FACING_TO_PROPERTY_MAP.get(dir.getClockWise())) &&
                        above.getValue(RopeBlock.FACING_TO_PROPERTY_MAP.get(dir.getCounterClockWise()));
            }
            return false;
        }
        return false;
    }

    @Override
    public BlockState updateShape(BlockState myState, Direction direction, BlockState otherState, LevelAccessor world, BlockPos myPos, BlockPos otherPos) {
        return direction == Direction.UP && !myState.canSurvive(world, myPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(myState, direction, myState, world, myPos, otherPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.DOWN) {
            BlockState blockstate = this.defaultBlockState();
            LevelReader world = context.getLevel();
            BlockPos blockpos = context.getClickedPos();
            blockstate = blockstate.setValue(FACING, context.getHorizontalDirection().getOpposite());
            boolean attached = this.canAttach(blockstate, world.getBlockState(blockpos.above()));
            blockstate = blockstate.setValue(ATTACHED, attached);
            if (blockstate.canSurvive(world, blockpos)) {
                return blockstate;
            }
        }
        return null;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACHED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CeilingBannerBlockTile(pPos, pState, this.getColor());
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.hasCustomHoverName()) {
            if (pLevel.getBlockEntity(pPos) instanceof CeilingBannerBlockTile tile) {
                tile.setCustomName(pStack.getHoverName());
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        return blockEntity instanceof CeilingBannerBlockTile ? ((CeilingBannerBlockTile) blockEntity).getItem(pState) : super.getCloneItemStack(pLevel, pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();

        //put post on map
        if (item instanceof MapItem) {
            if (!pLevel.isClientSide) {
                if (MapItem.getSavedData(itemstack, pLevel) instanceof ExpandedMapData data) {
                    data.toggleCustomDecoration(pLevel, pPos);
                }
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
