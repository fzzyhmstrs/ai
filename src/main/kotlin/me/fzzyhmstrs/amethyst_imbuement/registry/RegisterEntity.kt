package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.*
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object RegisterEntity {

    val CRYSTAL_GOLEM_ENTITY: EntityType<CrystallineGolemEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "crystal_golem"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<CrystallineGolemEntity>, world: World ->
            CrystallineGolemEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(1.4f, 2.7f)).trackRangeChunks(10).build()
    )

    val UNHALLOWED_ENTITY: EntityType<UnhallowedEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "unhallowed"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<UnhallowedEntity>, world: World ->
            UnhallowedEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.61f, 1.8f)).trackRangeChunks(8).build()
    )

    val DRACONIC_BOX_ENTITY: EntityType<DraconicBoxEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "draconic_box"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<DraconicBoxEntity>, world: World ->
            DraconicBoxEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    )

    val GLISTERING_TRIDENT_ENTITY: EntityType<GlisteringTridentEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "glistering_trident"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<GlisteringTridentEntity>, world: World ->
            GlisteringTridentEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(4).trackedUpdateRate(20).build()
    )

    val FLAMEBOLT_ENTITY: EntityType<FlameboltEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "flamebolt_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<FlameboltEntity>, world: World ->
            FlameboltEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val FREEZING_ENTITY: EntityType<FreezingEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "freezing_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<FreezingEntity>, world: World ->
            FreezingEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val SOUL_MISSILE_ENTITY: EntityType<SoulMissileEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "soul_missile_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<SoulMissileEntity>, world: World ->
            SoulMissileEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val PLAYER_BULLET: EntityType<PlayerBulletEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "player_bullet_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerBulletEntity>, world: World ->
            PlayerBulletEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).trackRangeChunks(8).build()
    )

    val PLAYER_FANGS: EntityType<PlayerFangsEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "player_fangs_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerFangsEntity>, world: World ->
            PlayerFangsEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.7f, 1.0f)).trackRangeChunks(6).trackedUpdateRate(2).build()
    )

    val ICE_SPIKE: EntityType<IceSpikeEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "ice_spike_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<IceSpikeEntity>, world: World ->
            IceSpikeEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.7f, 1.0f)).trackRangeChunks(6).trackedUpdateRate(2).build()
    )

    val PLAYER_FIREBALL: EntityType<PlayerFireballEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "player_fireball_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerFireballEntity>, world: World ->
            PlayerFireballEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(1.0f, 1.0f)).trackRangeChunks(12).trackedUpdateRate(10).build()
    )

    val PLAYER_LIGHTNING: EntityType<PlayerLightningEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "player_lightning_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerLightningEntity>, world: World ->
            PlayerLightningEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.0f, 0.0f)).disableSaving().trackRangeChunks(16).trackedUpdateRate(Integer.MAX_VALUE).build()
    )

    val PLAYER_WITHER_SKULL: EntityType<PlayerWitherSkullEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "player_wither_skull_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerWitherSkullEntity>, world: World ->
            PlayerWitherSkullEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).trackRangeChunks(4).trackedUpdateRate(10).build()
    )

    
    val IMBUING_TABLE_BLOCK_ENTITY: BlockEntityType<ImbuingTableBlockEntity> = Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":imbuing_table_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            ImbuingTableBlockEntity(
                pos,
                state
            )
        },RegisterBlock.IMBUING_TABLE).build(null))

    val ALTAR_OF_EXPERIENCE_BLOCK_ENTITY: BlockEntityType<AltarOfExperienceBlockEntity> = Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":altar_of_experience_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            AltarOfExperienceBlockEntity(
                pos,
                state
            )
        },RegisterBlock.ALTAR_OF_EXPERIENCE).build(null))

    val DISENCHANTING_TABLE_BLOCK_ENTITY: BlockEntityType<DisenchantingTableBlockEntity> = Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":disenchanting_table_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            DisenchantingTableBlockEntity(
                pos,
                state
            )
        },RegisterBlock.DISENCHANTING_TABLE).build(null))


    fun registerAll(){
        FabricDefaultAttributeRegistry.register(DRACONIC_BOX_ENTITY, DraconicBoxEntity.createMobAttributes())
        FabricDefaultAttributeRegistry.register(CRYSTAL_GOLEM_ENTITY, CrystallineGolemEntity.createGolemAttributes())
        FabricDefaultAttributeRegistry.register(UNHALLOWED_ENTITY, UnhallowedEntity.createUnhallowedAttributes())
    }


}