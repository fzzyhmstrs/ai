package me.fzzyhmstrs.amethyst_imbuement.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.screen.SpellRadialHud
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
                            AiConfig.hud.save()
                            context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.enable"))
                            1
                        }
                )
                .then(
                    ClientCommandManager.literal("disable")
                        .executes{ context ->
                            AiConfig.hud.showHud.validateAndSet(false)
                            AiConfig.hud.save()
                            context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.disable"))
                            1
                        }
                )
                .then(
                    ClientCommandManager.literal("spellHudSpacing")
                        .then(ClientCommandManager.argument("spacing", IntegerArgumentType.integer())
                            .executes{ context ->
                                val spacing = IntegerArgumentType.getInteger(context, "spacing")
                                AiConfig.hud.spellHudSpacing.validateAndSet(spacing)
                                val chk = AiConfig.hud.spellHudSpacing.get()
                                AiConfig.hud.save()
                                SpellRadialHud.markDirty()
                                if (spacing == chk)
                                    context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.spacing",spacing))
                                else
                                    context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.spacing_warning",spacing,chk))
                                1
                            }
                        )
                        .then(ClientCommandManager.literal("reset")
                            .executes{ context ->
                                AiConfig.hud.spellHudSpacing.reset()
                                AiConfig.hud.save()
                                SpellRadialHud.markDirty()
                                context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.spacing_reset"))
                                1
                            }
                        )
                )
                .then(
                    ClientCommandManager.literal("scrollChangesSpells")
                        .then(
                            ClientCommandManager.literal("enable")
                                .executes{ context ->
                                    AiConfig.hud.scrollChangesSpells.validateAndSet(true)
                                    AiConfig.hud.save()
                                    context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.scrollChangesSpells.enable"))
                                    1
                                }
                        )
                        .then(
                            ClientCommandManager.literal("disable")
                                .executes{ context ->
                                    AiConfig.hud.scrollChangesSpells.validateAndSet(false)
                                    AiConfig.hud.save()
                                    context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success.scrollChangesSpells.disable"))
                                    1
                                }
                        )
                )
        )
    }

    private fun hudUpdate(corner: AiConfig.Hud.Corner, context: CommandContext<FabricClientCommandSource>): Int{
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
            AiConfig.hud.save()
            context.source.sendFeedback(AcText.translatable("commands.amethyst_imbuement.success", corner.name,x,y))
        return 1
    }


}