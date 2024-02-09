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
import me.fzzyhmstrs.amethyst_imbuement.entity.variant.Variants
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.fzzy_core.entity_util.EntityBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup

object RegisterEntity: EntityBuilder() {

    fun <T: Entity> EntityType<T>.register(name: String): EntityType<T>{
        return FzzyPort.ENTITY_TYPE.register(AI.identity(name),this)
    }

    fun <T: BlockEntity> BlockEntityType<T>.register(name: String): BlockEntityType<T>{
        return FzzyPort.BLOCK_ENTITY_TYPE.register(AI.identity(name), this)
    }


    /// Hammy ///////////////////////////////////

    val BASIC_HAMSTER_ENTITY = buildCreature(
        { entityType: EntityType<BaseHamsterEntity>, world -> BaseHamsterEntity(entityType, world) },
        0.4f,
        0.3f,
        8).register("basic_hamster")
    
    /// Living Entities //////////////////////////

    val BONESTORM_ENTITY = buildCreature(
        { entityType: EntityType<BonestormEntity>, world -> BonestormEntity(entityType, world) },
        0.6f,
        1.8f,
        8).register("bonestorm")

    val BOOM_CHICKEN_ENTITY = buildCreature(
        { entityType: EntityType<BoomChickenEntity>, world -> BoomChickenEntity(entityType, world) },
        0.4f,
        0.7f,
        6).register("boom_chicken")

    val CHORSE_ENTITY = buildCreature(
        { entityType: EntityType<ChorseEntity>, world -> ChorseEntity(entityType, world) },
        1.3964844f,
        1.6f,
        10).register("chorse")

    val CHOLEM_ENTITY = buildCreature(
        { entityType: EntityType<CholemEntity>, world -> CholemEntity(entityType, world) },
        0.7f,
        1.1f,
        10).register("cholem")

    val CRYSTAL_GOLEM_ENTITY = buildCreature(
        { entityType: EntityType<CrystallineGolemEntity>, world -> CrystallineGolemEntity(entityType, world) },
        1.4f,
        2.7f,
        10).register("crystal_golem")

    val UNHALLOWED_ENTITY = buildCreature(
        { entityType: EntityType<UnhallowedEntity>, world -> UnhallowedEntity(entityType, world) },
        0.61f,
        1.8f,
        8).register("unhallowed")

    val DRACONIC_BOX_ENTITY = buildMisc(
        { entityType: EntityType<DraconicBoxEntity>, world -> DraconicBoxEntity(entityType, world) },
        0.75f,
        0.75f).register("draconic_box")

    val TOTEM_OF_FURY_ENTITY = buildMisc(
        { entityType: EntityType<TotemOfFuryEntity>, world -> TotemOfFuryEntity(entityType, world) },
        0.75f,
        1.5f).register("totem_of_fury")

    val TOTEM_OF_GRACE_ENTITY = buildMisc(
        { entityType: EntityType<TotemOfGraceEntity>, world -> TotemOfGraceEntity(entityType, world) },
        0.75f,
        1.5f).register("totem_of_grace")

    val SARDONYX_FRAGMENT = buildMonster(
        { entityType: EntityType<SardonyxFragmentEntity>, world -> SardonyxFragmentEntity(entityType, world) },
        0.7f,
        1.1f,
        10).register("sardonyx_fragment")

    val SARDONYX_ELEMENTAL = buildMonster(
        { entityType: EntityType<SardonyxElementalEntity>, world -> SardonyxElementalEntity(entityType, world) },
        1.95f,
        2.65f,
        10).register("sardonyx_elemental")

    /// Projectiles /////////////////////////////////////

    val GLISTERING_TRIDENT_ENTITY = buildMisc(
        { entityType: EntityType<GlisteringTridentEntity>, world -> GlisteringTridentEntity(entityType, world) },
        0.5f,
        0.5f,
        4,
        20).register("glistering_trident")

    val FLAMEBOLT_ENTITY = buildMisc(
        { entityType: EntityType<FlameboltEntity>, world -> FlameboltEntity(entityType, world) },
        0.3125f,
        0.3125f).register("flamebolt_entity")

    val CHAOS_BOLT_ENTITY = buildMisc(
        { entityType: EntityType<ChaosBoltEntity>, world -> ChaosBoltEntity(entityType, world) },
        0.3125f,
        0.3125f).register("chaos_bolt_entity")

    val BALL_LIGHTNING_ENTITY = buildMisc(
        { entityType: EntityType<BallLightningEntity>, world -> BallLightningEntity(entityType, world) },
        0.5f,
        0.5f).register("ball_lightning_entity")

    val BONE_SHARD_ENTITY = buildMisc(
        { entityType: EntityType<BoneShardEntity>, world -> BoneShardEntity(entityType, world) },
        0.3125f,
        0.3125f).register("bone_shard_entity")

