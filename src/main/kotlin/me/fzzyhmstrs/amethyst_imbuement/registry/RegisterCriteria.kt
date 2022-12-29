package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.util.AugmentCriterion
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.util.Identifier

object RegisterCriteria {

    val GIVE_IF_CONFIG = TickCriterion(Identifier(AI.MOD_ID,"give_if_config"))
    val DISENCHANTING_PILLARS = TickCriterion(Identifier(AI.MOD_ID,"disenchanting_pillars"))
    val DISENCHANTING_MAX = TickCriterion(Identifier(AI.MOD_ID,"disenchanting_max"))
    val CANDLELIT_ALTAR = TickCriterion(Identifier(AI.MOD_ID,"candlelit_altar"))
    val CANDLELIT_MAX = TickCriterion(Identifier(AI.MOD_ID,"candlelit_max"))
    val AMPED_UP = TickCriterion(Identifier(AI.MOD_ID,"amped_up"))
    val MASTER_RESEARCHER = TickCriterion(Identifier(AI.MOD_ID,"master_researcher"))
    val DEVOUT_CLERIC = TickCriterion(Identifier(AI.MOD_ID,"devout_cleric"))
    val AUGMENT_ITEM = AugmentCriterion(Identifier(AI.MOD_ID,"augment_item"))

    fun registerAll(){
        Criteria.register(GIVE_IF_CONFIG)
        Criteria.register(DISENCHANTING_PILLARS)
        Criteria.register(DISENCHANTING_MAX)
        Criteria.register(CANDLELIT_ALTAR)
        Criteria.register(AMPED_UP)
        Criteria.register(MASTER_RESEARCHER)
        Criteria.register(DEVOUT_CLERIC)
        Criteria.register(AUGMENT_ITEM)

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            if (AiConfig.items.giveGlisteringTome){
                //GIVE_IF_CONFIG.trigger(handler.player)
            }
        }
    }

}