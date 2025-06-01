package net.crayonsmp.commands;

import lombok.RequiredArgsConstructor;
import net.crayonsmp.CrayonAPI;
import net.crayonsmp.CrayonCommand;
import net.crayonsmp.modules.CrayonModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ModulesCommand implements CrayonCommand {

    private final CrayonAPI api;

    @Override
    public String getCommand() {
        return "modules";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        sender.sendMessage(Component.text("ℹ")
                .color(TextColor.color(0x349FDA))
                .append(Component.text(" Server Modules (" + api.loadedModules().size() + "):")
                        .color(TextColor.color(0xffffff))));

        sender.sendMessage(Component.text("Crayon Modules:").color(TextColor.color(0x0288D1)));

        for (CrayonModule module : api.loadedModules()) {
            Component moduleLine = Component.text(" - ")
                    .color(TextColor.color(0xAAAAAA))
                    .append(Component.text(module.getName())
                            .color(TextColor.color(0x55FF55))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("Author: ")
                                            .color(NamedTextColor.YELLOW)
                                            .append(Component.text(module.getAuthor().isEmpty() ? "Unknown" : module.getAuthor()))
                                            .append(Component.newline())
                                            .append(Component.text("Version: ", NamedTextColor.YELLOW))
                                            .append(Component.text(module.getVersion(), NamedTextColor.WHITE))
                            )));

            sender.sendMessage(moduleLine);
        }
        return true;
    }
}
