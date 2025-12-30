package de.jf.advancedanimalslite.entity.ai;

import de.jf.advancedanimalslite.entity.RaccoonEntity;
import de.jf.advancedanimalslite.entity.movement.MovementEngine;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * Wander Goal for the Raccoon.
 * Uses the modular movement engine for cat-like wandering.
 */
public class RaccoonWanderGoal extends Goal {
    
    private final RaccoonEntity raccoon;
    private final double speed;
    private int cooldown;
    
    public RaccoonWanderGoal(RaccoonEntity raccoon, double speed) {
        this.raccoon = raccoon;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }
    
    @Override
    public boolean canStart() {
        if (this.raccoon.isSitting()) {
            return false;
        }
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        
        // Random starting (like cats - not always active)
        return this.raccoon.getRandom().nextInt(120) == 0;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.raccoon.getNavigation().isIdle() && !this.raccoon.isSitting();
    }
    
    @Override
    public void start() {
        Vec3d target = this.findWanderTarget();
        if (target != null) {
            MovementEngine engine = this.raccoon.getMovementEngine();
            if (engine != null && engine.shouldMove(this.raccoon)) {
                engine.calculatePath(this.raccoon, target);
            } else {
                this.raccoon.getNavigation().startMovingTo(target.x, target.y, target.z, this.speed);
            }
        }
        this.cooldown = 200 + this.raccoon.getRandom().nextInt(200);
    }
    
    @Override
    public void stop() {
        this.raccoon.getNavigation().stop();
    }
    
    private Vec3d findWanderTarget() {
        double x = this.raccoon.getX();
        double y = this.raccoon.getY();
        double z = this.raccoon.getZ();
        
        // Find random point nearby
        double targetX = x + (this.raccoon.getRandom().nextDouble() - 0.5) * 10;
        double targetZ = z + (this.raccoon.getRandom().nextDouble() - 0.5) * 10;
        
        return new Vec3d(targetX, y, targetZ);
    }
}
