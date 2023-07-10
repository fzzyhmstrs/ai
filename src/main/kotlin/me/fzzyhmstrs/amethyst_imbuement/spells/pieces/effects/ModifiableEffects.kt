package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.util.Identifier

object ModifiableEffects {

    //used for the ticking of the entity
    val SHOCKING_EFFECT = ModifiableEffect.createAndRegisterConsumer(Identifier(AI.MOD_ID,"shocking_effect")) {entity,owner,context-> ShockingEffect.shock(entity,owner,context)}
    val STATIC_SHOCK_EFFECT = ModifiableEffect.createAndRegisterConsumer(Identifier(AI.MOD_ID,"static_shock_effect")) {entity,owner,context-> ShockingEffect.staticShock(entity,owner,context)}

}