package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World

class FlareAugment: ScepterAugment(ScepterTier.ONE, AugmentType.Builder().with(AugmentType.ENTITY).with(AugmentType.EXPLODES).with(AugmentType.BENEFICIAL).with(AugmentType.AOE).build()) {
    override val augmentData: AugmentDatapoint = 
        AugmentDatapoint(AI.identity("flare"),SpellType.GRACE,15,3,
            1,15,1,1, LoreTier.NO_TIER, Items.FIREWORK_STAR)

    //ml 15
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(1).withRange(4.5)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        val hit = RaycasterUtil.raycastHit(distance = effects.range(level),user, includeFluids = false)
        if (hit !is BlockHitResult || user !is PlayerEntity) return false
        val stack = ItemStack(Items.FIREWORK_ROCKET)
        val nbtList = NbtList()
        for (i in 1..world.random.nextInt(level/5+1)+1){
            val nbt = NbtCompound()
            val colors: MutableList<Int> = mutableListOf()
            val type = if (level <= 5) {
                colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
                world.random.nextInt(2)
            } else if (level <= 10) {
                if (world.random.nextFloat() < 0.5) {
                    nbt.putBoolean("Flicker", true)
                }
                colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
                colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
                world.random.nextInt(3)
            } else {
                if (world.random.nextFloat() < 0.5) {
                    nbt.putBoolean("Flicker", true)
                }
                if (world.random.nextFloat() < 0.5) {
                    nbt.putBoolean("Trail", true)
                }
                colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
                colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
                colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
                world.random.nextInt(5)
            }
            nbt.putIntArray("Colors", colors)
            nbt.putByte("Type", type.toByte())
            nbtList.add(nbt)
        }
        FireworkRocketItem.setFlight(stack, (world.random.nextInt(3) + 1).toByte())
        stack.getOrCreateSubNbt("Fireworks").put("Explosions",nbtList)
        val result = stack.useOnBlock(ItemUsageContext(world,user,hand,stack,hit))
        return result.isAccepted
    }
}
