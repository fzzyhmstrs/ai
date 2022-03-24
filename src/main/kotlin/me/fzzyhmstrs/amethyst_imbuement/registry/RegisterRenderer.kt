@file:Suppress("DEPRECATION")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.*
import net.fabricmc.fabric.api.`object`.builder.v1.client.model.FabricModelPredicateProviderRegistry
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.item.UnclampedModelPredicateProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.util.function.Consumer


object RegisterRenderer {
    val GLISTERING_TRIDENT: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"glistering_trident"),"glistering_trident_model")
    val DRACONIC_BOX_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"draconic_box"),"draconic_box_model")
    val CRYSTAL_GOLEM_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"crystal_golem"),"crystal_golem_model")

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
            RegisterEntity.MISSILE_ENTITY
        ){context: EntityRendererFactory.Context ->
            MissileEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.FREEZING_ENTITY
        ){context: EntityRendererFactory.Context ->
            FreezingEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.FLAMEBOLT_ENTITY
        ){context: EntityRendererFactory.Context ->
            FlameboltEntityRenderer(
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
        FabricModelPredicateProviderRegistry.register(
            RegisterItem.GLISTERING_TRIDENT, Identifier("throwing")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int -> if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f }


        ModelLoadingRegistry.INSTANCE.registerModelProvider { _: ResourceManager?, out: Consumer<Identifier?> ->
                out.accept(ModelIdentifier(AI.MOD_ID + ":glistering_trident_in_hand#inventory"))

            }

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

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register() {
                _ , registry ->
            registry.register(Identifier(AI.MOD_ID,"entity/imbuing_table_book"))
        }
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register() {
                _ , registry ->
            registry.register(Identifier(AI.MOD_ID,"entity/disenchanting_table_book"))
        }

    }

}


