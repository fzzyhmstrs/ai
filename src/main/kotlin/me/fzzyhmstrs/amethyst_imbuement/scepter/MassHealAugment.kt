package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.augments.MiscAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class MassHealAugment: MiscAugment(ScepterTier.TWO,5){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(7.0,1.0,0.0)
            .withDamage(2.5F,2.5F,0.0F)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,200,50,
            9,imbueLevel,10,LoreTier.LOW_TIER, Items.GLISTERING_MELON_SLICE)
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0
        if (entityList.isEmpty()){
            if (user.health < user.maxHealth){
                successes++
                user.heal(effect.damage(level))
                effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
            }
        } else {
            entityList.add(user)
            for (entity3 in entityList) {
                if (entity3 !is Monster && entity3 is LivingEntity) {
                    if (entity3 is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user, entity3,this)) continue
                    if (entity3.health == entity3.maxHealth) continue
                    successes++
                    entity3.heal(effect.damage(level)/1.25F)
                    effect.accept(entity3,AugmentConsumer.Type.BENEFICIAL)
                }
            }
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_ACTIVATE
    }
}
