package de.jf.advancedanimalslite.init;

import de.jf.advancedanimalslite.Advanced_animals_lite;
import de.jf.advancedanimalslite.entity.RaccoonEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * Registration of all entities in the mod.
 */
public class ModEntities {
    public static EntityType<RaccoonEntity> RACCOON;

    /**
     * Initializes all entities and their attributes.
     */
    public static void registerEntities() {
        Advanced_animals_lite.LOGGER.info("Registering entities for " + Advanced_animals_lite.MOD_ID);

        Advanced_animals_lite.LOGGER.info("Registering RACCOON entity...");
        RACCOON = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("advanced_animals_lite", "raccoon"),
            EntityType.Builder.create(RaccoonEntity::new, SpawnGroup.CREATURE)
                .dimensions(0.6F, 0.5F)
                .maxTrackingRange(8)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("advanced_animals_lite", "raccoon")))
        );
        if (RACCOON == null) {
            Advanced_animals_lite.LOGGER.error("Failed to register RACCOON entity!");
        } else {
            Advanced_animals_lite.LOGGER.info("RACCOON entity registered successfully.");
        }

        // Register attributes
        FabricDefaultAttributeRegistry.register(RACCOON, RaccoonEntity.createRaccoonAttributes());
    }

    public static void ensureEntitiesInitialized() {
        if (RACCOON == null) {
            registerEntities();
        }
    }
}
