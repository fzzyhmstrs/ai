package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.scepter_util.CustomCriterion
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.util.AugmentCriterion
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.util.Identifier

object RegisterCriteria {

    val GIVE_IF_CONFIG = CustomCriterion(Identifier(AI.MOD_ID,"give_if_config"))
    val DISENCHANT_USE = CustomCriterion(Identifier(AI.MOD_ID,"disenchant_use"))
    val DISENCHANTING_PILLARS = CustomCriterion(Identifier(AI.MOD_ID,"disenchanting_pillars"))
    val DISENCHANTING_MAX = CustomCriterion(Identifier(AI.MOD_ID,"disenchanting_max"))
    val ALTAR_USE = CustomCriterion(Identifier(AI.MOD_ID,"altar_use"))
    val CANDLELIT_ALTAR = CustomCriterion(Identifier(AI.MOD_ID,"candlelit_altar"))
    val CANDLELIT_MAX = CustomCriterion(Identifier(AI.MOD_ID,"candlelit_max"))
    val IMBUING_USE = CustomCriterion(Identifier(AI.MOD_ID,"imbuing_use"))
    val AMPED_UP = CustomCriterion(Identifier(AI.MOD_ID,"amped_up"))
    val MASTER_RESEARCHER = CustomCriterion(Identifier(AI.MOD_ID,"master_researcher"))
    val DEVOUT_CLERIC = CustomCriterion(Identifier(AI.MOD_ID,"devout_cleric"))
    val AUGMENT_ITEM = AugmentCriterion(Identifier(AI.MOD_ID,"augment_item"))
    val ENHANCE = CustomCriterion(Identifier(AI.MOD_ID,"enhance"))
    val IGNITE = CustomCriterion(Identifier(AI.MOD_ID,"ignite"))

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