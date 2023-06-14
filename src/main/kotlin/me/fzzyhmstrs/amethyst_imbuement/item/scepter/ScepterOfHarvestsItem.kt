package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.mixins.AxeItemAccessor
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Oxidizable
import net.minecraft.block.PillarBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.HoneycombItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent
import java.util.*

class BuilderScepterItem(material: ScepterToolMaterial, settings: Settings
): CustomSpellToolItem(material, 1.5f,-3.0f, BlockTags.HOE_MINEABLE, settings) {
    
}