    val ICE_SHARD_ENTITY = buildMisc(
        { entityType: EntityType<IceShardEntity>, world -> IceShardEntity(entityType, world) },
        0.3125f,
        0.3125f).register("ice_shard_entity")

    val FREEZING_ENTITY = buildMisc(
        { entityType: EntityType<FreezingEntity>, world -> FreezingEntity(entityType, world) },
        0.3125f,
        0.3125f).register("freezing_entity")

    val SOUL_MISSILE_ENTITY = buildMisc(
        { entityType: EntityType<SoulMissileEntity>, world -> SoulMissileEntity(entityType, world) },
        0.3125f,
        0.3125f).register("soul_missile_entity")

    val PLAYER_BULLET = buildMisc(
        { entityType: EntityType<PlayerBulletEntity>, world -> PlayerBulletEntity(entityType, world) },
        0.3125f,
        0.3125f,
        8).register("player_bullet_entity")

    val PLAYER_FANGS = buildMisc(
        { entityType: EntityType<PlayerFangsEntity>, world -> PlayerFangsEntity(entityType, world) },
        0.7f,
        1.0f,
        6,
        2).register("player_fangs_entity")

    val ICE_SPIKE = buildMisc(
        { entityType: EntityType<IceSpikeEntity>, world -> IceSpikeEntity(entityType, world) },
        0.7f,
        1.0f,
        6,
        2).register("ice_spike_entity")

    val PLAYER_FIREBALL = buildMisc(
        { entityType: EntityType<PlayerFireballEntity>, world -> PlayerFireballEntity(entityType, world) },
        1.0f,
        1.0f,
        12,
        10).register("player_fireball_entity")

    val PLAYER_METEOR = buildMisc(
        { entityType: EntityType<PlayerFireballEntity>, world -> PlayerFireballEntity(entityType, world) },
        1.0f,
        1.0f,
        12,
        10).register("player_meteor_entity")

    val PLAYER_LIGHTNING: EntityType<PlayerLightningEntity> =
        FabricEntityTypeBuilder.create(SpawnGroup.MISC)
        { entityType: EntityType<PlayerLightningEntity>, world -> PlayerLightningEntity(entityType, world) }
            .dimensions(EntityDimensions.fixed(0.0f, 0.0f))
            .disableSaving()
            .trackRangeChunks(16)
            .trackedUpdateRate(Integer.MAX_VALUE)
            .build()
            .register("player_lightning_entity")

    val PLAYER_WITHER_SKULL = buildMisc(
        { entityType: EntityType<PlayerWitherSkullEntity>, world -> PlayerWitherSkullEntity(entityType, world) },
        0.3125f,
        0.3125f,
        4,
        10).register("player_wither_skull_entity")

    val ENERGY_BLADE = buildMisc(
        { entityType: EntityType<EnergyBladeEntity>, world -> EnergyBladeEntity(entityType, world) },
        0.85f,
        0.85f).register("energy_blade_entity")

    val MANA_POTION = buildMisc(
        { entityType: EntityType<ManaPotionEntity>, world -> ManaPotionEntity(entityType, world) },
        0.25f,
        0.25f,
        4,
        10).register("mana_potion_entity")

    val PLAYER_EGG = buildMisc(
        { entityType: EntityType<PlayerEggEntity>, world -> PlayerEggEntity(entityType, world) },
        0.3125f,
        0.3125f).register("player_egg_entity")

    /// Block Entities ///////////////////////////////////////

    val IMBUING_TABLE_BLOCK_ENTITY = buildBlockEntity({ p, s -> ImbuingTableBlockEntity(p, s) }, RegisterBlock.IMBUING_TABLE).register("imbuing_table_entity")

    val ALTAR_OF_EXPERIENCE_BLOCK_ENTITY = buildBlockEntity({ p, s -> AltarOfExperienceBlockEntity(p, s) }, RegisterBlock.ALTAR_OF_EXPERIENCE).register("altar_of_experience_entity")

    val DISENCHANTING_TABLE_BLOCK_ENTITY = buildBlockEntity({ p, s -> DisenchantingTableBlockEntity(p, s) }, RegisterBlock.DISENCHANTING_TABLE).register("disenchanting_table_entity")

    val GILDED_LOCKBOX_BLOCK_ENTITY = buildBlockEntity({ p, s -> GildedLockboxBlockEntity(p, s) }, RegisterBlock.GILDED_LOCKBOX).register("gilded_lockbox_entity")

    val PLANAR_DOOR_BLOCK_ENTITY = buildBlockEntity({ p, s -> PlanarDoorBlockEntity(p, s) }, RegisterBlock.PLANAR_DOOR).register("planar_door_entity")

    val WITCHES_BOOKSHELF_BLOCK_ENTITY = buildBlockEntity({ p, s -> WitchesBookshelfBlockEntity(p, s) }, RegisterBlock.WITCHES_BOOKSHELF).register("witches_bookshelf_entity")

    fun registerAll(){
        Variants.registerAll()
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
