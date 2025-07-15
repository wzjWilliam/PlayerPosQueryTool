package com.wzjwilliam.playerposquerytool;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public class PlayerPosCommand {

    ModConfig config = new ModConfig();

    public PlayerPosCommand() {
        config.loadConfig();
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("playerpos")
                .requires(source -> {
                    if (config.isRequestedOpLevel()) {
                        return source.hasPermissionLevel(2);
                    } else {
                        return true;
                    }
                })
                .then(CommandManager.literal("query")
                        .then(CommandManager.argument("players", EntityArgumentType.players()) // 支持多玩家
                                .executes(context -> {
                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "players");
                                    targets.forEach(player -> {
                                        sendPlayerPosition(context.getSource(), player);
                                        savePositionToLog(player); // 保存到日志
                                    });
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal(("config"))
                        .then(CommandManager.literal("needOpLevel")
                                .then(CommandManager.literal("enable")
                                        .executes(context -> {
                                            config.setRequestedOpLevel(true);
                                            context.getSource().sendFeedback(()->Text.translatable("playerposquerytool.command.hint.enable_op_require").formatted(Formatting.GREEN), false);
                                            config.saveConfig();
                                            return 1;
                                        })
                                )
                                .then(CommandManager.literal("disable")
                                        .executes(context -> {
                                            config.setRequestedOpLevel(false);
                                            context.getSource().sendFeedback(()->Text.translatable("playerposquerytool.command.hint.disable_op_require").formatted(Formatting.RED), false);
                                            config.saveConfig();
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    boolean requestedOpLevel = config.isRequestedOpLevel();
                                    Text message = Text.translatable("playerposquerytool.command.hint.query_op_level", Text.translatable(requestedOpLevel ? "playerposquerytool.command.hint.enable":"playerposquerytool.command.hint.disable"));
                                    context.getSource().sendFeedback(()->message, false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("broadcastToOps")
                                .then(CommandManager.literal("enable")
                                        .executes(context->{
                                            config.setBroadcastToOps(true);
                                            context.getSource().sendFeedback(()->Text.translatable("playerposquerytool.command.hint.enable_broad_cast_to_ops").formatted(Formatting.GREEN),false);
                                            config.saveConfig();
                                            return 1;
                                        }))
                                .then(CommandManager.literal("disable")
                                        .executes(context -> {
                                            config.setBroadcastToOps(false);
                                            context.getSource().sendFeedback(()->Text.translatable("playerposquerytool.command.hint.disable_broad_cast_to_ops").formatted(Formatting.RED),false);
                                            config.saveConfig();
                                            return 1;
                                        })
                                )
                                .executes(context->{
                                    boolean broadcastToOps = config.isBroadcastToOps();
                                    Text message = Text.translatable("playerposquerytool.command.hint.query_op_level", Text.translatable(broadcastToOps ? "playerposquerytool.command.hint.enable":"playerposquerytool.command.hint.disable"));
                                    context.getSource().sendFeedback(()->message, false);
                                    return 1;
                                })

                        )

                )
        );

    }

    private void sendPlayerPosition(ServerCommandSource source, ServerPlayerEntity player) {
        // 获取坐标和维度
        String pos = String.format("X: %.1f, Y: %.1f, Z: %.1f",
                player.getX(), player.getY(), player.getZ());
        String dimension = player.getWorld().getRegistryKey().getValue().getPath();

        //构建提示
        Text message = Text.translatable(
                "playerposquerytool.command.hint.show_position",
                Text.literal(player.getName().getString()).formatted(Formatting.YELLOW),
                Text.literal(pos)
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, pos)))
                        .styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("playerposquerytool.command.hint.copy_hint"))))
                        .styled(style -> style.withUnderline(true)),
                Text.literal(dimension).formatted(Formatting.BLUE));

        source.sendFeedback(() -> message, config.isBroadcastToOps());
    }

    private void savePositionToLog(ServerPlayerEntity player) {
        try {
            Path logDir = Paths.get("logs", "playerposquerytool");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            Path logFile = logDir.resolve("positions.log");

            // 写入日志（格式：时间戳,玩家名,坐标,维度）
            String logEntry = String.format("[%s] %s at X: %.1f Y: %.1f Z:%.1f %s\n",
                    java.time.LocalDateTime.now(),
                    player.getName().getString(),
                    player.getX(), player.getY(), player.getZ(),
                    player.getWorld().getRegistryKey().getValue().getPath()
            );

            Files.write(logFile, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            player.sendMessage(Text.translatable("playerposquerytool.error.save_log_failed"));
        }
    }
}
