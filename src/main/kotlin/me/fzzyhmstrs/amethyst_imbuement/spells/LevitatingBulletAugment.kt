package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerBulletEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.MultiTargetAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class LevitatingBulletAugment: MultiTargetAugment(ScepterTier.THREE, AugmentType.BOLT) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("levitating_bullet"),SpellType.FURY,30,8,
            18,3,1,1,LoreTier.HIGH_TIER, Items.SHULKER_SHELL)
    //ml 3
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(8.0,1.0,0.0)
            .withDamage(4.0f)

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
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity?): DamageSourceBuilder {
        return DamageSourceBuilder(world, attacker, source).set(DamageTypes.MAGIC)
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
            if (result.acted() || !result.success())
                if (result.acted()){
                    if (entityHitResult.addStatus(StatusEffects.LEVITATION, 200))
                        return result.withResults(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
                }
                return result
        } else if (othersType.empty){
            val axis = getAxis(user)
            val entity = entityHitResult.entity
            val sbe = PlayerBulletEntity(world,user,entity,axis)
            sbe.passEffects(spells,effects,level)
            sbe.passContext(ProjectileAugment.projectileContext(context.copy()))
            return if (world.spawnEntity(sbe)){
                SpellActionResult.success(AugmentHelper.PROJECTILE_FIRED)
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_SHULKER_SHOOT,SoundCategory.PLAYERS,1f,1f)
    }

    private fun getAxis(user: LivingEntity): Direction.Axis{
        return user.horizontalFacing.axis
    }

}
