package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.util.Identifier

@Suppress("unused")
object RegisterTag {

    val POULTRYMORPH_IGNORES = FzzyPort.ENTITY_TYPE.tagOf(AI.identity("poultrymorph_ignores"))

    val DISENCHANTING_BLACKLIST = FzzyPort.ENCHANTMENT.tagOf(AI.identity("disenchanting_blacklist"))

    val GEMS_TAG = FzzyPort.ITEM.tagOf(Identifier("c","gems"))
    val SKULLS_TAG = FzzyPort.ITEM.tagOf(Identifier("c","skulls"))
    val STEEL_BOOTS_TAG = FzzyPort.ITEM.tagOf(Identifier("c","steel_boots"))
    val STEEL_CHESTPLATES_TAG = FzzyPort.ITEM.tagOf(Identifier("c","steel_chestplates"))
    val STEEL_HELMETS_TAG = FzzyPort.ITEM.tagOf(Identifier("c","steel_helmets"))
    val STEEL_LEGGINGS_TAG = FzzyPort.ITEM.tagOf(Identifier("c","steel_leggings"))
    val STEEL_INGOTS_TAG = FzzyPort.ITEM.tagOf(Identifier("c","steel_ingots"))
    val STEEL_BLOCKS_TAG = FzzyPort.ITEM.tagOf(Identifier("c","steel_blocks"))
    val FOCUS_REAGENTS_TAG = FzzyPort.ITEM.tagOf(AI.identity("focus_reagents"))
    val ASPECT_GEMS_TAG = FzzyPort.ITEM.tagOf(AI.identity("aspect_gems"))
    val PILLARS_ITEM_TAG = FzzyPort.ITEM.tagOf(AI.identity("disenchant_pillar_blocks"))
    val SHINE_LIGHTS_ITEM_TAG = FzzyPort.ITEM.tagOf(AI.identity("shine_lights"))
    val CRYSTALLIZED_LIGHTS_ITEM_TAG = FzzyPort.ITEM.tagOf(AI.identity("crystallized_lights"))
    val UNLOCKABLES_ITEM_TAG = FzzyPort.ITEM.tagOf(AI.identity("unlockables"))

    val PILLARS_TAG = FzzyPort.BLOCK.tagOf(AI.identity("disenchant_pillar_blocks"))
    val SHINE_LIGHTS_TAG = FzzyPort.BLOCK.tagOf(AI.identity("shine_lights"))
    val CRYSTALLIZED_LIGHTS_TAG = FzzyPort.BLOCK.tagOf(AI.identity("crystallized_lights"))
    val DRACONIC_VISION_BLACKLIST = FzzyPort.BLOCK.tagOf(AI.identity("draconic_vision_blacklist"))
    val EXCAVATE_BLACKLIST = FzzyPort.BLOCK.tagOf(AI.identity("excavate_blacklist"))
    val UNLOCKABLES_TAG = FzzyPort.BLOCK.tagOf(AI.identity("unlockables"))
    val WARDING_CANDLES_TAG = FzzyPort.BLOCK.tagOf(AI.identity("warding_candles"))

    val EQUINOX_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("equinox_scepters"))
    val LETHALITY_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("lethality_scepters"))
    val RESONANCE_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("resonance_scepters"))
    val SOJOURN_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("sojourn_scepters"))
    val AEGIS_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("aegis_scepters"))
    val REDEMPTION_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("redemption_scepters"))
    val FURY_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("fury_scepters"))
    val WIT_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("wit_scepters"))
    val GRACE_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("grace_scepters"))

    val ALL_FURY_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("all_fury_scepters"))
    val ALL_WIT_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("all_wit_scepters"))
    val ALL_GRACE_SCEPTERS_TAG = FzzyPort.ITEM.tagOf(AI.identity("all_grace_scepters"))

    val CROSSBOWS_TAG = FzzyPort.ITEM.tagOf(AI.identity("custom_crossbows"))
    val HEADBANDS_TAG = FzzyPort.ITEM.tagOf(AI.identity("headbands"))
    val AMULETS_TAG = FzzyPort.ITEM.tagOf(AI.identity("amulets"))
    val RINGS_TAG = FzzyPort.ITEM.tagOf(AI.identity("rings"))
    val TOTEMS_TAG = FzzyPort.ITEM.tagOf(AI.identity("totems"))
    val SNIPER_BOWS_TAG = FzzyPort.ITEM.tagOf(AI.identity("sniper_bows"))
    val WARDS_TAG = FzzyPort.ITEM.tagOf(AI.identity("wards"))
    val ALL_WARDS_TAG = FzzyPort.ITEM.tagOf(AI.identity("all_wards"))

    fun registerAll(){}

}
