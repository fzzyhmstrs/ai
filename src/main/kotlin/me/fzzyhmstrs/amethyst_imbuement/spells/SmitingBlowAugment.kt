package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addEffect
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SmitingBlowAugment: SingleTargetAugment(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("smiting_blow"),SpellType.FURY,20,8,
            10, 5,1,2, LoreTier.LOW_TIER, RegisterItem.GLOWING_FRAGMENT)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(4.5F,0.5F)
            .withRange(7.5,0.5)
            .withAmplifier(2)

    override fun <T> canTarget(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        user: T,
        hand: Hand,
        spells: PairedAugments
    ): Boolean where T : SpellCastingEntity,T : LivingEntity {
        return SpellHelper.hostileTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.smiting_blow.desc.damage", SpellAdvancementChecks.DAMAGE)
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.smiting_blow.desc.shield", SpellAdvancementChecks.SOUL.or(SpellAdvancementChecks.ENTITY_EFFECT))
        if (othersType.negativeEffect)
            description.addLang("enchantment.amethyst_imbuement.smiting_blow.desc.curse", SpellAdvancementChecks.SOUL.or(SpellAdvancementChecks.ENTITY_EFFECT))
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.DAMAGE_TRIGGER)
    }

    override fun <T> modifyDealtDamage(
        amount: Float,
        context: ProcessContext,
        entityHitResult: EntityHitResult,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Float where T : SpellCastingEntity, T : LivingEntity {
        val entity = entityHitResult.entity
        if (entity is LivingEntity && entity.group == EntityGroup.UNDEAD && !context.get(ContextData.CRIT))
            return amount * if (othersType.empty) effects.amplifier(level).toFloat() else  1.5f
        return amount
    }

    override fun <T> onEntityHit(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ):
            SpellActionResult
            where
            T: LivingEntity,
            T: SpellCastingEntity
    {
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (othersType.positiveEffect){
            entityHitResult.addEffect(ModifiableEffectEntity.ON_DAMAGED,ModifiableEffects.SHIELD_OF_FAITH_EFFECT,effects.duration(level))
        }
        if (othersType.negativeEffect){

            entityHitResult.addStatus(RegisterStatus.CURSED,effects.duration(level),1)
        }
        return SUCCESSFUL_PASS
    }

    override fun <T> entityEffects(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        if (othersType.empty){
            if (!canTarget(entityHitResult, context, world, user, hand, spells)) return FAIL
            val damage = spells.provideDealtDamage(effects.damage(level),context, entityHitResult, user, world, hand, level, effects)
            val damageSource = spells.provideDamageSource(context, entityHitResult, source, user, world, hand, level, effects)
            val bl = entityHitResult.entity.damage(damageSource,damage)
            if (bl){
                val pos = entityHitResult.entity.eyePos
                splashParticles(entityHitResult,world,pos.x,pos.y + 0.25,pos.z,spells)
                splashParticles(entityHitResult,world,pos.x,pos.y,pos.z,spells)
                splashParticles(entityHitResult,world,pos.x,pos.y - 0.25,pos.z,spells)
                user.applyDamageEffects(user,entityHitResult.entity)
                if (entityHitResult.entity.isAlive) {
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB)
                } else {
                    spells.processOnKill(entityHitResult, context, world, source, user, hand, level, effects)
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB, AugmentHelper.KILLED_MOB)
                }
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SPIT
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SPIT
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_ELDER_GUARDIAN_HURT,SoundCategory.PLAYERS,1f,1f)
    }
}
