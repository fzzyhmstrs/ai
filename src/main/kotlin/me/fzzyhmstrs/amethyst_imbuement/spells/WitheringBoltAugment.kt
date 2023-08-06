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
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerWitherSkullEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.and
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.EvilExplosionBehavior
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.WitheringExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.*
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
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
import net.minecraft.world.Difficulty
import net.minecraft.world.World

/*
    Checklist
     */

class WitheringBoltAugment: ProjectileAugment(ScepterTier.TWO, AugmentType.BALL){
    override val augmentData: AugmentDatapoint =
         AugmentDatapoint(AI.identity("withering_bolt"),SpellType.FURY, PerLvlI(30,-2),13,
            11,5,1,2,LoreTier.LOW_TIER, Items.WITHER_SKELETON_SKULL)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(7.5f,0.5f).withAmplifier(1).withDuration(200,600)

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
        when (other){
            RegisterEnchantment.SMITING_BLOW ->
                description.addLang("enchantment.amethyst_imbuement.withering_bolt.smiting_blow.desc", SpellAdvancementChecks.UNIQUE.and(SpellAdvancementChecks.HARMED_EFFECT))
            RegisterEnchantment.COMET_STORM ->
                description.addLang("enchantment.amethyst_imbuement.withering_bolt.comet_storm.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SOUL))
        }
        if (othersType.negativeEffect)
            description.addLang("enchantment.amethyst_imbuement.withering_bolt.desc.negative", SpellAdvancementChecks.STAT)
        if (othersType.has(AugmentType.DAMAGE)) {
            description.addLang("enchantment.amethyst_imbuement.withering_bolt.desc.damage", SpellAdvancementChecks.DAMAGE)
            description.addLang("enchantment.amethyst_imbuement.withering_bolt.desc.kill", SpellAdvancementChecks.DAMAGE)
        }
        if (othersType.has(AugmentType.EXPLODES))
            description.addLang("enchantment.amethyst_imbuement.withering_bolt.desc.explodes", SpellAdvancementChecks.EXPLODES)
        if (othersType.has(AugmentType.PROJECTILE) && othersType.has(AugmentType.BLOCK))
            description.addLang("enchantment.amethyst_imbuement.withering_bolt.desc.blockExplodes", SpellAdvancementChecks.EXPLODES.or(SpellAdvancementChecks.BLOCK))
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.EXPLODES_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SMITING_BLOW ->
                AcText.translatable("enchantment.amethyst_imbuement.withering_bolt.smiting_blow")
            RegisterEnchantment.COMET_STORM ->
                AcText.translatable("enchantment.amethyst_imbuement.withering_bolt.comet_storm")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.SMITING_BLOW)
            return manaCost.plus(4)
        return manaCost
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        if (spells.spellsAreEqual())
            return damage.plus(3f)
        if (other == RegisterEnchantment.SMITING_BLOW)
            return damage.plus(0f,0.5f)
        return damage
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
        if (spells.primary() == RegisterEnchantment.SMITING_BLOW) {
            val entity = entityHitResult.entity
            if (entity is LivingEntity){
                context.set(ContextData.CRIT,true)
                if (entity.group != EntityGroup.UNDEAD) {
                    return amount * effects.amplifier(level).toFloat()
                }
            }
        }
        return amount
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity): DamageSourceBuilder {
        return super.damageSourceBuilder(world, source, attacker).set(DamageTypes.MAGIC)
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
    ): DamageSourceBuilder where T : SpellCastingEntity, T : LivingEntity {
        return builder.add(DamageTypes.MAGIC)
    }

    override fun <T> createProjectileEntities(
        world: World,
        context: ProcessContext,
        user: T,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments,
        count: Int
    ): List<ProjectileEntity> where T : SpellCastingEntity,T : LivingEntity {
        val list: MutableList<ProjectileEntity> = mutableListOf()
        val direction = user.rotationVec3d
        for (i in 1..count){
            val wse = PlayerWitherSkullEntity(world,user,direction.multiply(4.0))
            wse.passEffects(spells,effects,level)
            wse.passContext(context)
            list.add(wse)
        }
        return list
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
    ): ProjectileEntity where T : ModifiableEffectEntity,T : ProjectileEntity, U : SpellCastingEntity,U : LivingEntity {
        if (spells.primary() == RegisterEnchantment.COMET_STORM){
            val direction = projectile.rotationVector
            val wse = PlayerWitherSkullEntity(world,user,direction.multiply(4.0))
            wse.passEffects(spells,effects,level)
            wse.passContext(context)
            return wse
        }
        return super.modifyProjectile(projectile, context, user, world, hand, level, effects, othersType, spells)
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
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success()) {
            if (othersType.empty && result.acted()) {
                var i = -1
                if (world.difficulty == Difficulty.NORMAL) {
                    i = 0
                } else if (world.difficulty == Difficulty.HARD) {
                    i = 1
                }
                if (i > -1) {
                    entityHitResult.addStatus(StatusEffects.WITHER, effects.duration(i), effects.amplifier(level))
                }
            }
            return result
        }
        if (othersType.has(AugmentType.DAMAGE) || othersType.negativeEffect){
            entityHitResult.addStatus(StatusEffects.WITHER,120,effects.amplifier(level))
        }
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
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        if (othersType.has(AugmentType.PROJECTILE)){
            val damageSourceBuilder = spells.primary()?.damageSourceBuilder(world,source,user) ?: return SUCCESSFUL_PASS
            val builder = ExplosionBuilder(damageSourceBuilder,source,blockHitResult.pos).withType(World.ExplosionSourceType.MOB).withPower(0.5f).modifyDamageSource{damage -> damage.add(DamageTypes.MAGIC)}
            builder.explode(world)
            return SpellActionResult.success(AugmentHelper.EXPLODED)
        }
        return SUCCESSFUL_PASS
    }

    override fun <T> onEntityKill(
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
        val pos = entityHitResult.entity.pos.add(0.0,entityHitResult.entity.height/2.0,0.0)
        if (world.isClient) return SUCCESSFUL_PASS
        val areaEffectCloudEntity = AreaEffectCloudEntity(world, pos.x, pos.y, pos.z)
        areaEffectCloudEntity.particleType = ParticleTypes.SOUL
        areaEffectCloudEntity.duration = 200
        areaEffectCloudEntity.radius = 2.0f
        if (source is ProjectileEntity){
            val owner = source.owner
            if (owner is LivingEntity)
                areaEffectCloudEntity.owner = owner
        } else if (source is Tameable){
            val owner = source.owner
            areaEffectCloudEntity.owner = owner
        }
        areaEffectCloudEntity.radiusGrowth = (7.0f - areaEffectCloudEntity.radius) / areaEffectCloudEntity.duration.toFloat()
        areaEffectCloudEntity.addEffect(StatusEffectInstance(StatusEffects.WITHER, 80, 0))
        areaEffectCloudEntity.setPosition(pos)
        world.spawnEntity(areaEffectCloudEntity)
        return SUCCESSFUL_PASS
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
        if (spells.spellsAreEqual())
            return builder.modifyPower{power -> power * 1.5f}.withCustomBehavior(EvilExplosionBehavior())
        return builder.withCreateFire(true).withCustomBehavior(WitheringExplosionBehavior()).withType(World.ExplosionSourceType.MOB).modifyDamageSource{damage -> damage.add(DamageTypes.MAGIC)}
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.LARGE_SMOKE
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.LARGE_SMOKE
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_WITHER_SHOOT,SoundCategory.PLAYERS,1f,1f)
    }
}
