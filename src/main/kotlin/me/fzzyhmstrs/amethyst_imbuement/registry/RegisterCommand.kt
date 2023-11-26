package me.fzzyhmstrs.amethyst_imbuement.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object RegisterCommand {

    fun registerClient(){
        ClientCommandRegistrationCallback.EVENT.register{dispatcher, _ ->
            registerClientCommands(dispatcher)
        }
    }

    private fun registerClientCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>){
        dispatcher.register(
            ClientCommandManager.literal("spellHud")
                .then(
                    ClientCommandManager.literal("topLeft")
                        .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
                            .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
                                .executes{ context ->
                                    hudUpdate(AiConfig.Hud.Corner.TOP_LEFT,context)
                                }
                            )
                        )
                        .executes{ context ->
                            hudUpdate(AiConfig.Hud.Corner.TOP_LEFT,0,0,context)
                        }
                )
                .then(
                    ClientCommandManager.literal("bottomLeft")
                        .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
                            .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
                                .executes{ context ->
                                    hudUpdate(AiConfig.Hud.Corner.BOTTOM_LEFT,context)
                                }
                            )
                        )
                        .executes{ context ->
                            hudUpdate(AiConfig.Hud.Corner.BOTTOM_LEFT,0,0,context)
                        }
                )
                .then(
                    ClientCommandManager.literal("topRight")
                        .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
                            .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
                                .executes{ context ->
                                    hudUpdate(AiConfig.Hud.Corner.TOP_RIGHT,context)
                                }
                            )
                        )
                        .executes{ context ->
                            hudUpdate(AiConfig.Hud.Corner.TOP_RIGHT,0,0,context)
                        }
                )
                .then(
                    ClientCommandManager.literal("bottomRight")
                        .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
                            .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
                                .executes{ context ->
                                    hudUpdate(AiConfig.Hud.Corner.BOTTOM_RIGHT,context)
                                }
                            )
                        )
                        .executes{ context ->
                            hudUpdate(AiConfig.Hud.Corner.BOTTOM_RIGHT,0,0,context)
                        }
                )
                .then(
                    ClientCommandManager.literal("bottomMiddle")
                        .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
                            .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
                                .executes{ context ->
                                    hudUpdate(AiConfig.Hud.Corner.BOTTOM_MIDDLE,context)
                                }
                            )
                        )
                )
                .then(
                    ClientCommandManager.literal("reset")
                        .executes{ context ->
                            hudUpdate(AiConfig.Hud.Corner.TOP_LEFT,0,0,context)
                        }
                )
                .then(
                    ClientCommandManager.literal("enable")
                        .executes{ context ->
                            AiConfig.hud.showHud.validateAndSet(true)
                            AiConfig.saveHud()
                            context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.enable"))
                            1
                        }
                )
                .then(
                    ClientCommandManager.literal("disable")
                        .executes{ context ->
                            AiConfig.hud.showHud.validateAndSet(false)
                            AiConfig.saveHud()
                            context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.disable"))
                            1
                        }
                )
        )
    }

    private fun hudUpdate(corner: AiConfig.Hud.Corner,context: CommandContext<FabricClientCommandSource>): Int{
        val x = IntegerArgumentType.getInteger(context, "x")
        val y = IntegerArgumentType.getInteger(context, "y")
        return hudUpdate(corner, x, y, context)
    }

    private fun hudUpdate(corner: AiConfig.Hud.Corner,x: Int, y: Int, context: CommandContext<FabricClientCommandSource>): Int{

        if (!corner.validate(x,y,context.source.client.window.scaledWidth,context.source.client.window.scaledHeight)){
            context.source.sendError(AcText.translatable("commands.amethyst_imbuement.failed.failed_validation",x,y))
            return 0
        }
        AiConfig.hud.hudCorner.validateAndSet(corner)
        AiConfig.hud.hudX.validateAndSet(x)
        AiConfig.hud.hudY.validateAndSet(y)
        AiConfig.saveHud()
        context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success", corner.name,x,y))
        return 1
    }
}