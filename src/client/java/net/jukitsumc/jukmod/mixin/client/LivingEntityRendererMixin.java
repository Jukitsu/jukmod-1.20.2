package net.jukitsumc.jukmod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    protected M model;

    public LivingEntityRendererMixin(EntityRendererProvider.Context context, M entityModel, float f) {
        super(context);
        this.model = entityModel;
        this.shadowRadius = f;
    }

    @Shadow protected abstract float getFlipDegrees(T livingEntity);

    @Inject(method="setupRotations", at=@At("TAIL"))
    protected void setupRotations(T livingEntity, PoseStack poseStack, float f, float g, float h, CallbackInfo info) {

        if (livingEntity.deathTime > 0 && livingEntity instanceof Mob) {
            float i = ((float) livingEntity.deathTime + h - 1.0F) / 20.0F * 1.6F;
            i = Mth.sqrt(i);
            if (i > 1.0F) {
                i = 1.0F;
            }
            poseStack.mulPose(Axis.ZN.rotationDegrees(i * this.getFlipDegrees(livingEntity)));
            poseStack.mulPose(Axis.XP.rotationDegrees(i * this.getFlipDegrees(livingEntity)));
        }

    }
/*
    @Inject(method="getRenderType", at=@At("HEAD"), cancellable = true) @Nullable
    protected void getRenderType(T livingEntity, boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<RenderType> returninfo) {
        if (livingEntity.hurtTime > 0) {
            ResourceLocation resourceLocation = this.getTextureLocation(livingEntity);
            returninfo.setReturnValue(RenderType.entityDecal(resourceLocation));
        }
    }
    */

}
