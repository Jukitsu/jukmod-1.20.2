package net.jukitsumc.jukmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoveControl.class)
public class MoveControlMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    @Final
    protected Mob mob;

    @Shadow
    public MoveControl.Operation operation;

    @ModifyExpressionValue(method="tick", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;onGround()Z"))
    public boolean cancelWaitingOperation(boolean bl) {
        return false;
    }

    @Inject(method="tick", at=@At("TAIL"))
    public void correctlySetOperation(CallbackInfo ci) {
        if (this.operation == MoveControl.Operation.JUMPING && this.mob.onGround()) {
            this.operation = MoveControl.Operation.MOVE_TO;
        }
    }



}
