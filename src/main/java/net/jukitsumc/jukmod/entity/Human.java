package net.jukitsumc.jukmod.entity;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;


public class Human extends PathfinderMob implements NeutralMob, Npc, InventoryCarrier, RangedAttackMob {
    private static final int ALERT_RANGE_Y = 64;
    private static final UniformInt ALERT_INTERVAL;
    private int ticksUntilNextAlert;
    private static final UniformInt PERSISTENT_ANGER_TIME;
    private final SimpleContainer inventory = new SimpleContainer(36);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    private int lastTimeSinceJumped = 0;
    private boolean canFightCreepers = false;
    private int timesBeforeNextHeal = 0;
    private boolean hasRangedWeapon = false;
    private static final double arrowVelocity = 3.0D;

    private final Goal meleeGoal = new HumanMeleeAttackGoal(this, 1.0D, false);
    private final Goal aggroEndermanGoal = new NearestAttackableTargetGoal(this, EnderMan.class, true, false);
    private final Goal fleeCreeperGoal = new AvoidEntityGoal(this, Creeper.class, 6.0F, 1.0D, 1.0D);
    private final Goal aggroCreeperGoal = new NearestAttackableTargetGoal(this, Creeper.class, true, true);
    private final Goal bowGoal = new RangedHumanBowAttackGoal(this, 1.0D, 20, 15.0F);
    private final Goal fleeEndermanGoal = new AvoidEntityGoal(this, EnderMan.class, 6.0F, 1.0D, 1.0D);


    public Human(EntityType<Human> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);

