@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.entity.MissileEntityRenderer
import me.fzzyhmstrs.amethyst_core.nbt.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.*
import me.fzzyhmstrs.amethyst_imbuement.renderer.*
import me.fzzyhmstrs.amethyst_imbuement.renderer.block.AltarOfExperienceBlockEntityRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.block.DisenchantingTableBlockEntityRenderer
import me.fzzyhmstrs.amethyst_imbuement.renderer.block.ImbuingTableBlockEntityRenderer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.item.CompassAnglePredicateProvider
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.model.Dilation
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.*
import net.minecraft.client.render.entity.model.BlazeEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.CompassItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier

@Environment(value = EnvType.CLIENT)
object RegisterRenderer {
    val GLISTERING_TRIDENT: EntityModelLayer = EntityModelLayer(AI.identity("glistering_trident"),"glistering_trident_model")
    val DRACONIC_BOX_ENTITY: EntityModelLayer = EntityModelLayer(AI.identity("draconic_box"),"draconic_box_model")
    val BONESTORM_ENTITY: EntityModelLayer = EntityModelLayer(AI.identity("bonestorm"),"bonestorm_model")
    val TOTEM_ENTITY: EntityModelLayer = EntityModelLayer(AI.identity("totem"),"totem_model")
    val CHORSE_ENTITY: EntityModelLayer = EntityModelLayer(AI.identity("chorse"),"chorse_model")
    val CRYSTAL_GOLEM_ENTITY: EntityModelLayer = EntityModelLayer(AI.identity("crystal_golem"),"crystal_golem_model")
    val FLORAL_CONSTRUCT_ENTITY: EntityModelLayer = EntityModelLayer(AI.identity("floral_construct"),"floral_construct_model")
    val PLAYER_WITHER_SKULL_ENTITY: EntityModelLayer = EntityModelLayer(AI.identity("player_wither_skull_entity"),"player_wither_skull_model")
    
    val HAMSTER_ENTITY_MAIN: EntityModelLayer = EntityModelLayer(AI.identity("base_hamster"),"hamster_main")
    val HAMSTER_ENTITY_ARMOR: EntityModelLayer = EntityModelLayer(AI.identity("base_hamster"),"hamster_armor")

    fun registerAll() {
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.EXPERIENCE_BUSH, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.HARD_LIGHT_BLOCK, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.FORCEFIELD_BLOCK, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_WHITE, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_LIGHT_GRAY, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_GRAY, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_BLACK, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_BROWN, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_RED, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_ORANGE, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_YELLOW, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_LIME, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_GREEN, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_CYAN, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_LIGHT_BLUE, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_BLUE, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_PURPLE, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_MAGENTA, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.CRYSTALLIZED_LIGHT_PINK, RenderLayer.getTranslucent())

        EntityRendererRegistry.register(
            RegisterEntity.BASIC_HAMSTER_ENTITY
        ){ context: EntityRendererFactory.Context -> BaseHamsterEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.BONESTORM_ENTITY
        ){ context: EntityRendererFactory.Context -> BonestormEntityRenderer(context) }
        
        EntityRendererRegistry.register(
            RegisterEntity.BOOM_CHICKEN_ENTITY
        ){ context: EntityRendererFactory.Context -> ChickenEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.FLORAL_CONSTRUCT_ENTITY
        ){ context: EntityRendererFactory.Context -> FloralConstructEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.CHORSE_ENTITY
        ){ context: EntityRendererFactory.Context -> ChorseEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.CRYSTAL_GOLEM_ENTITY
        ){ context: EntityRendererFactory.Context -> CrystallineGolemEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.UNHALLOWED_ENTITY
        ){ context: EntityRendererFactory.Context -> UnhallowedEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.DRACONIC_BOX_ENTITY
        ){ context: EntityRendererFactory.Context -> DraconicBoxEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.TOTEM_OF_FURY_ENTITY
        ){ context: EntityRendererFactory.Context -> TotemEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.TOTEM_OF_GRACE_ENTITY
        ){ context: EntityRendererFactory.Context -> TotemEntityRenderer(context) }

        //////////////////////////////////////////////////////////

        EntityRendererRegistry.register(
            RegisterEntity.GLISTERING_TRIDENT_ENTITY
        ){ context: EntityRendererFactory.Context -> GlisteringTridentEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.BONE_SHARD_ENTITY
        ){ context: EntityRendererFactory.Context -> BasicShardEntityRenderer(context, AI.identity("textures/entity/bone_shard.png")) }

        EntityRendererRegistry.register(
            RegisterEntity.ICE_SHARD_ENTITY
        ){ context: EntityRendererFactory.Context -> BasicShardEntityRenderer(context, AI.identity("textures/entity/ice_shard.png"))
        }

        EntityRendererRegistry.register(
            RegisterEntity.FREEZING_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.FLAMEBOLT_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.BALL_LIGHTNING_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.SOUL_MISSILE_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_BULLET
        ){ context: EntityRendererFactory.Context -> ShulkerBulletEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_FANGS
        ){ context: EntityRendererFactory.Context -> PlayerFangsRenderer(context, Identifier("textures/entity/illager/evoker_fangs.png")) }

        EntityRendererRegistry.register(
            RegisterEntity.ICE_SPIKE
        ){ context: EntityRendererFactory.Context -> PlayerFangsRenderer(context, AI.identity("textures/entity/ice_spike.png")) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_FIREBALL
        ){context: EntityRendererFactory.Context -> FlyingItemEntityRenderer(context, 3f, true) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_LIGHTNING
        ){ context: EntityRendererFactory.Context -> LightningEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_WITHER_SKULL
        ){ context: EntityRendererFactory.Context -> WitherSkullEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.MANA_POTION
        ){ context: EntityRendererFactory.Context -> FlyingItemEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_EGG
        ){ context: EntityRendererFactory.Context -> FlyingItemEntityRenderer(context) }

