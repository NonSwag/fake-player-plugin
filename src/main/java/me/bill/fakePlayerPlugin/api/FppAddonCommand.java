package me.bill.fakePlayerPlugin.api;

import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface FppAddonCommand {
  @NotNull String getName();
  @NotNull String getDescription();
  @NotNull String getUsage();
  @NotNull String getPermission();
  default @NotNull Material getIcon() { return Material.COMMAND_BLOCK; }
  default @NotNull java.util.List<String> getAliases() { return java.util.Collections.emptyList(); }
  default boolean canUse(@NotNull CommandSender sender) {
    String permission = getPermission();
    return permission == null || permission.isBlank() || sender.hasPermission(permission);
  }
  boolean execute(@NotNull CommandSender sender, @NotNull String[] args);
  default @NotNull java.util.List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) { return java.util.Collections.emptyList(); }
}
