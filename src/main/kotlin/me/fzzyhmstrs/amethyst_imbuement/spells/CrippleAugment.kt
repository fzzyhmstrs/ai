package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.MultiTargetAugment
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CrippleAugment: MultiTargetAugment(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint
        get() = AugmentDatapoint(Identifier(AI.MOD_ID,"cripple"),SpellType.FURY,20,5,
            13,13,1,1, LoreTier.NO_TIER, Items.STONE_SWORD)

    //maxlvl 13
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.4F,0.2F,0.0F)
            .withRange(7.75,0.25,0.0)
            .withDuration(110,10)
            .withAmplifier(-1,1,0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {

        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideVerb(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.resonate.fortify")
            RegisterEnchantment.INSPIRING_SONG ->
                AcText.translatable("enchantment.amethyst_imbuement.resonate.inspiring_song")
            else ->
                return super.specialName(otherSpell)
        }
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
    )
    :
    Float
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val critical = if (context.get(ProcessContext.FROM_ENTITY) || context.get(SummonAugment.SUMMONED_MOB)){
            world.random.nextFloat() < 0.15
        } else {
            context.get(ContextData.CRIT)
        }

        val criticalAmount = if (critical) {
            if (othersType.empty) 2f else 1.5f
        } else {
            1f
        }
        return amount * criticalAmount
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
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (!result.success())
            return result
        if (othersType.empty || othersType.has(AugmentType.DAMAGE)){
            val target = entityHitResult.entity
            if (target is LivingEntity) {
                val amp = if (context.get(ContextData.CRIT)) {
                    if(othersType.empty) 4 else 2
                } else {
                    1
                }
                target.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS, effects.duration(level), amp))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, effects.duration(level), amp))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, effects.duration(level), (amp + 1) * -1))
                return SpellActionResult.success(result).withResults(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            }
        }
        if (othersType.positiveEffect){
            val target = entityHitResult.entity
            if (target is LivingEntity) {
                target.removeStatusEffect(StatusEffects.SLOWNESS)
                target.removeStatusEffect(StatusEffects.WEAKNESS)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }




        return SUCCESSFUL_PASS
    }

    override fun <T> onCast(
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
            :
            SpellActionResult
            where
            T : SpellCastingEntity,
            T : LivingEntity
    {
        if (world.random.nextFloat() < 0.15) {
            context.set(ContextData.CRIT, true)
        }
        return SUCCESSFUL_PASS
    }

    /*override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int, effect: AugmentEffect): Boolean {
        val entityDistance: SortedMap<Double, Entity> = mutableMapOf<Double, Entity>().toSortedMap()
        for (entity in entityList){
            if (entity is MobEntity){
                val dist = entity.squaredDistanceTo(user)
                entityDistance[dist] = entity
            }
        }
        var bl = false
        if (entityDistance.isNotEmpty()) {
            val entityDistance2 = entityDistance.toList()
            val entity1 = entityDistance2[0].second
            bl = critTarget(world,user,entity1,level, effect)
            var nextTarget = 1
            while (entityDistance.size > nextTarget && effect.amplifier(level) > nextTarget){
                val entity2 = entityDistance2[nextTarget].second
                bl = bl || critTarget(world, user, entity2, level, effect, true)
                nextTarget++
            }
            if (bl){
                effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
            }
        }
        return bl
    }*/

    /*private fun critTarget(world: World,user: LivingEntity,target: Entity,level: Int,effect: AugmentEffect, splash: Boolean = false): Boolean{
        val crit = if (world.random.nextFloat() < 0.15) 2f else 1f
        val damage = if(!splash) {
            effect.damage(level) * crit
        } else {
            effect.damage(level)/2 * crit
        }
        val bl = target.damage(if (user is PlayerEntity) user.damageSources.playerAttack(user) else user.damageSources.mobAttack(user), damage)
        if (bl) {
            if (user is ServerPlayerEntity) {
                ServerPlayNetworking.send(user, NOTE_BLAST, ResonateAugment.writeBuf(user, target))
            }
            if (target is LivingEntity) {
                effect.accept(target, AugmentConsumer.Type.HARMFUL)
                val amp = if (crit > 1f) 4 else 1
                target.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS, effect.duration(level), amp))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, effect.duration(level), amp))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, effect.duration(level), (amp + 1) * -1))
            }
        }
        return bl
    }*/

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.EVENT_RAID_HORN.value(), SoundCategory.PLAYERS,1.0f,1.0f)
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,0.4f,0.8f + world.random.nextFloat() * 0.4f)
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.CRIT
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.CRIT
    }

    override fun particleSpeed(): Double {
        return 3.0
    }
}
