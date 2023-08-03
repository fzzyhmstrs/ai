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
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.world.World

class SoulMissileAugment: ProjectileAugment(ScepterTier.ONE,){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("soul_missile"),SpellType.FURY,16,3,
            1,21,1,1,LoreTier.NO_TIER,Items.SOUL_SAND)

    //ml 21
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.9F,0.1F,0.0F).withRange(2.0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.soul_missile.desc.damageSource", SpellAdvancementChecks.DAMAGE_SOURCE)
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.soul_missile.desc.positiveEffect", SpellAdvancementChecks.ENTITY_EFFECT)
        if (othersType.negativeEffect)
            description.addLang("enchantment.amethyst_imbuement.soul_missile.desc.negativeEffect", SpellAdvancementChecks.ENTITY_EFFECT)
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
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_SOURCE_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        if (spells.spellsAreEqual()){
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
    
    open fun <T> createProjectileEntities(world: World, context: ProcessContext, user: T, level: Int = 1, effects: AugmentEffect, spells: PairedAugments, count: Int)
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
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (spells.spellsAreEqual()){
            return burst(world, blockHitResult, context, source, user, level, effects, spells)
        }
    }

    private fun <T> burst(world: World, hitResult: HitResult: Vec3d, context: ProcessContext, source: Entity?, user: T, level: Int, effects: AugmentEffect, spells: PairedAugments): SpellActionResult
    where 
    T: LivingEntity,
    T: SpellCastingEntity
    {
        val range = effects.range(level)
        val pos = hitResult.pos
        val box = Box(pos.x + range, posy + range, pos.z + range, pos.x - range, pos.y - range, pos.z - range)
        val entities = (if (hitResult is EntityHitResult) world.getOtherEntities(hitResult.entity, box) else world.getOtherEntities(null, box)).stream().filter{ it !== user && it is LivingEntity }.map {it -> it as LivingEntity}
        var successes = 0
        var killed = false
        entities.forEach { entity ->
            val hit = EntityHitResult(entity)
            val damageSource = spells.provideDamageSource(context, hit, source, user, world, Hand.MAIN_HAND, level, effects)
            val amount = 0.4f * spells.provideDealtDamage(effects.damage(level), context, hit, user, world, hand, level, effects)
            val bl = entity.damage(damageSource, amount)
            if (bl){
                if (!entity.isAlive()) killed = true
                val pos = source?.pos?:entity.pos
                splashParticles(hit,world,entity.pos.x,entity.pos.y + entity.height/2.0,entity.pos.z,spells)
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

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.PLAYERS, 1f, 1f)
    }
    
    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ENDER_DRAGON_SHOOT
    }
}
