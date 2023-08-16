@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper.findSpawnPos
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.zombie.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class SummonZombieAugment: SummonAugment<UnhallowedEntity>(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_zombie"),SpellType.WIT, PerLvlI(1295,-15),150,
            11,13,1,40,LoreTier.LOW_TIER, Items.ROTTEN_FLESH)

    //ml 13
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(0,1,0)
            .withRange(AiConfig.entities.unhallowed.baseHealth.get().toInt(),0.0,0.0)
            .withDuration(AiConfig.entities.unhallowed.baseLifespan.get(),0,0)
            .withDamage(AiConfig.entities.unhallowed.baseDamage.get())

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun <T> startCount(
        user: T,
        effects: AugmentEffect,
        level: Int,
        othersType: AugmentType,
        spells: PairedAugments
    ): Int where T : SpellCastingEntity,T : LivingEntity {
        return max(min(effects.amplifier(level + 1)/2),1)
    }

    override fun entitiesToSpawn(
        world: World,
        user: LivingEntity,
        hand: Hand,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments,
        count: Int
    ): List<UnhallowedEntity> {
        val startPos = BlockPos.ofFloored(hit.pos)
        val list: MutableList<UnhallowedEntity> = mutableListOf()
        for (i in 1..count){
            val unhallowedEntity = UnhallowedEntity(RegisterEntity.UNHALLOWED_ENTITY, world, effects.duration(level), user, bonus(level))
            val success = findSpawnPos(world, startPos, unhallowedEntity, 3, 15, user.pitch, user.yaw)
            if (success) {
                unhallowedEntity.passEffects(spells, effects, level)
                list.add(unhallowedEntity)
            }
        }

        return list
    }

    private fun bonus(level: Int): Int{
        return if (level <= 5){
            0
        } else if (level <= 8){
            1
        } else if (level <= 10){
            2
        } else if (level <= 12){
            3
        } else {
            4
        }
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null, blockPos, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.PLAYERS, 1f, 1f)
    }
}
