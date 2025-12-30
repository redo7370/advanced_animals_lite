package de.jf.advancedanimalslite;

import de.jf.advancedanimalslite.init.ModEntities;
import de.jf.advancedanimalslite.model.RaccoonEntityModel;
import de.jf.advancedanimalslite.render.ModModelLayers;
import de.jf.advancedanimalslite.render.RaccoonEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class Advanced_animals_liteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		
		// Register model layer
		EntityModelLayerRegistry.registerModelLayer(
			ModModelLayers.RACCOON, 
			RaccoonEntityModel::getTexturedModelData
		);
		
		// Register entity renderer
		EntityRendererRegistry.register(ModEntities.RACCOON, RaccoonEntityRenderer::new);
		
		Advanced_animals_lite.LOGGER.info("Advanced Animals Lite Client initialized!");
	}
}