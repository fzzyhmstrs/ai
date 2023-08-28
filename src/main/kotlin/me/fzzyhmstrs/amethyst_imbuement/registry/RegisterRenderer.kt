@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntityRenderer
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.*
import me.fzzyhmstrs.amethyst_imbuement.renderer.*
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
    val GLISTERING_TRIDENT: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"glistering_trident"),"glistering_trident_model")
    val DRACONIC_BOX_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"draconic_box"),"draconic_box_model")
    val BONESTORM_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"bonestorm"),"bonestorm_model")
    val TOTEM_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"totem"),"totem_model")
    val CRYSTAL_GOLEM_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"crystal_golem"),"crystal_golem_model")
    val PLAYER_WITHER_SKULL_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"player_wither_skull_entity"),"player_wither_skull_model")
    
    val HAMSTER_ENTITY_MAIN: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"base_hamster"),"hamster_main")
    val HAMSTER_ENTITY_ARMOR: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"base_hamster"),"hamster_armor")

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
        ){ context: EntityRendererFactory.Context -> BaseShardEntityRenderer(context, Identifier(AI.MOD_ID,"textures/entity/bone_shard.png")) }

        EntityRendererRegistry.register(
            RegisterEntity.ICE_SHARD_ENTITY
        ){ context: EntityRendererFactory.Context -> BaseShardEntityRenderer(context, Identifier(AI.MOD_ID,"textures/entity/ice_shard.png"))
        }

        EntityRendererRegistry.register(
            RegisterEntity.FREEZING_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context, 0.7f, 0.7f) }

        EntityRendererRegistry.register(
            RegisterEntity.FLAMEBOLT_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context, 1f, 0.7f, 0.3f, 0.6666F, 1.2F) }

        EntityRendererRegistry.register(
            RegisterEntity.BALL_LIGHTNING_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context, 1f, 1f, 0f, -2F, 1.25F) }

        EntityRendererRegistry.register(
            RegisterEntity.SOUL_MISSILE_ENTITY
        ){ context: EntityRendererFactory.Context -> MissileEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_BULLET
        ){ context: EntityRendererFactory.Context -> ShulkerBulletEntityRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_FANGS
        ){ context: EntityRendererFactory.Context -> PlayerFangsRenderer(context) }

        EntityRendererRegistry.register(
            RegisterEntity.ICE_SPIKE
        ){ context: EntityRendererFactory.Context -> IceSpikeRenderer(context) }

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
            RegisterTool.GLISTERING_TRIDENT, Identifier("throwing")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int -> if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f }

        ModelPredicateProviderRegistry.register(
            RegisterScepter.SOJOURN, Identifier("angle"), CompassAnglePredicateProvider {world,stack,_ -> if(CompassItem.hasLodestone(stack)) {
                CompassItem.createLodestonePos(stack.orCreateNbt)
            } else {
                CompassItem.createSpawnPos(world)
            }}
        )

        ModelPredicateProviderRegistry.register(
            RegisterTool.SNIPER_BOW, Identifier("pulling")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack && !CrossbowItem.isCharged(
                    stack
                )
            ) 1.0f else 0.0f
        }
        ModelPredicateProviderRegistry.register(
            RegisterTool.SNIPER_BOW, Identifier("charged")
        ) { stack: ItemStack?, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && CrossbowItem.isCharged(
                    stack
                )
            ) 1.0f else 0.0f
        }
        ModelPredicateProviderRegistry.register(
            RegisterTool.SNIPER_BOW, Identifier("firework")
        ) { stack: ItemStack?, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && CrossbowItem.isCharged(
                    stack
                ) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET)
            ) 1.0f else 0.0f
        }
        ModelPredicateProviderRegistry.register(
            RegisterTool.SNIPER_BOW, Identifier("pull")
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
            val nbt = stack.nbt ?:  return@register 0f
            if (nbt.contains(NbtKeys.LORE_TYPE.str())){
                when(nbt.getString(NbtKeys.LORE_TYPE.str())){
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
            val nbt = stack.nbt ?:  return@register 0f
            if (nbt.contains(NbtKeys.LORE_TYPE.str())){
                when(nbt.getString(NbtKeys.LORE_TYPE.str())){
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
            RegisterItem.BOOK_OF_TALES, Identifier("type")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.nbt ?:  return@register 0f
            if (!nbt.getBoolean("unlocked")){
                0.1f
            } else if (nbt.contains(NbtKeys.LORE_TYPE.str())){
                when(nbt.getString(NbtKeys.LORE_TYPE.str())){
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
            RegisterScepter.SPELL_SCROLL, Identifier("model_key")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.nbt
            if (nbt == null){
                0.0f
            } else {
                val decimal = when (nbt.getString(RegisterScepter.SPELL_SCROLL.SPELL_TYPE)){
                    SpellType.FURY.str() ->{ 0.0f }
                    SpellType.GRACE.str() -> { 0.2f }
                    SpellType.WIT.str() -> { 0.4f }
                    else -> { return@register 0.0f }
                }
                val value = nbt.getFloat(RegisterScepter.SPELL_SCROLL.MODEL_KEY).takeIf { f -> f > 0f }?:33f
                (value + decimal)/1000f
            }
        }

        ModelPredicateProviderRegistry.register(
            RegisterTool.IMBUED_WARD, Identifier("blocking")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f
        }

        ModelPredicateProviderRegistry.register(
            RegisterTool.COPPER_WARD, Identifier("blocking")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f
        }

        ModelPredicateProviderRegistry.register(
            RegisterTool.STEEL_WARD, Identifier("blocking")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f
        }
    }

}


