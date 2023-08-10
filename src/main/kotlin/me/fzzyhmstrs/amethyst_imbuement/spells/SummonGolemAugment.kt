package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.golem.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
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

class SummonGolemAugment: SummonAugment<CrystallineGolemEntity>(ScepterTier.THREE) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_golem"),SpellType.WIT,6000,600,
            25,5,1,100,LoreTier.HIGH_TIER, Items.AMETHYST_BLOCK)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(AiConfig.entities.crystalGolem.spellBaseLifespan.get(),AiConfig.entities.crystalGolem.spellPerLvlLifespan.get())
            .withAmplifier(AiConfig.entities.crystalGolem.baseHealth.get().toInt())
            .withDamage(AiConfig.entities.crystalGolem.baseDamage.get())

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
    ): List<CrystallineGolemEntity> {
        val startPos = BlockPos.ofFloored(hit.pos)
        val list: MutableList<CrystallineGolemEntity> = mutableListOf()
        for (i in 1..count){
            val totemOfFuryEntity = CrystallineGolemEntity(RegisterEntity.CRYSTAL_GOLEM_ENTITY, world,effects.duration(level), user)
            val success = AugmentHelper.findSpawnPos(world, startPos, totemOfFuryEntity, 3, 30, user.pitch, user.yaw)
            if (success) {
                totemOfFuryEntity.passEffects(spells, effects, level)
                list.add(totemOfFuryEntity)
            }
        }

        return list
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.PLAYERS,1f,1f)
    }
}
