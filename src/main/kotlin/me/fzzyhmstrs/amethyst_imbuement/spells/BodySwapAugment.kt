package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

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
            RegisterEnchantment.CURSE ->
                description.addLang("enchantment.amethyst_imbuement.body_swap.curse.desc", SpellAdvancementChecks.UNIQUE)
        }
        if (othersType.has(AugmentType.PROJECTILE))
            description.addLang("enchantment.amethyst_imbuement.body_swap.desc.projectile", SpellAdvancementChecks.ON_KILLED)
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
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ON_KILLED_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.EMPOWERED_SLASH ->
                AcText.translatable("enchantment.amethyst_imbuement.body_swap.empowered_slash")
            RegisterEnchantment.FANGS ->
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
        val rotation = user.getRotationVec3d()
        val perpendicularVector = RaycasterUtil.perpendicularVector(rotation, RaycasterUtil.InPlane.XZ)
        val raycasterPos = user.pos.add(rotation.multiply(effect.range(level)/2)).add(Vec3d(0.0,user.height/2.0,0.0))
        val entityList: MutableList<Entity> =
            RaycasterUtil.raycastEntityRotatedArea(
                world.iterateEntities(),
                user,
                raycasterPos,
                rotation,
                perpendicularVector,
                effect.range(level),
                2.5,
                3.0)
        if (entityList.isEmpty()) return FAIL
        val targetedEntityList = entityList.stream().filter { it is LivingEntity && if (it is SpellCastingEntity) AiConfig.entities.isEntityPvpTeammate(user,it,this) else true }.toList()
        if (targetedEntityList.isEmpty()) return FAIL
        val map: SortedMap<Double,Entity> = sortedMapOf()
        targetedEntityList.forEach {
            map[it.squaredDistanceTo(user)] = it
        }
        val entity = map[map.lastKey()]?:return FAIL
        val list = spells.processSingleEntityHit(EntityHitResult(entity),context,world,null,user, hand, level, effects)
        list.addAll(onCastResults.results())
        return if (list.isEmpty()) {
            FAIL
        } else {
            spells.castSoundEvents(world,user.blockPos,context)
            SpellActionResult.success(list)
        }
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
        if (othersType.empty){
            val pos0 = entityHitResult.entity.pos
            val pos1 = Vec3d(pos0.toVector3f())
            val pos2 = Vec3d(user.pos.toVector3f())
            splashParticles(entityHitResult,world,pos0.x,pos0.y,pos0.z,spells)
            if (world is ServerWorld){
                val particle = spells.getCastParticleType()
                world.spawnParticles(particle,pos0.x,pos0.y,pos0.z,20,.25,.25,.25,0.2)
            }
            entity.teleport(pos2.x,pos2.y,pos2.z)
            user.teleport(pos1.x,pos1.y,pos1.z)
            return 
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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        if (othersType.has(AugmentType.PROJECTILE)){
            val pos0 = entityHitResult.entity.pos
            val pos1 = Vec3d(pos0.toVector3f())
            user.teleport(pos1.x,pos1.y,pos1.z)
            SpellActionResult.success(AugmentHelper.DAMAGED_MOB
        }
        return SUCCESSFUL_PASS
    }

    override hitParticleType(): ParticleEffect?{
        return ParticleTypes.PORTAL
    }
    
    override hitParticleType(hit: HitResult): ParticleEffect?{
        return ParticleTypes.PORTAL
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext){
        world.playSound(null, blockPos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategories.PLAYER, 0.7F, 1.1F)
    }
}
