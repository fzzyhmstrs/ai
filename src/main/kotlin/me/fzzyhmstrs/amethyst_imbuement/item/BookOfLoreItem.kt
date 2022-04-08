package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterAugment
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
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

class BookOfLoreItem(settings: Settings, _ttn: String, _glint: Boolean) : Item(settings) {

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
            tooltip.add(TranslatableText("lore_book.augment").formatted(Formatting.GOLD).append(TranslatableText("enchantment.amethyst_imbuement.$bola").formatted(Formatting.GOLD)))
            val type = ScepterObject.getAugmentType(bola)
            if (type == SpellType.NULL){
                tooltip.add(TranslatableText("lore_book.${type.str()}").formatted(type.fmt()))
            } else {
                val lvl = ScepterObject.getAugmentMinLvl(bola)
                tooltip.add(TranslatableText("lore_book.${type.str()}").formatted(type.fmt()).append(LiteralText(lvl.toString())))
            }
            val item = ScepterObject.getAugmentItem(bola)
            val itemText = item.name.shallowCopy().formatted(Formatting.WHITE)
            tooltip.add(TranslatableText("lore_book.key_item").formatted(Formatting.WHITE).append(itemText))
            val xpLevels = ScepterObject.getAugmentImbueLevel(bola)
            tooltip.add(TranslatableText("lore_book.xp_level").formatted(Formatting.WHITE).append(xpLevels.toString()))
            val cooldown = ScepterObject.getAugmentCooldown(bola).toFloat() / 20.0F
            tooltip.add(TranslatableText("lore_book.cooldown").formatted(Formatting.WHITE).append(LiteralText(cooldown.toString())).append(TranslatableText("lore_book.cooldown1").formatted(Formatting.WHITE)))
            val manaCost = ScepterObject.getAugmentManaCost(bola)
            tooltip.add(TranslatableText("lore_book.mana_cost").formatted(Formatting.WHITE).append(LiteralText(manaCost.toString())))
            val bole = Registry.ENCHANTMENT.get(Identifier(AI.MOD_ID,bola))
            if (bole is ScepterAugment) {
                val spellTier = bole.getTier()
                tooltip.add(
                    TranslatableText("lore_book.tier").formatted(Formatting.WHITE)
                        .append(LiteralText(spellTier.toString()))
                )
            }
        } else {
            tooltip.add(
                TranslatableText("item.amethyst_imbuement.$ttn.tooltip1").formatted(
                    Formatting.WHITE,
                    Formatting.ITALIC
                )
            )
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        if (world !is ServerWorld) return TypedActionResult.fail(stack)
        val nbt = stack.orCreateNbt
        if(!nbt.contains(NbtKeys.LORE_KEY.str())){
            val nbtTemp = ScepterObject.bookOfLoreNbtGenerator(LoreTier.LOW_TIER)
            val enchant = readAugNbt(NbtKeys.LORE_KEY.str(),nbtTemp)
            writeAugNbt(NbtKeys.LORE_KEY.str(),enchant,nbt)
            world.playSound(null,user.blockPos,SoundEvents.ITEM_BOOK_PAGE_TURN,SoundCategory.NEUTRAL,0.7f,1.0f)
            return TypedActionResult.success(stack)
        }
        return TypedActionResult.pass(stack)
    }

    fun addLoreKeyForREI(stack: ItemStack,augment: String){
        val nbt = stack.orCreateNbt
        if(!nbt.contains(NbtKeys.LORE_KEY.str())) {
            val identifier = Identifier(augment)
            val aug = identifier.path
            writeAugNbt(NbtKeys.LORE_KEY.str(),aug,nbt)
        }
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