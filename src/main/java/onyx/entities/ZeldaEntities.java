package onyx.entities;

import net.minecraft.util.Identifier;
import onyx.ZeldaOOTMod;
import onyx.entities.custom.DekuSeedEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class ZeldaEntities {
    private static RegistryKey<EntityType<?>> getEntityKey(String entityName){
        return RegistryKey.of(
            Registries.ENTITY_TYPE.getKey(),
            Identifier.of(ZeldaOOTMod.MOD_ID, entityName)
        );
    }

    // Entities to register ----------------------------------------------------------------------------------------------------------------------------

    public static final RegistryKey<EntityType<?>> DEKU_SEED_ENTITY_KEY = getEntityKey("deku_seed_entity");    
    public static final EntityType<DekuSeedEntity> DEKU_SEED_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        DEKU_SEED_ENTITY_KEY.getValue(),
        EntityType.Builder.<DekuSeedEntity>create(DekuSeedEntity::new, SpawnGroup.MISC)
        .dimensions(0.25f, 0.25f)
        .maxTrackingRange(4)
        .trackingTickInterval(10)
        .build(DEKU_SEED_ENTITY_KEY)
        );

	// ----------------------------------------------------------------------------------------------------------------------------------------------

    public static void initialize() {
        ZeldaOOTMod.LOGGER.info("Registering Zelda Mod entities...\n");
    }
}
