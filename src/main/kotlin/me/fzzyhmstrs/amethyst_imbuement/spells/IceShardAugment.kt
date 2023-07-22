package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.BasicShardEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class IceShardAugment: ProjectileAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("ice_shard"),SpellType.FURY, PerLvlI(15,-1),
            15,14, 6,1,1, LoreTier.LOW_TIER, Items.BLUE_ICE)

    //ml 6
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(3.6F,0.4F,0.0F)
            .withDuration(180,20)
            .withAmplifier(1)
            .withRange(4.3,0.2)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
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
        val rot = user.rotationVec3d
        val pos = user.eyePos.subtract(0.0,0.2,0.0)
        val list: MutableList<ProjectileEntity> = mutableListOf()
        for (i in 1..count){
            list.add(BasicShardEntity(RegisterEntity.ICE_SHARD_ENTITY,world,user,effects.range(level).toFloat(),0.75f,pos,rot))
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
        if (!result.success())
            return result
        if (result.acted() && othersType.empty){
            if (world.random.nextFloat() < 0.1){
                entityHitResult.entity.frozenTicks = effects.duration(0)
                result.withResults(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            }
            return result
        }

        return SUCCESSFUL_PASS
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_SNOWBALL_THROW,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
