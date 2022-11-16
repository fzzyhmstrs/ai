package me.fzzyhmstrs.amethyst_imbuement.util

import me.fzzyhmstrs.amethyst_imbuement.AI
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.logging.*

object LoggerUtil {

    fun getLogger(): Logger{
        val logger = Logger.getLogger(AI.MOD_ID)
        val handler = ConsoleHandler()
        handler.formatter = LogFormat()
        handler.level = Level.ALL
        logger.level = Level.ALL
        logger.addHandler(handler)
        logger.useParentHandlers = false
        return logger
    }

    private class LogFormat: Formatter() {
        override fun format(record: LogRecord): String {
            val zdt = ZonedDateTime.ofInstant(record.instant, ZoneId.systemDefault())
            val a = "[${zdt.hour}:${zdt.minute}:${zdt.second}] "
            val b = "[Amethyst Imbuement/${record.level.localizedName}] [${record.sourceClassName}]: "
            val c = "${record.message}\n"
            return a + b + c
        }
    }

}