package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class MassCleanseAugment: MiscAugment(ScepterTier.TWO,5){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(200,100)
            .withRange(7.0,1.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,1200,120,
            16,imbueLevel,30,LoreTier.LOW_TIER, Items.MILK_BUCKET)
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
            if(target !is Monster){
                if (target is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,target,this)) continue
                entityTask(world,target,user,level.toDouble(),null, effect)
                successes++
            }
        }
        if (!user.hasStatusEffect(RegisterStatus.IMMUNITY)){
            RegisterEnchantment.CLEANSE.supportEffect(world,null,user,level, effect)
            successes++
        }
        return successes > 0
    }

    override fun entityTask(
        world: World,
        target: Entity,
        user: LivingEntity,
        level: Double,
        hit: HitResult?,
        effects: AugmentEffect
    ) {
        RegisterEnchantment.CLEANSE.supportEffect(world,target,user,level.toInt(),effects)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON
    }
}
