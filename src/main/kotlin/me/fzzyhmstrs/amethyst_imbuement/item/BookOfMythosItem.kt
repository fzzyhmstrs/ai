package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class BookOfMythosItem(settings: Settings, _ttn: String, _glint: Boolean) : Item(settings) {

    private val ttn: String = _ttn
    private val glint: Boolean = _glint

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        val nbt = stack.orCreateNbt
        if (nbt?.contains(NbtKeys.LORE_KEY.str()) == true){
            val bola = readAugNbt(NbtKeys.LORE_KEY.str(),nbt)
            tooltip.add(TranslatableText("item.amethyst_imbuement.$ttn.augment").formatted(Formatting.GOLD).append(TranslatableText("enchantment.amethyst_imbuement.$bola").formatted(Formatting.GOLD)))
            val type = ScepterObject.getAugmentType(bola)
            if (type == SpellType.NULL){
                tooltip.add(TranslatableText("item.amethyst_imbuement.$ttn.${type.str()}").formatted(type.fmt()))
            } else {
                val lvl = ScepterObject.getAugmentMinLvl(bola)
                tooltip.add(TranslatableText("item.amethyst_imbuement.$ttn.${type.str()}").formatted(type.fmt()).append(
                    LiteralText(lvl.toString())
                ))
            }
            val item = ScepterObject.getAugmentItem(bola)
            val itemText = item.name.shallowCopy().formatted(Formatting.WHITE)
            tooltip.add(TranslatableText("item.amethyst_imbuement.$ttn.key_item").formatted(Formatting.WHITE))
            tooltip.add(itemText)
        } else {
            tooltip.add(
                TranslatableText("item.amethyst_imbuement.$ttn.tooltip1").formatted(
                    Formatting.WHITE,
                    Formatting.ITALIC
                )
            )
        }
    }

    /*override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (world !is ServerWorld) return
        if (tickCounter < 40){
            tickCounter++
        } else {
            val nbt = stack.orCreateNbt
            if(!nbt.contains("book_of_lore_augment")){
                val nbtTemp = ScepterObject.bookOfLoreNbtGenerator(LoreTier.LOW_TIER)
                val enchant = readAugNbt("book_of_lore_augment",nbtTemp)
                writeAugNbt("book_of_lore_augment",enchant,nbt)
            }
            tickCounter = 0
        }

    }*/

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        println("made it mythos!")
        if (world !is ServerWorld) return TypedActionResult.fail(stack)
        val nbt = stack.orCreateNbt
        if(!nbt.contains(NbtKeys.LORE_KEY.str())){
            val nbtTemp = ScepterObject.bookOfLoreNbtGenerator(LoreTier.HIGH_TIER)
            val enchant = readAugNbt(NbtKeys.LORE_KEY.str(),nbtTemp)
            writeAugNbt("book_of_lore_augment",enchant,nbt)
        }
        return TypedActionResult.success(stack)
    }

    override fun hasGlint(stack: ItemStack): Boolean {
        return if (glint) {
            true
        } else {
            super.hasGlint(stack)
        }
    }

    private fun writeAugNbt(key: String, enchant: String, nbt: NbtCompound){
        nbt.putString(key,enchant)
    }
    private fun readAugNbt(key: String, nbt: NbtCompound): String {
        return nbt.getString(key)
    }
}