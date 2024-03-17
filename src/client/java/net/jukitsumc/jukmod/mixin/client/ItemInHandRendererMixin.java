package net.jukitsumc.jukmod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Shadow @Final public static final float ITEM_SWING_X_ROT_AMOUNT = -75.0F;
    @Shadow @Final public static final float ITEM_SWING_Y_ROT_AMOUNT = -25.0F;
    @Shadow @Final public static final float ITEM_SWING_Z_ROT_AMOUNT = -25.0F;

    // private final Minecraft mc = Minecraft.getInstance();


    @Overwrite
    private void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f) {
        int i = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        float g = Mth.sin(f * f * 3.1415927F);
        poseStack.mulPose(Axis.YP.rotationDegrees((float)i * (45.0F + g * ITEM_SWING_Y_ROT_AMOUNT)));
        float h = Mth.sin(Mth.sqrt(f) * 3.1415927F);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float)i * h * ITEM_SWING_Z_ROT_AMOUNT));
        poseStack.mulPose(Axis.XP.rotationDegrees(h * ITEM_SWING_X_ROT_AMOUNT));
        poseStack.mulPose(Axis.YP.rotationDegrees((float)i * -45.0F));
    }

    @Overwrite
    private void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f) {
        int i = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((float)i * 0.50F, -0.52F + f * -0.6F, -0.75F);
    }

}
