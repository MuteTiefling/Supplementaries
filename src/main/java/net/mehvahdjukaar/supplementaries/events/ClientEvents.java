package net.mehvahdjukaar.supplementaries.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.client.gui.ConfigButton;
import net.mehvahdjukaar.supplementaries.client.renderers.BlackboardTextureManager;
import net.mehvahdjukaar.supplementaries.compat.CompatHandler;
import net.mehvahdjukaar.supplementaries.compat.quark.QuarkTooltipPlugin;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import com.mojang.math.Matrix4f;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;


@Mod.EventBusSubscriber(modid = Supplementaries.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if ((event.getPlayer() != null) && (event.getPlayer().level != null)) {

            if (ClientConfigs.cached.TOOLTIP_HINTS && Minecraft.getInstance().options.advancedItemTooltips) {
                ItemsOverrideHandler.addOverrideTooltips(event);
            }

            if (CompatHandler.quark) {
                QuarkTooltipPlugin.onItemTooltipEvent(event);
            }

            if(event.getItemStack().getItem() == ModRegistry.ROPE_ARROW_ITEM.get()){
                List<Component> tooltip = event.getToolTip();
                Optional<Component> r = tooltip.stream().filter(t-> (t instanceof TranslatableComponent) && ((TranslatableComponent) t)
                        .getKey().equals("item.durability")).findFirst();
                r.ifPresent(tooltip::remove);
            }
        }
    }

    //TODO: readd

//    @SubscribeEvent
//    public static void renderTooltipEvent(RenderTooltipEvent.PostText event) {
//        ItemStack stack = event.getStack();
//        Item i = stack.getItem();
//        if (CompatHandler.quark) {
//            if (i == ModRegistry.SACK_ITEM.get()) {
//                QuarkTooltipPlugin.renderTooltipEvent(event);
//            } else if (i == ModRegistry.SAFE_ITEM.get()) {
//                QuarkTooltipPlugin.renderTooltipEvent(event);
//            }
//        }
//        if (i == ModRegistry.BLACKBOARD_ITEM.get()) {
//            CompoundTag cmp = stack.getTagElement("BlockEntityTag");
//            if (cmp != null && cmp.contains("Pixels")) {
//                long[] packed = cmp.getLongArray("Pixels");
//
//                //credits to quark. Uses same code so it's consistent with map preview
//                Minecraft mc = Minecraft.getInstance();
//
//                int pad = 7;
//                float size = 135.0F;
//                float scale = 0.5F;
//
//                PoseStack matrixStack = event.getMatrixStack();
//                RenderSystem.color3f(1.0F, 1.0F, 1.0F);
//                mc.getTextureManager().bind(BlackboardTextureManager.INSTANCE.getResourceLocation(packed));
//                Tesselator tessellator = Tesselator.getInstance();
//                BufferBuilder buffer = tessellator.getBuilder();
//
//                matrixStack.translate(event.getX(), (float) event.getY() - size * scale - 5.0F, 500.0D);
//                matrixStack.scale(scale, scale, 1.0F);
//                RenderSystem.enableBlend();
//                Matrix4f mat = matrixStack.last().pose();
//
//                //AbstractGui.blit(matrix, x, y, 0.0F, 0.0F, 1*width, 1*width, 16*width, 16*width);
//                buffer.begin(7, DefaultVertexFormat.POSITION_TEX);
//                buffer.vertex(mat, (float) (-pad), size, 0.0F).uv(0.0F, 1.0F).endVertex();
//                buffer.vertex(mat, size, size, 0.0F).uv(1.0F, 1.0F).endVertex();
//                buffer.vertex(mat, size, (float) (-pad), 0.0F).uv(1.0F, 0.0F).endVertex();
//                buffer.vertex(mat, (float) (-pad), (float) (-pad), 0.0F).uv(0.0F, 0.0F).endVertex();
//                tessellator.end();
//            }
//        }
//    }


    //enderman hold block in rain
    /*
    @SubscribeEvent
    public static void onRenderEnderman(RenderLivingEvent<EndermanEntity, EndermanModel<EndermanEntity>> event) {
        if(event.getEntity()instanceof EndermanEntity){
            LivingRenderer<EndermanEntity, EndermanModel<EndermanEntity>> renderer = event.getRenderer();
            if(renderer instanceof EndermanRenderer) {
                MatrixStack matrixStack = event.getMatrixStack();
                matrixStack.push();

                //renderer.getEntityModel().bipedLeftArm.showModel=false;

                //event.getRenderer().getEntityModel().bipedLeftArm.rotateAngleX=180;


                event.getRenderer().getEntityModel().bipedLeftArm.showModel=true;
                //bipedRightArm.rotateAngleX=100;
                int i = getPackedOverlay(event.getEntity(), 0);
                //event.getRenderer().getEntityModel().bipedLeftArm.render(event.getMatrixStack(),event.getBuffers().getBuffer(RenderType.getEntityCutout(new ResourceLocation("textures/entity/enderman/enderman.png"))), event.getLight(),i);
                event.getRenderer().getEntityModel().bipedLeftArm.showModel=false;
                matrixStack.pop();
            }
        }
    }*/
    /*
    @SubscribeEvent
    public static void onRenderEnderman(PlayerInteractEvent.EntityInteractSpecific event) {

        Entity e = event.getTarget();
        if(e instanceof MobEntity && event.getItemStack().getItem() instanceof CompassItem){
            ((MobEntity) e).setHomePosAndDistance(new BlockPos(0,63,0),100);
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);
        }
    }*/


    @SubscribeEvent
    public static void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
        if (!ClientConfigs.cached.CONFIG_BUTTON) return;
        if (!CompatHandler.configured) return;
        ConfigButton.setupConfigButton(event);

    }
}
