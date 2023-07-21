package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.world.World

class IceShardAugment: ProjectileAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("ice_shard"),SpellType.FURY, PerLvlI(15,-1),
            15,14, 6,1,1, LoreTier.LOW_TIER, Items.BLUE_ICE)

    //ml 6
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(3.6F,0.4F,0.0F)
            .withDuration(180,20)
            .withAmplifier(1)
            .withRange(4.3,0.2)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
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
