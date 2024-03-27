package net.jukitsumc.jukmod.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    @Shadow
    @Final
    private BodyRotationControl bodyRotationControl;

    protected MobMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tickHeadTurn", at = @At("HEAD"))
    protected void tickHeadTurn(float f, float g, CallbackInfoReturnable returninfo) {
        float h = Mth.wrapDegrees(f - this.yBodyRot);
        this.yBodyRot += h * 0.3F;
        float i = Mth.wrapDegrees(this.getYRot() - this.yBodyRot);
        if (Math.abs(i) > 50.0F) {
            this.yBodyRot += i - (float) (Mth.sign(i) * 50);
        }

        boolean bl = i < -90.0F || i >= 90.0F;
        if (bl) {
            g *= -1.0F;
        }
    }
}
