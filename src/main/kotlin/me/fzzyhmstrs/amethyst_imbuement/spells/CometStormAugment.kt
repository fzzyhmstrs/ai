package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.ExplosionBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFireballEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.*
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CometStormAugment: MultiTargetAugment(ScepterTier.THREE, AugmentType.AREA_DAMAGE), PersistentEffectHelper.PersistentEffect{
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("comet_storm"),SpellType.FURY,PerLvlI(480,-20),75,
            21,9,1,10, LoreTier.HIGH_TIER, Items.TNT)

    //ml 9
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(70,4)
            .withAmplifier(1)
            .withRange(8.25,0.25)
            .withDamage(7.75f,0.25f)

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
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.EXPLODES_TRIGGER)
    }

    override val delay = PerLvlI(19,-1,0)

    override fun explosionBuilder(world: World, source: Entity?, attacker: LivingEntity?): ExplosionBuilder {
        return super.explosionBuilder(world, source, attacker).withType(World.ExplosionSourceType.MOB).withCreateFire(true)
    }

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
        if (!context.get(ContextData.PERSISTENT)) {
            context.set(ContextData.PERSISTENT,true)
            val data = ApplyTaskAugmentData(world, context, user, hand, level, effects, spells)
            PersistentEffectHelper.setPersistentTickerNeed(this,delay.value(level),effects.duration(level),data)
        }
        return result
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
        if (context.get(ProcessContext.FROM_ENTITY)){
            val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
            if (!result.success() || result.acted())
                return result
        } else if (othersType.empty){
            val vel = entityHitResult.entity.pos
                .add(0.0,entityHitResult.entity.height/2.0,0.0)
                .subtract(user.pos.add(0.0,user.standingEyeHeight.toDouble(),0.0))
                .normalize()
                .multiply(4.0)
            val pfe = PlayerFireballEntity(world, user, vel.x, vel.y, vel.z)
            pfe.passEffects(spells,effects,level)
            pfe.passContext(ProjectileAugment.projectileContext(context.copy()))
            return if (world.spawnEntity(pfe)) SpellActionResult.success(AugmentHelper.PROJECTILE_FIRED) else FAIL
        }
        return SUCCESSFUL_PASS
    }

    @Suppress("KotlinConstantConditions")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is ApplyTaskAugmentData<*>) return
        if (data.user !is LivingEntity) return
        this.applyTasks(data.world,data.context,data.user,data.hand,data.level,data.effects,data.spells)
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.LARGE_SMOKE
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.LARGE_SMOKE
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1f, 1f)
    }
}
