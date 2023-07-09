package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.*
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.BallLightningEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.pieces.explosion_behaviors.StunningExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BallLightningAugment: ProjectileAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint
        get() = AugmentDatapoint(Identifier(AI.MOD_ID,"ball_lightning"),SpellType.FURY,80,
            25, 14,8, 1,3, LoreTier.LOW_TIER, Items.COPPER_BLOCK)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(6.4F,0.2F,0.0F)
            .withDuration(24,-1)
            .withRange(3.0,.25)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.damage")
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.damage_type")
        }
        description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.duration")
        if (othersType.has(AugmentType.SUMMONS) && !othersType.has(AugmentType.BENEFICIAL)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.summon")
        }
        if (othersType.has(AugmentType.PROJECTILE) && !othersType.has(AugmentType.BENEFICIAL)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.projectile")
        }
        if (othersType.positiveEffect){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.positiveEffect")
        }
        if (othersType.has(AugmentType.EXPLODES)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.explode")
        }
    }

    override fun doubleNameDesc(): List<MutableText> {
        return listOf(AcText.translatable("$orCreateTranslationKey.double.desc1"),AcText.translatable("$orCreateTranslationKey.double.desc2"))
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        return if (spells.spellsAreEqual()){
            damage.plus(PerLvlF(0f,0f,-10f))
        } else if (othersType.has(AugmentType.DAMAGE)){
            damage.plus(PerLvlF(0f,0f,-15f))
        } else {
            damage
        }
    }

    override fun modifyDuration(
        duration: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        return duration.plus(PerLvlI(0,0,-15))
    }

    override fun <T> modifyDamageSource(builder: DamageSourceBuilder, context: ProcessContext, entityHitResult: EntityHitResult, source: Entity?, user: T, world: World, hand: Hand, level: Int, effects: AugmentEffect, othersType: AugmentType, spells: PairedAugments)
    :
    DamageSourceBuilder
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        return builder.add(DamageTypes.LIGHTNING_BOLT)
    }

    override fun <T> modifyExplosion(builder: ExplosionBuilder, context: ProcessContext, user: T, world: World, hand: Hand, level: Int, effects: AugmentEffect, othersType: AugmentType, spells: PairedAugments)
    :
    ExplosionBuilder
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        return builder.modifyPower{power -> power * 1.1f}.withCustomBehavior(StunningExplosionBehavior())
    }

    /*override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = 0.1f
        val div = 0.25F
        return BallLightningEntity.createBallLightning(world, user, speed, div, effects, level, this)
    }*/

    override fun castParticleType(): ParticleEffect? {
        return super.castParticleType()
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return super.hitParticleType(hit)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        super.castSoundEvent(world, blockPos, context)
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        super.hitSoundEvent(world, blockPos, context)
    }

    /*override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_POWER_SELECT
    }*/
}
