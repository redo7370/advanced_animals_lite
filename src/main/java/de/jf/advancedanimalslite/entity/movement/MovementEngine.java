package de.jf.advancedanimalslite.entity.movement;

import de.jf.advancedanimalslite.entity.RaccoonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Interface for the Raccoon's movement engine.
 * Allows swapping of movement logic.
 */
public interface MovementEngine {
    
    /**
     * Called every tick to update movement.
     */
    void tick(RaccoonEntity entity);
    
    /**
     * Calculates the path to a target.
     */
    void calculatePath(RaccoonEntity entity, Entity target);
    
    /**
     * Calculates the path to a position.
     */
    void calculatePath(RaccoonEntity entity, Vec3d targetPos);
    
    /**
     * Checks if the entity should move.
     */
    boolean shouldMove(RaccoonEntity entity);
    
    /**
     * Stops the current movement.
     */
    void stop(RaccoonEntity entity);
    
    /**
     * Returns the desired movement speed.
     */
    double getSpeed();
    
    /**
     * Sets the movement speed.
     */
    void setSpeed(double speed);
    
    /**
     * Adds randomness to the movement.
     */
    void addRandomness(double factor);
}
