package me.bill.fakePlayerPlugin.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface FppNameTagService {
  record BotSkin(String texture, @Nullable String signature) {
  }

  record NickData(@Nullable String nick, @Nullable String plainNick, @Nullable BotSkin skin) {
    public boolean canRename() {
      return plainNick != null && !plainNick.isBlank();
    }
  }

  boolean isAvailable();

  @Nullable String getNick(UUID uuid);

  @Nullable String getNick(Player player);

  @Nullable BotSkin getSkin(UUID uuid);

  @Nullable NickData clearBotFromCache(UUID botUuid);

  void resetBotNickname(Player bot);

  void applySkin(Player player, BotSkin skin);

  boolean isNickUsedByRealPlayer(String candidateName);
}
