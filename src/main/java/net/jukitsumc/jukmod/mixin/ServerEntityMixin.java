package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {


    @ModifyExpressionValue(method = "sendChanges", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerEntity;tickCount:I", ordinal = 2))
    private int hijackTickCountSoServerSendEntityUpdateEveryTick(int fuckThisShitImOut) {
        return 60;
    }

    @ModifyExpressionValue(method = "sendChanges", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerEntity;updateInterval:I"))
    private int modifyUpdateInterval(int interval) {
        return 1;
    }
}
