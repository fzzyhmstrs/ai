package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
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
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

/*
    Checklist
     */

class FangsAugment: ScepterAugment(ScepterTier.TWO, AugmentType.DIRECTED_ENERGY) {
    //ml 6
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("fangs"),SpellType.FURY,34,12,
            9,6,1,1, LoreTier.LOW_TIER, Items.EMERALD)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withRange(11.0,1.0,0.0)
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
            description.addLang("enchantment.amethyst_imbuement.fangs.desc.projectile", SpellAdvancementChecks.DAMAGE)
        if (othersType.has(AugmentType.AOE) && othersType.has(AugmentType.ENTITY))
            description.addLang("enchantment.amethyst_imbuement.fangs.desc.range", SpellAdvancementChecks.RANGE)
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.fangs.desc.damage", SpellAdvancementChecks.DAMAGE_SOURCE)
        when(other) {
            RegisterEnchantment.SUMMON_FURY_TOTEM -> {
                description.addLang("enchantment.amethyst_imbuement.fangs.summon_fury_totem.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
                description.addLang("enchantment.amethyst_imbuement.fangs.summon_fury_totem.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))

            }
            RegisterEnchantment.FORTIFY ->
                description.addLang("enchantment.amethyst_imbuement.fangs.fortify.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.BOOSTED_EFFECT))
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.RANGE_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_SOURCE_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_FURY_TOTEM ->
                AcText.translatable("enchantment.amethyst_imbuement.fangs.summon_fury_totem")
            RegisterEnchantment.FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.fangs.fortify")
            else ->
                super.specialName(otherSpell)
        }
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
        var successes = 0

        val angles = if (spells.spellsAreEqual()) arrayOf(-9,9) else arrayOf(0)
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
        return if(successes > 0) SpellActionResult.success(AugmentHelper.SUMMONED_MOB) else FAIL
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (other == RegisterEnchantment.SUMMON_FURY_TOTEM)
            return range.plus(2.5,0.5)
        if (othersType.has(AugmentType.AOE) && othersType.has(AugmentType.ENTITY))
            return range.plus(0.0,0.0,20.0)
        return range
    }

    override fun <T> modifyDamageSource(
        builder: DamageSourceBuilder,
        context: ProcessContext,
        entityHitResult: EntityHitResult,
        source: Entity?,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): DamageSourceBuilder where T: SpellCastingEntity,T: LivingEntity {
        return builder.add(DamageTypes.MAGIC)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity): DamageSourceBuilder {
        return DamageSourceBuilder(world, attacker, source).set(DamageTypes.MAGIC)
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
        if (spells.primary() == RegisterEnchantment.FORTIFY){
            val entity = entityHitResult.entity
            if (entity is LivingEntity) {
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE, effects.duration(level), 0))
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.WATER_BREATHING, effects.duration(level), 0))
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, effects.duration(level), 0))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }

        if (othersType.has(AugmentType.PROJECTILE) && othersType.has(AugmentType.DAMAGE)){
            val d: Double = entityHitResult.pos.y
            val e = d + 2.0
            val x = entityHitResult.pos.x
            val z = entityHitResult.pos.z
            val success = PlayerFangsEntity.conjureFang(world,user,x,z,d,e,user.yaw,12,effects,level,spells)
            if (success != Double.NEGATIVE_INFINITY){
                return SpellActionResult.success(AugmentHelper.SUMMONED_MOB)
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
    ): List<Entity> where T : ModifiableEffectEntity,T : Entity, U : SpellCastingEntity,U : LivingEntity {
        if (spells.primary() == RegisterEnchantment.SUMMON_FURY_TOTEM) {
            val x = hit.pos.x + 0.5
            val z = hit.pos.z + 0.5
            val y = hit.pos.y + 1.0
            val tofe = TotemOfFangsEntity(RegisterEntity.TOTEM_OF_FANGS_ENTITY,world,user,effects.duration(level))
            tofe.refreshPositionAndAngles(x, y, z,0.0f,0.0f)
            tofe.passEffects(spells,effects,level)
            tofe.passContext(context)
            return listOf(tofe)
        }
        return summons
    }


    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }
}
