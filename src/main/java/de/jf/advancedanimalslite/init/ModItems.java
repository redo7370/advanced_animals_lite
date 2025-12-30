package de.jf.advancedanimalslite.init;

import de.jf.advancedanimalslite.Advanced_animals_lite;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Registration of all items in the mod.
 */
public class ModItems {
    
    // Item reference (initialized in registerItems())
    public static Item RACCOON_SPAWN_EGG;
    
    /**
     * Initializes all items.
     * IMPORTANT: Must be called AFTER ModEntities.registerEntities()!
     */
    public static void registerItems() {
        Advanced_animals_lite.LOGGER.info("Registering items for " + Advanced_animals_lite.MOD_ID);
        
        // First ensure entities are registered
        ModEntities.ensureEntitiesInitialized();

        // Then register items
        RegistryKey<Item> spawnEggKey = RegistryKey.of(
            RegistryKeys.ITEM,
            Identifier.of("advanced_animals_lite", "raccoon_spawn_egg")
        );

        // SpawnEggItem mit korrekten Item.Settings erstellen (1.21.10 API)
        // In 1.21.10 verwendet man Item.Settings.spawnEgg(EntityType)
        RACCOON_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            spawnEggKey,
            new SpawnEggItem(new Item.Settings()
                .registryKey(spawnEggKey)
                .spawnEgg(ModEntities.RACCOON))
        );
        
        // Spawn Egg zur Creative-Tab hinzufügen
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(RACCOON_SPAWN_EGG);
        });
    }
}
