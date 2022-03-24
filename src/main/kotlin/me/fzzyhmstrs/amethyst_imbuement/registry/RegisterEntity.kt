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
        }.dimensions(EntityDimensions.fixed(1.4f, 2.7f)).build()
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
        }.dimensions(EntityDimensions.fixed(0.61f, 1.8f)).build()
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
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    )

    val MISSILE_ENTITY: EntityType<MissileEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(AI.MOD_ID, "missile_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<MissileEntity>, world: World ->
            MissileEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
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
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
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
        }.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
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

    val FORCEFIELD_BLOCK_ENTITY: BlockEntityType<ForcefieldBlockEntity> = Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        AI.MOD_ID + ":forcefield_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            ForcefieldBlockEntity(
                pos,
                state
            )
        },RegisterBlock.FORCEFIELD_BLOCK).build(null))


    fun registerAll(){
        FabricDefaultAttributeRegistry.register(DRACONIC_BOX_ENTITY, DraconicBoxEntity.createMobAttributes())
        FabricDefaultAttributeRegistry.register(CRYSTAL_GOLEM_ENTITY, CrystallineGolemEntity.createGolemAttributes())
        FabricDefaultAttributeRegistry.register(UNHALLOWED_ENTITY, UnhallowedEntity.createUnhallowedAttributes())
    }


}