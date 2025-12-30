package de.jf.advancedanimalslite.render;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/**
 * Render State for the Raccoon.
 * Transfers entity data to the model for rendering.
 */
public class RaccoonEntityRenderState extends LivingEntityRenderState {
    
    /**
     * Whether the raccoon is sitting.
     */
    public boolean isSitting = false;
    
    /**
     * Whether the raccoon is a baby.
     */
    public boolean isBaby = false;
}
