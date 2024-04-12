package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow @Final
    protected ServerLevel level;


    @ModifyExpressionValue(method = "sendChanges", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerEntity;tickCount:I", ordinal = 2))
    private int hijackTickCountSoServerSendEntityUpdateEveryTick(int fuckThisShitImOut) {
        return level.getServer().isDedicatedServer() ? fuckThisShitImOut : 60;
    }

    @ModifyExpressionValue(method = "sendChanges", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerEntity;updateInterval:I"))
    private int modifyUpdateInterval(int interval) {
        return level.getServer().isDedicatedServer() ? interval : 1;
    }
}
