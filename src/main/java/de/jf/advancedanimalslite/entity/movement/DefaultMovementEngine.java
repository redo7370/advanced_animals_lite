package de.jf.advancedanimalslite.entity.movement;

import de.jf.advancedanimalslite.entity.RaccoonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * Default implementation of the movement engine.
 * Cat-like behavior with optional randomness.
 */
public class DefaultMovementEngine implements MovementEngine {
    
    private double speed = 1.0;
    private double randomnessFactor = 0.0;
    private final Random random = new Random();
    
    private int pathCooldown = 0;
    private static final int PATH_RECALCULATE_COOLDOWN = 10;
    
    @Override
    public void tick(RaccoonEntity entity) {
        if (pathCooldown > 0) {
            pathCooldown--;
        }
        
        // Add random movement changes
        if (randomnessFactor > 0 && random.nextDouble() < randomnessFactor * 0.01) {
            addRandomMovement(entity);
        }
    }
    
    @Override
    public void calculatePath(RaccoonEntity entity, Entity target) {
        if (pathCooldown > 0) return;
        
        EntityNavigation navigation = entity.getNavigation();
        
        // Calculate offset for more natural behavior
        double offsetX = (random.nextDouble() - 0.5) * randomnessFactor;
        double offsetZ = (random.nextDouble() - 0.5) * randomnessFactor;
        
        navigation.startMovingTo(
            target.getX() + offsetX,
            target.getY(),
            target.getZ() + offsetZ,
            speed
        );
        
        pathCooldown = PATH_RECALCULATE_COOLDOWN;
    }
    
    @Override
    public void calculatePath(RaccoonEntity entity, Vec3d targetPos) {
        if (pathCooldown > 0) return;
        
        EntityNavigation navigation = entity.getNavigation();
        
        double offsetX = (random.nextDouble() - 0.5) * randomnessFactor;
        double offsetZ = (random.nextDouble() - 0.5) * randomnessFactor;
        
        navigation.startMovingTo(
            targetPos.x + offsetX,
            targetPos.y,
            targetPos.z + offsetZ,
            speed
        );
        
        pathCooldown = PATH_RECALCULATE_COOLDOWN;
    }
    
    @Override
    public boolean shouldMove(RaccoonEntity entity) {
        // Don't move when sitting
        if (entity.isSitting()) {
            return false;
        }
        
        // Random pauses for more natural behavior
        if (randomnessFactor > 0 && random.nextDouble() < randomnessFactor * 0.005) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void stop(RaccoonEntity entity) {
        entity.getNavigation().stop();
    }
    
    @Override
    public double getSpeed() {
        return speed;
    }
    
    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    @Override
    public void addRandomness(double factor) {
        this.randomnessFactor = Math.max(0, Math.min(1, factor));
    }
    
    /**
     * Adds random small movements (like cats).
     */
    private void addRandomMovement(RaccoonEntity entity) {
        if (entity.isSitting() || entity.getNavigation().isFollowingPath()) {
            return;
        }
        
        double currentX = entity.getX();
        double currentY = entity.getY();
        double currentZ = entity.getZ();
        double randomX = currentX + (random.nextDouble() - 0.5) * 4;
        double randomZ = currentZ + (random.nextDouble() - 0.5) * 4;
        
        entity.getNavigation().startMovingTo(randomX, currentY, randomZ, speed * 0.5);
    }
}
