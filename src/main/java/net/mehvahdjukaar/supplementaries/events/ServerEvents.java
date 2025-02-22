package net.mehvahdjukaar.supplementaries.events;


import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.block.blocks.ClockBlock;
import net.mehvahdjukaar.supplementaries.block.blocks.RakedGravelBlock;
import net.mehvahdjukaar.supplementaries.block.blocks.RopeBlock;
import net.mehvahdjukaar.supplementaries.common.CommonUtil;
import net.mehvahdjukaar.supplementaries.common.ModTags;
import net.mehvahdjukaar.supplementaries.common.SpecialPlayers;
import net.mehvahdjukaar.supplementaries.compat.CompatHandler;
import net.mehvahdjukaar.supplementaries.compat.quark.QuarkPlugin;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.mehvahdjukaar.supplementaries.configs.ServerConfigs;
import net.mehvahdjukaar.supplementaries.entities.ThrowableBrickEntity;
import net.mehvahdjukaar.supplementaries.entities.goals.EatFodderGoal;
import net.mehvahdjukaar.supplementaries.items.CandyItem;
import net.mehvahdjukaar.supplementaries.network.NetworkHandler;
import net.mehvahdjukaar.supplementaries.network.ClientBoundSendLoginMessagePacket;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.PacketDistributor;


public class ServerEvents {

    //high priority event to override other wall lanterns
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlockHigh(PlayerInteractEvent.RightClickBlock event) {
        if (ServerConfigs.cached.WALL_LANTERN_HIGH_PRIORITY) {
            Player player = event.getPlayer();
            if (!player.isSpectator()) {
                ItemsOverrideHandler.tryHighPriorityOverride(event, event.getItemStack());
            }
        }
    }

    //block placement should stay low in priority to allow other more important mod interaction that use the event
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getPlayer();
        if (!player.isSpectator()) {
            ItemStack stack = event.getItemStack();

            ItemsOverrideHandler.tryPerformOverride(event, stack, false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player playerIn = event.getPlayer();

        ItemsOverrideHandler.tryPerformOverride(event, playerIn.getItemInHand(event.getHand()));
    }

    //raked gravel
    @SubscribeEvent
    public static void onHoeUsed(UseHoeEvent event) {
        UseOnContext context = event.getContext();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (ServerConfigs.cached.RAKED_GRAVEL) {
            if (world.getBlockState(pos).is(Blocks.GRAVEL)) {
                BlockState raked = ModRegistry.RAKED_GRAVEL.get().defaultBlockState();
                if (raked.canSurvive(world, pos)) {
                    world.setBlock(pos, RakedGravelBlock.getConnectedState(raked, world, pos, context.getHorizontalDirection()), 11);
                    world.playSound(context.getPlayer(), pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    event.setResult(Event.Result.ALLOW);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (CompatHandler.quark) {
            QuarkPlugin.attachCapabilities(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            CandyItem.checkSweetTooth(event.player);
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Animal animal){
            EntityType<?> type = event.getEntity().getType();
            if(ModTags.EATS_FODDER.contains(type)){
                animal.goalSelector.addGoal(3, new EatFodderGoal(animal, 1, 8, 2, 30));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()),
                    new ClientBoundSendLoginMessagePacket());
        } catch (Exception exception) {
            Supplementaries.LOGGER.warn("failed to end login message: " + exception);
        }
        //send in pickles

        //TODO: readd
        //PicklePlayer.PickleData.onPlayerLogin(event.getPlayer());

    }




}
