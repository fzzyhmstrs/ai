package me.fzzyhmstrs.amethyst_imbuement.util

import net.minecraft.util.Formatting

enum class SpellType {
    FURY{
        override fun str(): String {
            return "fury"
        }

        override fun fmt(): Formatting {
            return Formatting.RED
        }
    },
    GRACE{
        override fun str(): String {
            return "grace"
        }

        override fun fmt(): Formatting {
            return Formatting.GREEN
        }
    },
    WIT{
        override fun str(): String {
            return "wit"
        }

        override fun fmt(): Formatting {
            return Formatting.BLUE
        }
    },
    NULL{
        override fun str(): String {
            return "null"
        }

        override fun fmt(): Formatting {
            return Formatting.WHITE
        }
    };

    abstract fun str(): String
    abstract fun fmt(): Formatting
}