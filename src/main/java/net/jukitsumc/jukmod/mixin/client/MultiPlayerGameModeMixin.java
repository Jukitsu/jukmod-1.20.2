package net.jukitsumc.jukmod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow
    private GameType localPlayerMode;

    @Unique
    private BooleanOption missTime = Jukmod.getInstance().getConfig().gameplay().missTime();
    /**
     * @author Jukitsu
     * @reason For PVP
     */
    @Inject(method="hasMissTime", at=@At("HEAD"), cancellable = true)
    public void hasMissTime(CallbackInfoReturnable cir) {
        cir.setReturnValue(missTime.get() && !this.localPlayerMode.isCreative());
    }
}
