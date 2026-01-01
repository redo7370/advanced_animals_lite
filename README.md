# Advanced Animals Lite

A Minecraft Fabric Mod for version **1.21.10** that adds new animals with enhanced behavior.

## Project Overview

| Setting | Value |
|---------|-------|
| Minecraft Version | 1.21.10 |
| Mod Loader | Fabric |
| Mappings | Yarn |
| Kotlin | ❌ No |
| Data Generation | ❌ No (can be added later) |
| Split Client/Common | ✔ Yes |

## Project Structure

```
src/
├── main/java/de/jf/advancedanimalslite/     # Serverside + Common Code
│   ├── Advanced_animals_lite.java            # Mod Initializer
│   ├── entity/                               # Entity Classes
│   │   └── RaccoonEntity.java
│   ├── entity/ai/                            # AI Goals
│   │   ├── RaccoonFollowOwnerGoal.java
│   │   └── RaccoonSitGoal.java
│   ├── entity/movement/                      # Movement Engine (swappable)
│   │   ├── MovementEngine.java               # Interface
│   │   └── DefaultMovementEngine.java        # Default Implementation
│   ├── init/                                 # Registrations
│   │   └── ModEntities.java
│   └── mixin/
│
├── client/java/de/jf/advancedanimalslite/   # Clientside Code
│   ├── Advanced_animals_liteClient.java      # Client Initializer
│   ├── render/                               # Renderer
│   │   └── RaccoonEntityRenderer.java
│   └── model/                                # Entity Models
│       └── RaccoonEntityModel.java
│
└── main/resources/
    ├── fabric.mod.json
    └── assets/advanced_animals_lite/
        ├── textures/entity/
        │   └── raccoon.png
        └── lang/
            └── en_us.json
```

## Features

### Raccoon

- **Spawning**: Spawns in all biomes
- **Tameable**: Can be tamed with any edible item
- **Behavior**: 
  - Follows owner when tamed
  - Can be commanded to sit (right-click)
  - Cat-like behavior
- **Modular Movement Engine**: Movement logic is swappable/extendable

## Development

### Prerequisites

- Java 21+
- Gradle 8.x

### Build

```bash
./gradlew build
```

### Run Client (Development)

```bash
./gradlew runClient
```

### Run Server (Development)

```bash
./gradlew runServer
```

## Architecture

### Movement Engine

The movement engine is implemented as an interface (`MovementEngine`), allowing different movement logic to be easily swapped:

```java
public interface MovementEngine {
    void tick(RaccoonEntity entity);
    void calculatePath(RaccoonEntity entity, Entity target);
    boolean shouldMove(RaccoonEntity entity);
}
```

This enables:
- Easy swapping of movement logic
- Adding randomness/unpredictability
- Testing different behaviors

---

## Changelog

### Iteration 1 - Initial Implementation
*Date: December 28, 2025*

- [x] Entity class `RaccoonEntity` created
  - Extends `TameableEntity` for taming functionality
  - Tameable with all edible items (33% chance) via `DataComponentTypes.FOOD`
  - Sit function via right-click (when tamed)
  - Uses `setInSittingPose()` for sitting status (TameableEntity built-in)
  - Sounds: Fox sounds as placeholder
  
- [x] Movement Engine interface and default implementation
  - `MovementEngine` interface for swappable movement logic
  - `DefaultMovementEngine` with cat-like behavior
  - Configurable randomness factor (30% default)
  - Random pauses and movements for natural behavior

- [x] AI Goals for following, sitting and wandering
  - `RaccoonSitGoal` - Stops all movement when sitting
  - `RaccoonFollowOwnerGoal` - Follows owner, teleports when too far away
  - `RaccoonWanderGoal` - Random wandering with cooldown

- [x] Entity registration (`ModEntities.java`)
  - EntityType registered with RegistryKey (1.21 API)
  - Attributes via `FabricDefaultAttributeRegistry`

- [x] Client renderer and model (1.21 API Update)
  - `RaccoonEntityRenderer` - MobEntityRenderer with LivingEntityRenderState
  - `RaccoonEntityModel` - Extends EntityModel<LivingEntityRenderState>
  - Uses `ModelTransform.of()` instead of deprecated `pivot()`
  - Simple walk and tail animation

- [x] Spawning configuration (`ModSpawns.java`)
  - Spawns in all Overworld biomes
  - Spawn weight: 10, Groups: 1-3
  - SpawnRestriction: ON_GROUND

