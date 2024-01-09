@file:Suppress("unused")

package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.SpellScrollItem
import me.fzzyhmstrs.amethyst_imbuement.spells.special.DebugAugment
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text

// don't know if this is better as a class or object. as an object it allows me to call it without needing to initialize an instance of it.
object RegisterItemGroup {

    fun registerItemGroup(): ItemGroup{
        return Registry.register(Registries.ITEM_GROUP,AI.identity("ai_group"), FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.amethyst_imbuement.ai_group"))
            .icon { ItemStack(RegisterBlock.IMBUING_TABLE.asItem()) }
            .entries { _, entries ->
                entries.addAll(RegisterItem.regItem.stream()
                    .map { item -> ItemStack(item) }.toList())
                entries.addAll(RegisterTool.regTool.stream()
                    .map { item -> ItemStack(item) }
                    .toList())
                entries.addAll(RegisterScepter.regScepter.stream()
                    .map { item -> ItemStack(item) }
                    .toList())
                entries.addAll(RegisterArmor.regArmor.stream()
                    .map { item -> ItemStack(item) }
                    .toList())
                entries.addAll(RegisterBlock.regBlockItem.stream()
                    .map { block -> ItemStack(block) }
                    .toList())
                entries.addAll(FzzyPort.ENCHANTMENT.stream()
                    .filter { enchant -> enchant is ScepterAugment && enchant !is DebugAugment }
                    .map { enchant -> SpellScrollItem.createSpellScroll(enchant as ScepterAugment) }
                    .toList()
                )
            }.build())
    }

    fun registerAll() {
    }
}
