package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.TravelerAugment
import me.fzzyhmstrs.amethyst_imbuement.item.SurveyMapItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class SurveyAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot),
    TravelerAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(16.0,0.0,0.0)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (user !is PlayerEntity) return false
        val mapStack = ItemStack(RegisterItem.SURVEY_MAP)
        val id = Nbt.makeItemStackId(mapStack)
        SurveyMapItem.buildMap(id, world, user.blockPos, mapStack)
        if (!user.inventory.insertStack(mapStack)) {
            user.dropItem(mapStack, false)
        }
        return true
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,1200,100,5,imbueLevel, LoreTier.LOW_TIER, Items.MAP)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_SPYGLASS_USE
    }
}
