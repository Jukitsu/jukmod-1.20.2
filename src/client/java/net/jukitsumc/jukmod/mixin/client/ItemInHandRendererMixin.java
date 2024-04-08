package net.jukitsumc.jukmod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(
        method = "applyItemArmAttackTransform",
        at = @At(value = "HEAD")
    )
    private void onApplyItemArmAttackTransform(PoseStack poseStack, HumanoidArm hand, float swingProgress, CallbackInfo callback)
    {
        float progress = Mth.sin((float)Math.PI * swingProgress);
        float scale = 1.0F - (0.3F * progress);

        poseStack.translate(-0.12F * progress, 0.085F * progress, 0.0F);
        poseStack.scale(scale, scale, scale);
    }
}
