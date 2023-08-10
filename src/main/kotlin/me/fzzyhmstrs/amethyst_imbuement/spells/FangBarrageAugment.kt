package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ApplyTaskAugmentData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class FangBarrageAugment: ScepterAugment(ScepterTier.THREE, AugmentType.DIRECTED_ENERGY), PersistentEffectHelper.PersistentEffect {
    //ml 6
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("fang_barrage"),SpellType.FURY,100,50,
            26,6,1,2, LoreTier.HIGH_TIER, Items.EMERALD_BLOCK)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(28,0,0)
            .withAmplifier(9,1,0)
            .withDamage(5.8F,0.2F)

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
        if (othersType.has(AugmentType.PROJECTILE) && othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.projectile", SpellAdvancementChecks.DAMAGE)
        if (othersType.has(AugmentType.AOE) && othersType.has(AugmentType.ENTITY))
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.range", SpellAdvancementChecks.RANGE)
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.damage", SpellAdvancementChecks.DAMAGE_SOURCE)
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.retaliation", SpellAdvancementChecks.DAMAGE.or(SpellAdvancementChecks.PROTECTED_EFFECT))
        when(other) {
            RegisterEnchantment.SOUL_MISSILE -> {
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.soul_missile.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.soul_missile.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))

            }
            RegisterEnchantment.MASS_FORTIFY ->
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.mass_fortify.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.BOOSTED_EFFECT))
            RegisterEnchantment.FLAMEWAVE ->
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.flamewave.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SOUL_MISSILE ->
                AcText.translatable("enchantment.amethyst_imbuement.fang_barrage.soul_missile")
            RegisterEnchantment.MASS_FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.fang_barrage.mass_fortify")
            RegisterEnchantment.FLAMEWAVE ->
                AcText.translatable("enchantment.amethyst_imbuement.fang_barrage.flamewave")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity): DamageSourceBuilder {
        return DamageSourceBuilder(world, attacker, source).set(DamageTypes.MAGIC)
    }

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        val result = conjureBarrage(user, world, effects, level, spells, context)
        if (!result.success() && !onCastResults.acted()) return FAIL
        spells.castSoundEvents(world,user.blockPos,context)
        val data = ApplyTaskAugmentData(world, context, user, hand, level, effects, spells)
        PersistentEffectHelper.setPersistentTickerNeed(this,delay.value(level), effects.duration(level), data)
        return result.withResults(onCastResults.results())
    }

    private fun conjureBarrage(user: LivingEntity, world: World, effects: AugmentEffect, level: Int, spells: PairedAugments, context: ProcessContext): SpellActionResult{
        var successes = 0
        val angles = if (spells.spellsAreEqual()) arrayOf(-11,0,11) else arrayOf(-11,0,11)
        for (a in angles) {
            var d: Double
            var e: Double

            d = user.y
            e = d + 2.0

            val f = (user.yaw + 90 + a) * MathHelper.PI / 180
            for (i in 0..effects.range(level).toInt()) {
                val g = 1.25 * (i + 1).toDouble()
                val success = PlayerFangsEntity.conjureFang(
                    world,
                    user,
                    user.x + MathHelper.cos(f).toDouble() * g,
                    user.z + MathHelper.sin(f).toDouble() * g,
                    d,
                    e,
                    f,
                    i,
                    effects,
                    level,
                    spells
                )
                if (success != Double.NEGATIVE_INFINITY) {
                    successes++
                    d = success
                    e = d + 2.0
                }
            }
        }
        val bl = successes > 0
        if (bl){
            spells.castSoundEvents(world,user.blockPos,context)
        }
        return if(bl) SpellActionResult.success(AugmentHelper.SUMMONED_MOB) else FAIL
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
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        if (othersType.empty){
            if (!canTarget(entityHitResult, context, world, user, hand, spells)) return SUCCESSFUL_PASS
            val amount = spells.provideDealtDamage(effects.damage(level), context, entityHitResult, user, world, hand, level, effects)
            val damageSource = spells.provideDamageSource(context, entityHitResult, source, user, world, hand, level, effects)
            val bl  = entityHitResult.entity.damage(damageSource, amount)
            return if(bl) {
                val pos = source?.pos?:entityHitResult.entity.pos
                splashParticles(entityHitResult,world,pos.x,pos.y,pos.z,spells)
                user.applyDamageEffects(user,entityHitResult.entity)
                spells.hitSoundEvents(world, entityHitResult.entity.blockPos, context)
                val list: MutableList<Identifier> = mutableListOf()
                if (entityHitResult.entity.isAlive) {
                    list.add(AugmentHelper.DAMAGED_MOB)
                    SpellActionResult.success(list)
                } else {
                    list.add(AugmentHelper.DAMAGED_MOB)
                    list.add(AugmentHelper.KILLED_MOB)
                    SpellActionResult.success(list)
                }
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override val delay = PerLvlI(15,-1,0)

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is ApplyTaskAugmentData<*>) return
        if (data.user !is LivingEntity) return
        conjureBarrage(data.user,data.world,data.effects,data.level,data.spells,data.context)
    }

}
