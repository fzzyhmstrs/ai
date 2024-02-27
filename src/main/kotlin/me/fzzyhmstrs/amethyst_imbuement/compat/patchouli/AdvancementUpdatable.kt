package me.fzzyhmstrs.amethyst_imbuement.compat.patchouli

import vazkii.patchouli.client.book.BookEntry

interface AdvancementUpdatable {
    fun updateLockStatus(entry: BookEntry): Boolean
}