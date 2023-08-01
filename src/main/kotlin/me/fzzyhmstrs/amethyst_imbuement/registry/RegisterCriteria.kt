package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.util.AugmentCriterion
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.advancement.criterion.TickCriterion

object RegisterCriteria {

    val GIVE_IF_CONFIG = TickCriterion(AI.identity("give_if_config"))
    val DISENCHANT_USE = TickCriterion(AI.identity("disenchant_use"))
    val DISENCHANTING_PILLARS = TickCriterion(AI.identity("disenchanting_pillars"))
    val DISENCHANTING_MAX = TickCriterion(AI.identity("disenchanting_max"))
    val ALTAR_USE = TickCriterion(AI.identity("altar_use"))
    val CANDLELIT_ALTAR = TickCriterion(AI.identity("candlelit_altar"))
    val CANDLELIT_MAX = TickCriterion(AI.identity("candlelit_max"))
    val IMBUING_USE = TickCriterion(AI.identity("imbuing_use"))
    val AMPED_UP = TickCriterion(AI.identity("amped_up"))
    val MASTER_RESEARCHER = TickCriterion(AI.identity("master_researcher"))
    val DEVOUT_CLERIC = TickCriterion(AI.identity("devout_cleric"))
    val AUGMENT_ITEM = AugmentCriterion(AI.identity("augment_item"))
    val ENHANCE = TickCriterion(AI.identity("enhance"))
    val IGNITE = TickCriterion(AI.identity("ignite"))

    fun registerAll(){
        Criteria.register(GIVE_IF_CONFIG)
        Criteria.register(DISENCHANT_USE)
        Criteria.register(DISENCHANTING_PILLARS)
        Criteria.register(DISENCHANTING_MAX)
        Criteria.register(ALTAR_USE)
        Criteria.register(CANDLELIT_ALTAR)
        Criteria.register(CANDLELIT_MAX)
        Criteria.register(IMBUING_USE)
        Criteria.register(AMPED_UP)
        Criteria.register(MASTER_RESEARCHER)
        Criteria.register(DEVOUT_CLERIC)
        Criteria.register(AUGMENT_ITEM)
        Criteria.register(ENHANCE)
        Criteria.register(IGNITE)

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            if (AiConfig.items.giveGlisteringTome.get()){
                GIVE_IF_CONFIG.trigger(handler.player)
            }
        }
    }

}