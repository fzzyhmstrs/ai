package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.SlashAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes

@Suppress("SameParameterValue")
open class SpectralSlashAugment: SlashAugment(ScepterTier.ONE,9){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(4.5F,0.5F,0.0F)
            .withRange(2.625,0.125,0.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,18,4,
            9,imbueLevel,1,LoreTier.LOW_TIER, Items.IRON_SWORD)
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
        return ParticleTypes.ELECTRIC_SPARK
    }
}
