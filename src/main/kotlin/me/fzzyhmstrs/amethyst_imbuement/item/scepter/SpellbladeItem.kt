package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.item.DefaultAugmentSwordItem
import me.fzzyhmstrs.amethyst_core.scepter.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.Reactant
import me.fzzyhmstrs.amethyst_imbuement.item.Reagent
import me.fzzyhmstrs.amethyst_imbuement.item.SpellScrollItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.recipe.AltarRecipe
import net.minecraft.block.BlockState
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class SpellbladeItem(
    material: ScepterToolMaterial,
    damage: Int,
    attackSpeed: Float,
    settings: Settings)
    :
    DefaultAugmentSwordItem(material, damage, attackSpeed, settings),
    Reactant,
    Reagent
{
    override val fallbackId: Identifier = Identifier(AI.MOD_ID, "magic_missile")

    override fun postHit(stack: ItemStack, target: LivingEntity?, attacker: LivingEntity): Boolean {
        if (stack.maxDamage - stack.damage > 1) {
            stack.damage(1, attacker) { e: LivingEntity ->
                e.sendEquipmentBreakStatus(
                    EquipmentSlot.MAINHAND
                )
            }
        }
        return true
    }
    override fun postMine(
        stack: ItemStack,
        world: World?,
        state: BlockState,
        pos: BlockPos?,
        miner: LivingEntity
    ): Boolean {
        if (stack.maxDamage - stack.damage > 2) {
            if (state.getHardness(world, pos) != 0.0f) {
                stack.damage(2, miner) { e: LivingEntity ->
                    e.sendEquipmentBreakStatus(
                        EquipmentSlot.MAINHAND
                    )
                }
            }
        }
        return true
    }

    override fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?): Boolean {
        if (type != AltarRecipe.Type) return true
        for (reagent in reagents){
            if (reagent.item is SpellScrollItem){
                if (reagent.nbt?.contains(RegisterItem.SPELL_SCROLL.SPELL) != true) return false
                if ((reagent.nbt?.contains(RegisterItem.SPELL_SCROLL.DISENCHANTED) != true)) return false
                var count = 0
                for (reagent1 in reagents){
                    if (reagent1.isOf(RegisterItem.KNOWLEDGE_POWDER)){
                        count++
                    }
                }
                return count > 0
            }
        }
        return true
    }

    override fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?) {
        for (reagent in reagents){
            if (reagent.item is SpellScrollItem){
                var count = 0
                for (reagent1 in reagents){
                    if (reagent1.isOf(RegisterItem.KNOWLEDGE_POWDER)){
                        count++
                    }
                }
                if (count == 0) return
                val nbt = reagent.nbt?:return
                val spellString = nbt.getString(RegisterItem.SPELL_SCROLL.SPELL)
                val spell = Registries.ENCHANTMENT.get(Identifier(spellString))?:return
                stack.addEnchantment(spell,1)
                return
            }
        }
    }
}