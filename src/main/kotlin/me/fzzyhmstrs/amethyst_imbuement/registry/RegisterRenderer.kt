@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntityRenderer
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.model.*
import me.fzzyhmstrs.amethyst_imbuement.model.DisenchantingTableBlockEntityRenderer.Companion.DISENCHANTING_TABLE_BOOK_SPRITE_ID
import me.fzzyhmstrs.amethyst_imbuement.model.ImbuingTableBlockEntityRenderer.Companion.IMBUING_TABLE_BOOK_SPRITE_ID
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.item.UnclampedModelPredicateProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.CompassItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import java.util.*
import kotlin.math.atan2

@Environment(value = EnvType.CLIENT)
object RegisterRenderer {
    val GLISTERING_TRIDENT: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"glistering_trident"),"glistering_trident_model")
    val DRACONIC_BOX_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"draconic_box"),"draconic_box_model")
    val TOTEM_ENTITY: EntityModelLayer = EntityModelLayer(Identifier(AI.MOD_ID,"totem"),"totem_model")
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
            RegisterEntity.TOTEM_OF_FURY_ENTITY
        ){context: EntityRendererFactory.Context ->
            TotemEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.TOTEM_OF_GRACE_ENTITY
        ){context: EntityRendererFactory.Context ->
            TotemEntityRenderer(
                context
            )
        }

        EntityRendererRegistry.register(
            RegisterEntity.TOTEM_OF_WIT_ENTITY
        ){context: EntityRendererFactory.Context ->
            TotemEntityRenderer(
                context
            )
        }

        //////////////////////////////////////////////////////////

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

        EntityRendererRegistry.register(
            RegisterEntity.MANA_POTION
        ){context: EntityRendererFactory.Context ->
            FlyingItemEntityRenderer(
                context
            )
        }

        /////////////////////////////////

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

        /////////////////////////////////

        EntityModelLayerRegistry.registerModelLayer(CRYSTAL_GOLEM_ENTITY,CrystallineGolemEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(DRACONIC_BOX_ENTITY,DraconicBoxModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(TOTEM_ENTITY,TotemEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(GLISTERING_TRIDENT,GlisteringTridentEntityModel::getTexturedModelData)
        EntityModelLayerRegistry.registerModelLayer(PLAYER_WITHER_SKULL_ENTITY,WitherSkullEntityRenderer::getTexturedModelData)

        //////////////////////////////////

        ModelPredicateProviderRegistry.register(
            RegisterItem.GLISTERING_TRIDENT, Identifier("throwing")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity?, _: Int -> if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f }

        ModelPredicateProviderRegistry.register(
            RegisterItem.SOJOURN, Identifier("angle"), CompassAnglePredicateProvider()
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

        ModelPredicateProviderRegistry.register(
            RegisterItem.BOOK_OF_LORE, Identifier("type")
        ) { stack: ItemStack, _: ClientWorld?, _: LivingEntity?, _: Int ->
            val nbt = stack.orCreateNbt
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
            val nbt = stack.orCreateNbt
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

        //////////////////////////////////////

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register {
                _ , registry ->
            registry.register(IMBUING_TABLE_BOOK_SPRITE_ID)
        }
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register {
                _ , registry ->
            registry.register(DISENCHANTING_TABLE_BOOK_SPRITE_ID)
        }

    }

    private class CompassAnglePredicateProvider: UnclampedModelPredicateProvider {
        private val aimedInterpolator = AngleInterpolator()
        private val aimlessInterpolator = AngleInterpolator()
        override fun unclampedCall(
            itemStack: ItemStack,
            world: ClientWorld?,
            livingEntity: LivingEntity?,
            i: Int
        ): Float {
            var clientWorld = world
            val g: Double
            val entity: Entity? = livingEntity ?: itemStack.holder
            if (entity == null) {
                return 0.0f
            }
            if (clientWorld == null && entity.world is ClientWorld) {
                clientWorld = entity.world as ClientWorld
            }
            val blockPos = if (CompassItem.hasLodestone(itemStack)) getLodestonePos(
                clientWorld,
                itemStack.orCreateNbt
            ) else getSpawnPos(clientWorld)
            val l = clientWorld!!.time
            if (blockPos == null || entity.pos.squaredDistanceTo(
                    blockPos.x.toDouble() + 0.5,
                    entity.pos.getY(),
                    blockPos.z.toDouble() + 0.5
                ) < 1.0E-5
            ) {
                if (aimlessInterpolator.shouldUpdate(l)) {
                    aimlessInterpolator.update(l, Math.random())
                }
                val d = aimlessInterpolator.value + (scatter(i).toFloat() / 2.14748365E9f).toDouble()
                return MathHelper.floorMod(d.toFloat(), 1.0f)
            }
            val bl = livingEntity is PlayerEntity && livingEntity.isMainPlayer
            var e = 0.0
            if (bl) {
                e = livingEntity!!.yaw.toDouble()
            } else if (entity is ItemFrameEntity) {
                e = getItemFrameAngleOffset(entity)
            } else if (entity is ItemEntity) {
                e = (180.0f - entity.getRotation(0.5f) / (Math.PI.toFloat() * 2) * 360.0f).toDouble()
            } else if (livingEntity != null) {
                e = livingEntity.bodyYaw.toDouble()
            }
            e = MathHelper.floorMod(e / 360.0, 1.0)
            val f = getAngleToPos(Vec3d.ofCenter(blockPos), entity) / 6.2831854820251465
            g = if (bl) {
                if (aimedInterpolator.shouldUpdate(l)) {
                    aimedInterpolator.update(l, 0.5 - (e - 0.25))
                }
                f + aimedInterpolator.value
            } else {
                0.5 - (e - 0.25 - f)
            }
            return MathHelper.floorMod(g.toFloat(), 1.0f)
        }

        /**
         * Scatters a seed by integer overflow in multiplication onto the whole
         * int domain.
         */
        private fun scatter(seed: Int): Int {
            return seed * 1327217883
        }

        private fun getSpawnPos(world: ClientWorld?): BlockPos? {
            return if (world!!.dimension.isNatural) world.spawnPos else null
        }

        private fun getLodestonePos(world: World?, nbt: NbtCompound): BlockPos? {
            val optional: Optional<RegistryKey<World?>> = if(nbt.contains("LodestoneDimension")){CompassItem.getLodestoneDimension(nbt)} else Optional.empty()
            val bl = nbt.contains("LodestonePos")
            val bl2 = nbt.contains("LodestoneDimension")

            return if (bl && bl2 && optional.isPresent && (world?.registryKey === optional.get())
            ) {
                NbtHelper.toBlockPos(nbt.getCompound("LodestonePos"))
            } else null
        }

        private fun getItemFrameAngleOffset(itemFrame: ItemFrameEntity): Double {
            val direction = itemFrame.horizontalFacing
            val i = if (direction.axis.isVertical) 90 * direction.direction.offset() else 0
            return MathHelper.wrapDegrees(180 + direction.horizontal * 90 + itemFrame.rotation * 45 + i).toDouble()
        }

        private fun getAngleToPos(pos: Vec3d, entity: Entity): Double {
            return atan2(pos.getZ() - entity.z, pos.getX() - entity.x)
        }
    }

    @Environment(value = EnvType.CLIENT)
    private class AngleInterpolator {
        var value = 0.0
        private var speed = 0.0
        private var lastUpdateTime: Long = 0
        fun shouldUpdate(time: Long): Boolean {
            return lastUpdateTime != time
        }

        fun update(time: Long, target: Double) {
            lastUpdateTime = time
            var d = target - value
            d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5
            speed += d * 0.1
            speed *= 0.8
            value = MathHelper.floorMod(value + speed, 1.0)
        }
    }

}


