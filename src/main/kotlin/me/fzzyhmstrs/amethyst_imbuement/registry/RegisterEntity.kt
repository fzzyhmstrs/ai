package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.DraconicBoxEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.GlisteringTridentEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.ManaPotionEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.block.*
import me.fzzyhmstrs.amethyst_imbuement.entity.living.*
import me.fzzyhmstrs.amethyst_imbuement.entity.monster.SardonyxElementalEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.monster.SardonyxFragmentEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.*
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
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object RegisterEntity {

    /// Hammy ///////////////////////////////////

    val BASIC_HAMSTER_ENTITY: EntityType<BaseHamsterEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "basic_hamster"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<BaseHamsterEntity>, world: World ->
            BaseHamsterEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.4f, 0.3f)).trackRangeChunks(8).build()
    )
    
    /// Living Entities //////////////////////////

    val BONESTORM_ENTITY: EntityType<BonestormEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "bonestorm"),
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
        Identifier(AI.MOD_ID, "boom_chicken"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<BoomChickenEntity>, world: World ->
            BoomChickenEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.4f, 0.7f)).trackRangeChunks(6).build()
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

    val CHOLEM_ENTITY: EntityType<CholemEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "cholem"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.CREATURE
        ) { entityType: EntityType<CholemEntity>, world: World ->
            CholemEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.7f, 1.1f)).trackRangeChunks(10).build()
    )

    val CRYSTAL_GOLEM_ENTITY: EntityType<CrystallineGolemEntity> = Registry.register(
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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

    val TOTEM_OF_FURY_ENTITY: EntityType<TotemOfFuryEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "totem_of_fury"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<TotemOfFuryEntity>, world: World ->
            TotemOfFuryEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.75f, 1.5f)).build()
    )

    val TOTEM_OF_GRACE_ENTITY: EntityType<TotemOfGraceEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "totem_of_grace"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<TotemOfGraceEntity>, world: World ->
            TotemOfGraceEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.75f, 1.5f)).build()
    )

    val SARDONYX_FRAGMENT: EntityType<SardonyxFragmentEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "sardonyx_fragment"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MONSTER
        ) { entityType: EntityType<SardonyxFragmentEntity>, world: World ->
            SardonyxFragmentEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.7f, 1.1f)).trackRangeChunks(10).build()
    )

    val SARDONYX_ELEMENTAL: EntityType<SardonyxElementalEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "sardonyx_elemental"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MONSTER
        ) { entityType: EntityType<SardonyxElementalEntity>, world: World ->
            SardonyxElementalEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(1.95f, 2.65f)).trackRangeChunks(10).build()
    )

    /// Projectiles /////////////////////////////////////

    val GLISTERING_TRIDENT_ENTITY: EntityType<GlisteringTridentEntity> = Registry.register(
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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

    val CHAOS_BOLT_ENTITY: EntityType<ChaosBoltEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "chaos_bolt_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<ChaosBoltEntity>, world: World ->
            ChaosBoltEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val BALL_LIGHTNING_ENTITY: EntityType<BallLightningEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "ball_lightning_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BallLightningEntity>, world: World ->
            BallLightningEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    )

    val BONE_SHARD_ENTITY: EntityType<BoneShardEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "bone_shard_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<BoneShardEntity>, world: World ->
            BoneShardEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )
    
    val ICE_SHARD_ENTITY: EntityType<IceShardEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "ice_shard_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<IceShardEntity>, world: World ->
            IceShardEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    val FREEZING_ENTITY: EntityType<FreezingEntity> = Registry.register(
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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
        Registries.ENTITY_TYPE,
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

    val PLAYER_METEOR: EntityType<PlayerFireballEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "player_meteor_entity"),
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
        Registries.ENTITY_TYPE,
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

    val ENERGY_BLADE: EntityType<EnergyBladeEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "energy_blade_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<EnergyBladeEntity>, world: World ->
            EnergyBladeEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.85f, 0.85f)).build()
    )

    val MANA_POTION: EntityType<ManaPotionEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "mana_potion_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<ManaPotionEntity>, world: World ->
            ManaPotionEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.25f, 0.25f)).trackRangeChunks(4).trackedUpdateRate(10).build()
    )

    val PLAYER_EGG: EntityType<PlayerEggEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "player_egg_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<PlayerEggEntity>, world: World ->
            PlayerEggEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.3125f, 0.3125f)).build()
    )

    /// Block Entities ///////////////////////////////////////
    
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

    val GILDED_LOCKBOX_BLOCK_ENTITY: BlockEntityType<GildedLockboxBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":gilded_lockbox_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            GildedLockboxBlockEntity(
                pos,
                state
            )
        },RegisterBlock.GILDED_LOCKBOX).build(null))

    val PLANAR_DOOR_BLOCK_ENTITY: BlockEntityType<PlanarDoorBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":planar_door_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            PlanarDoorBlockEntity(
                pos,
                state
            )
        },RegisterBlock.PLANAR_DOOR).build(null))


    fun registerAll(){
        FabricDefaultAttributeRegistry.register(DRACONIC_BOX_ENTITY, DraconicBoxEntity.createMobAttributes())
        FabricDefaultAttributeRegistry.register(BASIC_HAMSTER_ENTITY, BaseHamsterEntity.createBaseHamsterAttributes())
        FabricDefaultAttributeRegistry.register(BONESTORM_ENTITY, BonestormEntity.createBonestormAttributes())
        FabricDefaultAttributeRegistry.register(BOOM_CHICKEN_ENTITY, BoomChickenEntity.createBoomChickenAttributes())
        FabricDefaultAttributeRegistry.register(CHORSE_ENTITY, ChorseEntity.createChorseBaseAttributes())
        FabricDefaultAttributeRegistry.register(CHOLEM_ENTITY, CholemEntity.createCholemAttributes())
        FabricDefaultAttributeRegistry.register(CRYSTAL_GOLEM_ENTITY, CrystallineGolemEntity.createGolemAttributes())
        FabricDefaultAttributeRegistry.register(UNHALLOWED_ENTITY, UnhallowedEntity.createUnhallowedAttributes())
        FabricDefaultAttributeRegistry.register(TOTEM_OF_FURY_ENTITY,TotemOfFuryEntity.createTotemAttributes())
        FabricDefaultAttributeRegistry.register(TOTEM_OF_GRACE_ENTITY,TotemOfGraceEntity.createTotemAttributes())
        FabricDefaultAttributeRegistry.register(SARDONYX_FRAGMENT, SardonyxFragmentEntity.createFragmentAttributes())
        FabricDefaultAttributeRegistry.register(SARDONYX_ELEMENTAL, SardonyxElementalEntity.createElementalAttributes())
    }


}
