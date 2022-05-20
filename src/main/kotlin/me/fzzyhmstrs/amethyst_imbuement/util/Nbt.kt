package me.fzzyhmstrs.amethyst_imbuement.util

import net.minecraft.nbt.NbtCompound

object Nbt {

    fun writeBoolNbt(key: String, state: Boolean, nbt: NbtCompound) {
        nbt.putBoolean(key, state)
    }
    fun readBoolNbt(key: String, nbt: NbtCompound): Boolean {
        return nbt.getBoolean(key)
    }
    fun writeIntNbt(key: String, input: Int, nbt: NbtCompound){
        nbt.putInt(key,input)
    }
    fun readIntNbt(key: String, nbt: NbtCompound): Int {
        return nbt.getInt(key)
    }
    fun writeStringNbt(key: String, input: String, nbt: NbtCompound){
        nbt.putString(key,input)
    }
    fun readStringNbt(key: String, nbt: NbtCompound): String {
        return nbt.getString(key)
    }
}