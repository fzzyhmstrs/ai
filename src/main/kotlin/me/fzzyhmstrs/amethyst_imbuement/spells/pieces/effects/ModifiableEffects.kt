package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.entity.LivingEntity
import net.minecraft.world.World
import kotlin.math.min

object ModifiableEffects {

    //used for the ticking of the entity
    val SHOCKING_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("shocking_effect")) {entity,owner,context-> ShockingEffect.shock(entity,owner,context)}
    val STATIC_SHOCK_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("static_shock_effect")) {entity,owner,context-> ShockingEffect.staticShock(entity,owner,context)}
    val CHAIN_LIGHTNING_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("chain_lightning_effect")) {entity,owner,context-> ShockingEffect.chainLightning(entity,owner,context)}
    val GROW_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("grow_effect")) {entity,owner,context-> AbundanceEffect.grow(entity,owner,context)}
    val GUST_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("gust_effect")) {entity,owner,context-> SummonChickenEffect.gust(entity,owner,context)}
    val CALL_HOSTILES_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("call_hostiles_effect")) {entity,owner,context-> BodySwapEffect.callHostiles(entity,owner,context)}
    val CALL_SUMMONS_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("call_summons_effect")) {entity,owner,context-> BodySwapEffect.callSummons(entity,owner,context)}
    val SHINE_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("shine_effect")) {entity,owner,context-> ShineEffect.shine(entity,owner,context)}
    val ECHO_EFFECT = ModifiableEffect.createAndRegisterConsumer(AI.identity("echo_effect")) {entity,owner,context-> EchoingEffect.resonate(entity,owner,context)}
    fun getRndEntityList(world: World, list: MutableList<LivingEntity>, level: Int): MutableList<LivingEntity>{
        if (list.isNotEmpty()){
            val listTmp: MutableList<LivingEntity> = mutableListOf()
            val startSize = min(level, list.size)
            var remaining = min(level, list.size)
            val leftOver = if(list.size < level) {
                level - list.size
            } else {
                0
            }
            for (i in 1..startSize) {
                val rnd = world.random.nextInt(remaining)
                val ent = list[rnd]
                listTmp.add(ent)
                list.remove(ent)
                remaining--
            }
            return listTmp
        } else {
            return list
        }
    }

}
