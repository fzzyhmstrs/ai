package me.fzzyhmstrs.amethyst_imbuement.util
@Deprecated("moving to amethyst_core")

fun <T> MutableList<T>.addIfDistinct(element: T) {
    if (!this.contains(element)){
        this.add(element)
    }
}

@Deprecated("moving to amethyst_core")
enum class LoreTier {
    LOW_TIER{
        private val bookOfLoreListT1: MutableList<String> =  mutableListOf()

        override fun addToList(string: String) {
            bookOfLoreListT1.addIfDistinct(string)
        }
        override fun list(): List<String> {
            return bookOfLoreListT1
        }
    },
    HIGH_TIER{
        private val bookOfLoreListT2: MutableList<String> =  mutableListOf()

        override fun addToList(string: String) {
            bookOfLoreListT2.addIfDistinct(string)
        }
        override fun list(): List<String> {
            return bookOfLoreListT2
        }
    },
    EXTREME_TIER{
        private val bookOfLoreListT3: MutableList<String> =  mutableListOf()

        override fun addToList(string: String) {
            bookOfLoreListT3.addIfDistinct(string)
        }
        override fun list(): List<String> {
            return bookOfLoreListT3
        }
    },
    ANY_TIER{
        private val bookOfLoreListT12: MutableList<String> =  mutableListOf()

        override fun addToList(string: String) {
            bookOfLoreListT12.addIfDistinct(string)
        }
        override fun list(): List<String> {
            return bookOfLoreListT12
        }

    },
    NO_TIER{
        override fun addToList(string: String) {
        }

        override fun list(): List<String> {
            return listOf()
        }
    };

    abstract fun addToList(string: String)
    abstract fun list(): List<String>
}