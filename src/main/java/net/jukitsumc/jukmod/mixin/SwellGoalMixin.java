package net.jukitsumc.jukmod.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwellGoal.class)
public class SwellGoalMixin {
    @Shadow @Final private Creeper creeper;
    @Shadow @Nullable
    private LivingEntity target;

    @Inject(method = "tick", at = @At("TAIL"))
    public void lookAtMeWhenExploding(CallbackInfo info) {
        this.creeper.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }
}
