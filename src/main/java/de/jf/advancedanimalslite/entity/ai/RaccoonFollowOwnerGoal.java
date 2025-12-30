package de.jf.advancedanimalslite.entity.ai;

import de.jf.advancedanimalslite.entity.RaccoonEntity;
import de.jf.advancedanimalslite.entity.movement.MovementEngine;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Goal for following the owner.
 * Uses the modular movement engine.
 */
public class RaccoonFollowOwnerGoal extends Goal {
    
    private final RaccoonEntity raccoon;
    private LivingEntity owner;
    private World world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    
    public RaccoonFollowOwnerGoal(RaccoonEntity raccoon, double speed, float maxDistance, float minDistance) {
        this.raccoon = raccoon;
        this.speed = speed;
        this.navigation = raccoon.getNavigation();
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    
    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.raccoon.getOwner();
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity.isSpectator()) {
            return false;
        }
        if (this.raccoon.isSitting()) {
            return false;
        }
        if (this.raccoon.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
            return false;
        }
        
        this.owner = livingEntity;
        this.world = this.raccoon.getEntityWorld();
        return true;
    }
    
    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        }
        if (this.raccoon.isSitting()) {
            return false;
        }
        return this.raccoon.squaredDistanceTo(this.owner) > (double)(this.minDistance * this.minDistance);
    }
    
    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.raccoon.getPathfindingPenalty(PathNodeType.WATER);
        this.raccoon.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }
    
    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.raccoon.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }
    
    @Override
    public void tick() {
        this.raccoon.getLookControl().lookAt(this.owner, 10.0F, (float)this.raccoon.getMaxLookPitchChange());
        
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            
            if (this.raccoon.squaredDistanceTo(this.owner) >= (double)(this.maxDistance * this.maxDistance)) {
                // Teleport when too far away
                this.tryTeleport();
            } else {
                // Use movement engine
                MovementEngine engine = this.raccoon.getMovementEngine();
                if (engine != null && engine.shouldMove(this.raccoon)) {
                    engine.calculatePath(this.raccoon, this.owner);
                } else {
                    this.navigation.startMovingTo(this.owner, this.speed);
                }
            }
        }
    }
    
    private void tryTeleport() {
        BlockPos ownerPos = this.owner.getBlockPos();
        
        for (int i = 0; i < 10; ++i) {
            int x = this.getRandomInt(-3, 3);
            int y = this.getRandomInt(-1, 1);
            int z = this.getRandomInt(-3, 3);
            
            if (this.tryTeleportTo(ownerPos.getX() + x, ownerPos.getY() + y, ownerPos.getZ() + z)) {
                return;
            }
        }
    }
    
    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0 && Math.abs((double)z - this.owner.getZ()) < 2.0) {
            return false;
        }
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        }
        
        this.raccoon.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, 
            this.raccoon.getYaw(), this.raccoon.getPitch());
        this.navigation.stop();
        return true;
    }
    
    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(
            this.raccoon, pos);
        
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        }
        
        BlockPos blockPos = pos.subtract(this.raccoon.getBlockPos());
        return this.world.isSpaceEmpty(this.raccoon, 
            this.raccoon.getBoundingBox().offset(blockPos));
    }
    
    private int getRandomInt(int min, int max) {
        return this.raccoon.getRandom().nextInt(max - min + 1) + min;
    }
}
