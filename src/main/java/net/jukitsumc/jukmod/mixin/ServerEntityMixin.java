package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.config.option.LongSliderOption;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow
    @Final
    private ServerLevel level;

    @Unique
    private LongSliderOption entityUpdateInterval;

    @Unique
    private BooleanOption universalEntityUpdateInterval;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initialize(CallbackInfo ci) {
        this.entityUpdateInterval = Jukmod.getInstance().getConfig().entities().entityUpdateInterval();
        this.universalEntityUpdateInterval = Jukmod.getInstance().getConfig().entities().universalEntityUpdateInterval();
    }

    @ModifyExpressionValue(method = "sendChanges", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerEntity;updateInterval:I"))
    private int modifyUpdateInterval(int interval) {
        return (interval <= 5 || this.universalEntityUpdateInterval.get()) && this.entityUpdateInterval.get() > 0 ?
                (int) (long) this.entityUpdateInterval.get() : interval;
    }
}
