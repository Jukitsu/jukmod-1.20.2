package net.jukitsumc.jukmod.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MoveControl.class)
public class MoveControlMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    @Final
    protected Mob mob;

    @Shadow
    protected MoveControl.Operation operation;


}
