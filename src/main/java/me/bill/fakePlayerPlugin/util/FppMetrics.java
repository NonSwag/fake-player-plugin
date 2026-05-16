package me.bill.fakePlayerPlugin.util;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import me.bill.fakePlayerPlugin.FakePlayerPlugin;
import me.bill.fakePlayerPlugin.config.Config;
import me.bill.fakePlayerPlugin.fakeplayer.FakePlayerManager;
import org.bukkit.Bukkit;

/**
 * FastStats anonymous usage metrics - developer-only, not user-configurable.
 *
 * <p>FastStats is loaded at runtime via a dedicated {@link URLClassLoader} pointing to jars
 * bundled inside the plugin jar as resources (src/main/resources/faststats/). This sidesteps all
 * Maven shade-plugin relocation issues: the shade plugin does NOT update references in the
 * project's own class files, causing NoClassDefFoundError. With URLClassLoader the classes are found
 * directly - no relocation needed.
 *
 * <p>No personal data, player names, or server addresses are ever collected.
 */
public final class FppMetrics {

  private static final String TOKEN = "376511af6c97b56954ff2abed24dfaea";

  private URLClassLoader fsLoader;
  private Object metrics;
  private Object errorTracker;
  private boolean initialised = false;

  public void init(FakePlayerPlugin plugin, FakePlayerManager botManager) {
    if (TOKEN.isBlank()) {
      FppLogger.warn("Metrics: TOKEN is blank - FastStats disabled.");
      return;
    }

    FppLogger.debug("Metrics: Initialising FastStats (token=" + TOKEN.substring(0, 8) + "...)");

    ClassLoader prevCtx = Thread.currentThread().getContextClassLoader();
    try {

      File libsDir = new File(plugin.getDataFolder(), ".faststats-libs");
      libsDir.mkdirs();

      File coreJar = extractResource(plugin, "faststats/faststats-core.jar", libsDir);
      File bukkitJar = extractResource(plugin, "faststats/faststats-bukkit.jar", libsDir);

      fsLoader =
          new URLClassLoader(
              new URL[] {coreJar.toURI().toURL(), bukkitJar.toURI().toURL()},
              plugin.getClass().getClassLoader());

      Thread.currentThread().setContextClassLoader(fsLoader);

      // ── [1/4] ErrorTracker ────────────────────────────────────────────────
      FppLogger.debug("Metrics: [1/4] Creating ErrorTracker...");
      Class<?> etClass = fsLoader.loadClass("dev.faststats.core.ErrorTracker");
      errorTracker = etClass.getMethod("contextAware").invoke(null);
      FppLogger.debug("Metrics:       ErrorTracker ✔");

      // ── [2/4] Factory + Metrics ───────────────────────────────────────────
      FppLogger.debug("Metrics: [2/4] Building BukkitMetrics...");
      Class<?> bmClass = fsLoader.loadClass("dev.faststats.bukkit.BukkitMetrics");
      Class<?> mClass = fsLoader.loadClass("dev.faststats.core.data.Metric");

      Method numberMethod = findMethod(mClass, "number", 2);
      Method stringMethod = findMethod(mClass, "string", 2);
      Method stringArrayMethod = findMethod(mClass, "stringArray", 2);

      Object factory = bmClass.getMethod("factory").invoke(null);
      factory = chain(factory, "token", TOKEN);

      // Core numeric metrics
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "active_bots",
                  (Callable<Long>) () -> (long) (botManager == null ? 0 : botManager.getCount())));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "online_players",
                  (Callable<Long>) () -> (long) Bukkit.getOnlinePlayers().size()));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "max_bots_config",
                  (Callable<Long>) () -> (long) Config.maxBots()));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "user_bot_limit",
                  (Callable<Long>) () -> (long) Config.userBotLimit()));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "persistence_enabled",
                  (Callable<Long>) () -> Config.persistOnRestart() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "body_enabled",
                  (Callable<Long>) () -> Config.spawnBody() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "body_damageable",
                  (Callable<Long>) () -> Config.bodyDamageable() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "body_pushable",
                  (Callable<Long>) () -> Config.bodyPushable() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "fake_chat_enabled",
                  (Callable<Long>) () -> Config.fakeChatEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "chunk_loading_enabled",
                  (Callable<Long>) () -> Config.chunkLoadingEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "swap_enabled",
                  (Callable<Long>) () -> Config.swapEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "peak_hours_enabled",
                  (Callable<Long>) () -> Config.peakHoursEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "head_ai_enabled",
                  (Callable<Long>) () -> Config.headAiEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "swim_ai_enabled",
                  (Callable<Long>) () -> Config.swimAiEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "fall_damage_enabled",
                  (Callable<Long>) () -> Config.fallDamageEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "respawn_on_death",
                  (Callable<Long>) () -> Config.respawnOnDeath() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "tab_list_enabled",
                  (Callable<Long>) () -> Config.tabListEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "ping_enabled",
                  (Callable<Long>) () -> Config.pingEnabled() ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "luckperms_installed",
                  (Callable<Long>)
                      () -> Bukkit.getPluginManager().getPlugin("LuckPerms") != null ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "placeholderapi_installed",
                  (Callable<Long>)
                      () -> Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "worldguard_installed",
                  (Callable<Long>)
                      () -> Bukkit.getPluginManager().getPlugin("WorldGuard") != null ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "worldedit_installed",
                  (Callable<Long>)
                      () -> Bukkit.getPluginManager().getPlugin("WorldEdit") != null ? 1L : 0L));
      factory =
          addMetric(
              factory,
              numberMethod.invoke(
                  null,
                  "nametag_installed",
                  (Callable<Long>)
                      () -> Bukkit.getPluginManager().getPlugin("NameTag") != null ? 1L : 0L));

      // String metrics
      factory =
          addMetric(
              factory,
              stringMethod.invoke(null, "skin_mode", (Callable<String>) Config::skinMode));
      factory =
          addMetric(
              factory,
              stringMethod.invoke(null, "database_type", (Callable<String>) () -> Config.mysqlEnabled() ? "mysql" : "sqlite"));
      factory =
          addMetric(
              factory,
              stringMethod.invoke(null, "bot_name_mode", (Callable<String>) Config::botNameMode));
      factory =
          addMetric(
              factory,
              stringMethod.invoke(null, "mc_version", (Callable<String>) () -> plugin.getDetectedMcVersion()));
      factory =
          addMetric(
              factory,
              stringMethod.invoke(null, "plugin_version", (Callable<String>) () -> plugin.getPluginMeta().getVersion()));

      // String array metric: active feature flags
      factory =
          addMetric(
              factory,
              stringArrayMethod.invoke(
                  null,
                  "active_features",
                  (Callable<String[]>) () -> collectActiveFeatures()));

      // Error tracker + debug + flush callback
      factory = chain(factory, "errorTracker", errorTracker);
      factory = chain(factory, "debug", Config.metricsDebug());
      factory = chainFlushCallback(factory);

      FppLogger.debug("Metrics:       BukkitMetrics built ✔");

      // ── [3/4] Create ──────────────────────────────────────────────────────
      FppLogger.debug("Metrics: [3/4] Calling create()...");
      metrics = chain(factory, "create", plugin);
      FppLogger.debug("Metrics:       create() returned ✔");

      // ── [4/4] Ready ─────────────────────────────────────────────────────────
      FppLogger.debug("Metrics: [4/4] Calling ready()...");
      findMethod(metrics.getClass(), "ready", 0).invoke(metrics);
      initialised = true;
      FppLogger.debug("Metrics: FastStats connected and reporting ✔");

    } catch (Throwable t) {
      FppLogger.error("╔══════════════════════════════════════════════════");
      FppLogger.error("║  Metrics: FastStats init FAILED");
      FppLogger.error("║  " + t.getClass().getName() + ": " + t.getMessage());
      Throwable c = t;
      int d = 0;
      while (c.getCause() != null && d++ < 6) {
        c = c.getCause();
        FppLogger.error("║  Caused by: " + c.getClass().getName() + ": " + c.getMessage());
      }
      FppLogger.error("║  Stack (top 10):");
      StackTraceElement[] st = t.getStackTrace();
      for (int i = 0; i < Math.min(10, st.length); i++) FppLogger.error("║    at " + st[i]);
      FppLogger.error("╚══════════════════════════════════════════════════");
      metrics = null;
      errorTracker = null;
      closeLoader();
    } finally {
      Thread.currentThread().setContextClassLoader(prevCtx);
    }
  }

  public void shutdown() {
    if (metrics != null && initialised) {
      try {
        findMethod(metrics.getClass(), "shutdown", 0).invoke(metrics);
      } catch (Throwable ignored) {
      }
      metrics = null;
    }
    errorTracker = null;
    initialised = false;
    closeLoader();
  }

  public boolean isActive() {
    return initialised && metrics != null;
  }

  /**
   * Track an error through the context-aware ErrorTracker.
   * Safe to call even if metrics are not initialised - silently no-ops.
   */
  public void trackError(Throwable throwable) {
    if (errorTracker == null) return;
    try {
      Method trackMethod = findMethod(errorTracker.getClass(), "trackError", 1);
      // Try Throwable overload first, then String overload as fallback
      for (Method m : errorTracker.getClass().getMethods()) {
        if (m.getName().equals("trackError") && m.getParameterCount() == 1) {
          try {
            m.invoke(errorTracker, throwable);
            return;
          } catch (IllegalArgumentException ignored) {
          }
        }
      }
      // Fallback to string representation
      trackMethod.invoke(errorTracker, throwable.toString());
    } catch (Throwable ignored) {
    }
  }

  public void trackError(String message) {
    if (errorTracker == null) return;
    try {
      for (Method m : errorTracker.getClass().getMethods()) {
        if (m.getName().equals("trackError") && m.getParameterCount() == 1) {
          try {
            m.invoke(errorTracker, message);
            return;
          } catch (IllegalArgumentException ignored) {
          }
        }
      }
    } catch (Throwable ignored) {
    }
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private static Object chain(Object obj, String name, Object... args)
      throws ReflectiveOperationException {
    Method found = null;

    Class<?> cls = obj.getClass();
    outer:
    while (cls != null) {
      for (Method m : cls.getDeclaredMethods()) {
        if (m.getName().equals(name) && m.getParameterCount() == args.length) {
          found = m;
          break outer;
        }
      }
      for (Class<?> iface : cls.getInterfaces()) {
        for (Method m : iface.getDeclaredMethods()) {
          if (m.getName().equals(name) && m.getParameterCount() == args.length) {
            found = m;
            break outer;
          }
        }
      }
      cls = cls.getSuperclass();
    }

    if (found == null) {
      for (Method m : obj.getClass().getMethods()) {
        if (m.getName().equals(name) && m.getParameterCount() == args.length) {
          found = m;
          break;
        }
      }
    }
    if (found == null)
      throw new NoSuchMethodException(
          obj.getClass().getSimpleName() + "." + name + "/" + args.length);
    found.setAccessible(true);
    return found.invoke(obj, args);
  }

  private static Method findMethod(Class<?> cls, String name, int paramCount)
      throws NoSuchMethodException {
    Class<?> c = cls;
    while (c != null) {
      for (Method m : c.getDeclaredMethods()) {
        if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
          m.setAccessible(true);
          return m;
        }
      }
      c = c.getSuperclass();
    }
    for (Method m : cls.getMethods()) {
      if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
        m.setAccessible(true);
        return m;
      }
    }
    throw new NoSuchMethodException(cls.getSimpleName() + "." + name + "/" + paramCount);
  }

  private static Object addMetric(Object factory, Object metric)
      throws ReflectiveOperationException {
    Class<?> cls = factory.getClass();
    while (cls != null) {
      for (Method m : cls.getDeclaredMethods()) {
        if (m.getName().equals("addMetric") && m.getParameterCount() == 1) {
          m.setAccessible(true);
          try {
            return m.invoke(factory, metric);
          } catch (IllegalArgumentException ignored) {
          }
        }
      }
      cls = cls.getSuperclass();
    }
    for (Method m : factory.getClass().getMethods()) {
      if (m.getName().equals("addMetric") && m.getParameterCount() == 1) {
        m.setAccessible(true);
        try {
          return m.invoke(factory, metric);
        } catch (IllegalArgumentException ignored) {
        }
      }
    }
    throw new NoSuchMethodException("addMetric");
  }

  private static Object chainFlushCallback(Object factory) {
    try {
      // Try to find onFlush(Runnable) or onFlush(java.util.function.Consumer)
      Class<?> factoryClass = factory.getClass();
      Method flushMethod = null;
      while (factoryClass != null) {
        for (Method m : factoryClass.getDeclaredMethods()) {
          if (m.getName().equals("onFlush") && m.getParameterCount() == 1) {
            flushMethod = m;
            break;
          }
        }
        if (flushMethod != null) break;
        factoryClass = factoryClass.getSuperclass();
      }
      if (flushMethod == null) {
        for (Method m : factory.getClass().getMethods()) {
          if (m.getName().equals("onFlush") && m.getParameterCount() == 1) {
            flushMethod = m;
            break;
          }
        }
      }
      if (flushMethod != null) {
        flushMethod.setAccessible(true);
        // Pass a simple Runnable that just logs at debug level
        Runnable callback = () -> FppLogger.debug("Metrics: FastStats flush completed.");
        return flushMethod.invoke(factory, callback);
      }
    } catch (Throwable ignored) {
    }
    return factory;
  }

  private static String[] collectActiveFeatures() {
    java.util.List<String> features = new java.util.ArrayList<>();
    if (Config.spawnBody()) features.add("body");
    if (Config.bodyDamageable()) features.add("body_damageable");
    if (Config.bodyPushable()) features.add("body_pushable");
    if (Config.persistOnRestart()) features.add("persistence");
    if (Config.fakeChatEnabled()) features.add("fake_chat");
    if (Config.chunkLoadingEnabled()) features.add("chunk_loading");
    if (Config.swapEnabled()) features.add("swap");
    if (Config.peakHoursEnabled()) features.add("peak_hours");
    if (Config.headAiEnabled()) features.add("head_ai");
    if (Config.swimAiEnabled()) features.add("swim_ai");
    if (Config.fallDamageEnabled()) features.add("fall_damage");
    if (Config.respawnOnDeath()) features.add("respawn");
    if (Config.tabListEnabled()) features.add("tab_list");
    if (Config.pingEnabled()) features.add("ping");
    if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) features.add("luckperms");
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) features.add("placeholderapi");
    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) features.add("worldguard");
    if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) features.add("worldedit");
    if (Bukkit.getPluginManager().getPlugin("NameTag") != null) features.add("nametag");
    return features.toArray(new String[0]);
  }

  private static File extractResource(FakePlayerPlugin plugin, String resourcePath, File destDir)
      throws IOException {
    String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
    File dest = new File(destDir, fileName);
    try (InputStream in = plugin.getResource(resourcePath)) {
      if (in == null) throw new IOException("Bundled resource not found: " + resourcePath);
      byte[] data = in.readAllBytes();
      if (!dest.exists() || dest.length() != data.length) {
        try (OutputStream out = new FileOutputStream(dest)) {
          out.write(data);
        }
        FppLogger.debug("Metrics: extracted " + fileName + " (" + data.length / 1024 + " KB)");
      }
    }
    return dest;
  }

  private void closeLoader() {
    if (fsLoader != null) {
      try {
        fsLoader.close();
      } catch (IOException ignored) {
      }
      fsLoader = null;
    }
  }
}
