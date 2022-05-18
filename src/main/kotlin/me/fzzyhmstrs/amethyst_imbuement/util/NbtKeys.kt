package me.fzzyhmstrs.amethyst_imbuement.util

enum class NbtKeys {

    TOTEM{
        override fun str(): String {
            return "totem_active"
        }
    },
    ANGELIC{
        override fun str(): String {
            return "angelic"
        }
    },
    ALTAR_KEY{
        override fun str(): String {
            return "altar_used"
        }
    },
    SCEPTER_ID{
        override fun str(): String {
            return "scepter_id"
        }
    },
    LORE_KEY{
        override fun str(): String {
            return "book_of_lore_augment"
        }
    },
    ACTIVE_ENCHANT{
        override fun str(): String {
            return "active_enchant_id"
        }
    },
    DISENCHANT_COUNT{
        override fun str(): String {
            return "disenchant_count"
        }
    },
    MODIFIERS{
        override fun str(): String {
            return "modifiers"
        }
    },
    MODIFIER_ID{
        override fun str(): String {
            return "modifier_id"
        }
    };

    abstract fun str(): String
}