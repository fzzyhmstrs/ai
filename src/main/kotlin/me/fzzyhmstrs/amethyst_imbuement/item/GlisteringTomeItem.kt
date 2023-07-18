package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.item.Item


class GlisteringTomeItem(settings: Settings): Item(settings) {
    override fun use(world: World, playerEntity: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        import vazkii.patchouli.api.PatchouliAPI

        if (!world.isClient) {
            PatchouliAPI.get().openBookGUI(playerEntity as ServerPlayerEntity, AI.identity("glistering_tome"))
            return TypedActionResult.success(playerEntity.getStackInHand(hand))
        }
        return TypedActionResult.consume(playerEntity.getStackInHand(hand))
    }
}
