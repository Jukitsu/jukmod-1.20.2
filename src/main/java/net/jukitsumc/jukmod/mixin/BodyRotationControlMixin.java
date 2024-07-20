package net.jukitsumc.jukmod.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BodyRotationControl.class)
public class BodyRotationControlMixin {

    @Shadow @Final
    private Mob mob;

    @Unique
    private BooleanOption oldBackwardsOption;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        oldBackwardsOption = Jukmod.getInstance().getConfig().animations().oldBackwards();
    }
    @ModifyExpressionValue(method="clientTick",
            at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getYRot()F"))
    public float handleMobRotation(float original) {
        return oldBackwardsOption.get() ? mob.yBodyRot : original;
    }

    @Overwrite
    private void rotateHeadIfNecessary() {
        if (!oldBackwardsOption.get())
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
    }
}
