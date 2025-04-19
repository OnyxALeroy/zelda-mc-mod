package onyx.entities;

import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class ZeldaEntities {
    // Entities to register ----------------------------------------------------------------------------------------------------------------------------

    public static final RegistryKey<EntityType<?>> DEKU_SEED_KEY = RegistryKey.of(
        Registries.ENTITY_TYPE.getKey(),
        Identifier.of("zelda-oot-mod", "deku_seed_entity")
    );

    public static final EntityType<DekuSeedEntity> DEKU_SEED_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        DEKU_SEED_KEY.getValue(),
        EntityType.Builder.<DekuSeedEntity>create(DekuSeedEntity::new, SpawnGroup.MISC)
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
            .build(DEKU_SEED_KEY)
    );

	// ----------------------------------------------------------------------------------------------------------------------------------------------

    public static void initialize() {}
}
