package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.*
import me.fzzyhmstrs.amethyst_core.entity.MissileEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
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
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

/*
    Checklist
     */

class SoulMissileAugment: ProjectileAugment(ScepterTier.ONE,){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("soul_missile"),SpellType.FURY,16,3,
            1,21,1,1,LoreTier.NO_TIER,Items.SOUL_SAND)

    //ml 21
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.9F,0.1F,0.0F).withRange(2.0)

    override fun <T> canTarget(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        user: T,
        hand: Hand,
        spells: PairedAugments
    ): Boolean where T : SpellCastingEntity, T : LivingEntity {
        return SpellHelper.hostileTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (other == RegisterEnchantment.WITHERING_BOLT){
            description.addLang("enchantment.amethyst_imbuement.soul_missile.withering_bolt", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.EXPLODES))
            return
        }
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.soul_missile.desc.damageSource", SpellAdvancementChecks.DAMAGE_SOURCE)
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.soul_missile.desc.positiveEffect", SpellAdvancementChecks.ENTITY_EFFECT)
        if (othersType.negativeEffect)
            description.addLang("enchantment.amethyst_imbuement.soul_missile.desc.negativeEffect", SpellAdvancementChecks.ENTITY_EFFECT)
        if (othersType.has(AugmentType.EXPLODES))
            description.addLang("enchantment.amethyst_imbuement.soul_missile.desc.explodes", SpellAdvancementChecks.EXPLODES)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_SOURCE_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.SOUL_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.WITHERING_BOLT ->
                AcText.translatable("enchantment.amethyst_imbuement.soul_missile.withering_bolt.desc")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        if (spells.spellsAreEqual())
            return damage.plus(0f,0f,20f)
        return damage
    }

    override fun <T> modifyDamageSource(builder: DamageSourceBuilder, context: ProcessContext, entityHitResult: EntityHitResult, source: Entity?, user: T, world: World, hand: Hand, level: Int, effects: AugmentEffect, othersType: AugmentType, spells: PairedAugments)
    :
    DamageSourceBuilder
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        return builder.add(DamageTypes.MAGIC)
    }

    
    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity): DamageSourceBuilder {
        return super.damageSourceBuilder(world, source, attacker).set(DamageTypes.MAGIC)
    }
    
    override fun <T> createProjectileEntities(world: World, context: ProcessContext, user: T, level: Int, effects: AugmentEffect, spells: PairedAugments, count: Int)
    : 
    List<ProjectileEntity>
    where 
    T: LivingEntity,
    T: SpellCastingEntity
    {
        val list: MutableList<ProjectileEntity> = mutableListOf()
        for (i in 1..count){
            val me = MissileEntity(world, user)
            val direction = user.rotationVec3d
            me.setVelocity(direction.x,direction.y,direction.z, 2.0f, 0.1f)
            me.passEffects(spells,effects,level)
            me.passContext(context)
            list.add(me)
        }
        return list
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
        if (spells.spellsAreEqual()){
            return burst(world, entityHitResult, context, source, user, level, effects, spells)
        }
        if (othersType.positiveEffect)
            entityHitResult.addStatus(RegisterStatus.SOUL_SHIELD,effects.duration(level), min(effects.amplifier(level),4))
        if (othersType.negativeEffect)
            entityHitResult.addStatus(RegisterStatus.SOUL_SHIELD,effects.duration(level), max((effects.amplifier(level) + 1) * -1,-5))
        return SUCCESSFUL_PASS
    }

     override fun <T> onBlockHit(
         blockHitResult: BlockHitResult,
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
    T: LivingEntity,
    T: SpellCastingEntity
    {
        val result = super.onBlockHit(blockHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (spells.spellsAreEqual()){
            return burst(world, blockHitResult, context, source, user, level, effects, spells)
        }
        return SUCCESSFUL_PASS
    }

    private fun <T> burst(world: World, hitResult: HitResult, context: ProcessContext, source: Entity?, user: T, level: Int, effects: AugmentEffect, spells: PairedAugments): SpellActionResult
    where 
    T: LivingEntity,
    T: SpellCastingEntity
    {
        val range = effects.range(level)
        val pos = hitResult.pos
        val box = Box(pos.x + range, pos.y + range, pos.z + range, pos.x - range, pos.y - range, pos.z - range)
        val entities = (if (hitResult is EntityHitResult) world.getOtherEntities(hitResult.entity, box) else world.getOtherEntities(null, box)).stream().filter{ it !== user && it is LivingEntity }.map {it -> it as LivingEntity}
        var successes = 0
        var killed = false
        entities.forEach { entity ->
            val hit = EntityHitResult(entity)
            val damageSource = spells.provideDamageSource(context, hit, source, user, world, Hand.MAIN_HAND, level, effects)
            val amount = 0.4f * spells.provideDealtDamage(effects.damage(level), context, hit, user, world, Hand.MAIN_HAND, level, effects)
            val bl = entity.damage(damageSource, amount)
            if (bl){
                if (!entity.isAlive) killed = true
                val pos2 = source?.pos?:entity.pos.add(0.0,entity.height/2.0,0.0)
                splashParticles(hit,world,pos2.x,pos2.y,pos2.z,spells)
                user.applyDamageEffects(user,entity)
                successes++
            }
        }
        return if (successes > 0) {
            if (killed) {
                SpellActionResult.success(AugmentHelper.DAMAGED_MOB, AugmentHelper.KILLED_MOB)
            } else {
                SpellActionResult.success(AugmentHelper.DAMAGED_MOB)
            }
        } else {
            SUCCESSFUL_PASS
        }
    }

    override fun modifyExplosion(
        builder: ExplosionBuilder,
        context: ProcessContext,
        user: LivingEntity?,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): ExplosionBuilder {
        if (spells.primary() == RegisterEnchantment.WITHERING_BOLT)
            return builder.modifyPower{power -> power * 1.25f}.modifyDamageSource{source -> source.add(DamageTypes.SONIC_BOOM)}
        return builder.modifyDamageSource {source -> source.add(DamageTypes.MAGIC)}
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SOUL
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SOUL
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.PLAYERS, 0.5f, 1f)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.PLAYERS, 1f, 1f)
    }
}
