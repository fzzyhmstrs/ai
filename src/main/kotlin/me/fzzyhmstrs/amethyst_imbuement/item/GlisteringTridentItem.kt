package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import me.fzzyhmstrs.amethyst_imbuement.entity.GlisteringTridentEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.UseAction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class GlisteringTridentItem(settings: Settings) : TridentItem(settings), Flavorful<GlisteringTridentItem> {
 private var attributeModifiers: Multimap<EntityAttribute, EntityAttributeModifier>
    override var flavor: String = ""
    override var glint: Boolean = false
    override var flavorDesc: String = ""
    override fun getFlavorItem(): GlisteringTridentItem {
        return this
    }

    private val flavorText: MutableText by lazy{
        makeFlavorText()
    }

    private val flavorTextDesc: MutableText by lazy{
        makeFlavorTextDesc()
    }

    private fun makeFlavorText(): MutableText {
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor"
        val text = AcText.translatable(key).formatted(Formatting.WHITE, Formatting.ITALIC)
        if (text.string == key) return AcText.empty()
        return text
    }

    private fun makeFlavorTextDesc(): MutableText {
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor.desc"
        val text = AcText.translatable(key).formatted(Formatting.WHITE)
        if (text.string == key) return AcText.empty()
        return text
    }

    override fun flavorText(): MutableText{
        return flavorText
    }
    override fun flavorDescText(): MutableText{
        return flavorTextDesc
    }

    init {
        val builder = ImmutableMultimap.builder<EntityAttribute, EntityAttributeModifier>()
        builder.put(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            EntityAttributeModifier(
                ATTACK_DAMAGE_MODIFIER_ID,
                "Tool modifier",
                11.0,
                EntityAttributeModifier.Operation.ADDITION
            )
        )
        builder.put(
            EntityAttributes.GENERIC_ATTACK_SPEED,
            EntityAttributeModifier(
                ATTACK_SPEED_MODIFIER_ID,
                "Tool modifier",
                -2.9,
                EntityAttributeModifier.Operation.ADDITION
            )
        )
        this.attributeModifiers = builder.build()
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
    }

    override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean {
        return ingredient.isOf(RegisterItem.GARNET) && stack.isOf(RegisterItem.GLISTERING_TRIDENT)
    }


    override fun getEnchantability(): Int {
        return 15
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.SPEAR
    }

    override fun isFireproof(): Boolean {
        return true
    }

    override fun getAttributeModifiers(slot: EquipmentSlot): Multimap<EntityAttribute, EntityAttributeModifier> {
        return if (slot == EquipmentSlot.MAINHAND) {
            this.attributeModifiers
        } else super.getAttributeModifiers(slot)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (user !is PlayerEntity) {
            return
        }
        val i = getMaxUseTime(stack) - remainingUseTicks
        if (i < 10) {
            return
        }
        val j = EnchantmentHelper.getRiptide(stack)
        if (j > 0 && !user.isTouchingWaterOrRain) {
            return
        }
        if (!world.isClient) {
            stack.damage(1, user) { p: PlayerEntity ->
                p.sendToolBreakStatus(
                    user.getActiveHand()
                )
            }
            if (j == 0) {
                val glisteringTridentEntity = GlisteringTridentEntity(world, user as LivingEntity, stack)
                glisteringTridentEntity.setVelocity(
                    user,
                    user.pitch,
                    user.yaw,
                    0.0f,
                    2.5f + j.toFloat() * 0.5f,
                    1.0f
                )
                if (user.abilities.creativeMode) {
                    glisteringTridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
                }
                world.spawnEntity(glisteringTridentEntity)
                world.playSoundFromEntity(
                    null,
                    glisteringTridentEntity,
                    SoundEvents.ITEM_TRIDENT_THROW,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
                )
                if (!user.abilities.creativeMode) {
                    user.inventory.removeOne(stack)
                }
            }
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this))
        if (j > 0) {
            val glisteringTridentEntity = user.yaw
            val f = user.pitch
            var g =
                -MathHelper.sin(glisteringTridentEntity * (Math.PI.toFloat() / 180)) * MathHelper.cos(f * (Math.PI.toFloat() / 180))
            var h = -MathHelper.sin(f * (Math.PI.toFloat() / 180))
            var k =
                MathHelper.cos(glisteringTridentEntity * (Math.PI.toFloat() / 180)) * MathHelper.cos(f * (Math.PI.toFloat() / 180))
            //val l = MathHelper.sqrt(g * g + h * h + k * k)
            val m = 3.0f * ((1.0f + j.toFloat()) / 4.0f)

            g *= m
            h *= m
            k *= m
            user.addVelocity(g.toDouble(),h.toDouble(),k.toDouble())
            user.useRiptide(20)

            if (user.isOnGround) {
                //val n = 1.1999999f
                user.move(MovementType.SELF, Vec3d(0.0, 1.1999999284744263, 0.0))
            }
            val n =
                if (j >= 3) SoundEvents.ITEM_TRIDENT_RIPTIDE_3 else if (j == 2) SoundEvents.ITEM_TRIDENT_RIPTIDE_2 else SoundEvents.ITEM_TRIDENT_RIPTIDE_1
            world.playSoundFromEntity(null, user, n, SoundCategory.PLAYERS, 1.0f, 1.0f)
        }
    }

}