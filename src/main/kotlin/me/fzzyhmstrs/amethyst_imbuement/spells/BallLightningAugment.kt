package me.fzzyhmstrs.amethyst_imbuement.spells

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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.StunningExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.projectile.ProjectileEntity
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

class BallLightningAugment: ProjectileAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint
        get() = AugmentDatapoint(Identifier(AI.MOD_ID,"ball_lightning"),SpellType.FURY,80,
            25, 14,8, 1,3, LoreTier.LOW_TIER, Items.COPPER_BLOCK)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(6.4F,0.2F,0.0F)
            .withDuration(24,-1)
            .withRange(3.0,.25)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.damage")
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.damage_type")
            if (!othersType.has(AugmentType.PROJECTILE)){
                description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.stun")
            }
        }
        description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.duration")
        if (othersType.has(AugmentType.SUMMONS) && !othersType.has(AugmentType.BENEFICIAL)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.summon")
        }
        if (othersType.has(AugmentType.PROJECTILE) && !othersType.has(AugmentType.BENEFICIAL)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.projectile")
        }
        if (othersType.positiveEffect){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.positiveEffect")
        }
        if (othersType.has(AugmentType.EXPLODES)){
            description.addLang("enchantment.amethyst_imbuement.ball_lightning.desc.explode")
        }
    }

    override fun doubleNameDesc(): List<MutableText> {
        return listOf(AcText.translatable("$orCreateTranslationKey.double.desc1"),AcText.translatable("$orCreateTranslationKey.double.desc2"))
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
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

    override fun modifyDuration(
        duration: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual()){
            return duration.plus(0,0,-35)
        }
        return duration.plus(0,0,-15)
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (spells.spellsAreEqual()){
            return range.plus(0.0,0.0,25.0)
        }
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
        FabricLoader.getInstance().allMods
        if (othersType.has(AugmentType.DAMAGE) && !othersType.has(AugmentType.PROJECTILE)){
            if (AI.aiRandom().nextFloat() < 0.1f && livingEntity is LivingEntity){
                livingEntity.addStatusEffect(StatusEffectInstance(RegisterStatus.STUNNED,60))
            }
        }
        if (othersType.positiveEffect){
            if (livingEntity is ModifiableEffectMobOrPlayer)
                livingEntity.amethyst_imbuement_addTemporaryEffect(ModifiableEffectEntity.TICK,ModifiableEffects.SHOCKING_EFFECT,400)
            if (livingEntity is ModifiableEffectEntity)
                livingEntity.addTemporaryEffect(ModifiableEffectEntity.TICK,ModifiableEffects.SHOCKING_EFFECT,400)
        }
        return SUCCESSFUL_PASS
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

    /*override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = 0.1f
        val div = 0.25F
        return BallLightningEntity.createBallLightning(world, user, speed, div, effects, level, this)
    }*/

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
