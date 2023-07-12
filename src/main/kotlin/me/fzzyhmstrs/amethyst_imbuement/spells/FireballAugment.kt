package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class FireballAugment: ProjectileAugment(ScepterTier.TWO, AugmentType.BALL){
    //ml 5
    override val augmentData: AugmentDatapoint
        get() = AugmentDatapoint(AI.identity("fireball"),SpellType.FURY,32,10,
            10,imbueLevel,2, LoreTier.LOW_TIER, Items.TNT)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(7.75F,0.25f)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val yaw = user.yaw
        val pitch = user.pitch
        val roll = user.roll
        val speed = 4.0F
        val div = 1.0F
        val f = (-MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        val g = (-MathHelper.sin((pitch + roll) * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        val h = (MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(pitch * (Math.PI.toFloat() / 180)) * ((world.random.nextFloat()-0.5F)*div/10 + 1.0F) * speed).toDouble()
        return createFireball(world, user, Vec3d(f,g,h), user.eyePos.subtract(0.0,0.2,0.0),effects, level,this)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_GHAST_SHOOT
    }
}
