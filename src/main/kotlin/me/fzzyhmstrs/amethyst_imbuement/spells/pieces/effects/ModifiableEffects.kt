package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World
import kotlin.math.min

object ModifiableEffects {

    //used for the ticking of the entity
    val SHOCKING_EFFECT = ModifiableEffect.createAndRegisterConsumer(Identifier(AI.MOD_ID,"shocking_effect")) {entity,owner,context-> ShockingEffect.shock(entity,owner,context)}
    val STATIC_SHOCK_EFFECT = ModifiableEffect.createAndRegisterConsumer(Identifier(AI.MOD_ID,"static_shock_effect")) {entity,owner,context-> ShockingEffect.staticShock(entity,owner,context)}


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