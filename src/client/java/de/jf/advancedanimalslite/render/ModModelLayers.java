package de.jf.advancedanimalslite.render;

import de.jf.advancedanimalslite.Advanced_animals_lite;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/**
 * Registration of model layers for client rendering.
 */
public class ModModelLayers {
    
    public static final EntityModelLayer RACCOON = new EntityModelLayer(
        Identifier.of(Advanced_animals_lite.MOD_ID, "raccoon"),
        "main"
    );
}
