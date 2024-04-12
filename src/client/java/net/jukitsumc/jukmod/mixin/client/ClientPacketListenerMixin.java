package net.jukitsumc.jukmod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;



@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Redirect(method="handleMoveEntity", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;lerpTo(DDDFFI)V"))
    private void doNotSmoothEntityMovement(Entity entity, double x, double y, double z, float g, float h, int steps) {

        entity.lerpTo(x, y, z, g, h, 1);
    }
}
