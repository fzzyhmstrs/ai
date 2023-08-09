package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.MissileEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
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
import net.minecraft.text.Text
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class FrostboltAugment: ProjectileAugment(ScepterTier.ONE){

    /*
    Checklist
    - canTarget if entity spell
    - Build description for
        - Unique combinations
        - stat modifications
        - other type interactions
        - add Lang
    - provideArgs
    - spells are equal check
    - special names for uniques
    - onPaired to grant relevant adv.
    - implement all special combinations
    - fill up interaction methods
        - onEntityHit?
        - onEntityKill?
        - onBlockHit?
        - Remember to call and check results of the super for the "default" behavior
    - modify stats. don't forget mana cost and cooldown!
        - modifyDealtDamage for unique interactions
    - modifyDamageSource?
        - remember DamageSourceBuilder for a default damage source
    - modify other things
        - summon?
        - projectile?
        - explosion?
        - drops?
        - count? (affects some things like summon count and projectile count)
    - sound and particles
     */

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("freezing"),SpellType.FURY, PerLvlI(36,-2),8,
            5,6,1,1, LoreTier.LOW_TIER, Items.PACKED_ICE)

    //ml 6
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(230,50,0)
            .withDamage(2.9F,0.1f)
            .withRange(3.8,0.2)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity): DamageSourceBuilder {
        return super.damageSourceBuilder(world, source, attacker).set(DamageTypes.FREEZE)
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
            val me = MissileEntity(world, user).color(MissileEntity.ColorData(0.7f, 0.7f))
            val direction = user.rotationVec3d
            me.place(user,direction,-0.2, 2f, 0.1f, 0.6)
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
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success()) {
            if (result.acted()){
                val entity = entityHitResult.entity
                if (entity is LivingEntity) entity.frozenTicks = effects.duration(level)
                val entityList = RaycasterUtil.raycastEntityArea(distance = entityEffects.range(0), entityHitResult.entity)
                for (entity2 in entityList){
                    if (entity2 is LivingEntity) entity2.frozenTicks = effects.duration(level)
                    val amount = spells.provideDealtDamage(effects.damage(level)*0.6f, context, entityHitResult, user, world, hand, level, effects)
                    val damageSource = spells.provideDamageSource(context,entityHitResult, source, user, world, hand, level, effects)
                    val bl  = entityHitResult.entity.damage(damageSource, amount)
                    if (bl){
                        splashParticles(entityHitResult,world,pos.x,pos.y,pos.z,spells)
                        user.applyDamageEffects(user,entityHitResult.entity)
                    }
                }
                return result.withResults(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            }
            return result
        }
        
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS, 0.5f, 1f)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS, 1f, 1f)
    }
}
