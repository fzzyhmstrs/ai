package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonProjectileAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.spell.IceShardEntity
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class IceShardAugment: SummonProjectileAugment(ScepterTier.TWO,6){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(3.6F,0.4F,0.0F)
                                                .withAmplifier(1)
                                                .withDuration(180,20)
                                                .withRange(4.3,0.2)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY, PerLvlI(15,-1),
            15,14, imbueLevel,1, LoreTier.LOW_TIER, Items.BLUE_ICE)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = effects.range(level).toFloat()
        val div = 0.75F
        val ise = IceShardEntity(world,user,speed,div,user.eyePos.subtract(0.0,0.2,0.0),user.rotationVector)
        ise.passEffects(effects, level)
        ise.setAugment(this)
        return ise
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_SNOWBALL_THROW
    }
}
