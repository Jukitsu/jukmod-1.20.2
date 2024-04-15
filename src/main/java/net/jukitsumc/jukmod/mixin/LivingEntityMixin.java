package net.jukitsumc.jukmod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void knockback(double d, double e, double f);

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;abs(F)F"))
    private float forceOldBackwardsAnimations(float f) {
        return 0.0F;
    }

    @Overwrite
    public void blockedByShield(LivingEntity livingEntity) {
        this.knockback(0.5, livingEntity.getX() - this.getX(), livingEntity.getZ() - this.getZ());
    }


}
