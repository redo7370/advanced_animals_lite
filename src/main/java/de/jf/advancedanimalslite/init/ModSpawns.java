package de.jf.advancedanimalslite.init;

import de.jf.advancedanimalslite.Advanced_animals_lite;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.Heightmap;

/**
 * Configuration for entity spawning.
 */
public class ModSpawns {
    
    /**
     * Registers all spawn rules.
     */
    public static void registerSpawns() {
        Advanced_animals_lite.LOGGER.info("Registering spawns for " + Advanced_animals_lite.MOD_ID);
        
        // Raccoon spawn restrictions
        SpawnRestriction.register(
            ModEntities.RACCOON,
            SpawnLocationTypes.ON_GROUND,
            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
            AnimalEntity::isValidNaturalSpawn
        );
        
        // Raccoon spawns in all biomes (except Nether/End)
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInOverworld(),
            SpawnGroup.CREATURE,
            ModEntities.RACCOON,
            10,  // weight
            1,   // minGroupSize
            3    // maxGroupSize
        );
    }
}
