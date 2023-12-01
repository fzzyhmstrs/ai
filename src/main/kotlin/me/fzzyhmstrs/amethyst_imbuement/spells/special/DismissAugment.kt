package me.fzzyhmstrs.amethyst_imbuement.spells.special

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class DismissAugment: MiscAugment(ScepterTier.ONE,1){

    override val baseEffect: AugmentEffect = super.baseEffect.withRange(16.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,20,1,
            1,imbueLevel,0,LoreTier.NO_TIER, Items.STICK)
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0
        for (target in entityList) {
            if(target is PlayerCreatedConstructEntity && target.owner == user) {
                target.kill()
                successes++
            }
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE
    }
}