        if ((double)this.random.nextFloat() > 0.75D) {
            this.canFightCreepers = true;
            if ((double)this.random.nextFloat() > 0.5D) {
                this.hasRangedWeapon = true;
                this.canFightCreepers = true;
            }
        }



    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, WitherBoss.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Warden.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Vex.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Zoglin.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, PiglinBrute.class, 6.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(Human.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, EnderDragon.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Blaze.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Raider.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, ElderGuardian.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Silverfish.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Endermite.class, true, false));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));


    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        RandomSource randomSource = serverLevelAccessor.getRandom();

        if (this.canFightCreepers) {
            if (this.hasRangedWeapon) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            }

        }
        this.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        this.populateDefaultEquipmentEnchantments(randomSource, difficultyInstance);
        this.reassessWeaponGoal();
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.reassessWeaponGoal();
    }

    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        super.setItemSlot(equipmentSlot, itemStack);
        if (!this.level().isClientSide) {
            this.reassessWeaponGoal();
        }

    }

    public void reassessWeaponGoal() {
        if (this.level() != null && !this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            this.goalSelector.removeGoal(this.aggroEndermanGoal);
            this.goalSelector.removeGoal(this.fleeEndermanGoal);
            this.goalSelector.removeGoal(this.fleeCreeperGoal);
            this.goalSelector.removeGoal(this.aggroCreeperGoal);

            this.hasRangedWeapon = false;
            this.canFightCreepers = false;

            ItemStack itemStack = this.getMainHandItem();
            if (itemStack.is(Items.BOW)) {
                this.hasRangedWeapon = true;
                this.canFightCreepers = true;
            } else if (itemStack.getDamageValue() > 3) {
                this.canFightCreepers = true;
            }
            if (!hasRangedWeapon) {
                this.goalSelector.addGoal(2, meleeGoal);
                this.targetSelector.addGoal(3, aggroEndermanGoal);
            }
            else {
                this.goalSelector.addGoal(2, bowGoal);
                this.goalSelector.addGoal(3, fleeEndermanGoal);
            }

            if (!canFightCreepers) {
                this.goalSelector.addGoal(3, fleeCreeperGoal);
            }
            else {
                this.targetSelector.addGoal(10, aggroCreeperGoal);
            }

        }

    }




    public void performRangedAttack(LivingEntity livingEntity, float f) {
        ItemStack itemStack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
        AbstractArrow abstractArrow = ProjectileUtil.getMobArrow(this, itemStack, f);
        double x = livingEntity.getX() - this.getX();
        double y = livingEntity.getY(0.33333333333333333D) - abstractArrow.getY();
        double z = livingEntity.getZ() - this.getZ();
        double d = Math.sqrt(x * x + z * z);

        Vec3 du = livingEntity.getDeltaMovement().scale(d / arrowVelocity);
        abstractArrow.shoot(x + du.x, y + d * 0.1D, z + du.z, (float)arrowVelocity, (float)(12 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractArrow);
    }

    public static AttributeSupplier.Builder createHumanAttributes() {
        return PathfinderMob.createMobAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.ATTACK_KNOCKBACK, 0.0D).add(Attributes.FOLLOW_RANGE, 128);
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
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
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

    protected void customServerAiStep() {
        this.updatePersistentAnger((ServerLevel)this.level(), true);
        if (this.getTarget() != null) {
            this.maybeAlertOthers();
        }

        if (this.isAngry()) {
            this.lastHurtByPlayerTime = this.tickCount;
        }

        super.customServerAiStep();
    }


    private void maybeAlertOthers() {
        if (this.ticksUntilNextAlert > 0) {
            --this.ticksUntilNextAlert;
        } else {
            if (this.getSensing().hasLineOfSight(this.getTarget())) {
                this.alertOthers();
            }

            this.ticksUntilNextAlert = ALERT_INTERVAL.sample(this.random);
        }
    }

    private void alertOthers() {
        double d = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aABB = AABB.unitCubeFromLowerCorner(this.position()).inflate(d, 10.0D, d);
        this.level().getEntitiesOfClass(Human.class, aABB, EntitySelector.NO_SPECTATORS).stream().filter((human) -> {
            return human != this;
        }).filter((human) -> {
            return human.getTarget() == null;
        }).filter((human) -> {
            return !human.isAlliedTo(this.getTarget());
        }).forEach((human) -> {
            human.setTarget(this.getTarget());
        });
    }


    public void setTarget(@Nullable LivingEntity livingEntity) {
        if (livingEntity instanceof Human) {
            livingEntity = null;
        }
        if (this.getTarget() == null && livingEntity != null) {
            this.ticksUntilNextAlert = ALERT_INTERVAL.sample(this.random);
        }

        if (livingEntity instanceof Player) {
            this.setLastHurtByPlayer((Player)livingEntity);
        }

        super.setTarget(livingEntity);
    }


    public void crit(Entity entity) {

    }

    public void magicCrit(Entity entity) {

    }

    public boolean removeWhenFarAway(double d) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float knockback = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        float damageBonus = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entity).getMobType());
        boolean falling = this.fallDistance > 0.0F && !this.onGround() && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && entity instanceof LivingEntity;

        if (falling) {
            damage *= 1.5f;
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
            this.crit(entity);
        }

        if (entity instanceof LivingEntity) {
            damage += damageBonus;
            knockback += (float)EnchantmentHelper.getKnockbackBonus(this);
        }

        if (this.isSprinting()) {
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
            knockback += 1.0f;
        }

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            entity.setSecondsOnFire(i * 4);
        }

        this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);

        if (damageBonus > 0.0F) {
            this.magicCrit(entity);
        }


        boolean bl = entity.hurt(this.damageSources().mobAttack(this), damage);
        if (bl) {


            if (knockback > 0.0F && entity instanceof LivingEntity) {
                ((LivingEntity)entity).knockback((double)(knockback * 0.5F), (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                this.setSprinting(false);
            }

            if (entity instanceof Player) {
                Player player = (Player) entity;
                this.maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            this.doEnchantDamageEffects(this, entity);
            this.setLastHurtMob(entity);
        }

        return bl;
    }

    public static boolean checkHumanSpawnRules(EntityType<? extends Human> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return levelAccessor.getBlockState(blockPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON);
    }

    private void maybeDisableShield(Player player, ItemStack itemStack, ItemStack itemStack2) {
        if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.getItem() instanceof AxeItem && itemStack2.is(Items.SHIELD)) {
            float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                player.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level().broadcastEntityEvent(player, (byte)30);
            }
        }

    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity livingEntity) {
        return this.distanceToSqr(livingEntity) <= 9.0D;
    }



    @Override
    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    public int getMaxHeadYRot() {
        return 50;
    }

    @Override
    public void tick() {
        super.tick();

        this.lastTimeSinceJumped++;
        this.timesBeforeNextHeal++;

        if (this.timesBeforeNextHeal >= 80) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.heal(Math.min(1.0f, this.getMaxHealth() - this.getHealth()));
            }
            this.timesBeforeNextHeal = 0;
        }

        double d = this.getX() - this.xo;
        double e = this.getZ() - this.zo;

        boolean isMoving = d * d + e * e > 2.500000277905201E-7D;
        this.setSprinting(isMoving);


        if (isMoving && this.onGround() && this.lastTimeSinceJumped > 16) {
            if ((isAggressive() && !this.hasRangedWeapon && this.getTarget() != null && distanceToSqr(this.getTarget()) > 144.0F)
                    || (!isAggressive() && (double)this.random.nextFloat() < 0.125D)) {
                this.jumpControl.jump();
                if (this.isSprinting()) {
                    float f = this.getYRot() * 0.017453292F;
                    this.setDeltaMovement(this.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0D, (double)(Mth.cos(f) * 0.2F)));
                }
                this.lastTimeSinceJumped = 0;
            }

        }
    }

    public class HumanMeleeAttackGoal extends Goal {
        protected final PathfinderMob mob;
        private final double speedModifier;
        private final boolean followingTargetEvenIfNotSeen;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        private int ticksUntilNextAttack;
        private final int attackInterval = 20;
        private long lastCanUseCheck;
        private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;

        public HumanMeleeAttackGoal(PathfinderMob pathfinderMob, double d, boolean bl) {
            this.mob = pathfinderMob;
            this.speedModifier = d;
            this.followingTargetEvenIfNotSeen = bl;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.getAttackReachSqr(livingEntity) >= this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (!this.mob.isWithinRestriction(livingEntity.blockPosition())) {
                return false;
            } else {
                return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player)livingEntity).isCreative();
            }
        }

        public void start() {
            this.mob.getNavigation().moveTo(this.path, this.speedModifier);
            this.mob.setAggressive(true);
            this.ticksUntilNextPathRecalculation = 0;
            this.ticksUntilNextAttack = 0;
        }

        public void stop() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                this.mob.setTarget((LivingEntity)null);
            }

            this.mob.setAggressive(false);
            this.mob.getNavigation().stop();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
                double d = this.mob.distanceToSqr(livingEntity);
                this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingEntity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                    this.pathedTargetX = livingEntity.getX();
                    this.pathedTargetY = livingEntity.getY();
                    this.pathedTargetZ = livingEntity.getZ();
                    this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                    if (d > 1024.0D) {
                        this.ticksUntilNextPathRecalculation += 10;
                    } else if (d > 256.0D) {
                        this.ticksUntilNextPathRecalculation += 5;
                    }

                    if (!this.mob.getNavigation().moveTo(livingEntity, this.speedModifier)) {
                        this.ticksUntilNextPathRecalculation += 15;
                    }

                    this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
                }

                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                this.checkAndPerformAttack(livingEntity, d);
            }
        }

        protected void checkAndPerformAttack(LivingEntity livingEntity, double d) {
            double e = this.getAttackReachSqr(livingEntity);
            if (d <= e && this.ticksUntilNextAttack <= 0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(livingEntity);
            }

        }

        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.adjustedTickDelay(10);
        }

        protected boolean isTimeToAttack() {
            return this.ticksUntilNextAttack <= 0;
        }

        protected int getTicksUntilNextAttack() {
            return this.ticksUntilNextAttack;
        }

        protected int getAttackInterval() {
            return this.adjustedTickDelay(10);
        }

        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return 9.0D;
        }
    }

    public class RangedHumanBowAttackGoal<T extends Human> extends Goal {
        private final T mob;
        private final double speedModifier;
        private int attackIntervalMin;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public RangedHumanBowAttackGoal(T monster, double d, int i, float f) {
            this.mob = monster;
            this.speedModifier = d;
            this.attackIntervalMin = i;
            this.attackRadiusSqr = f * f;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public void setMinAttackInterval(int i) {
            this.attackIntervalMin = i;
        }

        public boolean canUse() {
            return this.mob.getTarget() != null && this.isHoldingBow();
        }

        protected boolean isHoldingBow() {
            return this.mob.isHolding(Items.BOW);
        }

        public boolean canContinueToUse() {
            return this.mob.getTarget() != null && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingBow();
        }

        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        public void stop() {
            super.stop();

            LivingEntity livingEntity = this.mob.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                this.mob.setTarget((LivingEntity)null);
            }

            this.mob.setAggressive(false);
            this.seeTime = 0;
            this.attackTime = -1;
            this.mob.stopUsingItem();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                double d = this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                boolean bl = this.mob.getSensing().hasLineOfSight(livingEntity);
                boolean bl2 = this.seeTime > 0;
                if (bl != bl2) {
                    this.seeTime = 0;
                }

                if (bl) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                if (!(d > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else {
                    this.mob.getNavigation().moveTo(livingEntity, this.speedModifier);
                    this.strafingTime = -1;
                }

                if (this.strafingTime >= 20) {
                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }

                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }

                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (d > (double)(this.attackRadiusSqr * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d < (double)(this.attackRadiusSqr * 0.25F)) {
                        this.strafingBackwards = true;
                    }

                    this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                    Entity var7 = this.mob.getControlledVehicle();
                    if (var7 instanceof Mob) {
                        Mob mob = (Mob)var7;
                        mob.lookAt(livingEntity, 30.0F, 30.0F);
                    }

                    this.mob.lookAt(livingEntity, 30.0F, 30.0F);

                }

                this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);

                if (this.mob.isUsingItem()) {
                    if (!bl && this.seeTime < -60) {
                        this.mob.stopUsingItem();
                    } else if (bl) {
                        int i = this.mob.getTicksUsingItem();
                        if (i >= 20) {
                            this.mob.stopUsingItem();
                            ((RangedAttackMob)this.mob).performRangedAttack(livingEntity, BowItem.getPowerForTime(i));
                            this.attackTime = this.attackIntervalMin;
                        }
                    }
                } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                    this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.BOW));
                }

            }
        }
    }


    static {
        PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
        ALERT_INTERVAL = TimeUtil.rangeOfSeconds(1, 8);
    }

}
