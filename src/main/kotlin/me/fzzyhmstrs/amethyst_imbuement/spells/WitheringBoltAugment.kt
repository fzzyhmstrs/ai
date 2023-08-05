package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
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
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerWitherSkullEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Difficulty
import net.minecraft.world.World

class WitheringBoltAugment: ProjectileAugment(ScepterTier.TWO, AugmentType.BALL){
    override val augmentData: AugmentDatapoint =
         AugmentDatapoint(AI.identity("withering_bolt"),SpellType.FURY, PerLvlI(30,-2),13,
            11,5,1,2,LoreTier.LOW_TIER, Items.WITHER_SKELETON_SKULL)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(7.5f,0.5f).withAmplifier(1).withDuration(200,600)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.withering_bolt.desc.damage", SpellAdvancementChecks.DAMAGE)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.EXPLODES_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_TRIGGER)
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
        val list: MutableList<ProjectileEntity> = mutableListOf()
        val direction = user.rotationVec3d
        for (i in 1..count){
            val wse = PlayerWitherSkullEntity(world,user,direction.multiply(4.0))
            wse.passEffects(spells,effects,level)
            wse.passContext(context)
            list.add(wse)
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
            if (othersType.empty && result.acted()) {
                var i = -1
                if (world.difficulty == Difficulty.NORMAL) {
                    i = 0
                } else if (world.difficulty == Difficulty.HARD) {
                    i = 1
                }
                if (i > -1) {
                    entityHitResult.addStatus(StatusEffects.WITHER, effects.duration(i), effects.amplifier(level))
                }
            }
            return result
        }
        if (othersType.has(AugmentType.DAMAGE)){
            entityHitResult.addStatus(StatusEffects.WITHER,120)
        }
        return SUCCESSFUL_PASS
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_WITHER_SHOOT,SoundCategory.PLAYERS,1f,1f)
    }
}
