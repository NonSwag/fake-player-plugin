package me.bill.fakePlayerPlugin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public final class FppScheduler {

  private FppScheduler() {
  }

  public static void runAtEntity(Plugin plugin, Entity entity, Runnable runnable) {
    if (entity == null) return;
    runSync(plugin, runnable);
  }

  public static int runAtEntityRepeatingWithId(
      Plugin plugin, Entity entity, Runnable runnable, long delayTicks, long periodTicks) {
    return runSyncRepeatingWithId(plugin, runnable, delayTicks, periodTicks);
  }

  public static int runAtEntityLaterWithId(
      Plugin plugin, Entity entity, Runnable runnable, long delayTicks) {
    return runSyncLaterWithId(plugin, runnable, delayTicks);
  }

  public static void runAtLocation(Plugin plugin, Location location, Runnable runnable) {
    runSync(plugin, runnable);
  }

  public static void runAtChunk(Plugin plugin, World world, int chunkX, int chunkZ, Runnable runnable) {
    runSync(plugin, runnable);
  }

  public static void runSync(Plugin plugin, Runnable runnable) {
    Bukkit.getScheduler().runTask(plugin, runnable);
  }

  public static void runSyncRepeating(Plugin plugin, Runnable runnable, long delayTicks, long periodTicks) {
    Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
  }

  public static int runSyncLaterWithId(Plugin plugin, Runnable runnable, long delayTicks) {
    return Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks).getTaskId();
  }

  public static int runSyncRepeatingWithId(
      Plugin plugin, Runnable runnable, long delayTicks, long periodTicks) {
    return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks).getTaskId();
  }

  public static void runSyncLater(Plugin plugin, Runnable runnable, long delayTicks) {
    Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
  }

  public static void runAsync(Plugin plugin, Runnable runnable) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
  }

  public static void teleportAsync(Entity entity, Location dest) {
    if (entity instanceof Player p) {
      p.teleportAsync(dest, PlayerTeleportEvent.TeleportCause.PLUGIN);
    } else {
      entity.teleport(dest);
    }
  }

  public static void cancelTask(int taskId) {
    Bukkit.getScheduler().cancelTask(taskId);
  }
}
