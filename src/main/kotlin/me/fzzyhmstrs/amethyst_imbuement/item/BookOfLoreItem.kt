package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

open class BookOfLoreItem(settings: Settings, _ttn: String, _glint: Boolean) : Item(settings) {

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
        if (nbt.contains(NbtKeys.LORE_KEY.str())){
            val bolaNbtString = Nbt.readStringNbt(NbtKeys.LORE_KEY.str(),nbt)
            val bola = if (Identifier(bolaNbtString).namespace == "minecraft"){
                Identifier(AI.MOD_ID,Identifier(bolaNbtString).path).toString()
            } else {
                bolaNbtString
            }
            tooltip.add(Text.translatable("lore_book.augment").formatted(Formatting.GOLD).append(TranslatableText("enchantment.amethyst_imbuement.${Identifier(bola).path}").formatted(Formatting.GOLD)))
            tooltip.add(Text.translatable("enchantment.amethyst_imbuement.${Identifier(bola).path}.desc").formatted(Formatting.WHITE))
            val type = ScepterHelper.getAugmentType(bola)
            if (type == SpellType.NULL){
                tooltip.add(Text.translatable("lore_book.${type.str()}").formatted(type.fmt()))
            } else {
                val lvl = ScepterHelper.getAugmentMinLvl(bola)
                tooltip.add(Text.translatable("lore_book.${type.str()}").formatted(type.fmt()).append(LiteralText(lvl.toString())))
            }
            val item = ScepterHelper.getAugmentItem(bola)
            val itemText = item.name.shallowCopy().formatted(Formatting.WHITE)
            tooltip.add(Text.translatable("lore_book.key_item").formatted(Formatting.WHITE).append(itemText))
            val xpLevels = ScepterHelper.getAugmentImbueLevel(bola)
            tooltip.add(Text.translatable("lore_book.xp_level").formatted(Formatting.WHITE).append(xpLevels.toString()))
            val cooldown = ScepterHelper.getAugmentCooldown(bola).toFloat() / 20.0F
            tooltip.add(Text.translatable("lore_book.cooldown").formatted(Formatting.WHITE).append(LiteralText(cooldown.toString())).append(TranslatableText("lore_book.cooldown1").formatted(Formatting.WHITE)))
            val manaCost = ScepterHelper.getAugmentManaCost(bola)
            tooltip.add(Text.translatable("lore_book.mana_cost").formatted(Formatting.WHITE).append(LiteralText(manaCost.toString())))
            val bole = Registry.ENCHANTMENT.get(Identifier(bola))
            if (bole is ScepterAugment) {
                val spellTier = bole.getTier()
                tooltip.add(
                    Text.translatable("lore_book.tier").formatted(Formatting.WHITE)
                        .append(Text.literal(spellTier.toString()))
                )
            }
        } else {
            tooltip.add(
                Text.translatable("item.amethyst_imbuement.$ttn.tooltip1").formatted(
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
            val nbtTemp = ScepterHelper.bookOfLoreNbtGenerator(LoreTier.LOW_TIER)
            val enchant = Nbt.readStringNbt(NbtKeys.LORE_KEY.str(),nbtTemp)
            Nbt.writeStringNbt(NbtKeys.LORE_KEY.str(),enchant,nbt)
            world.playSound(null,user.blockPos,SoundEvents.ITEM_BOOK_PAGE_TURN,SoundCategory.NEUTRAL,0.7f,1.0f)
            return TypedActionResult.success(stack)
        }
        return TypedActionResult.pass(stack)
    }

    fun addLoreKeyForREI(stack: ItemStack,augment: String){
        val nbt = stack.orCreateNbt
        if(!nbt.contains(NbtKeys.LORE_KEY.str())) {
            Nbt.writeStringNbt(NbtKeys.LORE_KEY.str(),augment,nbt)
        }
    }

    override fun hasGlint(stack: ItemStack): Boolean {
        return if (glint) {
            true
        } else {
            super.hasGlint(stack)
        }
    }
}
