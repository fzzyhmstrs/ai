package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.*
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFireballEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.and
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class FireballAugment: ProjectileAugment(ScepterTier.TWO, AugmentType.BALL){
    //ml 5
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("fireball"),SpellType.FURY,32,10,
            10,5,1,2, LoreTier.LOW_TIER, Items.TNT)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(7.75F,0.25f)

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
            RegisterEnchantment.ZAP -> {
                description.addLang("enchantment.amethyst_imbuement.fireball.zap.desc1", SpellAdvancementChecks.UNIQUE.and(SpellAdvancementChecks.EXPLODES))
                description.addLang("enchantment.amethyst_imbuement.fireball.zap.desc2", SpellAdvancementChecks.UNIQUE.and(SpellAdvancementChecks.MANA_COST))
                return
            }
            RegisterEnchantment.SHINE -> {
                description.addLang("enchantment.amethyst_imbuement.fireball.shine.desc", SpellAdvancementChecks.UNIQUE.and(SpellAdvancementChecks.EXPLODES))
                return
            }
        }
        if (othersType.positiveEffect){
            if (othersType.has(AugmentType.AOE)) {
                description.addLang("enchantment.amethyst_imbuement.fireball.desc.positive_aoe", SpellAdvancementChecks.PROTECTED_EFFECT)
            } else {
                description.addLang("enchantment.amethyst_imbuement.fireball.desc.positive_nonAoe", SpellAdvancementChecks.PROTECTED_EFFECT)
            }
        }
        if (othersType.has(AugmentType.PROJECTILE))
            description.addLang("enchantment.amethyst_imbuement.fireball.desc.projectile", SpellAdvancementChecks.PROJECTILE)
        if (othersType.has(AugmentType.EXPLODES))
            description.addLang("enchantment.amethyst_imbuement.fireball.desc.explodes", SpellAdvancementChecks.EXPLODES.and(SpellAdvancementChecks.FLAME))
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.EXPLODES_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.PROJECTILE_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.ZAP ->
                AcText.translatable("enchantment.amethyst_imbuement.fireball.zap")
            RegisterEnchantment.SHINE ->
                AcText.translatable("enchantment.amethyst_imbuement.fireball.shine")
            else ->
                return super.specialName(otherSpell)
        }
    }

    override fun explosionBuilder(world: World, source: Entity?, attacker: LivingEntity?): ExplosionBuilder {
        return super.explosionBuilder(world, source, attacker).withType(World.ExplosionSourceType.MOB).withCreateFire(true)
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
            val direction = user.rotationVec3d
            val vec3d: Vec3d = Vec3d(direction.x, direction.y, direction.z).normalize().add(
                world.random.nextTriangular(0.0, 0.0172275 * 0.2),
                world.random.nextTriangular(0.0, 0.0172275 * 0.2),
                world.random.nextTriangular(0.0, 0.0172275 * 0.2)
            ).multiply(2.0)

            val pfe = PlayerFireballEntity(world, user, vec3d.x,vec3d.y,vec3d.z)
            val pos = user.pos.add(0.0,user.standingEyeHeight -0.4,0.0).add(direction.multiply(0.5))
            pfe.refreshPositionAfterTeleport(pos)
            pfe.passEffects(spells,effects,level)
            list.add(pfe)
        }
        return list
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity?): DamageSourceBuilder {
        return DamageSourceBuilder(world, attacker, source).set(DamageTypes.FIREBALL)
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
        return builder.add(DamageTypes.FIREBALL)
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
        if (result.acted() || !result.success())
            return result
        if (othersType.has(AugmentType.PROJECTILE)){
            TODO()
        }
        if (othersType.positiveEffect){
            if (othersType.has(AugmentType.AOE)) {
                entityHitResult.addStatus(RegisterStatus.BLAST_RESISTANT, effects.duration(level), 7)
            } else {
                entityHitResult.addStatus(RegisterStatus.BLAST_RESISTANT, effects.duration(level), 3)
            }
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
    )
    :
            SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val result = super.onBlockHit(blockHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (othersType.has(AugmentType.PROJECTILE)){
            TODO()
        }
        if (spells.primary() == RegisterEnchantment.SHINE){
            TODO()
        }
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
            return builder.modifyPower{power -> power * 2.5f}
        return builder.modifyPower{power -> power * 1.75f}.withCreateFire(true)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }
}
