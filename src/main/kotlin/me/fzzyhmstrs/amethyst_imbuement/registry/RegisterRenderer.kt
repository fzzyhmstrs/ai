@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntityRenderer
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.*
import me.fzzyhmstrs.amethyst_imbuement.model.DisenchantingTableBlockEntityRenderer.Companion.DISENCHANTING_TABLE_BOOK_SPRITE_ID
import me.fzzyhmstrs.amethyst_imbuement.model.ImbuingTableBlockEntityRenderer.Companion.IMBUING_TABLE_BOOK_SPRITE_ID
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.`object`.builder.v1.client.model.FabricModelPredicateProviderRegistry
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.item.UnclampedModelPredicateProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier

@Environment(value = EnvType.CLIENT)
object RegisterRenderer {
    val GLISTERING_TRIDENT: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"glistering_trident"),"glistering_trident_model")
    val DRACONIC_BOX_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"draconic_box"),"draconic_box_model")
    val CRYSTAL_GOLEM_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"crystal_golem"),"crystal_golem_model")
    val PLAYER_WITHER_SKULL_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"player_wither_skull_entity"),"player_wither_skull_model")

    fun registerAll() {
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.EXPERIENCE_BUSH, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.HARD_LIGHT_BLOCK, RenderLayer.getTranslucent())
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlock.FORCEFIELD_BLOCK, RenderLayer.getTranslucent())

        EntityRendererRegistry.register(
            RegisterEntity.CRYSTAL_GOLEM_ENTITY
        ){context: EntityRendererFactory.Context ->
            CrystallineGolemEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.UNHALLOWED_ENTITY
        ){context: EntityRendererFactory.Context ->
            UnhallowedEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.DRACONIC_BOX_ENTITY
        ){context: EntityRendererFactory.Context ->
            DraconicBoxEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.GLISTERING_TRIDENT_ENTITY
        ){context: EntityRendererFactory.Context ->
            GlisteringTridentEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.FREEZING_ENTITY
        ){context: EntityRendererFactory.Context ->
            MissileEntityRenderer(
                context,
                0.7f,
                0.7f
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.FLAMEBOLT_ENTITY
        ){context: EntityRendererFactory.Context ->
            MissileEntityRenderer(
                context,
                1.0f,
                0.7f,
                0.3f,
                0.6666F,
                1.2F
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.SOUL_MISSILE_ENTITY
        ){context: EntityRendererFactory.Context ->
            MissileEntityRenderer(context)
        }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_BULLET
        ){context: EntityRendererFactory.Context ->
            ShulkerBulletEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_FANGS
        ){context: EntityRendererFactory.Context ->
            PlayerFangsRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.ICE_SPIKE
        ){context: EntityRendererFactory.Context ->
            IceSpikeRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_FIREBALL
        ){context: EntityRendererFactory.Context ->
            FlyingItemEntityRenderer(
                context,
                3.0f,
                true
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_LIGHTNING
        ){context: EntityRendererFactory.Context ->
            LightningEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.PLAYER_WITHER_SKULL
        ){context: EntityRendererFactory.Context ->
            WitherSkullEntityRenderer(
                context
            )
        }


        BlockEntityRendererRegistry.register(RegisterEntity.IMBUING_TABLE_BLOCK_ENTITY
        ){context: BlockEntityRendererFactory.Context ->
            ImbuingTableBlockEntityRenderer(
                context
            )
        }

        BlockEntityRendererRegistry.register(RegisterEntity.DISENCHANTING_TABLE_BLOCK_ENTITY
        ){context: BlockEntityRendererFactory.Context ->
            DisenchantingTableBlockEntityRenderer(
                context
            )
        }

        BlockEntityRendererRegistry.register(RegisterEntity.ALTAR_OF_EXPERIENCE_BLOCK_ENTITY
        ){context: BlockEntityRendererFactory.Context ->
           AltarOfExperienceBlockEntityRenderer(
                context
            )
        }

        EntityModelLayerRegistry.registerModelLayer(CRYSTAL_GOLEM_ENTITY,CrystallineGolemEntityModel::getTexturedModelData)

        EntityModelLayerRegistry.registerModelLayer(DRACONIC_BOX_ENTITY,DraconicBoxModel::getTexturedModelData)

        EntityModelLayerRegistry.registerModelLayer(GLISTERING_TRIDENT,GlisteringTridentEntityModel::getTexturedModelData)

        EntityModelLayerRegistry.registerModelLayer(PLAYER_WITHER_SKULL_ENTITY,WitherSkullEntityRenderer::getTexturedModelData)

        FabricModelPredicateProviderRegistry.register(
            RegisterItem.GLISTERING_TRIDENT, Identifier("throwing")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int -> if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f }

        FabricModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("pulling")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack && !CrossbowItem.isCharged(
                    stack
                )
            ) 1.0f else 0.0f
        }
        FabricModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("charged")
        ) { stack: ItemStack?, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && CrossbowItem.isCharged(
                    stack
                )
            ) 1.0f else 0.0f
        }
        FabricModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("firework")
        ) { stack: ItemStack?, _: ClientWorld?, entity: LivingEntity?, _: Int ->
            if (entity != null && CrossbowItem.isCharged(
                    stack
                ) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET)
            ) 1.0f else 0.0f
        }
        FabricModelPredicateProviderRegistry.register(
            RegisterItem.SNIPER_BOW, Identifier("pull"),
            UnclampedModelPredicateProvider { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int ->
                if (entity == null) {
                    return@UnclampedModelPredicateProvider 0.0f
                }
                if (CrossbowItem.isCharged(stack)) {
                    return@UnclampedModelPredicateProvider 0.0f
                }
                (stack.maxUseTime - entity.itemUseTimeLeft).toFloat() / CrossbowItem.getPullTime(stack).toFloat()
        })

        FabricModelPredicateProviderRegistry.register(
            RegisterItem.BOOK_OF_LORE, Identifier("type")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.orCreateNbt
            if (nbt.contains(NbtKeys.LORE_TYPE.str())){
                when(Nbt.readStringNbt(NbtKeys.LORE_TYPE.str(), nbt)){
                    SpellType.FURY.str() ->{ 0.3f }
                    SpellType.GRACE.str() -> { 0.7f }
                    SpellType.WIT.str() -> { 1.0f }
                    else -> { 0.0f }
                }
            } else {
                0.0f
            }
        }

        FabricModelPredicateProviderRegistry.register(
            RegisterItem.BOOK_OF_MYTHOS, Identifier("type")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.orCreateNbt
            if (nbt.contains(NbtKeys.LORE_TYPE.str())){
                when(Nbt.readStringNbt(NbtKeys.LORE_TYPE.str(), nbt)){
                    SpellType.FURY.str() ->{ 0.3f }
                    SpellType.GRACE.str() -> { 0.7f }
                    SpellType.WIT.str() -> { 1.0f }
                    else -> { 0.0f }
                }
            } else {
                0.0f
            }
        }


        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register {
                _ , registry ->
            registry.register(IMBUING_TABLE_BOOK_SPRITE_ID)
        }
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register {
                _ , registry ->
            registry.register(DISENCHANTING_TABLE_BOOK_SPRITE_ID)
        }

    }

}


