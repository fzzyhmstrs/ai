package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.PlaceItemAugment
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
import me.fzzyhmstrs.amethyst_imbuement.block.HardLightBlock
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.PlaceBlockAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts.DyeBoost
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

class CreateHardLightAugment: PlaceBlockAugment(ScepterTier.ONE) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("create_hard_light"),SpellType.WIT, 7,4,
            5,1,1,1, LoreTier.LOW_TIER, RegisterBlock.HARD_LIGHT_BLOCK.asItem())

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(4.5)

    override fun getBlockStateToPlace(context: ProcessContext, world: World, pos: BlockPos, spells: PairedAugments): BlockState {
        val boost = spells.boost()
        val block = if (boost is DyeBoost){
            (blocks[boost.getDyeColor()]?:RegisterBlock.HARD_LIGHT_BLOCK).getHardLightState()
        } else if(spells.paired() == RegisterEnchantment.SHINE) {
            Blocks.SHROOMLIGHT.defaultState
        } else {
            RegisterBlock.HARD_LIGHT_BLOCK.getHardLightState()
        }
        if (AiConfig.blocks.isCreateBlockTemporary()) {
            world.scheduleBlockTick(pos, block.block,AiConfig.blocks.hardLight.temporaryDuration.get())
        }
        return block
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.BARRIER ->
                description.addLang("enchantment.amethyst_imbuement.create_hard_light.barrier.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.PROTECTED_EFFECT))
            RegisterEnchantment.SHINE ->
                description.addLang("enchantment.amethyst_imbuement.create_hard_light.shine.desc", SpellAdvancementChecks.BLOCK)
        }
        if (other is PlaceItemAugment)
            description.addLang("enchantment.amethyst_imbuement.create_hard_light.desc.block", arrayOf(other.item(),itemAfterHardLightTransform(other.item())), SpellAdvancementChecks.BLOCK)
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.create_hard_light.desc.damage", SpellAdvancementChecks.DAMAGE)


    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.BARRIER ->
                AcText.translatable("enchantment.amethyst_imbuement.create_hard_light.barrier")
            else ->
                return super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.BLOCK_TRIGGER)
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.BARRIER)
            return cooldown.plus(0,0,100)
        return super.modifyCooldown(cooldown, other, othersType, spells)
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.BARRIER)
            return manaCost.plus(0,0,100)
        return super.modifyManaCost(manaCost, other, othersType, spells)
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.BARRIER)
            return amplifier.plus(3)
        return super.modifyAmplifier(amplifier, other, othersType, spells)
    }

    override fun <T> modifyDealtDamage(
        amount: Float,
        context: ProcessContext,
        entityHitResult: EntityHitResult,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Float where T : SpellCastingEntity, T : LivingEntity {
        val entity = entityHitResult.entity
        if (entity is LivingEntity){
            if (entity.hasStatusEffect(StatusEffects.GLOWING))
                return amount * 2f
        }
        return amount
    }

    override fun <T> onBlockHit(
        blockHitResult: BlockHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
            :
            SpellActionResult
            where
            T : SpellCastingEntity,
            T : LivingEntity
    {
        val result = super.onBlockHit(blockHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        val other = spells.primary()
        if (!othersType.empty && other is PlaceItemAugment) {
            val item = itemAfterHardLightTransform(other.item())
            if (user !is ServerPlayerEntity) return FAIL
            when (item) {
                is BlockItem -> {
                    val stack = ItemStack(item)
                    if (!item.place(ItemPlacementContext(user, hand, stack, blockHitResult)).isAccepted) return FAIL
                    spells.hitSoundEvents(world, blockHitResult.blockPos,context)
                    return SpellActionResult.overwrite(AugmentHelper.BLOCK_PLACED)
                }
                is BucketItem -> {
                    if (!item.placeFluid(user,world,blockHitResult.blockPos,blockHitResult)) return FAIL
                    spells.hitSoundEvents(world, blockHitResult.blockPos, context)
                    return SpellActionResult.overwrite(AugmentHelper.BLOCK_PLACED)
                }
                else -> {
                    return SUCCESSFUL_PASS
                }
            }
        }
        return SUCCESSFUL_PASS
    }

    private fun itemAfterHardLightTransform(item: Item): Item {
        return items[item]?:item
    }

    companion object{
        val blocks: EnumMap<DyeColor,HardLightBlock> = EnumMap(mapOf(
            DyeColor.WHITE to RegisterBlock.CRYSTALLIZED_LIGHT_WHITE,
            DyeColor.LIGHT_GRAY to RegisterBlock.CRYSTALLIZED_LIGHT_LIGHT_GRAY,
            DyeColor.GRAY to RegisterBlock.CRYSTALLIZED_LIGHT_GRAY,
            DyeColor.BLACK to RegisterBlock.CRYSTALLIZED_LIGHT_BLACK,
            DyeColor.BROWN to RegisterBlock.CRYSTALLIZED_LIGHT_BROWN,
            DyeColor.RED to RegisterBlock.CRYSTALLIZED_LIGHT_RED,
            DyeColor.ORANGE to RegisterBlock.CRYSTALLIZED_LIGHT_ORANGE,
            DyeColor.YELLOW to RegisterBlock.CRYSTALLIZED_LIGHT_YELLOW,
            DyeColor.LIME to RegisterBlock.CRYSTALLIZED_LIGHT_LIME,
            DyeColor.GREEN to RegisterBlock.CRYSTALLIZED_LIGHT_GREEN,
            DyeColor.CYAN to RegisterBlock.CRYSTALLIZED_LIGHT_CYAN,
            DyeColor.LIGHT_BLUE to RegisterBlock.CRYSTALLIZED_LIGHT_LIGHT_BLUE,
            DyeColor.BLUE to RegisterBlock.CRYSTALLIZED_LIGHT_BLUE,
            DyeColor.PURPLE to RegisterBlock.CRYSTALLIZED_LIGHT_PURPLE,
            DyeColor.MAGENTA to RegisterBlock.CRYSTALLIZED_LIGHT_MAGENTA,
            DyeColor.PINK to RegisterBlock.CRYSTALLIZED_LIGHT_PINK

        ))

        val items: Map<Item,Item> = mapOf(
            Items.WATER_BUCKET to Items.PRISMARINE_BRICKS,
            Items.LAVA_BUCKET to Items.OCHRE_FROGLIGHT,
            Items.SPONGE to Items.HAY_BLOCK
        )

    }
}
