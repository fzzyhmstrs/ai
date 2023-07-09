package me.fzzyhmstrs.amethyst_imbuement.scepter.pieces.effects

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.util.Identifier

object ModifiableEffects {

    //used for the ticking of the entity
    val SHOCKING_EFFECT = ModifiableEffect.createAndRegisterConsumer(Identifier(AI.MOD_ID,"shocking_effect")) {entity,owner,context-> ShockingEffect.shock(entity,owner,context)}
    val SHOCK_EXPLODE = ModifiableEffect.createAndRegisterConsumer(Identifier(AI.MOD_ID,"shock_explode")) {entity,owner,context-> ShockingEffect.finalShock(entity,owner,context)}

}