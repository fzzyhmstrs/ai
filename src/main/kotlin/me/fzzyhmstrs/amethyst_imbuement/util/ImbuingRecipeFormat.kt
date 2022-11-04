package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.gson.JsonObject

class ImbuingRecipeFormat {

    var type: String = "amethyst_imbuement:imbuing"
    var imbueA: JsonObject? = null
    var imbueB: JsonObject? = null
    var imbueC: JsonObject? = null
    var imbueD: JsonObject? = null
    var craftA: JsonObject? = null
    var craftB: JsonObject? = null
    var craftC: JsonObject? = null
    var craftD: JsonObject? = null
    var craftE: JsonObject? = null
    var craftF: JsonObject? = null
    var craftG: JsonObject? = null
    var craftH: JsonObject? = null
    var craftI: JsonObject? = null
    var title: String = ""
    var cost: Int = 0
    var augment: String = ""
    var resultA: String = ""
    var countA: Int = 0
    var transferEnchant: Boolean = false


}