        /////////////////////////////////

        BlockEntityRendererFactories.register(RegisterEntity.IMBUING_TABLE_BLOCK_ENTITY
        ){ context: BlockEntityRendererFactory.Context -> ImbuingTableBlockEntityRenderer(context) }

        BlockEntityRendererFactories.register(RegisterEntity.DISENCHANTING_TABLE_BLOCK_ENTITY
        ){ context: BlockEntityRendererFactory.Context -> DisenchantingTableBlockEntityRenderer(context) }

        BlockEntityRendererFactories.register(RegisterEntity.ALTAR_OF_EXPERIENCE_BLOCK_ENTITY
        ){ context: BlockEntityRendererFactory.Context -> AltarOfExperienceBlockEntityRenderer(context) }

        /////////////////////////////////

        EntityModelLayerRegistry.registerModelLayer(FLORAL_CONSTRUCT_ENTITY,FloralConstructEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(CHORSE_ENTITY,ChorseEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(CRYSTAL_GOLEM_ENTITY,CrystallineGolemEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(DRACONIC_BOX_ENTITY,DraconicBoxModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(BONESTORM_ENTITY) { BlazeEntityModel.getTexturedModelData() }
        EntityModelLayerRegistry.registerModelLayer(TOTEM_ENTITY,TotemEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(GLISTERING_TRIDENT,GlisteringTridentEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(PLAYER_WITHER_SKULL_ENTITY,WitherSkullEntityRenderer::getTexturedModelData)
        
        EntityModelLayerRegistry.registerModelLayer(HAMSTER_ENTITY_MAIN) { BaseHamsterEntityModel.getTexturedModelData() }
        EntityModelLayerRegistry.registerModelLayer(HAMSTER_ENTITY_ARMOR) { BaseHamsterEntityModel.getTexturedModelData(Dilation(0.15f)) }

        //////////////////////////////////

        ModelPredicateProviderRegistry.register(
            RegisterItem.GLISTERING_TRIDENT, Identifier("throwing")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int -> if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f }

        ModelPredicateProviderRegistry.register(
            RegisterItem.SOJOURN, Identifier("angle"), CompassAnglePredicateProvider {world,stack,_ -> if(CompassItem.hasLodestone(stack)) {
                CompassItem.createLodestonePos(stack.orCreateNbt)
            } else {
                CompassItem.createSpawnPos(world)
            }}
        )

        ModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("pulling")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack && !CrossbowItem.isCharged(
                    stack
                )
            ) 1.0f else 0.0f
        }
        ModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("charged")
        ) { stack: ItemStack?, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && CrossbowItem.isCharged(
                    stack
                )
            ) 1.0f else 0.0f
        }
        ModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("firework")
        ) { stack: ItemStack?, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && CrossbowItem.isCharged(
                    stack
                ) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET)
            ) 1.0f else 0.0f
        }
        ModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("pull")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity == null) {
                return@register 0.0f
            }
            if (CrossbowItem.isCharged(stack)) {
                return@register 0.0f
            }
            (stack.maxUseTime - entity.itemUseTimeLeft).toFloat() / CrossbowItem.getPullTime(stack).toFloat()
        }

        ModelPredicateProviderRegistry.register(
            RegisterItem.BOOK_OF_LORE, Identifier("type")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.orCreateNbt
            if (nbt.contains(NbtKeys.LORE_TYPE)){
                when(nbt.getString(NbtKeys.LORE_TYPE)){
                    SpellType.FURY.str() ->{ 0.3f }
                    SpellType.GRACE.str() -> { 0.7f }
                    SpellType.WIT.str() -> { 1.0f }
                    else -> { 0.0f }
                }
            } else {
                0.0f
            }
        }

        ModelPredicateProviderRegistry.register(
            RegisterItem.BOOK_OF_MYTHOS, Identifier("type")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.orCreateNbt
            if (nbt.contains(NbtKeys.LORE_TYPE)){
                when(nbt.getString(NbtKeys.LORE_TYPE)){
                    SpellType.FURY.str() ->{ 0.3f }
                    SpellType.GRACE.str() -> { 0.7f }
                    SpellType.WIT.str() -> { 1.0f }
                    else -> { 0.0f }
                }
            } else {
                0.0f
            }
        }

        ModelPredicateProviderRegistry.register(
            RegisterItem.SPELL_SCROLL, Identifier("model_key")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.nbt
            if (nbt == null){
                0.0f
            } else {
                val decimal = when (nbt.getString(RegisterItem.SPELL_SCROLL.SPELL_TYPE)){
                    SpellType.FURY.str() ->{ 0.0f }
                    SpellType.GRACE.str() -> { 0.2f }
                    SpellType.WIT.str() -> { 0.4f }
                    else -> { return@register 0.0f }
                }
                val value = nbt.getFloat(RegisterItem.SPELL_SCROLL.MODEL_KEY).takeIf { f -> f > 0f }?:33f
                (value + decimal)/1000f
            }
        }

        ModelPredicateProviderRegistry.register(
            RegisterItem.IMBUED_WARD, Identifier("blocking")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f
        }

        ModelPredicateProviderRegistry.register(
            RegisterItem.COPPER_WARD, Identifier("blocking")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f
        }
    }

}


