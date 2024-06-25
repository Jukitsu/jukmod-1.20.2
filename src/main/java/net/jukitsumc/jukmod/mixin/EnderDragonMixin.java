package net.jukitsumc.jukmod.mixin;

import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {

    @Unique
    private BooleanOption dragonMovement;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        dragonMovement = Jukmod.getInstance().getConfig().gameplay().dragonMovement();
    }


    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 properlyDive(Vec3 instance, double d, double e, double f) {
        int i = dragonMovement.get() ? 10 : 1;
        return instance.add(d, e * i, f);
    }

}
