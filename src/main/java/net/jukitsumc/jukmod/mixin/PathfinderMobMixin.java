package net.jukitsumc.jukmod.mixin;

import net.jukitsumc.jukmod.entity.AvoidSwollenCreeperGoal;
import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathfinderMob.class)
public class PathfinderMobMixin extends Mob {

    protected PathfinderMobMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super((EntityType<? extends Mob>)entityType, level);
    }

    @Inject(method="<init>", at=@At("TAIL"))
    public void fleeSwellingCreeper(EntityType<? extends Mob> entityType, Level level, CallbackInfo info) {
        if (level != null && !level.isClientSide) {
            this.goalSelector.addGoal(1, new AvoidSwollenCreeperGoal((PathfinderMob)(Object)this, 4.0F, 1.0D, 1.2D));
        }
    }

}
