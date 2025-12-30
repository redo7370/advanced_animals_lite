package de.jf.advancedanimalslite.render;

import de.jf.advancedanimalslite.Advanced_animals_lite;
import de.jf.advancedanimalslite.entity.RaccoonEntity;
import de.jf.advancedanimalslite.model.RaccoonEntityModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * Renderer for the Raccoon Entity.
 */
public class RaccoonEntityRenderer extends MobEntityRenderer<RaccoonEntity, RaccoonEntityRenderState, RaccoonEntityModel> {
    
    private static final Identifier TEXTURE = Identifier.of(
        Advanced_animals_lite.MOD_ID, 
        "textures/entity/raccoon.png"
    );
    
    public RaccoonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RaccoonEntityModel(context.getPart(ModModelLayers.RACCOON)), 0.4F);
    }
    
    @Override
    public RaccoonEntityRenderState createRenderState() {
        return new RaccoonEntityRenderState();
    }
    
    @Override
    public void updateRenderState(RaccoonEntity entity, RaccoonEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        // Pass entity-specific data to the RenderState
        // isInSittingPose() is the correct method from TameableEntity
        state.isSitting = entity.isInSittingPose();
        state.isBaby = entity.isBaby();
    }
    
    @Override
    public Identifier getTexture(RaccoonEntityRenderState state) {
        return TEXTURE;
    }
    
    @Override
    protected float getShadowRadius(RaccoonEntityRenderState state) {
        // Smaller shadow for babies
        return state.isBaby ? 0.2F : 0.4F;
    }
    
    @Override
    protected void scale(RaccoonEntityRenderState state, net.minecraft.client.util.math.MatrixStack matrices) {
        // Scale babies to 50% size
        if (state.isBaby) {
            matrices.scale(0.5F, 0.5F, 0.5F);
        }
    }
}
