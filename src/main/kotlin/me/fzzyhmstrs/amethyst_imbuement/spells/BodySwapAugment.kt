package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.interfaces.ModifiableEffectMobOrPlayer
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

/*
    Checklist
     */

class BodySwapAugment: ScepterAugment(ScepterTier.THREE,AugmentType.SINGLE_TARGET){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("body_swap"),SpellType.FURY,600,125,
            19,11,1,40, LoreTier.HIGH_TIER, Items.ENDER_EYE)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(15.75,0.75)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.EMPOWERED_SLASH ->
                description.addLang("enchantment.amethyst_imbuement.body_swap.empowered_slash.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))
            RegisterEnchantment.CURSE -> {
                description.addLang("enchantment.amethyst_imbuement.body_swap.curse.desc", SpellAdvancementChecks.UNIQUE)
            }
        }
        if (othersType.has(AugmentType.PROJECTILE))
            description.addLang("enchantment.amethyst_imbuement.body_swap.desc.projectile", SpellAdvancementChecks.ON_KILL)
        if (othersType == AugmentType.DIRECTED_ENERGY) {
            description.addLang("enchantment.amethyst_imbuement.body_swap.desc.range", SpellAdvancementChecks.RANGE)
            description.addLang("enchantment.amethyst_imbuement.body_swap.desc.damage", SpellAdvancementChecks.DAMAGE)
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ON_KILL_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.RANGE_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.EMPOWERED_SLASH ->
                AcText.translatable("enchantment.amethyst_imbuement.body_swap.empowered_slash")
            RegisterEnchantment.CURSE ->
                AcText.translatable("enchantment.amethyst_imbuement.body_swap.curse")
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
    )
    :
            SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return FAIL
        if (onCastResults.overwrite()) return onCastResults

        if (world !is ServerWorld) return FAIL
        val entity = swapEntity(world, user, effects, level)?:return FAIL
        val list = spells.processSingleEntityHit(EntityHitResult(entity),context,world,null,user, hand, level, effects)
        list.addAll(onCastResults.results())
        return if (list.isEmpty()) {
            FAIL
        } else {
            spells.castSoundEvents(world,user.blockPos,context)
            SpellActionResult.success(list)
        }
    }

    private fun <T> swapEntity(world: ServerWorld,user: T,effects: AugmentEffect,level: Int): Entity? where T : SpellCastingEntity,T : LivingEntity{
        val rotation = user.rotationVec3d
        val perpendicularVector = RaycasterUtil.perpendicularVector(rotation, RaycasterUtil.InPlane.XZ)
        val raycasterPos = user.pos.add(rotation.multiply(effects.range(level)/2)).add(Vec3d(0.0,user.height/2.0,0.0))
        val entityList: MutableList<Entity> =
            RaycasterUtil.raycastEntityRotatedArea(
                world.iterateEntities(),
                user,
                raycasterPos,
                rotation,
                perpendicularVector,
                effects.range(level),
                2.5,
                3.0)
        if (entityList.isEmpty()) return null
        val targetedEntityList = entityList.stream().filter { it is LivingEntity && if (it is SpellCastingEntity) AiConfig.entities.isEntityPvpTeammate(user,it,this) else true }.toList()
        if (targetedEntityList.isEmpty()) return null
        val map: SortedMap<Double,Entity> = sortedMapOf()
        targetedEntityList.forEach {
            map[it.squaredDistanceTo(user)] = it
        }
        return map[map.lastKey()]?:return null
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return manaCost.plus(15)
        return manaCost
    }

    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        if (othersType == AugmentType.DIRECTED_ENERGY)
            return damage.plus(2f)
        return damage
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (spells.spellsAreEqual())
            return range.plus(0.0,0.0,100.0)
        if (othersType == AugmentType.DIRECTED_ENERGY)
            return range.plus(0.0,0.0,25.0)
        return range
    }

    override fun <T> onCast(
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
        if (spells.primary() == RegisterEnchantment.EMPOWERED_SLASH){
            if (world !is ServerWorld) return SUCCESSFUL_PASS
            val entity = swapEntity(world, user, effects, level)?:return FAIL

            val hit = EntityHitResult(entity)
            if (!RegisterEnchantment.EMPOWERED_SLASH.canTarget(hit,context, world, user, hand, spells)) return FAIL
            val damageSource = spells.provideDamageSource(context,hit,null,user, world, hand, level, effects)
            val amount = spells.provideDealtDamage(effects.damage(level), context, hit, user, world, hand, level, effects)
            val bl = entity.damage(damageSource, amount)
            return if(bl) {
                val pos = source?.pos?:entity.pos
                splashParticles(hit,world,pos.x,pos.y,pos.z,spells)
                user.applyDamageEffects(user,entity)
                spells.hitSoundEvents(world, entity.blockPos, context)
                val box = Box(entity.x + 3.0,entity.y + 3.0,entity.z + 3.0,entity.x - 3.0,entity.y - 3.0,entity.z - 3.0)
                val entities = world.getOtherEntities(entity,box){ RegisterEnchantment.EMPOWERED_SLASH.canTarget(EntityHitResult(it),context, world, user, hand, spells) }
                for (splashTarget in entities){
                    val bl2 = splashTarget.damage(damageSource,amount/2.5f)
                    if (bl2){
                        splashParticles(EntityHitResult(splashTarget),world,pos.x,pos.y,pos.z,spells)
                        user.applyDamageEffects(user,splashTarget)
                        if (!splashTarget.isAlive){
                            spells.processOnKill(EntityHitResult(splashTarget),context, world, source, user, hand, level, effects)
                        }
                    }
                }
                val list: MutableList<Identifier> = mutableListOf()
                if (entity.isAlive) {
                    list.add(AugmentHelper.DAMAGED_MOB)
                    SpellActionResult.overwrite(list)
                } else {
                    list.add(AugmentHelper.DAMAGED_MOB)
                    list.add(AugmentHelper.KILLED_MOB)
                    spells.processOnKill(hit,context, world, source, user, hand, level, effects)
                    SpellActionResult.overwrite(list)
                }
            } else {
                FAIL
            }
        }
        return super.onCast(context, world, source, user, hand, level, effects, othersType, spells)
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
        if (spells.primary() == RegisterEnchantment.CURSE){
            return when (val entity = entityHitResult.entity) {
                is PlayerEntity -> {
                    (entity as ModifiableEffectMobOrPlayer).amethyst_imbuement_addTemporaryEffect(ModifiableEffectEntity.TICK,ModifiableEffects.CALL_HOSTILES_EFFECT,1200)
                    SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
                }

                is HostileEntity -> {
                    (entity as ModifiableEffectMobOrPlayer).amethyst_imbuement_addTemporaryEffect(ModifiableEffectEntity.TICK,ModifiableEffects.CALL_SUMMONS_EFFECT,1200)
                    SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
                }

                else -> {
                    FAIL
                }
            }
        }
        if (othersType.empty){
            val pos0 = entityHitResult.entity.pos
            val pos1 = Vec3d(pos0.toVector3f())
            val pos2 = Vec3d(user.pos.toVector3f())
            splashParticles(entityHitResult,world,pos0.x,pos0.y,pos0.z,spells)
            if (world is ServerWorld){
                val particle = spells.getCastParticleType()
                world.spawnParticles(particle,pos0.x,pos0.y,pos0.z,20,.25,.25,.25,0.2)
            }
            entityHitResult.entity.teleport(pos2.x,pos2.y,pos2.z)
            user.teleport(pos1.x,pos1.y,pos1.z)
            return SpellActionResult.success(AugmentHelper.TELEPORTED)
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
        if (othersType.has(AugmentType.PROJECTILE)){
            val pos0 = entityHitResult.entity.pos
            val pos1 = Vec3d(pos0.toVector3f())
            user.teleport(pos1.x,pos1.y,pos1.z)
            SpellActionResult.success(AugmentHelper.TELEPORTED)
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect?{
        return ParticleTypes.PORTAL
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect?{
        return ParticleTypes.PORTAL
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext){
        world.playSound(null, blockPos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 0.7F, 1.1F)
    }
}
