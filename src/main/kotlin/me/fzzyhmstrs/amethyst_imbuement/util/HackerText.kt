package me.fzzyhmstrs.amethyst_imbuement.util

import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.lang.StringBuilder
import net.minecraft.world.World
import java.util.*

class HackerText(private val baseText: String, private val speed: Int) {
    private val wholeText: String
    private var counter = 0
    private val totalCount: Int
    private val baseTextLen: Int
    private val wholeTextLen: Int
    val hackerText: MutableText
        get() {
            if (counter == totalCount) {
                return LiteralText(baseText)
            }
            counter++
            val indexOffset = counter / speed
            val start = wholeTextLen - baseTextLen - indexOffset
            val end = wholeTextLen - indexOffset
            val subStr = wholeText.substring(start, end)

            return LiteralText(subStr)
        }

    init {
        baseTextLen = baseText.length
        val len = baseText.length * 2
        val builder = StringBuilder(len)
        val random = Random(System.currentTimeMillis())
            val list = listOf('\\', '/', '<', '>')
            for (i in 0 until len) {
                val index = random.nextInt(4)
                builder.append(list[index])
            }
            val hackerText: String = builder.toString()
        wholeText = baseText + hackerText
        wholeTextLen = wholeText.length
        totalCount = hackerText.length * speed
    }
}