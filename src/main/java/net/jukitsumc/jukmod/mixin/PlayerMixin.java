package net.jukitsumc.jukmod.mixin;

import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.KohiConstants;
import net.jukitsumc.jukmod.config.option.LongSliderOption;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Unique
    private LongSliderOption knockbackType;

    @Inject(method="<init>", at=@At("TAIL"))
    public void initialize(CallbackInfo ci) {
        knockbackType = Jukmod.getInstance().getConfig().gameplay().knockbackType();
    }

    @Redirect(method="attack", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V", ordinal = 0))
    public void onSprintHitKnockback(LivingEntity entity, double d, double x, double z) {
        if (knockbackType.get() == 0) {
            double amount = 2.0D * d;
            entity.push(
                    -x * KohiConstants.knockbackExtraHorizontal * amount,
                    KohiConstants.knockbackExtraVertical,
                    -z * KohiConstants.knockbackExtraHorizontal * amount
            );
        } else if (knockbackType.get() == -1) {
            entity.push(
                    -x * d,
                    0.1D,
                    -z * d
            );
        } else {
            entity.knockback(d, x, z);
        }

    }
}
