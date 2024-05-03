package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.LongSliderOption;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow @Final
    protected ServerLevel level;

    @Unique
    private LongSliderOption entityUpdateInterval = Jukmod.getInstance().getConfig().entities().entityUpdateInterval();

    @ModifyExpressionValue(method = "sendChanges", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerEntity;updateInterval:I"))
    private int modifyUpdateInterval(int interval) {
        return (int)(long)entityUpdateInterval.get();
    }
}
