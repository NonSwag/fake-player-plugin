package me.bill.fakePlayerPlugin.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FppBotSettingsTab extends FppSettingsTab {
  @Override
  default @NotNull List<FppSettingsItem> getItems(@NotNull Player viewer) {
    return List.of();
  }

  @NotNull List<FppSettingsItem> getItems(@NotNull Player viewer, @NotNull FppBot bot);
}
