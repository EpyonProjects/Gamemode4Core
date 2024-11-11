package com.kruthers.gamemode4core.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.kruthers.gamemode4core.Gamemode4Core
import com.kruthers.gamemode4core.utils.getMessage
import com.kruthers.gamemode4core.utils.parseString
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FreezeCommand(val plugin: Gamemode4Core) {

    @CommandMethod("freeze")
    @CommandPermission("gm4core.freeze")
    @CommandDescription("Freeze everyone on the server")
    fun onFreezeCommand(sender: CommandSender) {
        if (Gamemode4Core.allPlayersFrozen) {
            sender.sendMessage(Component.text("Everyone is already frozen, do /unfreeze to allow people to move again",NamedTextColor.RED))
            return
        }
        Gamemode4Core.allPlayersFrozen = true
        Bukkit.broadcast(getMessage(plugin,"freeze.start_brodcast"))
        Bukkit.broadcast(getMessage(plugin,"freeze.staff_start", TagResolver.resolver(
            Placeholder.unparsed("name",sender.name))),"gm4core.freeze.notify")
    }

    @CommandMethod("freeze <player>")
    @CommandPermission("gm4core.freeze.player")
    @CommandDescription("Freeze a specific player on the server")
    fun onFreezeCommand(sender: CommandSender, @Argument("player") target: Player) {
        if (target in Gamemode4Core.frozenPlayers) {
            sender.sendMessage(Component.text("That player is already frozen.  To allow them to move, use /unfreeze <name>",NamedTextColor.RED))
            return
        }

        Gamemode4Core.frozenPlayers[target] = target.uniqueId
        target.sendMessage(getMessage(plugin, "freeze.start_player"))
    }

    @CommandMethod("unfreeze")
    @CommandPermission("gm4core.freeze")
    @CommandDescription("Unfreeze everyone")
    fun onUnfreezeCommand(sender: CommandSender) {
        // No one is frozen
        if (!Gamemode4Core.allPlayersFrozen && Gamemode4Core.frozenPlayers.isEmpty()) {
            sender.sendMessage(Component.text("Freeze is not active, do /freeze to change this",NamedTextColor.RED))
            return
        }
        // All frozen
        if (Gamemode4Core.allPlayersFrozen) {
            Gamemode4Core.allPlayersFrozen = false
            Bukkit.broadcast(getMessage(plugin,"freeze.end_brodcast"))
            Bukkit.broadcast(getMessage(plugin,"freeze.staff_end", TagResolver.resolver(
                Placeholder.unparsed("name",sender.name))),"gm4core.freeze.notify")
            // Almost all unfrozen
            if (Gamemode4Core.frozenPlayers.isNotEmpty())
                sender.sendMessage(getMessage(plugin, "freeze.staff_end_partial"))

                sender.sendMessage(Component.text(Gamemode4Core.frozenPlayers.keys.toList().joinToString { it -> it.name }))
            return
        }
        // Individually frozen
        Gamemode4Core.frozenPlayers.clear()
        sender.sendMessage(Component.text("All remaining frozen players have been unfrozen",NamedTextColor.RED))
    }

    @CommandMethod("unfreeze <player>")
    @CommandPermission("gm4core.unfreeze.player")
    @CommandDescription("Unfreeze a specific player on the server")
    fun onUnfreezeCommand(sender: CommandSender, @Argument("player") target: Player) {
        if (target !in Gamemode4Core.frozenPlayers) {
            sender.sendMessage(Component.text("That player is already frozen.  To allow them to move, use /unfreeze <name>",NamedTextColor.RED))
            return
        }

        Gamemode4Core.frozenPlayers.remove(target)
        target.sendMessage(getMessage(plugin, "freeze.end_player"))
    }

}