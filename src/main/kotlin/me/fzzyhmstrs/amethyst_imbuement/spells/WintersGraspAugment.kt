package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ApplyTaskAugmentData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WintersGraspAugment: EntityAoeAugment(ScepterTier.THREE,false), PersistentEffectHelper.PersistentEffect{
    override val augmentData: AugmentDatapoint =
         AugmentDatapoint(AI.identity("winters_grasp"),SpellType.FURY,600,120,
            23,17,1,25, LoreTier.HIGH_TIER, RegisterBlock.GLISTENING_ICE.asItem())

    //ml 17
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(1)
            .withDuration(115,5)
            .withDamage(1.8f,0.1f)
            .withRange(11.75,0.25)

    private val hailDelay = PerLvlF(20f,-0.5f,0f)

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        val result = super.applyTasks(world,context,user,hand,level,effects,spells)
        if (!result.success()) return FAIL
        if (world is ServerWorld && result.success()){
            world.spawnParticles(ParticleTypes.SNOWFLAKE,user.x,user.getBodyY(0.5),user.z,250,effects.range(level),0.8,effects.range(level),0.0)
        }
        if (!context.get(ContextData.PERSISTENT)) {
            context.set(ContextData.PERSISTENT,true)
            val data = ApplyTaskAugmentData(world, context, user, hand, level, effects, spells)
            PersistentEffectHelper.setPersistentTickerNeed(this,hailDelay.value(level).toInt(),effects.duration(level),data)
        }
        return result
    }
            
    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return SpellHelper.hostileFilter(list,user,this)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
            return grasp(entityHitResult, context, world, source, user, hand, level, effects, spells)
        }
        return SUCCESSFUL_PASS
    }

    private fun <T> grasp(entityHitResult: EntityHitResult,
                      context: ProcessContext,
                      world: World,
                      source: Entity?,
                      user: T,
                      hand: Hand,
                      level: Int,
                      effects: AugmentEffect,
                      spells: PairedAugments)
    :
    SpellActionResult
    where T : SpellCastingEntity, T : LivingEntity
    {
        val amount = spells.provideDealtDamage(effects.damage(level), context, entityHitResult, user, world, hand, level, effects)
        val damageSource = spells.provideDamageSource(context,entityHitResult, source, user, world, hand, level, effects)
        val bl  = entityHitResult.entity.damage(damageSource, amount)

        return if(bl) {
            val pos = source?.pos?:entityHitResult.entity.pos
            splashParticles(entityHitResult,world,pos.x,pos.y,pos.z,spells)
            user.applyDamageEffects(user,entityHitResult.entity)
            entityHitResult.addStatus(StatusEffects.SLOWNESS,effects.duration(level), effects.amplifier(level))
            entityHitResult.addStatus(StatusEffects.WEAKNESS,effects.duration(level), effects.amplifier(level))
            if(world.random.nextFloat() < 0.2f){
                val entity = entityHitResult.entity
                entity.frozenTicks = 360
                val snowPos = entity.blockPos
                if (world.getBlockState(snowPos).isAir){
                    world.setBlockState(snowPos, Blocks.SNOW.defaultState)
                }
            }
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

    override val delay: PerLvlI
        get() = PerLvlI()

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS, 1f, 1f)
    }

    @Suppress("KotlinConstantConditions")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is ApplyTaskAugmentData<*>) return
        if (data.user !is LivingEntity) return
        this.applyTasks(data.world,data.context,data.user,data.hand,data.level,data.effects,data.spells)
    }
}
