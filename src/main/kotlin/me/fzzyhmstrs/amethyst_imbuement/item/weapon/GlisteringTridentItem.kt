package me.fzzyhmstrs.amethyst_imbuement.item.weapon

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.GlisteringTridentEntity
import me.fzzyhmstrs.fzzy_core.entity_util.BasicCustomTridentEntity
import me.fzzyhmstrs.fzzy_core.item_util.BasicCustomTridentItem
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolMaterial
import net.minecraft.world.World

class GlisteringTridentItem(settings: Settings) : BasicCustomTridentItem<BasicCustomTridentEntity>(AiConfig.materials.tools.glistering,settings) {

    override fun isFireproof(): Boolean {
        return true
    }

    override fun makeTridentEntity(
        material: ToolMaterial,
        world: World,
        livingEntity: LivingEntity,
        stack: ItemStack
    ): BasicCustomTridentEntity {
        return GlisteringTridentEntity(world, livingEntity, stack).also { it.damage = material.attackDamage.toDouble() }
    }
}