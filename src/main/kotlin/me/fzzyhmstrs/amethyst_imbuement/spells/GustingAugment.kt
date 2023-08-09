package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class GustingAugment: EntityAoeAugment(ScepterTier.ONE,false){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("gusting"),SpellType.WIT,60,15,
            1,3,1,2,LoreTier.LOW_TIER, Items.FEATHER)

    //ml 3
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(8.0,0.0).withAmplifier(2,1)

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return SpellHelper.hostileFilter(list, user, this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }



    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val entityList = RaycasterUtil.raycastEntityArea(effect.range(level), user)
        if (entityList.isEmpty()) return false
        var minDist = 10000000.0
        var maxDist = 0.0
        for (entity in entityList){
            val dist = entity.squaredDistanceTo(user)
            minDist = min(dist,minDist)
            maxDist = max(dist,maxDist)
        }
        if (maxDist == 0.0) return false
        val minDistNorm = minDist/maxDist
        val maxDistNorm = 1.0
        for (entity in entityList){
            if (entity is LivingEntity){
                val distNorm = 1.0 - (entity.squaredDistanceTo(user) - minDist)/maxDist
                val strength = effect.amplifier(level) * MathHelper.lerp(distNorm,minDistNorm,maxDistNorm)
                entityTask(world,entity,user,strength,null, effect)
            }
        }
        effect.accept(toLivingEntityList(entityList), AugmentConsumer.Type.HARMFUL)
        effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null,user.blockPos,soundEvent(),SoundCategory.PLAYERS,0.8F,1.2F)
        return true
    }

    override fun entityTask(
        world: World,
        target: Entity,
        user: LivingEntity,
        level: Double,
        hit: HitResult?,
        effects: AugmentEffect
    ) {
        if (target is LivingEntity){
            target.takeKnockback(level,user.x - target.x,user.z - target.z)
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_ELYTRA_FLYING
    }

}
