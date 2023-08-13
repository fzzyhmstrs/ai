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
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addEffect
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class IceSpikesAugment: ScepterAugment(ScepterTier.TWO, AugmentType.DIRECTED_ENERGY){
    //ml 5
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("ice_spikes"),SpellType.FURY,32,14,
            8, 5,1,2, LoreTier.NO_TIER, Items.BLUE_ICE)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(11,1,0)
            .withDamage(5.25F,0.25F)
            .withDuration(225,25)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
        if (othersType.has(AugmentType.DAMAGE)) {
            if (othersType.has(AugmentType.PROJECTILE))
                description.addLang("enchantment.amethyst_imbuement.ice_spikes.desc.damage", SpellAdvancementChecks.DAMAGE.or(SpellAdvancementChecks.PROJECTILE))
            description.addLang("enchantment.amethyst_imbuement.ice_spikes.desc.damage2", SpellAdvancementChecks.DAMAGE)
        }
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.ice_spikes.desc.thorns", SpellAdvancementChecks.HARMED_EFFECT)
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.ice_spikes.desc.thorns2", SpellAdvancementChecks.SUMMONS)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.DAMAGE_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.ICE_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity?): DamageSourceBuilder {
        return super.damageSourceBuilder(world, source, attacker).set(DamageTypes.FREEZE)
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
        if (entityHitResult.entity.isFireImmune && othersType.has(AugmentType.PROJECTILE))
            return amount * 1.75f
        return amount
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
                val entity = entityHitResult.entity
                if (entity is LivingEntity){
                    entity.frozenTicks = effects.duration(level)
                }
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
        if (othersType.has(AugmentType.DAMAGE)){
            val entity = entityHitResult.entity
            if (entity is LivingEntity){
                if (entity.isFireImmune) {
                    entity.frozenTicks = 540
                } else {
                    entity.frozenTicks = 180
                }
            }
        }
        if (othersType.positiveEffect){
            entityHitResult.addEffect(ModifiableEffectEntity.ON_DAMAGED,ModifiableEffects.ICE_SPIKES_BOOST_EFFECT,effects.duration(level))
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
    ): List<Entity> where T : ModifiableEffectEntity, U : SpellCastingEntity, T : Entity, U : LivingEntity {
        for (summon in summons){
            summon.addEffect(ModifiableEffectEntity.ON_DAMAGED, ModifiableEffects.ICE_SPIKES_SUMMON_EFFECT)
        }
        return summons
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }
}
