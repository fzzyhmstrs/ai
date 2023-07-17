package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper.findSpawnPos
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.item.ScepterLike
import me.fzzyhmstrs.amethyst_core.item.SpellCasting
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.ChorseEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class SummonChickenAugment: ScepterAugment(ScepterTier.ONE, AugmentType.SUMMON_GOOD) {

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_chicken"),SpellType.GRACE,900,75,
            5,3,1,20,LoreTier.LOW_TIER, Items.EGG)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.SUMMON_SEAHORSE -> {
                description.addLang("enchantment.amethyst_imbuement.summon_chicken.summon_seahorse.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
                description.addLang("enchantment.amethyst_imbuement.summon_chicken.summon_seahorse.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))

            }
            RegisterEnchantment.FORTIFY ->
                description.addLang("enchantment.amethyst_imbuement.fangs.fortify.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.BOOSTED_EFFECT))
        }
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

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_SEAHORSE ->
                AcText.translatable("enchantment.amethyst_imbuement.summon_chicken.summon_seahorse")
            RegisterEnchantment.FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.fangs.fortify")
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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        TODO("Not yet implemented")
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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        if (spells.primary() == RegisterEnchantment.SUMMON_SEAHORSE){
            val scepter = user.getStackInHand(hand)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                val nbt = scepter.nbt
                if (nbt != null){
                    if (nbt.contains("current_chorse") && world is ServerWorld){
                        val chorse = world.getEntity(nbt.getUuid("current_chorse"))
                        val chorseNbt = NbtCompound()
                        chorse?.saveSelfNbt(chorseNbt) ?: return SUCCESSFUL_PASS
                        nbt.put("stored_chorse",chorseNbt)
                        chorse.discard()
                        nbt.remove("current_chorse")
                        context.set(ProcessContext.COOLDOWN,200)
                        return SpellActionResult.overwrite(AugmentHelper.DRY_FIRED)
                    }
                }
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T, U> modifySummons(
        summons: List<T>,
        hit: HitResult,
        context: ProcessContext,
        user: U,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): List<Entity> where T : ModifiableEffectEntity,T : Entity, U : SpellCastingEntity,U : LivingEntity {
        if (spells.primary() == RegisterEnchantment.SUMMON_SEAHORSE) {
            val scepter = user.getStackInHand(hand)
            var chorseLoaded: Entity? = null
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                val nbt = scepter.nbt
                if (nbt != null){
                    if (nbt.contains("stored_chorse")){
                        val storedChorse = nbt.getCompound("stored_chorse")
                        chorseLoaded = EntityType.loadEntityWithPassengers(storedChorse,world) {entity -> entity}
                        nbt.remove("stored_chorse")
                    }
                }
            }
            val chorse = if(chorseLoaded is ChorseEntity) {
                chorseLoaded
            } else {
                RegisterEntity.CHORSE_ENTITY.create(world) ?: return summons
            }
            val found = findSpawnPos(world, BlockPos.ofFloored(hit.pos),chorse,3, tries = 16)
            if (!found){
                return listOf()
            }
            chorse.setChorseOwner(user)
            chorse.passEffects(spells,effects,level)
            chorse.passContext(context)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                scepter.orCreateNbt.putUuid("current_chorse",chorse.uuid)
            }
            return listOf(chorse)
        }
        return summons
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var successes = 0
        for(i in 1..level) {
            val startPos = (hit as BlockHitResult).blockPos
            val spawnPos = findSpawnPos(world,startPos,2,1)
            if (spawnPos == BlockPos.ORIGIN) continue

            val chikin = ChickenEntity(EntityType.CHICKEN, world)
            chikin.refreshPositionAndAngles(spawnPos.x + 0.5,spawnPos.y + 0.5, spawnPos.z + 0.5,user.yaw,user.pitch)
            if (world.spawnEntity(chikin)){
                successes++
            }
        }
        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT
    }
}
