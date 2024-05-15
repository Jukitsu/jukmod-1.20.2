package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(BodyRotationControl.class)
public abstract class BodyRotationControlMixin {

    @Shadow
    @Final
    private Mob mob;
    @Unique
    private BooleanOption oldBackwards;

    @Shadow
    protected abstract void rotateHeadIfNecessary();

    @Shadow
    protected abstract void rotateBodyIfNecessary();

    @Shadow
    protected abstract void rotateHeadTowardsFront();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        oldBackwards = Jukmod.getInstance().getConfig().animations().oldBackwards();
    }

    @ModifyExpressionValue(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getYRot()F"))
    public float clientTick(float yRot) {
        return oldBackwards.get() ? mob.yBodyRot : yRot;
    }

    @Redirect(method = "clientTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/control/BodyRotationControl;rotateHeadIfNecessary()V"))
    public void noRotateHead(BodyRotationControl instance) {
        if (!oldBackwards.get()) {
            this.rotateHeadIfNecessary();
        }
    }


}
