package net.mehvahdjukaar.supplementaries.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.setup.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommonUtil {

    //blockstate properties
    public static final BooleanProperty EXTENDING = BooleanProperty.create("extending");
    public static final IntegerProperty HOUR = IntegerProperty.create("hour", 0, 23);
    public static final IntegerProperty EXTENSION = IntegerProperty.create("extension", 0, 2);
    public static final BooleanProperty TILE = BooleanProperty.create("tile");
    public static final IntegerProperty TILE_3 = IntegerProperty.create("tile_3", 0, 2);
    public static final BooleanProperty HAS_LAVA = BooleanProperty.create("has_lava");
    public static final BooleanProperty HAS_WATER = BooleanProperty.create("has_water");
    public static final BooleanProperty HAS_JAR = BooleanProperty.create("has_jar");
    public static final EnumProperty<WoodType> WOOD_TYPE = EnumProperty.create("wood_type", WoodType.class);
    // it's detecting incoming laser and its distance
    public static final IntegerProperty RECEIVING = IntegerProperty.create("laser_receiving", 0, 15);
    public static final IntegerProperty LIGHT_LEVEL_0_15 = IntegerProperty.create("light_level", 0, 15);


    //sounds
    //TODO: add alot more
    public static final ResourceLocation TICK_1 = new ResourceLocation("supplementaries:tick_1");
    public static final ResourceLocation TICK_2 = new ResourceLocation(Supplementaries.MOD_ID, "tick_2");


    //textures
    public static final ResourceLocation WATER_TEXTURE = new ResourceLocation("minecraft:block/water_still");
    public static final ResourceLocation LAVA_TEXTURE = new ResourceLocation("minecraft:block/lava_still");
    public static final ResourceLocation MILK_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/milk_liquid");
    public static final ResourceLocation POTION_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/potion_liquid");
    public static final ResourceLocation HONEY_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/honey_liquid");
    public static final ResourceLocation DRAGON_BREATH_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/dragon_breath_liquid");
    public static final ResourceLocation XP_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/xp_liquid");
    public static final ResourceLocation FAUCET_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/faucet_water");
    public static final ResourceLocation FISHIES_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/fishies");
    public static final ResourceLocation BELLOWS_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/bellows");
    public static final ResourceLocation LASER_BEAM_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/laser_beam");
    public static final ResourceLocation LASER_BEAM_END_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/laser_beam_end");
    public static final ResourceLocation SOUP_TEXTURE = new ResourceLocation(Supplementaries.MOD_ID, "blocks/soup_liquid");


    public static List<ResourceLocation> getTextures() {
        return new ArrayList<>(Arrays.asList(MILK_TEXTURE, POTION_TEXTURE, HONEY_TEXTURE, DRAGON_BREATH_TEXTURE, SOUP_TEXTURE,
                XP_TEXTURE, FAUCET_TEXTURE, FISHIES_TEXTURE, BELLOWS_TEXTURE, LASER_BEAM_TEXTURE, LASER_BEAM_END_TEXTURE));
    }

    //fluids
    public enum JarContentType {
        // color is handles separately. here it's just for default case
        WATER(WATER_TEXTURE, 0x3F76E4, true, 1f, true, true, false, -1),
        LAVA(LAVA_TEXTURE, 0xFF6600, false, 1f, false, true, false, -1),
        MILK(MILK_TEXTURE, 0xFFFFFF, false, 1f, false, true, false, -1),
        POTION(POTION_TEXTURE, 0x3F76E4, true, 0.88f, true, false, false, -1),
        HONEY(HONEY_TEXTURE, 0xFAAC1C, false, 0.85f, true, false, false, -1),
        DRAGON_BREATH(DRAGON_BREATH_TEXTURE, 0xFF33FF, true, 0.8f, true, false, false, -1),
        XP(XP_TEXTURE, 0x33FF33, false, 0.95f, true, false, false, -1),
        TROPICAL_FISH(WATER_TEXTURE, 0x3F76E4, true, 1f, false, true, false, 0),
        SALMON(WATER_TEXTURE, 0x3F76E4, true, 1f, false, true, false, 1),
        COD(WATER_TEXTURE, 0x3F76E4, true, 1f, false, true, false, 2),
        PUFFER_FISH(WATER_TEXTURE, 0x3F76E4, true, 1f, false, true, false, 3),
        COOKIES(WATER_TEXTURE, 0x000000, false, 1f, false, false, false, -1),
        EMPTY(WATER_TEXTURE, 0x000000, false, 1f, false, false, false, -1),
        MUSHROOM_STEW(SOUP_TEXTURE, 0xffad89, true, 1f, false, false, true, -1),
        BEETROOT_SOUP(SOUP_TEXTURE, 0xC93434, true, 1f, false, false, true, -1),
        SUSPICIOUS_STEW(SOUP_TEXTURE, 0xBAE85F, true, 1f, false, false, true, -1),
        RABBIT_STEW(SOUP_TEXTURE, 0xFF904F, true, 1f, false, false, true, -1);

        public final ResourceLocation texture;
        public final float opacity;
        public final int color;
        public final boolean applyColor;
        public final boolean bucket;
        public final boolean bottle;
        public final int fishType;
        public final boolean bowl;

        JarContentType(ResourceLocation texture, int color, boolean applycolor, float opacity, boolean bottle, boolean bucket, boolean bowl, int fishtype) {
            this.texture = texture;
            this.color = color; // beacon color. this will also be texture color if applycolor is true
            this.applyColor = applycolor; // is texture grayscale and needs to be colored?
            this.opacity = opacity;
            this.bottle = bottle;
            this.bucket = bucket;
            this.bowl = bowl;
            this.fishType = fishtype;
            // offset for fish textures. -1 is no fish
        }

        public boolean isFish() {
            return this.fishType != -1;
        }

        public boolean isLava() {
            return this == JarContentType.LAVA;
        }

        public boolean isWater() {
            return this.isFish() || this == JarContentType.WATER;
        }

        public Item getReturnItem() {
            if (this.bottle)
                return Items.GLASS_BOTTLE;
            else if (this.bucket)
                return Items.BUCKET;
            else if (this.bowl)
                return Items.BOWL;
            return null;
        }

        public boolean makesSound() {
            return this.bottle || this.bowl || this.bucket;
        }

        //only for bucket
        public SoundEvent getSound() {
            if (this.isLava()) return SoundEvents.ITEM_BUCKET_FILL_LAVA;
            else if (this.isFish()) return SoundEvents.ITEM_BUCKET_FILL_FISH;
            else return SoundEvents.ITEM_BUCKET_FILL;
        }

    }

    public static JarContentType getJarContentTypeFromItem(ItemStack stack) {
        Item i = stack.getItem();
        if (i instanceof PotionItem) {
            if (PotionUtils.getPotionFromItem(stack).equals(Potions.WATER)) {
                return JarContentType.WATER;
            } else {
                return JarContentType.POTION;
            }
        } else if (i instanceof FishBucketItem) {
            if (i == Items.COD_BUCKET) {
                return JarContentType.COD;
            } else if (i == Items.PUFFERFISH_BUCKET) {
                return JarContentType.PUFFER_FISH;
            } else if (i == Items.SALMON_BUCKET) {
                return JarContentType.SALMON;
            } else {
                return JarContentType.TROPICAL_FISH;
            }
        } else if (i == Items.LAVA_BUCKET) {
            return JarContentType.LAVA;
        } else if (i instanceof HoneyBottleItem) {
            return JarContentType.HONEY;
        } else if (i instanceof MilkBucketItem) {
            return JarContentType.MILK;
        } else if (i == Items.DRAGON_BREATH) {
            return JarContentType.DRAGON_BREATH;
        } else if (i instanceof ExperienceBottleItem) {
            return JarContentType.XP;
        } else if (i == Items.MUSHROOM_STEW) {
            return JarContentType.MUSHROOM_STEW;
        } else if (i == Items.RABBIT_STEW) {
            return JarContentType.RABBIT_STEW;
        } else if (i == Items.BEETROOT_SOUP) {
            return JarContentType.BEETROOT_SOUP;
        } else if (i instanceof SuspiciousStewItem) {
            return JarContentType.SUSPICIOUS_STEW;
        } else if (i == Items.COOKIE) {
            return JarContentType.COOKIES;
        }
        return JarContentType.EMPTY;
    }

    public static int getLiquidCountFromItem(Item i) {
        if (i instanceof FishBucketItem) return 1;
        if (i instanceof MilkBucketItem || i == Items.LAVA_BUCKET || i == Items.WATER_BUCKET) return 4;
        else if (i instanceof SoupItem || i instanceof SuspiciousStewItem) return 2;
        else return 1;
    }

    public enum WoodType implements IStringSerializable {
        NONE("none"),
        OAK("oak"),
        BIRCH("birch"),
        SPRUCE("spruce"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak"),
        CRIMSON("crimson"),
        WARPED("warped");
        private final String name;

        WoodType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getString() {
            return this.name;
        }

    }

    //else if else if else if
    public static WoodType getWoodTypeFromSignPostItem(Item item) {
        if (item == Registry.SIGN_POST_ITEM_OAK.get()) {
            return WoodType.OAK;
        } else if (item == Registry.SIGN_POST_ITEM_BIRCH.get()) {
            return WoodType.BIRCH;
        } else if (item == Registry.SIGN_POST_ITEM_SPRUCE.get()) {
            return WoodType.SPRUCE;
        } else if (item == Registry.SIGN_POST_ITEM_JUNGLE.get()) {
            return WoodType.JUNGLE;
        } else if (item == Registry.SIGN_POST_ITEM_ACACIA.get()) {
            return WoodType.ACACIA;
        } else if (item == Registry.SIGN_POST_ITEM__DARK_OAK.get()) {
            return WoodType.DARK_OAK;
        } else if (item == Registry.SIGN_POST_ITEM_CRIMSON.get()) {
            return WoodType.CRIMSON;
        } else if (item == Registry.SIGN_POST_ITEM_WARPED.get()) {
            return WoodType.WARPED;
        }
        return WoodType.NONE;
    }

    public static Item getSignPostItemFromWoodType(WoodType wood) {
        switch (wood) {
            case OAK:
                return Registry.SIGN_POST_ITEM_OAK.get();
            case BIRCH:
                return Registry.SIGN_POST_ITEM_BIRCH.get();
            case SPRUCE:
                return Registry.SIGN_POST_ITEM_SPRUCE.get();
            case JUNGLE:
                return Registry.SIGN_POST_ITEM_JUNGLE.get();
            case ACACIA:
                return Registry.SIGN_POST_ITEM_ACACIA.get();
            case DARK_OAK:
                return Registry.SIGN_POST_ITEM__DARK_OAK.get();
            case CRIMSON:
                return Registry.SIGN_POST_ITEM_CRIMSON.get();
            case WARPED:
                return Registry.SIGN_POST_ITEM_WARPED.get();
            default:
            case NONE:
                return Items.AIR.getItem();
        }
    }

    //bounding box
    static public AxisAlignedBB getDirectionBB(BlockPos pos, Direction facing, int offset) {
        BlockPos endPos = pos.offset(facing, offset);
        switch (facing) {
            default:
            case NORTH:
                endPos = endPos.add(1, 1, 0);
                break;
            case SOUTH:
                endPos = endPos.add(1, 1, 1);
                pos = pos.add(0,0,1);
                break;
            case UP:
                endPos = endPos.add(1, 1, 1);
                pos = pos.add(0,1,0);
                break;
            case EAST:
                endPos = endPos.add(1, 1, 1);
                pos = pos.add(1,0,0);
                break;
            case WEST:
                endPos = endPos.add(0, 1, 1);
                break;
            case DOWN:
                endPos = endPos.add(1, 0, 1);
                break;
        }
        return new AxisAlignedBB(pos, endPos);
    }

    //renderer


    //centered on x,z. aligned on y=0
    @OnlyIn(Dist.CLIENT)
    public static void addCube(IVertexBuilder builder, MatrixStack matrixStackIn, float w, float h, TextureAtlasSprite sprite, int combinedLightIn,
                               int color, float a, int combinedOverlayIn, boolean up, boolean down, boolean fakeshading, boolean flippedY) {
        int lu = combinedLightIn & '\uffff';
        int lv = combinedLightIn >> 16 & '\uffff'; // ok
        float atlasscaleU = sprite.getMaxU() - sprite.getMinU();
        float atlasscaleV = sprite.getMaxV() - sprite.getMinV();
        float minu = sprite.getMinU();
        float minv = sprite.getMinV();
        float maxu = minu + atlasscaleU * w;
        float maxv = minv + atlasscaleV * h;
        float maxv2 = minv + atlasscaleV * w;

        float r = (float) ((color >> 16 & 255)) / 255.0F;
        float g = (float) ((color >> 8 & 255)) / 255.0F;
        float b = (float) ((color & 255)) / 255.0F;


        // float a = 1f;// ((color >> 24) & 0xFF) / 255f;
        // shading:

        float r8, g8, b8, r6, g6, b6, r5, g5, b5;

        r8 = r6 = r5 = r;
        g8 = g6 = g5 = g;
        b8 = b6 = b5 = b;
        //TODO: make this affect uv values not rgb
        if (fakeshading) {
            float s1 = 0.8f, s2 = 0.6f, s3 = 0.5f;
            // 80%: s,n
            r8 *= s1;
            g8 *= s1;
            b8 *= s1;
            // 60%: e,w
            r6 *= s2;
            g6 *= s2;
            b6 *= s2;
            // 50%: d
            r5 *= s3;
            g5 *= s3;
            b5 *= s3;
            //100%

        }

        float hw = w / 2f;
        float hh = h / 2f;

        // up
        if (up)
            addQuadTop(builder, matrixStackIn, -hw, h, hw, hw, h, -hw, minu, minv, maxu, maxv2, r, g, b, a, lu, lv, 0, 1, 0);
        // down
        if (down)
            addQuadTop(builder, matrixStackIn, -hw, 0, -hw, hw, 0, hw, minu, minv, maxu, maxv2, r5, g5, b5, a, lu, lv, 0, -1, 0);


        if (flippedY) {
            float temp = minv;
            minv = maxv;
            maxv = temp;
        }
        // south z+
        // x y z u v r g b a lu lv
        addQuadSide(builder, matrixStackIn, hw, 0, -hw, -hw, h, -hw, minu, minv, maxu, maxv, r8, g8, b8, a, lu, lv, 0, 0, 1);
        // west
        addQuadSide(builder, matrixStackIn, -hw, 0, -hw, -hw, h, hw, minu, minv, maxu, maxv, r6, g6, b6, a, lu, lv, -1, 0, 0);
        // north
        addQuadSide(builder, matrixStackIn, -hw, 0, hw, hw, h, hw, minu, minv, maxu, maxv, r8, g8, b8, a, lu, lv, 0, 0, -1);
        // east
        addQuadSide(builder, matrixStackIn, hw, 0, hw, hw, h, -hw, minu, minv, maxu, maxv, r6, g6, b6, a, lu, lv, 1, 0, 0);
    }

    /*
        @OnlyIn(Dist.CLIENT)
        public static void addDoubleQuadSide(IVertexBuilder builder, MatrixStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, float r, float g,
                                             float b, float a, int lu, int lv){
            addQuadSide(builder, matrixStackIn, x0, y0, z0, x1, y1, z1, u0, v0, u1, v1, r, g, b, a, lu, lv);
            addQuadSide(builder, matrixStackIn, x1, y0, z1, x0, y1, z0, u0, v0, u1, v1, r, g, b, a, lu, lv);
        }
    */
    @OnlyIn(Dist.CLIENT)
    public static void addQuadSide(IVertexBuilder builder, MatrixStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, float r, float g,
                                   float b, float a, int lu, int lv, float nx, float ny, float nz) {
        addVert(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y0, z1, u1, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x0, y1, z0, u0, v0, r, g, b, a, lu, lv, nx, ny, nz);
    }

    /*
        @OnlyIn(Dist.CLIENT)
        public static void addQuadSideF(IVertexBuilder builder, MatrixStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, float r, float g,
                                        float b, float a, int lu, int lv) {
            addVert(builder, matrixStackIn, x0, y0, z0, u0, v0, r, g, b, a, lu, lv);
            addVert(builder, matrixStackIn, x1, y0, z1, u1, v0, r, g, b, a, lu, lv);
            addVert(builder, matrixStackIn, x1, y1, z1, u1, v1, r, g, b, a, lu, lv);
            addVert(builder, matrixStackIn, x0, y1, z0, u0, v1, r, g, b, a, lu, lv);
        }
    */
    @OnlyIn(Dist.CLIENT)
    public static void addQuadTop(IVertexBuilder builder, MatrixStack matrixStackIn, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, float r, float g,
                                  float b, float a, int lu, int lv, float nx, float ny, float nz) {
        addVert(builder, matrixStackIn, x0, y0, z0, u0, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y0, z0, u1, v1, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x1, y1, z1, u1, v0, r, g, b, a, lu, lv, nx, ny, nz);
        addVert(builder, matrixStackIn, x0, y1, z1, u0, v0, r, g, b, a, lu, lv, nx, ny, nz);
    }

    @OnlyIn(Dist.CLIENT)
    public static void addVert(IVertexBuilder builder, MatrixStack matrixStackIn, float x, float y, float z, float u, float v, float r, float g,
                               float b, float a, int lu, int lv, float nx, float ny, float nz) {
        builder.pos(matrixStackIn.getLast().getMatrix(), x, y, z).color(r, g, b, a).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(lu, lv)
                .normal(matrixStackIn.getLast().getNormal(), nx, ny, nz).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderFish(IVertexBuilder builder, MatrixStack matrixStackIn, float wo, float ho, int fishType, int combinedLightIn,
                                  int combinedOverlayIn) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(FISHIES_TEXTURE);
        float w = 5 / 16f;
        float h = 4 / 16f;
        float hw = w / 2f;
        float hh = h / 2f;
        int lu = combinedLightIn & '\uffff';
        int lv = combinedLightIn >> 16 & '\uffff';
        float atlasscaleU = sprite.getMaxU() - sprite.getMinU();
        float atlasscaleV = sprite.getMaxV() - sprite.getMinV();
        float minu = sprite.getMinU();
        float maxv = sprite.getMinV() + atlasscaleV * fishType * h;
        float maxu = atlasscaleU * w + minu;
        float minv = atlasscaleV * h + maxv;
        for (int j = 0; j < 2; j++) {
            CommonUtil.addVert(builder, matrixStackIn, hw - Math.abs(wo / 2), -hh + ho, +wo, minu, minv, 1, 1, 1, 1, lu, lv, 0, 1, 0);
            CommonUtil.addVert(builder, matrixStackIn, -hw + Math.abs(wo / 2), -hh + ho, -wo, maxu, minv, 1, 1, 1, 1, lu, lv, 0, 1, 0);
            CommonUtil.addVert(builder, matrixStackIn, -hw + Math.abs(wo / 2), hh + ho, -wo, maxu, maxv, 1, 1, 1, 1, lu, lv, 0, 1, 0);
            CommonUtil.addVert(builder, matrixStackIn, hw - Math.abs(wo / 2), hh + ho, +wo, minu, maxv, 1, 1, 1, 1, lu, lv, 0, 1, 0);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
            float temp = minu;
            minu = maxu;
            maxu = temp;
        }
    }


}