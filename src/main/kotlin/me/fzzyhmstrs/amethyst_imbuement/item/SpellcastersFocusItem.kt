package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.event.AfterSpellEvent
import me.fzzyhmstrs.amethyst_core.event.ModifyModifiersEvent
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.registry.ModifierRegistry
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import me.fzzyhmstrs.fzzy_core.modifier_util.AbstractModifier
import me.fzzyhmstrs.fzzy_core.modifier_util.ModifierHelperType
import me.fzzyhmstrs.fzzy_core.modifier_util.ModifierInitializer
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class SpellcastersFocusItem(settings: Settings): CustomFlavorItem(settings), Modifiable {

    private val FOCUS_TIER = "focus_tier"
    private val FOCUS_RECORDS = "focus_records"
    private val FOCUS_XP = "focus_xp"
    private val FOCUS_SPECIAL = "focus_special"
    private val tiers: Array<TierData> = arrayOf(
        TierData("", Rarity.UNCOMMON, 500,1),
        TierData(".novice", Rarity.UNCOMMON, 1250,2),
        TierData(".adept", Rarity.RARE, 2250,3),
        TierData(".master", Rarity.RARE, 3500,4),
        TierData(".savant", Rarity.EPIC, -1,-1)
    )

    init{
        ModifyModifiersEvent.EVENT.register{ _,user,_,modifiers ->
            val offhand = user.offHandStack
            if (offhand.item is SpellcastersFocusItem){
                val focusMods = ModifierHelper.getActiveModifiers(offhand)
                val compiledModifiers = modifiers.modifiers.toMutableList()
                compiledModifiers.addAll(focusMods.modifiers)
                val compiledData = modifiers.compiledData.plus(focusMods.compiledData)
                AbstractModifier.CompiledModifiers(compiledModifiers,compiledData)
            } else {
                modifiers
            }
        }

        AfterSpellEvent.EVENT.register{ world,user,_,spell ->
            val offhand = user.offHandStack
            val item = offhand.item
            if (item is SpellcastersFocusItem){
                addXpAndLevelUp(offhand, spell, user, world)
            }
        }
    }

    private fun addXpAndLevelUp(stack: ItemStack, spell: ScepterAugment, user: LivingEntity, world: World){
        val nbt = stack.orCreateNbt
        val tier = getTier(nbt)
        if (tier.nextTier == -1) return
        val id = spell.id?.toString()?:return
        val newXp = AugmentHelper.getAugmentCastXp(id)
        val currentXp = nbt.getInt(FOCUS_XP)
        val newCurrentXp = currentXp + newXp
        nbt.putInt(FOCUS_XP,newCurrentXp)
        val records = nbt.getCompound(FOCUS_RECORDS)
        val currentRecordedXp = if (records.contains(id)) { records.getInt(id) }else{ 0 }
        val newCurrentRecordedXp = currentRecordedXp + newXp
        if (newCurrentXp > tier.xpToNextTier){
            nbt.putInt(FOCUS_TIER,tier.nextTier)
            if (newCurrentRecordedXp/tier.xpToNextTier > 0.9 && !nbt.contains(FOCUS_SPECIAL)){
                nbt.putBoolean(FOCUS_SPECIAL,true)
                addSpecialAugmentModifier(stack, spell)
            } else {
                if (user is ServerPlayerEntity && world is ServerWorld) {
                    ModifierHelper.rollScepterModifiers(stack,user,world)
                }
            }
        }
    }

    private fun addSpecialAugmentModifier(stack: ItemStack,spell: ScepterAugment){
        val modifier = spell.augmentSpecificModifier
        ModifierHelper.addModifier(modifier.modifierId,stack)
    }

    private fun getTier(nbt: NbtCompound?): TierData{
        if (nbt == null) return tiers[0]
        if(!nbt.contains(FOCUS_TIER)) return tiers[0]
        val tier = MathHelper.clamp(nbt.getByte(FOCUS_TIER),0,4)
        return tiers[tier.toInt()]
    }

    private data class TierData(val key: String, val rarity: Rarity, val xpToNextTier: Int, val nextTier: Int)

    override fun getRarity(stack: ItemStack): Rarity {
        val tier = getTier(stack.nbt)
        return tier.rarity
    }

    override fun getTranslationKey(stack: ItemStack): String {
        val tier = getTier(stack.nbt)
        return super.getTranslationKey(stack) + tier.key
    }

    override fun canBeModifiedBy(type: ModifierHelperType): Boolean {
        return type == ModifierRegistry.MODIFIER_TYPE || type.id == Identifier("gear_core","gear_modifier_helper")

    }

}
