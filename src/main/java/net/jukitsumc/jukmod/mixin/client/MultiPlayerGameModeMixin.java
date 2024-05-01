package net.jukitsumc.jukmod.mixin.client;

import net.jukitsumc.jukmod.Jukmod;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow
    private GameType localPlayerMode;
    /**
     * @author Jukitsu
     * @reason For PVP
     */
    @Overwrite
    public boolean hasMissTime() {
        return Jukmod.getInstance().getConfig().gameplay().missTime().get() && !this.localPlayerMode.isCreative();
    }
}
