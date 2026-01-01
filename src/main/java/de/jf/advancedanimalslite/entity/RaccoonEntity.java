package de.jf.advancedanimalslite.entity;

import de.jf.advancedanimalslite.entity.ai.RaccoonFollowOwnerGoal;
import de.jf.advancedanimalslite.entity.ai.RaccoonSitGoal;
import de.jf.advancedanimalslite.entity.ai.RaccoonWanderGoal;
import de.jf.advancedanimalslite.entity.movement.DefaultMovementEngine;
import de.jf.advancedanimalslite.entity.movement.MovementEngine;
import de.jf.advancedanimalslite.init.ModEntities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Raccoon Entity - Tameable with edible items, can sit and breed.
 * Once tamed, the raccoon permanently belongs to its owner (like Wolf).
 * Feeding heals the raccoon, Love Mode only at full health.
 */
public class RaccoonEntity extends TameableEntity {
    
    private MovementEngine movementEngine;
    
    public RaccoonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.movementEngine = new DefaultMovementEngine();
        this.movementEngine.addRandomness(0.3); // 30% randomness
    }
    
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }
    
    @Override
    protected void initGoals() {
        // Priorities: Lower number = higher priority
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new RaccoonSitGoal(this));
        this.goalSelector.add(2, new RaccoonFollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.add(3, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(4, new TemptGoal(this, 0.6, (stack) -> isBreedingItem(stack), false));
        this.goalSelector.add(5, new FollowParentGoal(this, 0.8));
        this.goalSelector.add(6, new RaccoonWanderGoal(this, 0.6));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }
    
    public static DefaultAttributeContainer.Builder createRaccoonAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 10.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
            .add(EntityAttributes.ATTACK_DAMAGE, 2.0)
            .add(EntityAttributes.TEMPT_RANGE, 10.0);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Tick movement engine
        if (movementEngine != null) {
            movementEngine.tick(this);
        }
    }
    
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        World world = this.getEntityWorld();
        
        // Only process MAIN_HAND to avoid double toggle!
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        
        boolean tamed = this.isTamed();
        boolean isOwner = tamed && this.isOwner(player);
        
        // Server-side logic
        if (!world.isClient()) {
            
            if (!tamed) {
                // === NOT TAMED: Taming attempt ===
                if (isFood(itemStack)) {
                    // Consume item
                    this.eat(player, hand, itemStack);
                    
                    // Taming attempt with 33% chance (like Wolf with bones)
                    if (this.random.nextInt(3) == 0) {
                        // Successfully tamed - IMPORTANT: call setTamed BEFORE setOwner!
                        this.setTamed(true, true);
                        this.setOwner(player);
                        this.setSitting(true); // Raccoon sits down after successful taming
                        this.setInSittingPose(true);
                        this.getNavigation().stop();
                        world.sendEntityStatus(this, (byte) 7); // Heart particles
                    } else {
                        // Failed
                        world.sendEntityStatus(this, (byte) 6); // Smoke particles
                    }
                    return ActionResult.SUCCESS;
                }
            } else {
                // === TAMED ===
                // Only the owner can interact
                if (isOwner) {
                    
                    // Interact with breeding item (berries/meat)
                    if (this.isBreedingItem(itemStack)) {
                        // If injured -> heal
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.eat(player, hand, itemStack);
                            this.heal(4.0F);
                            return ActionResult.SUCCESS;
                        }
                        
                        // If full health and can breed -> Love Mode
                        if (this.canBreed()) {
                            this.eat(player, hand, itemStack);
                            this.lovePlayer(player);
                            return ActionResult.SUCCESS;
                        }
                        // Already in Love Mode or cannot breed
                        return ActionResult.PASS;
                    }
                    
                    // Heal with other food (everything that isFood())
                    if (this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
                        this.eat(player, hand, itemStack);
                        this.heal(2.0F); // Less healing than breeding items
                        return ActionResult.SUCCESS;
                    }
                    
                    // With empty hand: Toggle sitting
                    if (itemStack.isEmpty()) {
                        boolean newSitting = !this.isSitting();
                        this.setSitting(newSitting);
                        this.setInSittingPose(newSitting);
                        if (newSitting) {
                            this.getNavigation().stop();
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        
        // Client-side: If tamed and owner with breeding item, return SUCCESS
        // so the arm animation is played
        if (world.isClient() && tamed && isOwner && this.isBreedingItem(itemStack)) {
            return ActionResult.SUCCESS;
        }
        
        return super.interactMob(player, hand);
    }
    
    /**
     * Checks if an item is edible (for taming).
     */
    public boolean isFood(ItemStack stack) {
        return stack.contains(DataComponentTypes.FOOD);
    }
    
    /**
     * Checks if an item can be used for breeding.
     * Raccoons like berries and meat.
     */
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.SWEET_BERRIES) 
            || stack.isOf(Items.GLOW_BERRIES)
            || stack.isOf(Items.CHICKEN)
            || stack.isOf(Items.COOKED_CHICKEN)
            || stack.isOf(Items.RABBIT)
            || stack.isOf(Items.COOKED_RABBIT);
    }
    
    /**
     * Checks if the raccoon can breed.
     * Only adult animals that are not in love cooldown or love mode.
     */
    public boolean canBreed() {
        return !this.isBaby() && this.getBreedingAge() == 0 && !this.isInLove() && !this.isSitting();
    }
    
    /**
     * Checks if the raccoon can breed with another animal.
     * Both must be in Love Mode and of the same type.
     */
    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        }
        if (!(other instanceof RaccoonEntity)) {
            return false;
        }
        RaccoonEntity otherRaccoon = (RaccoonEntity) other;
        return this.isInLove() && otherRaccoon.isInLove();
    }
    
    // Sitting State - nutzt TameableEntity's built-in sitting
    public boolean isSitting() {
        return this.isInSittingPose();
    }
    
    public void setSitting(boolean sitting) {
        this.setInSittingPose(sitting);
    }
    
    // Bewegungs-Engine Getter/Setter
    public MovementEngine getMovementEngine() {
        return movementEngine;
    }
    
    public void setMovementEngine(MovementEngine engine) {
        this.movementEngine = engine;
    }
    
    // Sounds
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_FOX_AMBIENT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_FOX_HURT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_FOX_DEATH;
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        RaccoonEntity child = ModEntities.RACCOON.create(world, null);
        if (child != null) {
            // Child inherits owner from parents
            if (this.isTamed()) {
                child.setTamed(true, true);
                child.setOwner(this.getOwner());
            }
        }
        return child;
    }
    
    // ==================== NBT Data Synchronization ====================
    
    /**
     * List of deprecated/removed attribute names.
     * These attributes are ignored on load and not saved again.
     * Add attribute names here that existed in older versions but were removed.
     */
    private static final java.util.Set<String> DEPRECATED_ATTRIBUTES = java.util.Set.of(
        // Example: "OldAttributeName", "AnotherRemovedAttribute"
    );
    
    /**
     * Flag indicating if the entity has been initially loaded.
     * Used to run the tamed/owner consistency check only once on first load.
     */
    private boolean hasBeenInitiallyLoaded = false;
    
    // ==================== Custom Persistent Attributes ====================
    // Add custom attributes here that should be saved.
    // For each new attribute:
    // 1. Declare the variable with a default value
    // 2. Add it to writeCustomData()
    // 3. Add it to readCustomData() with default fallback
    
    // NBT Key für den Sitz-Status
    private static final String NBT_SITTING = "RaccoonSitting";
    
    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        
        // Save sitting state - uses isInSittingPose() from TameableEntity
        view.putBoolean(NBT_SITTING, this.isInSittingPose());
        
        // IMPORTANT: Deprecated attributes are NOT written anymore,
        // since they are listed in DEPRECATED_ATTRIBUTES and are simply
        // omitted on the next save.
    }
    
    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        
        // Load sitting state with default fallback (false if not present)
        boolean wasSitting = view.getBoolean(NBT_SITTING, false);
        this.setSitting(wasSitting);
        this.setInSittingPose(wasSitting);
        
        // Deprecated attributes are automatically ignored:
        // - They are not read here
        // - They are not written in writeCustomData()
        // - On the next save they disappear from the data
        
        // Consistency check: Only run on initial entity load
        // This ensures entities without an owner are not marked as tamed
        if (!this.hasBeenInitiallyLoaded) {
            this.hasBeenInitiallyLoaded = true;
            validateTamedOwnerConsistency();
        }
    }
    
    /**
     * Validates consistency between tamed status and owner.
     * An entity should only be considered tamed if it has an owner.
     * This method is ONLY called once on initial entity load,
     * NOT during the taming process to avoid race conditions.
     */
    private void validateTamedOwnerConsistency() {
        if (this.isTamed() && this.getOwnerReference() == null) {
            // Entity is marked as tamed but has no owner
            // This can happen if data is corrupt or due to a bug
            this.setTamed(false, false);
            this.setSitting(false);
            this.setInSittingPose(false);
            
            // Log diese Korrektur für Debugging
            if (this.getEntityWorld() != null && !this.getEntityWorld().isClient()) {
                de.jf.advancedanimalslite.Advanced_animals_lite.LOGGER.warn(
                    "Raccoon at {} had tamed=true but no owner. Resetting tamed status.",
                    this.getBlockPos()
                );
            }
        }
    }
}
