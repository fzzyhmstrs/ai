package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.item.ScepterLike
import me.fzzyhmstrs.amethyst_core.item.SpellCasting
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.horse.SeahorseEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SummonSeahorseAugment: SummonAugment<SeahorseEntity>(ScepterTier.ONE, AugmentType.SUMMON_GOOD) {

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_seahorse"),SpellType.WIT,1200,100,
            5,7,1,40,LoreTier.LOW_TIER, Items.NAUTILUS_SHELL)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
        if (othersType.empty){
            val scepter = user.getStackInHand(hand)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                val nbt = scepter.nbt
                if (nbt != null){
                    if (nbt.contains("current_seahorse") && world is ServerWorld){
                        val chorse = world.getEntity(nbt.getUuid("current_seahorse"))
                        val chorseNbt = NbtCompound()
                        chorse?.saveSelfNbt(chorseNbt) ?: return SUCCESSFUL_PASS
                        nbt.put("stored_seahorse",chorseNbt)
                        chorse.discard()
                        nbt.remove("current_seahorse")
                        context.set(ProcessContext.COOLDOWN,200)
                        return SpellActionResult.overwrite(AugmentHelper.DRY_FIRED)
                    }
                }
            }
        }
        return super.onCast(context, world, source, user, hand, level, effects, othersType, spells)
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
    ): List<SeahorseEntity> {
        val startPos = BlockPos.ofFloored(hit.pos)
        val list: MutableList<SeahorseEntity> = mutableListOf()
        val scepter = user.getStackInHand(hand)
        var seahorseEntity: Entity? = null
        if (scepter.item is ScepterLike && scepter.item is SpellCasting){
            val nbt = scepter.nbt
            if (nbt != null){
                if (nbt.contains("stored_seahorse")){
                    val storedChorse = nbt.getCompound("stored_seahorse")
                    seahorseEntity = EntityType.loadEntityWithPassengers(storedChorse,world) { entity -> entity}
                    nbt.remove("stored_seahorse")
                }
            }
        }
        val seahorse = if(seahorseEntity is SeahorseEntity) {
            seahorseEntity
        } else {
            SeahorseEntity(RegisterEntity.SEAHORSE_ENTITY, world)
        }
        val success = AugmentHelper.findSpawnPos(world, startPos, seahorse, 1, 40, user.pitch, user.yaw)
        if (success) {
            seahorse.setPlayerHorseOwner(user)
            seahorse.passEffects(spells, effects, level)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                scepter.orCreateNbt.putUuid("current_seahorse",seahorse.uuid)
            }
            list.add(seahorse)
        }

        return list
    }

}