package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.Nbt
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class BookOfMythosItem(settings: Settings, _ttn: String, _glint: Boolean) : BookOfLoreItem(settings, _ttn, _glint) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        if (world !is ServerWorld) return TypedActionResult.fail(stack)
        val nbt = stack.orCreateNbt
        if(!nbt.contains(NbtKeys.LORE_KEY.str())){
            val nbtTemp = ScepterObject.bookOfLoreNbtGenerator(LoreTier.HIGH_TIER)
            val enchant = Nbt.readStringNbt(NbtKeys.LORE_KEY.str(),nbtTemp)
            Nbt.writeStringNbt(NbtKeys.LORE_KEY.str(),enchant,nbt)
            world.playSound(null,user.blockPos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.NEUTRAL,0.7f,1.0f)
            return TypedActionResult.success(stack)
        }
        return TypedActionResult.pass(stack)
    }
}
