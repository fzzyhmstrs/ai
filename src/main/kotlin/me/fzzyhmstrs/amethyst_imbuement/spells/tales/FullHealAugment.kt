package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.world.World

class FullHealAugment: MinorSupportAugment(ScepterTier.THREE,7){

    override val baseEffect: AugmentEffect = super.baseEffect.withRange(5.5,0.5, 0.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, PerLvlI(330,-10),150,
            24,imbueLevel,25,BookOfTalesItem.TALES_TIER, RegisterItem.DAZZLING_MELON_SLICE)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if (target is LivingEntity &&   AiConfig.entities.shouldItHitFriend(user,target,this)) {
            val toHeal = (target as LivingEntity).maxHealth - target.health
            if (toHeal > 0f) {
                target.heal(toHeal)
                if (target.health != target.maxHealth) return false
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                return true
            }
        }

        val toHeal = user.maxHealth - user.health
        if (toHeal <= 0f) return false
        user.heal(toHeal)
        if (user.health != user.maxHealth) return false
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        return true
    }
}
