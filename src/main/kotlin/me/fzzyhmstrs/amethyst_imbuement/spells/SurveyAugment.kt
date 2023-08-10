package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
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
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Items
import net.minecraft.item.map.MapIcon
import net.minecraft.item.map.MapState
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.StructureTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.Structure

class SurveyAugment: ScepterAugment(ScepterTier.THREE, AugmentType().plus(AugmentType.BENEFICIAL)){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("survey"),SpellType.WIT,1200,120,
            20,1,1,80, LoreTier.NO_TIER, Items.MAP)
    //ml 1

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(100.0,0.0,0.0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
    where T : SpellCastingEntity, T : LivingEntity
    {
        if (user !is PlayerEntity) return FAIL
        if (world !is ServerWorld) return FAIL
        val type = mapList[world.random.nextInt(mapList.size)]
        val blockPos: BlockPos? = world.locateStructure(type.structure, user.getBlockPos(), effects.range(level).toInt(), true)
        return if (blockPos != null) {
            val mapStack = FilledMapItem.createMap(world, blockPos.x, blockPos.z, 2.toByte(), true, true)
            FilledMapItem.fillExplorationMap(world,mapStack)
            MapState.addDecorationsNbt(mapStack,blockPos,"+",type.iconType)
            if (type.tint >= 0){
                val nbtCompound2: NbtCompound = mapStack.getOrCreateSubNbt("display")
                nbtCompound2.putInt("MapColor", type.tint)
            }
            mapStack.setCustomName(AcText.translatable(type.nameKey))
            if (!user.inventory.insertStack(mapStack)) {
                user.dropItem(mapStack, false)
            }
            SpellActionResult.success(AugmentHelper.DRY_FIRED)
        } else {
            FAIL
        }
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ITEM_SPYGLASS_USE,SoundCategory.PLAYERS,1f,1f)
    }

    companion object{

        private val mapList: List<MapType> = listOf(
            MapType(StructureTags.ON_OCEAN_EXPLORER_MAPS,MapIcon.Type.MONUMENT,"filled_map.monument"),
            MapType(StructureTags.ON_WOODLAND_EXPLORER_MAPS,MapIcon.Type.MANSION,"filled_map.mansion"),
            MapType(StructureTags.ON_TREASURE_MAPS,MapIcon.Type.RED_X,"filled_map.buried_treasure"),
            MapType(StructureTags.VILLAGE,MapIcon.Type.TARGET_X,"filled_map.village",0x007F0E),
            MapType(StructureTags.MINESHAFT,MapIcon.Type.TARGET_POINT,"filled_map.mineshaft",0x7F0000)
        )

        private class MapType(val structure: TagKey<Structure>, val iconType: MapIcon.Type, val nameKey: String, val tint: Int = -1)
    }


}
