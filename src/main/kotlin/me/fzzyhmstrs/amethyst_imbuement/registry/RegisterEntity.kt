package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.entity.PlayerItemEntity
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.*
import me.fzzyhmstrs.amethyst_imbuement.entity.block.AltarOfExperienceBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.block.DisenchantingTableBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.block.ImbuingTableBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.*
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfFuryEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfGraceEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object RegisterEntity {
    
    val BASIC_HAMSTER_ENTITY: EntityType<BaseHamsterEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "basic_hamster"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<BaseHamsterEntity>, world: World ->
            BaseHamsterEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.4f, 0.3f)).trackRangeChunks(8).build()
    )
    
    /////////

    val BONESTORM_ENTITY: EntityType<BonestormEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "bonestorm"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<BonestormEntity>, world: World ->
            BonestormEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.6f, 1.8f)).trackRangeChunks(8).build()
    )

    val BOOM_CHICKEN_ENTITY: EntityType<BoomChickenEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "boom_chicken"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<BoomChickenEntity>, world: World ->
            BoomChickenEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.4f, 0.7f)).trackRangeChunks(6).build()
    )

    val CRYSTAL_GOLEM_ENTITY: EntityType<CrystallineGolemEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "crystal_golem"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<CrystallineGolemEntity>, world: World ->
            CrystallineGolemEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(1.4f, 2.7f)).trackRangeChunks(10).build()
    )

    val CHORSE_ENTITY: EntityType<ChorseEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "chorse"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<ChorseEntity>, world: World ->
            ChorseEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(1.3964844f, 1.6f)).trackRangeChunks(10).build()
    )

    val FLORAL_CONSTRUCT_ENTITY: EntityType<FloralConstructEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "floral_construct"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<FloralConstructEntity>, world: World ->
            FloralConstructEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.6f, 0.6f)).trackRangeChunks(8).build()
    )

    val UNHALLOWED_ENTITY: EntityType<UnhallowedEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "unhallowed"),
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
        Registries.ENTITY_TYPE,
        AI.identity( "draconic_box"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<DraconicBoxEntity>, world: World ->
            DraconicBoxEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    )

    val TOTEM_OF_FURY_ENTITY: EntityType<TotemOfFuryEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "totem_of_fury"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<TotemOfFuryEntity>, world: World ->
            TotemOfFuryEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.75f, 1.5f)).build()
    )

    val TOTEM_OF_FANGS_ENTITY: EntityType<TotemOfFangsEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "totem_of_fangs"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<TotemOfFangsEntity>, world: World ->
            TotemOfFangsEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.75f, 1.5f)).build()
    )

    val TOTEM_OF_GRACE_ENTITY: EntityType<TotemOfGraceEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "totem_of_grace"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<TotemOfGraceEntity>, world: World ->
            TotemOfGraceEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.75f, 1.5f)).build()
    )

    ////////////////////////////////////////

    val GLISTERING_TRIDENT_ENTITY: EntityType<GlisteringTridentEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "glistering_trident"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<GlisteringTridentEntity>, world: World ->
            GlisteringTridentEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(4).trackedUpdateRate(20).build()
    )

    val BASIC_MISSILE_ENTITY: EntityType<BasicMissileEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "basic_missile_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BasicMissileEntity>, world: World ->
            BasicMissileEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val FLAMEBOLT_ENTITY: EntityType<BasicMissileEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "flamebolt_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BasicMissileEntity>, world: World ->
            BasicMissileEntity(
                entityType,
                world
            ).burning()
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val BALL_LIGHTNING_ENTITY: EntityType<BallLightningEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "ball_lightning_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BallLightningEntity>, world: World ->
            BallLightningEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    )

    val BONE_SHARD_ENTITY: EntityType<BasicShardEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "bone_shard_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BasicShardEntity>, world: World ->
            BasicShardEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )
    
    val ICE_SHARD_ENTITY: EntityType<BasicShardEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "ice_shard_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BasicShardEntity>, world: World ->
            BasicShardEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val FREEZING_ENTITY: EntityType<BasicMissileEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "freezing_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BasicMissileEntity>, world: World ->
            BasicMissileEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val SOUL_MISSILE_ENTITY: EntityType<BasicMissileEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "soul_missile_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BasicMissileEntity>, world: World ->
            BasicMissileEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val PLAYER_BULLET: EntityType<PlayerBulletEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "player_bullet_entity"),
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
        Registries.ENTITY_TYPE,
        AI.identity( "player_fangs_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerFangsEntity>, world: World ->
            PlayerFangsEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.7f, 1.0f)).trackRangeChunks(6).trackedUpdateRate(2).build()
    )

    val ICE_SPIKE: EntityType<PlayerFangsEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "ice_spike_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerFangsEntity>, world: World ->
            PlayerFangsEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.7f, 1.0f)).trackRangeChunks(6).trackedUpdateRate(2).build()
    )

    val PLAYER_FIREBALL: EntityType<PlayerFireballEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "player_fireball_entity"),
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
        Registries.ENTITY_TYPE,
        AI.identity( "player_lightning_entity"),
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
        Registries.ENTITY_TYPE,
        AI.identity( "player_wither_skull_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerWitherSkullEntity>, world: World ->
            PlayerWitherSkullEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).trackRangeChunks(4).trackedUpdateRate(10).build()
    )
    val MANA_POTION: EntityType<ManaPotionEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "mana_potion_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<ManaPotionEntity>, world: World ->
            ManaPotionEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeChunks(4).trackedUpdateRate(10).build()
    )

    val PLAYER_EGG: EntityType<PlayerItemEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        AI.identity( "player_egg_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerItemEntity>, world: World ->
            PlayerItemEntity(
                entityType,
                world,
                Items.EGG
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    //////////////////////////////////////////
    
    val IMBUING_TABLE_BLOCK_ENTITY: BlockEntityType<ImbuingTableBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":imbuing_table_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            ImbuingTableBlockEntity(
                pos,
                state
            )
        },RegisterBlock.IMBUING_TABLE).build(null))

    val ALTAR_OF_EXPERIENCE_BLOCK_ENTITY: BlockEntityType<AltarOfExperienceBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":altar_of_experience_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            AltarOfExperienceBlockEntity(
                pos,
                state
            )
        },RegisterBlock.ALTAR_OF_EXPERIENCE).build(null))

    val DISENCHANTING_TABLE_BLOCK_ENTITY: BlockEntityType<DisenchantingTableBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":disenchanting_table_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            DisenchantingTableBlockEntity(
                pos,
                state
            )
        },RegisterBlock.DISENCHANTING_TABLE).build(null))


    fun registerAll(){
        FabricDefaultAttributeRegistry.register(CHORSE_ENTITY, ChorseEntity.createChorseBaseAttributes())
        FabricDefaultAttributeRegistry.register(DRACONIC_BOX_ENTITY, DraconicBoxEntity.createMobAttributes())
        FabricDefaultAttributeRegistry.register(BASIC_HAMSTER_ENTITY, BaseHamsterEntity.createBaseHamsterAttributes())
        FabricDefaultAttributeRegistry.register(BONESTORM_ENTITY, BonestormEntity.createBonestormAttributes())
        FabricDefaultAttributeRegistry.register(BOOM_CHICKEN_ENTITY, BoomChickenEntity.createBoomChickenAttributes())
        FabricDefaultAttributeRegistry.register(FLORAL_CONSTRUCT_ENTITY, FloralConstructEntity.createFloralAttributes())
        FabricDefaultAttributeRegistry.register(CRYSTAL_GOLEM_ENTITY, CrystallineGolemEntity.createGolemAttributes())
        FabricDefaultAttributeRegistry.register(UNHALLOWED_ENTITY, UnhallowedEntity.createUnhallowedAttributes())
        FabricDefaultAttributeRegistry.register(TOTEM_OF_FURY_ENTITY,TotemOfFuryEntity.createTotemAttributes())
        FabricDefaultAttributeRegistry.register(TOTEM_OF_GRACE_ENTITY,TotemOfGraceEntity.createTotemAttributes())
    }


}
