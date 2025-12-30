package de.jf.advancedanimalslite.entity.ai;

import de.jf.advancedanimalslite.entity.RaccoonEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Goal for the Raccoon sitting.
 * Based on the vanilla SitGoal for TameableEntity.
 * When the Raccoon sits, it doesn't move and doesn't follow the owner.
 */
public class RaccoonSitGoal extends Goal {
    
    private final RaccoonEntity raccoon;
    
    public RaccoonSitGoal(RaccoonEntity raccoon) {
        this.raccoon = raccoon;
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }
    
    @Override
    public boolean canStart() {
        // Only tamed raccoons can be commanded to sit
        if (!this.raccoon.isTamed()) {
            return false;
        }
        return this.raccoon.isSitting();
    }
    
    @Override
    public boolean shouldContinue() {
        return this.raccoon.isSitting() && !this.raccoon.isTouchingWater();
    }
    
    @Override
    public void start() {
        this.raccoon.getNavigation().stop();
    }
    
    @Override
    public void stop() {
        // Do nothing - the sitting state is managed by interactMob
    }
}
