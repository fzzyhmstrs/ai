package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.*
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.BallLightningEntity
import me.fzzyhmstrs.amethyst_imbuement.interfaces.ModifiableEffectMobOrPlayer
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.StunningExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/*
Checklist
*/

class BallLightningAugment: ProjectileAugment(ScepterTier.TWO){


    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("ball_lightning"),SpellType.FURY,80,
            25, 14,8, 1,3, LoreTier.LOW_TIER, Items.COPPER_BLOCK)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(6.4F,0.2F,0.0F)
            .withDuration(24,-1)
            .withRange(3.0,.25)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.damage", SpellAdvancementChecks.STAT)
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.damage_type", SpellAdvancementChecks.DAMAGE_SOURCE)
            if (!othersType.has(AugmentType.PROJECTILE)){
                description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.stun", SpellAdvancementChecks.STUNS)
            }
        }
        description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.duration", SpellAdvancementChecks.STAT)
        if (other.augmentData.cooldown.base < 35 && other.augmentData.cooldown.perLevel < 5) {
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.cooldown_small", SpellAdvancementChecks.COOLDOWN)
        } else if (other == RegisterEnchantment.BEDAZZLE) {
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.cooldown_bedazzle", SpellAdvancementChecks.or(SpellAdvancementChecks.COOLDOWN,SpellAdvancementChecks.DOUBLE))
        } else {
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.cooldown_big", SpellAdvancementChecks.COOLDOWN)
        }
        if (othersType.has(AugmentType.SUMMONS) && !othersType.has(AugmentType.BENEFICIAL))
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.summon", SpellAdvancementChecks.or(SpellAdvancementChecks.LIGHTNING, SpellAdvancementChecks.SUMMONS))
        if (othersType.has(AugmentType.PROJECTILE) && !othersType.has(AugmentType.BENEFICIAL))
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.projectile", SpellAdvancementChecks.LIGHTNING)
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.positiveEffect", SpellAdvancementChecks.LIGHTNING)
        if (othersType.has(AugmentType.EXPLODES))
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.explode", SpellAdvancementChecks.or(SpellAdvancementChecks.EXPLODES,SpellAdvancementChecks.STUNS))
        when(other) {
            RegisterEnchantment.BARRIER ->
                description.addLang("enchantment.amethyst_imbuement.ball_lightning.barrier.desc", SpellAdvancementChecks.UNIQUE)
            RegisterEnchantment.CRIPPLE ->
                description.addLang("enchantment.amethyst_imbuement.ball_lightning.cripple.desc", SpellAdvancementChecks.UNIQUE)
            RegisterEnchantment.BEDAZZLE ->
                description.addLang("enchantment.amethyst_imbuement.ball_lightning.bedazzle.desc", SpellAdvancementChecks.UNIQUE)
            RegisterEnchantment.INSPIRING_SONG ->
                description.addLang("enchantment.amethyst_imbuement.ball_lightning.inspiring_song.desc", SpellAdvancementChecks.UNIQUE)
        }
    }

    override fun doubleNameDesc(): List<MutableText> {
        val list: MutableList<Text> = mutableListOf()
        list.addLang("$orCreateTranslationKey.double.desc1",SpellAdvancementChecks.DOUBLE)
        return listOf(AcText.translatable("$orCreateTranslationKey.double.desc1"),AcText.translatable("$orCreateTranslationKey.double.desc2"))
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.BARRIER ->
                AcText.translatable("enchantment.amethyst_imbuement.ball_lightning.barrier")
            RegisterEnchantment.CRIPPLE ->
                AcText.translatable("enchantment.amethyst_imbuement.ball_lightning.cripple")
            RegisterEnchantment.BEDAZZLE ->
                AcText.translatable("enchantment.amethyst_imbuement.ball_lightning.bedazzle")
            RegisterEnchantment.INSPIRING_SONG ->
                AcText.translatable("enchantment.amethyst_imbuement.ball_lightning.inspiring_song")
            else ->
                return super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player,SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player,SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.STAT_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.DAMAGE_SOURCE_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.LIGHTNING_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.STUNNED_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        return if(cooldown.base < 35 && cooldown.perLevel < 5){
            cooldown.plus(10,0,0)
        } else if (other == RegisterEnchantment.BEDAZZLE) {
            cooldown.plus(300)
        } else {
            cooldown.plus(0,0,10)
        }
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        return if (spells.spellsAreEqual()){
            damage.plus(0f,0f,-10f)
        } else if (othersType.has(AugmentType.DAMAGE)){
            damage.plus(0f,0f,-15f)
        } else {
            damage
        }
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.BARRIER)
            return amplifier.plus(5)
        return amplifier
    }

    override fun modifyDuration(
        duration: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return duration.plus(0,0,-35)
        if (other == RegisterEnchantment.BARRIER)
            return duration.plus(0,0,25)
        if (other == RegisterEnchantment.INSPIRING_SONG)
            return duration.plus(0,-50,0)
        return duration.plus(0,0,-15)
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (spells.spellsAreEqual() || other == RegisterEnchantment.CRIPPLE)
            return range.plus(0.0,0.0,25.0)
        return range
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
        val livingEntity = entityHitResult.entity
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (spells.primary() == RegisterEnchantment.BARRIER){
            if (user is ModifiableEffectMobOrPlayer){
                user.amethyst_imbuement_addTemporaryEffect(ModifiableEffectEntity.ON_DAMAGED,ModifiableEffects.STATIC_SHOCK_EFFECT,effects.duration(level))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        } else if (spells.primary() == RegisterEnchantment.CRIPPLE){
            val amount = spells.provideDealtDamage(effects.damage(level), context, entityHitResult, user, world, hand, level, effects)
            val damageSource = spells.provideDamageSource(context, entityHitResult, source, user, world, hand, level, effects)
            val bl  = entityHitResult.entity.damage(damageSource, amount)
            if (bl) {
                val critical = context.get(ContextData.CRIT)
                if (critical && livingEntity is LivingEntity) {
                    livingEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED, effects.duration(level)))
                    livingEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.CURSED, effects.duration(level), 1))
                }
                user.applyDamageEffects(user,entityHitResult.entity)
                spells.hitSoundEvents(world, entityHitResult.entity.blockPos,context)
                return if (entityHitResult.entity.isAlive) {
                    SpellActionResult.overwrite(AugmentHelper.DAMAGED_MOB, AugmentHelper.SLASHED)
                } else {
                    spells.processOnKill(entityHitResult,context, world, source, user, hand, level, effects)
                    SpellActionResult.overwrite(AugmentHelper.DAMAGED_MOB, AugmentHelper.SLASHED, AugmentHelper.KILLED_MOB)
                }
            }
        } else if (spells.primary() == RegisterEnchantment.INSPIRING_SONG){
            if (livingEntity is ModifiableEffectEntity) {
                livingEntity.addTemporaryEffect(ModifiableEffectEntity.TICK, ModifiableEffects.SHOCKING_EFFECT, effects.duration(level))
                (livingEntity as? LivingEntity)?.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE,effects.duration(level)))
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            } else if (livingEntity is ModifiableEffectMobOrPlayer) {
                livingEntity.amethyst_imbuement_addTemporaryEffect(ModifiableEffectEntity.TICK, ModifiableEffects.SHOCKING_EFFECT, effects.duration(level))
                return SpellActionResult.overwrite(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        if (othersType.has(AugmentType.DAMAGE) && !othersType.has(AugmentType.PROJECTILE)){
            if (AI.aiRandom().nextFloat() < 0.1f && livingEntity is LivingEntity){
                livingEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,60))
                return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            }
        }
        if (othersType.positiveEffect){
            if (livingEntity is ModifiableEffectEntity) {
                livingEntity.addTemporaryEffect(ModifiableEffectEntity.TICK, ModifiableEffects.SHOCKING_EFFECT, 400)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            } else if (livingEntity is ModifiableEffectMobOrPlayer) {
                livingEntity.amethyst_imbuement_addTemporaryEffect(ModifiableEffectEntity.TICK, ModifiableEffects.SHOCKING_EFFECT, 400)
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
        if (spells.primary() != RegisterEnchantment.BEDAZZLE) return SUCCESSFUL_PASS
        val entityList = RaycasterUtil.raycastEntityArea(effects.range(level), user)
        if (entityList.isEmpty()) return FAIL
        for (entity in entityList){
            if (entity is LivingEntity && entity !is VillagerEntity){
                val stunDuration = entity.getStatusEffect(RegisterStatus.CHARMED)?.duration ?: (effects.duration(level)/1.5)
                entity.removeStatusEffect(RegisterStatus.CHARMED)
                entity.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,stunDuration.toInt()))
            }
        }
        return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity): DamageSourceBuilder {
        return super.damageSourceBuilder(world, source, attacker).set(DamageTypes.LIGHTNING_BOLT)
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

    override fun <T, U> modifyProjectile(
        projectile: T,
        context: ProcessContext,
        user: U,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
    :
    T
    where
    T : ModifiableEffectEntity,
    T : Entity,
    U : SpellCastingEntity,
    U : LivingEntity
    {
        projectile.addTemporaryEffect(ModifiableEffectEntity.TICK,ModifiableEffects.SHOCKING_EFFECT,600)
        projectile.addEffect(ModifiableEffectEntity.ON_REMOVED,ModifiableEffects.SHOCKING_EFFECT)
        return projectile
    }

    override fun <T, U> modifySummons(
        summons: List<T>,
        context: ProcessContext,
        user: U,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
    :
    List<T>
    where
    T : ModifiableEffectEntity,
    T : Entity,
    U : SpellCastingEntity,
    U : LivingEntity
    {
        for (summon in summons){
            summon.addEffect(ModifiableEffectEntity.TICK,ModifiableEffects.SHOCKING_EFFECT)
            summon.addEffect(ModifiableEffectEntity.ON_REMOVED,ModifiableEffects.SHOCKING_EFFECT)
        }
        return summons
    }

    override fun <T> createProjectileEntities(
        world: World,
        context: ProcessContext,
        user: T,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    )
    :
    List<ProjectileEntity>
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val dir = user.rotationVec3d
        val pos = user.eyePos.subtract(0.0,0.5,0.0).add(dir.multiply(0.75))
        val ble = BallLightningEntity(world,user,dir,1.0f,0.25f,pos)
        ble.passEffects(spells,effects, level)
        ble.passContext(context)
        return listOf(ble)
    }

    override fun castParticleType(): ParticleEffect? {
        return null
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        if (context.isBeforeRemoval()) {
            world.playSound(null, blockPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
            return
        }
        world.playSound(null,blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)

    }
}
