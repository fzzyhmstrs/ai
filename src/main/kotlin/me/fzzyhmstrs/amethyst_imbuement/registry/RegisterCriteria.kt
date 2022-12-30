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
    val DISENCHANT_USE = TickCriterion(Identifier(AI.MOD_ID,"disenchant_use"))
    val DISENCHANTING_PILLARS = TickCriterion(Identifier(AI.MOD_ID,"disenchanting_pillars"))
    val DISENCHANTING_MAX = TickCriterion(Identifier(AI.MOD_ID,"disenchanting_max"))
    val ALTAR_USE = TickCriterion(Identifier(AI.MOD_ID,"altar_use"))
    val CANDLELIT_ALTAR = TickCriterion(Identifier(AI.MOD_ID,"candlelit_altar"))
    val CANDLELIT_MAX = TickCriterion(Identifier(AI.MOD_ID,"candlelit_max"))
    val IMBUING_USE = TickCriterion(Identifier(AI.MOD_ID,"imbuing_use"))
    val AMPED_UP = TickCriterion(Identifier(AI.MOD_ID,"amped_up"))
    val MASTER_RESEARCHER = TickCriterion(Identifier(AI.MOD_ID,"master_researcher"))
    val DEVOUT_CLERIC = TickCriterion(Identifier(AI.MOD_ID,"devout_cleric"))
    val AUGMENT_ITEM = AugmentCriterion(Identifier(AI.MOD_ID,"augment_item"))
    val ENHANCE = TickCriterion(Identifier(AI.MOD_ID,"enhance"))
    val IGNITE = TickCriterion(Identifier(AI.MOD_ID,"ignite"))

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
            if (AiConfig.items.giveGlisteringTome){
                //GIVE_IF_CONFIG.trigger(handler.player)
            }
        }
    }

}