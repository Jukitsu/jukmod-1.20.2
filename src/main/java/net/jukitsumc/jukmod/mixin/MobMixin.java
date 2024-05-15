package net.jukitsumc.jukmod.mixin;

import com.mojang.logging.LogUtils;
import net.jukitsumc.jukmod.Jukmod;
import net.jukitsumc.jukmod.config.option.BooleanOption;
import net.jukitsumc.jukmod.entity.Human;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.stream.StreamSupport;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow
    @Final
    protected GoalSelector targetSelector;
    @Shadow
    @Final
    protected GoalSelector goalSelector;
    @Shadow
    protected JumpControl jumpControl;
    @Unique
    private int autoJumpTime = 0;
    @Shadow
    @Final
    private BodyRotationControl bodyRotationControl;
    @Unique
    private BooleanOption oldBackwardsOption;

    protected MobMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author Jukitsu
     * @reason Attack Speed
     */
    @Overwrite
    public static AttributeSupplier.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_KNOCKBACK).add(Attributes.ATTACK_SPEED);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        oldBackwardsOption = Jukmod.getInstance().getConfig().animations().oldBackwards();
    }

    @Shadow
    public abstract int getMaxHeadYRot();

    @Shadow
    public abstract MoveControl getMoveControl();

    @Shadow
    public abstract JumpControl getJumpControl();

    @Shadow
    public abstract boolean isEffectiveAi();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void targetHumanIfHostile(EntityType<? extends Mob> entityType, Level level, CallbackInfo info) {
        if (level != null && !level.isClientSide && !entityType.getCategory().isFriendly() && !(this instanceof NeutralMob)) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal((Mob) (Object) this, Human.class, true));
        }
    }

    private boolean isMoving() {
        return this.xxa != 0.0F || this.zza != 0.0F;
    }

    private boolean canAutoJump() {
        return this.autoJumpTime <= 0
                && this.onGround()
                && !this.isPassenger()
                && this.isMoving()
                && (double) this.getBlockJumpFactor() >= 1.0D
                && this.isEffectiveAi();
    }

    protected void updateAutoJump(double f, double g) {
        if (!canAutoJump())
            return;
        Vec3 vec3 = this.position();
        Vec3 vec32 = vec3.add(f, 0.0D, g);
        Vec3 vec33 = new Vec3(f, 0.0D, g);
        float h = this.getSpeed();
        float i = (float) vec33.lengthSqr();
        float l;
        if (i <= 0.001F) {
            double j = h * this.xxa;
            double k = h * this.zza;
            l = Mth.sin(this.getYRot() * 0.017453292F);
            float m = Mth.cos(this.getYRot() * 0.017453292F);
            vec33 = new Vec3(j * m - k * l, vec33.y, k * m + j * l);
            i = (float) vec33.lengthSqr();
            if (i <= 0.001F) {
                return;
            }
        }

        float n = Mth.invSqrt(i);
        Vec3 vec34 = vec33.scale(n);
        Vec3 vec35 = this.getForward();
        l = (float) (vec35.x * vec34.x + vec35.z * vec34.z);
        if (!(l < -0.15F)) {
            CollisionContext collisionContext = CollisionContext.of(this);
            BlockPos blockPos = BlockPos.containing(this.getX(), this.getBoundingBox().maxY, this.getZ());
            BlockState blockState = this.level().getBlockState(blockPos);
            if (blockState.getCollisionShape(this.level(), blockPos, collisionContext).isEmpty()) {
                blockPos = blockPos.above();
                BlockState blockState2 = this.level().getBlockState(blockPos);
                if (blockState2.getCollisionShape(this.level(), blockPos, collisionContext).isEmpty()) {
                    float p = 1.2F;
                    if (this.hasEffect(MobEffects.JUMP)) {
                        p += (float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.75F;
                    }

                    float q = Math.max(h * 7.0F, 1.0F / n);
                    Vec3 vec37 = vec32.add(vec34.scale(q));
                    float r = this.getBbWidth();
                    float s = this.getBbHeight();
                    AABB aABB = (new AABB(vec3, vec37.add(0.0D, s, 0.0D))).inflate(r, 0.0D, r);
                    Vec3 vec36 = vec3.add(0.0D, 0.5, 0.0D);
                    vec37 = vec37.add(0.0D, 0.5, 0.0D);
                    Vec3 vec38 = vec34.cross(new Vec3(0.0D, 1.0D, 0.0D));
                    Vec3 vec39 = vec38.scale(r * 0.5F);
                    Vec3 vec310 = vec36.subtract(vec39);
                    Vec3 vec311 = vec37.subtract(vec39);
                    Vec3 vec312 = vec36.add(vec39);
                    Vec3 vec313 = vec37.add(vec39);
                    Iterable<VoxelShape> iterable = this.level().getCollisions(this, aABB);
                    Iterator<AABB> iterator = StreamSupport.stream(
                            iterable.spliterator(), false).flatMap((voxelShapex)
                            -> voxelShapex.toAabbs().stream()).iterator();
                    float t = 1.4E-45F;

                    label73:
                    while (iterator.hasNext()) {
                        AABB aABB2 = iterator.next();
                        if (aABB2.intersects(vec310, vec311) || aABB2.intersects(vec312, vec313)) {
                            t = (float) aABB2.maxY;
                            Vec3 vec314 = aABB2.getCenter();
                            BlockPos blockPos2 = BlockPos.containing(vec314);
                            int u = 1;

                            while (true) {
                                if (!((float) u < p)) {
                                    break label73;
                                }

                                BlockPos blockPos3 = blockPos2.above(u);
                                BlockState blockState3 = this.level().getBlockState(blockPos3);
                                VoxelShape voxelShape;
                                if (!(voxelShape = blockState3.getCollisionShape(this.level(), blockPos3, collisionContext)).isEmpty()) {
                                    t = (float) voxelShape.max(Direction.Axis.Y) + (float) blockPos3.getY();
                                    if ((double) t - this.getY() > (double) p) {
                                        return;
                                    }
                                }

                                if (u > 1) {
                                    blockPos = blockPos.above();
                                    BlockState blockState4 = this.level().getBlockState(blockPos);
                                    if (!blockState4.getCollisionShape(this.level(), blockPos, collisionContext).isEmpty()) {
                                        return;
                                    }
                                }

                                ++u;
                            }
                        }
                    }

                    if (t != 1.4E-45F) {
                        float v = (float) ((double) t - this.getY());
                        if (v > 0.5F && v <= p) {
                            LOGGER.info("Auto jump effective!");
                            this.autoJumpTime = 1;
                        }
                    }
                }
            }
        }

    }
/*
    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    private void checkAutoJump(CallbackInfo ci) {
        if (this.autoJumpTime > 0) {
            --this.autoJumpTime;
            this.getJumpControl().jump();
            this.getMoveControl().operation = MoveControl.Operation.JUMPING;
        }

    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        double d = this.getX();
        double e = this.getZ();
        super.move(moverType, vec3);
        this.updateAutoJump((float)(this.getX() - d), (float)(this.getZ() - e));
    }
*/

    @Inject(method = "tickHeadTurn", at = @At("TAIL"), cancellable = true)
    public void tickHeadTurn(float f, float g, CallbackInfoReturnable ci) {
        if (oldBackwardsOption.get()) {
            float h = Mth.wrapDegrees(f - this.yBodyRot);
            this.yBodyRot += h * 0.3F;
            float i = Mth.wrapDegrees(this.yHeadRot - this.yBodyRot);
            if (Math.abs(i) > this.getMaxHeadYRot()) {
                this.yBodyRot += i - (float) (Mth.sign(i) * this.getMaxHeadYRot());
            }

            boolean bl = i < -90.0F || i >= 90.0F;
            if (bl) {
                g *= -1.0F;
            }

            ci.setReturnValue(g);
        }

    }
}
