package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {


    @ModifyExpressionValue(method="sendChanges",  at = @At(value="FIELD", target="Lnet/minecraft/server/level/ServerEntity;tickCount:I", ordinal = 2))
    private int hijackTickCountSoServerSendEntityUpdateEveryTick(int fuckThisShitImOut) {
        return 60;
    }

    @ModifyExpressionValue(method = "sendChanges", at = @At(value="FIELD", target="Lnet/minecraft/server/level/ServerEntity;updateInterval:I"))
    private int modifyUpdateInterval(int interval) {
        return 1;
    }
}
