package net.jukitsumc.jukmod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Unique
    private BooleanOption oldSwing;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        oldSwing = Jukmod.getInstance().getConfig().animations().oldSwing();
    }

    /*
    @Inject(method="applyItemArmTransform", at=@At("TAIL"))
    public void onApplyItemTransforms(PoseStack poseStack, HumanoidArm humanoidArm, float f, CallbackInfo ci) {
        int i = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.mulPose(Axis.YP.rotationDegrees(i * 5.0F));
        poseStack.translate(-0.01F, -0.01F, -0.015F);
    }
    */



}
