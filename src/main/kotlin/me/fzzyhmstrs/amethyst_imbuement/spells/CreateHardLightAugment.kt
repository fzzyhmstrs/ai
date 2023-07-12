package me.fzzyhmstrs.amethyst_imbuement.spells

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.BeamAugment
import me.fzzyhmstrs.amethyst_core.augments.base.PlaceItemAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.block.HardLightBlock
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.PlaceBlockAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts.DyeBoost
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.EnumMap

class CreateHardLightAugment: PlaceBlockAugment(ScepterTier.ONE) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("create_hard_light"),SpellType.WIT, 7,4,
            5,1,1,1, LoreTier.LOW_TIER, RegisterBlock.HARD_LIGHT_BLOCK.asItem())

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(4.5)

    override fun getBlockStateToPlace(context: ProcessContext, world: World, pos: BlockPos, spells: PairedAugments): BlockState {
        val boost = spells.boost()
        val block = if (boost is DyeBoost){
            blocks[boost.getDyeColor()]?:RegisterBlock.HARD_LIGHT_BLOCK
        } else {
            RegisterBlock.HARD_LIGHT_BLOCK
        }
        if (AiConfig.blocks.isCreateBlockTemporary()) {
            world.scheduleBlockTick(pos, block,AiConfig.blocks.hardLight.temporaryDuration.get())
        }
        return block.getHardLightState()
    }

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

    }

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        if (user !is ServerPlayerEntity) return false
        val hit = RaycasterUtil.raycastHit(effects.range(level),entity = user)
        if (hit is BlockHitResult  && CommonProtection.canPlaceBlock(world,hit.blockPos,user.gameProfile,user)){
            /*val item = RegisterBlock.HARD_LIGHT_BLOCK.asItem() as BlockItem
            if (!item.place(ItemPlacementContext(user, hand, ItemStack(RegisterBlock.HARD_LIGHT_BLOCK),hit)).isAccepted) return false*/
            val context = ItemPlacementContext(user, hand, ItemStack(RegisterBlock.SHINE_LIGHT),hit)
            if (!RegisterBlock.HARD_LIGHT_BLOCK.isEnabled(world.enabledFeatures)) {
                return false
            }
            if (!context.canPlace()) {
                return false
            }
            val blockPos = context.blockPos
            val state = RegisterBlock.HARD_LIGHT_BLOCK.getHardLightState()
            world.setBlockState(blockPos,state)
            if (AiConfig.blocks.isCreateBlockTemporary()){
                world.scheduleBlockTick(blockPos, RegisterBlock.HARD_LIGHT_BLOCK,AiConfig.blocks.hardLight.temporaryDuration.get())
            }
            val group = RegisterBlock.HARD_LIGHT_BLOCK.defaultState.soundGroup
            val sound = group.placeSound
            world.playSound(null,hit.blockPos,sound, SoundCategory.BLOCKS,(group.volume + 1.0f)/2.0f,group.pitch * 0.8f)
            //sendItemPacket(user, stack, hand, hit)
            effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
            return true
        } else {
            var range = effects.range(level)
            do {
                val pos = user.eyePos.subtract(0.0, 0.2, 0.0).add(user.rotationVector.multiply(range))
                val blockPos = BlockPos.ofFloored(pos)
                if (CommonProtection.canPlaceBlock(world,blockPos,user.gameProfile,user)){
                    val state = RegisterBlock.HARD_LIGHT_BLOCK.getHardLightState()
                    if (world.canPlayerModifyAt(user,blockPos) && world.getBlockState(blockPos).isReplaceable && world.canPlace(state,blockPos, ShapeContext.of(user)) && state.canPlaceAt(world,blockPos)){
                        world.setBlockState(blockPos,state)
                        if (AiConfig.blocks.isCreateBlockTemporary()){
                            world.scheduleBlockTick(blockPos, RegisterBlock.HARD_LIGHT_BLOCK,AiConfig.blocks.hardLight.temporaryDuration.get())
                        }
                        val group = state.soundGroup
                        val sound = group.placeSound
                        world.playSound(null,blockPos,sound, SoundCategory.BLOCKS,(group.volume + 1.0f)/2.0f,group.pitch * 0.8f)
                        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
                        return true
                    }
                }
                range -= 1.0
            }while (range > 0.0)
        }
        return false
    }
}
