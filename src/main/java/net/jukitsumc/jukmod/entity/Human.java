package net.jukitsumc.jukmod.entity;

import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class Human extends PathfinderMob implements NeutralMob, Npc, InventoryCarrier {

    private static final int PERSISTENT_ANGER_TIME = 256;
    private final SimpleContainer inventory = new SimpleContainer(36);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    private int lastTimeSinceJumped = 0;

    public Human(EntityType<Human> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createHumanAttributes() {
        return PathfinderMob.createMobAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.ATTACK_KNOCKBACK, 2.0D).add(Attributes.FOLLOW_RANGE, 128);
    }

    protected ItemStack addToInventory(ItemStack itemStack) {
        return this.inventory.addItem(itemStack);
    }

    protected boolean canAddToInventory(ItemStack itemStack) {
        return this.inventory.canAddItem(itemStack);
    }

    @VisibleForDebug
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public boolean canSprint() {
        return true;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME);
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setRemainingPersistentAngerTime(int i) {
        this.remainingPersistentAngerTime = i;
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID uUID) {
        this.persistentAngerTarget = uUID;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(5, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, Endermite.class, true, false));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal(this, false));
    }

    @Override
    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, (double)this.getJumpPower(), vec3.z);
        if (this.isSprinting()) {
            float f = this.getYRot() * 0.017453292F;
            this.setDeltaMovement(this.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0D, (double)(Mth.cos(f) * 0.2F)));
        }

        this.hasImpulse = true;
    }

    @Override
    public int getMaxHeadYRot() {
        return 50;
    }

    @Override
    public void tick() {
        super.tick();

        this.lastTimeSinceJumped++;

        double d = this.getX() - this.xo;
        double e = this.getZ() - this.zo;

        boolean isMoving = d * d + e * e > 2.500000277905201E-7D;
        this.setSprinting(isMoving);


        if (isMoving && this.lastTimeSinceJumped > 20 && Math.floorMod(this.random.nextInt(), 4) < 1 && !isAngry()) {
            this.jumpControl.jump();
            this.lastTimeSinceJumped = 0;
        }
    }

}