- [x] Language files
  - `en_us.json` - English
  - `de_de.json` - German

### API Adjustments for 1.21.10
- `getWorld()` → `getEntityWorld()` for entity access
- `world.isClient` → `getEntityWorld().isClient()` 
- `Item.getFoodComponent()` → `stack.contains(DataComponentTypes.FOOD)`
- `ModelTransform.pivot()` → `ModelTransform.of()`
- `EntityModel<Entity>` → `EntityModel<LivingEntityRenderState>`
- `MobEntityRenderer<T, M>` → `MobEntityRenderer<T, S, M>` with RenderState

### Iteration 2 - Items and Resources
*Date: December 28, 2025*

- [x] Spawn Egg Item (`ModItems.java`)
  - Uses `SpawnEggItem.forEntity()` (1.21 API)
  - Automatically added to SPAWN_EGGS Creative Tab
  - Model JSON created (`raccoon_spawn_egg.json`)
  - Language files updated

### Iteration 3 - Entity Data Migration & Version 0.1.0-beta
*Date: December 30, 2025*

- [x] Automatic data migration for entity attributes
  - Missing attributes are populated with default values on load
  - Removes deprecated attributes on load
  - No versioning anymore, migration runs always
- [x] Consistency check for tamed entities
  - Entity is only marked as tamed if an owner exists
  - Check runs only once on load
- [x] Preparations for future attribute changes
  - `DEPRECATED_ATTRIBUTES` set for removable fields
- [x] Build successful with Minecraft 1.21.10, Java 21+, Fabric
- [x] Mod version set to `0.1.0-beta`
- [x] Custom texture for the Raccoon created (`textures/entity/raccoon.png`)

### Iteration 4 - Spawn Update & Biome Support
*Date: December 30, 2025*

- [x] Raccoon spawn rate significantly increased (weight: 15)
- [x] Group size adjusted to 1-10
- [x] Now spawns in all Overworld biomes (1.21.10)

### Iteration 5 - Bugfix: Sitting Status is Saved
*Date: January 1, 2026*

 - [x] Bugfix: The sitting status (whether a raccoon is sitting) is now correctly saved and loaded
  - Raccoons that were sitting when saved will be sitting again after loading
  - Sitting status is stored as an NBT attribute ("RaccoonSitting")
- [x] README & Changelog updated

### Todo (Next Iterations)

- [ ] Add custom sounds
- [ ] Extended movement engines (e.g., `CuriousMovementEngine`)
- [ ] Configuration file for spawn rate, tame chance, etc.

## Notes for Further Development

### Adding Sounds

1. Create OGG sound files at:
   ```
   src/main/resources/assets/advanced_animals_lite/sounds/
   ```

2. Create `sounds.json`:
   ```json
   {
     "entity.raccoon.ambient": {
       "sounds": ["advanced_animals_lite:raccoon_ambient"]
     },
     "entity.raccoon.hurt": {
       "sounds": ["advanced_animals_lite:raccoon_hurt"]
     }
   }
   ```

3. Register sounds in a `ModSounds.java` class

4. Override `getAmbientSound()`, `getHurtSound()`, `getDeathSound()` in `RaccoonEntity`

### Adding a New Movement Engine

Implement the `MovementEngine` interface:

```java
public class CuriousMovementEngine implements MovementEngine {
    @Override
    public void tick(RaccoonEntity entity) {
        // Curious behavior: looks at items, etc.
    }
    
    @Override
    public void calculatePath(RaccoonEntity entity, Entity target) {
        // Pathfinding with exploration
    }
    
    @Override
    public boolean shouldMove(RaccoonEntity entity) {
        // Conditional movement
        return true;
    }
}
```

Setze die Engine in RaccoonEntity:
```java
raccoon.setMovementEngine(new CuriousMovementEngine());
```

### Adding Configuration

Recommended: Cloth Config API or custom JSON config

```java
public class ModConfig {
    public static int SPAWN_WEIGHT = 10;
    public static double TAME_CHANCE = 0.33;
    public static int FOLLOW_DISTANCE = 10;
}
```

### Debugging

If the entity doesn't spawn:
1. Check `/summon advanced_animals_lite:raccoon`
2. Check logs for registration errors
3. Check if texture exists (entity is invisible otherwise)

If crash on startup:
1. Check if all mixins are correctly configured
2. Check `fabric.mod.json` entrypoints
3. Run `./gradlew clean build`
