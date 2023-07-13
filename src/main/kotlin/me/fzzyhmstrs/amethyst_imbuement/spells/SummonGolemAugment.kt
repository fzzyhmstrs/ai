package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper.findSpawnPos
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
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
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val startPos = (hit as BlockHitResult).blockPos
        val spawnPos = findSpawnPos(world,startPos,3,3, tries = 12)
        if (spawnPos == BlockPos.ORIGIN) return false
        val golem = CrystallineGolemEntity(RegisterEntity.CRYSTAL_GOLEM_ENTITY, world,effects.duration(level), user, effects)
        golem.setConstructOwner(user)
        golem.setPos(spawnPos.x +0.5, spawnPos.y +0.05, spawnPos.z + 0.5)
        golem.refreshPositionAndAngles(spawnPos.x +0.5, spawnPos.y +0.05, spawnPos.z + 0.5,(world.random.nextFloat() * 360f) - 180f,user.pitch)
        if (world.spawnEntity(golem)) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_IRON_GOLEM_REPAIR
    }
}
