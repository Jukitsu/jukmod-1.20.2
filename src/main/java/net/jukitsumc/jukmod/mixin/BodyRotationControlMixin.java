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
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(BodyRotationControl.class)
public abstract class BodyRotationControlMixin {

    @Shadow
    @Final
    private Mob mob;

    @Unique
    private BooleanOption oldBackwards = Jukmod.getInstance().getConfig().animations().oldBackwards();

    @ModifyExpressionValue(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getYRot()F"))
    public float clientTick(float yRot) {
        return oldBackwards.get() ? mob.yBodyRot : yRot;
    }


}
