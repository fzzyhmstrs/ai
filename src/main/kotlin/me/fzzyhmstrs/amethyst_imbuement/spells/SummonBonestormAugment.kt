package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper.findSpawnPos
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.ExplosionBuilder
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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.BasicShardEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BonestormEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.zombie.BonesEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBoost
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.BonestormExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
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
import java.util.*
import kotlin.math.max

/*
    Checklist
     */

class SummonBonestormAugment: SummonAugment<BonestormEntity>(ScepterTier.TWO){

    private val uuid = UUID.fromString("bb56e04c-2691-11ee-be56-0242ac120002")

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_bonestorm"),SpellType.FURY,2400,320,
            15,12,1,75,LoreTier.LOW_TIER, Items.BONE_BLOCK)

    //ml 12
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(AiConfig.entities.bonestorm.baseHealth.get().toInt(),0,0)
            .withDuration(AiConfig.entities.bonestorm.baseLifespan.get(),AiConfig.entities.bonestorm.perLvlLifespan.get(),0)
            .withDamage(AiConfig.entities.bonestorm.baseDamage.get(),AiConfig.entities.bonestorm.perLvlDamage.get())

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.SUMMON_ZOMBIE -> {
                description.addLang("enchantment.amethyst_imbuement.summon_bonestorm.summon_zombie.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
                return
            }

            RegisterEnchantment.ICE_SHARD ->
                description.addLang("enchantment.amethyst_imbuement.summon_bonestorm.ice_shard.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))

        }
        if (othersType.has(AugmentType.EXPLODES)) {
            description.addLang("enchantment.amethyst_imbuement.summon_bonestorm.desc.explodes1", SpellAdvancementChecks.EXPLODES.or(SpellAdvancementChecks.MANA_COST))
            description.addLang("enchantment.amethyst_imbuement.summon_bonestorm.desc.explodes2", SpellAdvancementChecks.EXPLODES)
        }
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.summon_bonestorm.desc.summons", SpellAdvancementChecks.STAT.or(SpellAdvancementChecks.SUMMONS))
        if (othersType.has(AugmentType.AOE)){
            if(othersType.has(AugmentType.DAMAGE))
                description.addLang("enchantment.amethyst_imbuement.summon_bonestorm.desc.damage", SpellAdvancementChecks.DAMAGE)
            if(othersType.has(AugmentType.BENEFICIAL))
                description.addLang("enchantment.amethyst_imbuement.summon_bonestorm.desc.armor", SpellAdvancementChecks.DAMAGE)
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.SUMMONS_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.MANA_COST_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.EXPLODES_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_BONESTORM ->
                AcText.translatable("enchantment.amethyst_imbuement.summon_bonestorm.summon_zombie")
            RegisterEnchantment.FANGS ->
                AcText.translatable("enchantment.amethyst_imbuement.summon_bonestorm.ice_shard")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.ICE_SHARD)
            return cooldown.plus(5)
        if (othersType.has(AugmentType.AOE) && othersType.has(AugmentType.BENEFICIAL))
            return cooldown.plus(0,0,10)
        return cooldown
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return manaCost.plus(180)
        if (othersType.has(AugmentType.EXPLODES))
            return manaCost.plus(0,0,50)
        return manaCost
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        if (othersType.has(AugmentType.AOE) && othersType.has(AugmentType.DAMAGE))
            return damage.plus(1f)
        if (other == RegisterEnchantment.SUMMON_BONESTORM)
            return PerLvlF(AiConfig.entities.bones.baseDamage.get())
        return super.modifyDamage(damage, other, othersType, spells)
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.ICE_SHARD)
            return amplifier.plus(2)
        return amplifier
    }

    override fun <T> modifyCount(
        start: Int,
        context: ProcessContext,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Int where T : SpellCastingEntity, T : LivingEntity {
        if (spells.primary() == RegisterEnchantment.ICE_SHARD)
            return start + 4
        return start
    }

    override fun entitiesToSpawn(
        world: World,
        user: LivingEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments,
        count: Int
    ): List<BonestormEntity> {
        val startPos = BlockPos.ofFloored(hit.pos)
        val list: MutableList<BonestormEntity> = mutableListOf()
        for (i in 1..count) {
            val boneStorm = BonestormEntity(RegisterEntity.BONESTORM_ENTITY, world, effects.duration(level), user)
            val success = findSpawnPos(world, startPos, boneStorm, 4, 12, user.pitch, user.yaw)
            if (success) {
                boneStorm.passEffects(spells, effects, level)
                list.add(boneStorm)
            }
        }
        return list
    }

    override fun <T> startCount(
        user: T,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Int where T : SpellCastingEntity, T : LivingEntity {
        return 1
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
        if (othersType.has(AugmentType.AOE) && othersType.has(AugmentType.BENEFICIAL)){
            val entity = entityHitResult.entity
            if (entity is LivingEntity){
                entity.addStatusEffect(StatusEffectInstance(RegisterStatus.BONE_ARMOR,600))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (othersType.has(AugmentType.EXPLODES) && source is BasicShardEntity){
            afterSummonEntityEffects(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        }
        return SUCCESSFUL_PASS
    }

    override fun <T> afterSummonEntityEffects(
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
        if (othersType.empty || (othersType.has(AugmentType.EXPLODES) && source is BasicShardEntity)){
            if (!canTarget(entityHitResult, context, world, user, hand, spells)) return FAIL
            val amount = spells.provideDealtDamage(effects.damage(level), context, entityHitResult, user, world, hand, level, effects)
            val damageSource = spells.provideDamageSource(context,entityHitResult, source, user, world, hand, level, effects)
            val bl  = entityHitResult.entity.damage(damageSource, amount)

            return if(bl) {
                val pos = source?.pos?:entityHitResult.entity.pos
                splashParticles(entityHitResult,world,pos.x,pos.y,pos.z,spells)
                user.applyDamageEffects(user,entityHitResult.entity)
                spells.hitSoundEvents(world, entityHitResult.entity.blockPos,context)
                if (entityHitResult.entity.isAlive) {
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB, AugmentHelper.PROJECTILE_HIT)
                } else {
                    spells.processOnKill(entityHitResult, context, world, source, user, hand, level, effects)
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB, AugmentHelper.PROJECTILE_HIT, AugmentHelper.KILLED_MOB)
                }
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T, U> modifySummons(
        summons: List<T>,
        hit: HitResult,
        context: ProcessContext,
        user: U,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): List<Entity> where T : ModifiableEffectEntity,T : Entity, U : SpellCastingEntity, U : LivingEntity {
        if (spells.primary() == RegisterEnchantment.SUMMON_ZOMBIE){
            val count = max(1,summons.size - 1)
            val list: MutableList<BonesEntity> = mutableListOf()
            for (i in 1..count){
                val bone = BonesEntity(RegisterEntity.BONES_ENTITY, world, effects.duration(level), user)
                val success = findSpawnPos(world, BlockPos.ofFloored(hit.pos), bone, 4, 12, user.pitch, user.yaw)
                if (success){
                    bone.passEffects(spells, effects, level)
                    list.add(bone)
                }
                return list
            }
        }
        if (spells.spellsAreEqual()){
            for (summon in summons){
                summon.processContext.set(ContextData.SPEEDY,0.2f)
                if (summon is LivingEntity){
                    val hp = summon.maxHealth
                    val boost = spells.boost()
                    val hpBoost = if (boost == RegisterBoost.ENCHANTED_GOLDEN_APPLE_BOOST) hp/1.0 else hp/2.0
                    val modifier = EntityAttributeModifier(
                        uuid,
                        "Bone Tempest Bonus",
                        hpBoost,
                        EntityAttributeModifier.Operation.ADDITION)
                    if (summon.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.hasModifier(modifier) == true) continue
                    summon.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addPersistentModifier(modifier)
                }
                summon.customName = AcText.translatable("entity.amethyst_imbuement.bonestorm.double")
            }
        } else {
            if (othersType.has(AugmentType.SUMMONS)){
                for (summon in summons){
                    summon.processContext.set(ContextData.SPEEDY,0.2f)
                    if (summon is LivingEntity){
                        val modifier = EntityAttributeModifier(
                            uuid,
                            "Bonestorm Speed Bonus",
                            0.15,
                            EntityAttributeModifier.Operation.ADDITION)
                        if (summon.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.hasModifier(modifier) == true) continue
                        summon.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.addPersistentModifier(modifier)
                    }
                }
            }
        }
        return summons
    }

    override fun <T> modifyExplosion(
        builder: ExplosionBuilder,
        context: ProcessContext,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): ExplosionBuilder where T : SpellCastingEntity,T : LivingEntity {
        return builder.withCustomBehavior(BonestormExplosionBehavior())
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.END_ROD
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.END_ROD
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT,SoundCategory.PLAYERS,1.0f,1.0f)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_WITHER_SKELETON_HURT,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
