package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.SlashAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

@Suppress("SameParameterValue")
open class EmpoweredSlashAugment: SlashAugment(ScepterTier.TWO,5) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(7.5F,1.5F,0.0F)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,20,20,
            16,imbueLevel,1,LoreTier.LOW_TIER, RegisterItem.GARNET_SWORD)
    }
    
    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<Entity>{
        val hostileEntityList: MutableList<Entity> = mutableListOf()
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity !== user) {
                    if (entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user, entity,this)) continue
                    hostileEntityList.add(entity)
                }
            }
        }
        return hostileEntityList
    }

    override fun particleType(): DefaultParticleType{
        return ParticleTypes.CRIT
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_ATTACK_CRIT
    }
}
