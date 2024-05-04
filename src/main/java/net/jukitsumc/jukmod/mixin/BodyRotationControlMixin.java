package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.phys.shapes.BooleanOp;
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

    @Shadow protected abstract void rotateHeadIfNecessary();

    @Unique
    private BooleanOption oldBackwards;

    @Inject(method="<init>", at=@At("TAIL"))
    private void initialize(CallbackInfo ci) {
        oldBackwards = Jukmod.getInstance().getConfig().animations().oldBackwards();
    }

    @ModifyExpressionValue(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getYRot()F"))
    public float clientTick(float yRot) {
        return oldBackwards.get() ? mob.yBodyRot : yRot;
    }

    @Redirect(method = "clientTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/control/BodyRotationControl;rotateHeadIfNecessary()V"))
    public void clientTick(BodyRotationControl instance) {
        if (!oldBackwards.get()) {
            this.rotateHeadIfNecessary();
        }
    }


}
