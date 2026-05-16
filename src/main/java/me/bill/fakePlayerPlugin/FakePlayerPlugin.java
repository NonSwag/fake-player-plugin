package me.bill.fakePlayerPlugin;

import me.bill.fakePlayerPlugin.command.*;
import me.bill.fakePlayerPlugin.config.BotNameConfig;
import me.bill.fakePlayerPlugin.config.Config;
import me.bill.fakePlayerPlugin.database.DatabaseManager;
import me.bill.fakePlayerPlugin.database.NetworkDatabase;
import me.bill.fakePlayerPlugin.fakeplayer.BotChatController;
import me.bill.fakePlayerPlugin.fakeplayer.BotPersistence;
import me.bill.fakePlayerPlugin.fakeplayer.ChunkLoader;
import me.bill.fakePlayerPlugin.fakeplayer.FakePlayerManager;
import me.bill.fakePlayerPlugin.fakeplayer.PathfindingService;
import me.bill.fakePlayerPlugin.gui.BotSettingGui;
import me.bill.fakePlayerPlugin.gui.SettingGui;
import me.bill.fakePlayerPlugin.lang.Lang;
import me.bill.fakePlayerPlugin.listener.BotCollisionListener;
import me.bill.fakePlayerPlugin.listener.BotLoginOverrideListener;
import me.bill.fakePlayerPlugin.listener.FakePlayerEntityListener;
import me.bill.fakePlayerPlugin.listener.FakePlayerKickListener;
import me.bill.fakePlayerPlugin.listener.PlayerJoinListener;
import me.bill.fakePlayerPlugin.listener.PlayerWorldChangeListener;
import me.bill.fakePlayerPlugin.messaging.VelocityChannel;
import me.bill.fakePlayerPlugin.util.BackupManager;
import me.bill.fakePlayerPlugin.util.BadwordFilter;
import me.bill.fakePlayerPlugin.util.CompatibilityChecker;
import me.bill.fakePlayerPlugin.util.ConfigMigrator;
import me.bill.fakePlayerPlugin.util.ConfigValidator;
import me.bill.fakePlayerPlugin.util.FppLogger;
import me.bill.fakePlayerPlugin.util.FppMetrics;
import me.bill.fakePlayerPlugin.util.FppScheduler;
import me.bill.fakePlayerPlugin.util.UpdateChecker;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class FakePlayerPlugin extends JavaPlugin {

  private static FakePlayerPlugin instance;

  @SuppressWarnings("unused")
  public static FakePlayerPlugin getInstance() {
    return instance;
  }

  private CommandManager commandManager;
  private FakePlayerManager fakePlayerManager;
  private ChunkLoader chunkLoader;
  private DatabaseManager databaseManager;
  private BotPersistence botPersistence;
  private FppMetrics fppMetrics;
  private VelocityChannel velocityChannel;
  private BotChatController botChatAI;
  private me.bill.fakePlayerPlugin.fakeplayer.RemoteBotCache remoteBotCache;
  private me.bill.fakePlayerPlugin.sync.ConfigSyncManager configSyncManager;
  private SettingGui settingGui;
  private me.bill.fakePlayerPlugin.gui.BotSettingGui botSettingGui;
  private me.bill.fakePlayerPlugin.gui.HelpGui helpGui;
  private me.bill.fakePlayerPlugin.fakeplayer.BotIdentityCache botIdentityCache;
  private me.bill.fakePlayerPlugin.network.NetworkHeartbeatManager networkHeartbeat;
  private XpCommand xpCommand;
  private me.bill.fakePlayerPlugin.command.MoveCommand moveCommand;
  private me.bill.fakePlayerPlugin.command.MineCommand mineCommand;
  private me.bill.fakePlayerPlugin.command.PlaceCommand placeCommand;
  private me.bill.fakePlayerPlugin.command.UseCommand useCommand;
  private me.bill.fakePlayerPlugin.command.AttackCommand attackCommand;
  private me.bill.fakePlayerPlugin.command.FollowCommand followCommand;
  private me.bill.fakePlayerPlugin.command.SleepCommand sleepCommand;
  private me.bill.fakePlayerPlugin.command.FindCommand findCommand;
  private me.bill.fakePlayerPlugin.command.StopCommand stopCommand;
  private PathfindingService pathfindingService;
  private me.bill.fakePlayerPlugin.command.StorageStore storageStore;
  private me.bill.fakePlayerPlugin.command.InventoryCommand inventoryCommand;
  private me.bill.fakePlayerPlugin.fakeplayer.SkinManager skinManager;
  private me.bill.fakePlayerPlugin.fakeplayer.SkinFetchService skinFetchService =
      me.bill.fakePlayerPlugin.fakeplayer.SkinFetchService.NOOP;
  private me.bill.fakePlayerPlugin.util.HeartbeatSender heartbeatSender;

  private me.bill.fakePlayerPlugin.api.impl.FppApiImpl fppApi;
  private me.bill.fakePlayerPlugin.extension.ExtensionLoader extensionLoader;

  private Component updateNotificationMessage = null;

  private boolean worldGuardAvailable = false;

  public boolean isWorldGuardAvailable() {
    return worldGuardAvailable;
  }

  private boolean worldEditAvailable = false;

  public boolean isWorldEditAvailable() {
    return worldEditAvailable;
  }

  private boolean versionUnsupported = false;

  private String detectedMcVersion = "unknown";

  public boolean isVersionUnsupported() {
    return versionUnsupported;
  }

  public String getDetectedMcVersion() {
    return detectedMcVersion;
  }

  private long enabledAt;

  @Override
  public void onEnable() {
    instance = this;
    enabledAt = System.currentTimeMillis();
    FppLogger.init(getLogger());

    ConfigMigrator.migrateIfNeeded(this);

    Config.init(this);
    Config.debugStartup("config.yml loaded.");

    me.bill.fakePlayerPlugin.util.AttributionManager.validate(this);
    me.bill.fakePlayerPlugin.util.AttributionApiManager.init(this);

    BadwordFilter.reload(this);
    if (Config.isBadwordFilterEnabled() && BadwordFilter.getBadwordCount() == 0) {
      FppLogger.warn("═══════════════════════════════════════════════════════════════════");
      FppLogger.warn("  ⚠  BADWORD FILTER IS ENABLED BUT NO SOURCES ARE ACTIVE  ⚠");
      FppLogger.warn("  Enable 'badword-filter.use-global-list' or add words to");
      FppLogger.warn("  'badword-filter.words' / 'bad-words.yml', then run /fpp reload");
      FppLogger.warn("═══════════════════════════════════════════════════════════════════");
    }

    Lang.init(this);
    Config.debugStartup("Language file loaded (lang=" + Config.getLanguage() + ").");

    detectedMcVersion = CompatibilityChecker.extractMcVersion();
    if (!CompatibilityChecker.isSupportedVersion(detectedMcVersion)) {
      versionUnsupported = true;
      String pv = getPluginMeta().getVersion();
      FppLogger.warn("═══════════════════════════════════════════════════════════════════");
      FppLogger.warn("  ⚠  FakePlayerPlugin - UNSUPPORTED MINECRAFT VERSION  ⚠");
      FppLogger.warn("═══════════════════════════════════════════════════════════════════");
      FppLogger.warn("  Plugin    : FakePlayerPlugin v" + pv);
      FppLogger.warn("  Server MC : " + detectedMcVersion + "  (NOT supported)");
      FppLogger.warn("  Supported : up to MC 1.21.11, and 26.1.x");
      FppLogger.warn("  Action    : All /fpp commands have been DISABLED.");
      FppLogger.warn("  Support   : If you think this is a bug, contact us:");
      FppLogger.warn("              Discord → https://discord.gg/RfjEJDG2TM");
      FppLogger.warn("═══════════════════════════════════════════════════════════════════");
    }

    BotNameConfig.init(this);
    Config.debugStartup("Bot name pool: " + BotNameConfig.getNames().size() + " names.");

    ensureDataDirectories();

    boolean dbOk = false;
    if (Config.databaseEnabled()) {
      databaseManager = new DatabaseManager();
      dbOk = databaseManager.init(this);
      if (!dbOk) {
        FppLogger.warn("Database could not be initialised - session tracking disabled.");
        databaseManager = null;
      } else {

        String mode = Config.databaseMode();
        String serverId = Config.serverId();
        Config.debugDatabase("Database mode: " + mode + " | server-id=" + serverId);

        databaseManager.cleanExpiredSkinCache();
      }
    } else {
      Config.debugDatabase("Database disabled in config - skipping database initialisation.");
      databaseManager = null;
    }

    botIdentityCache =
        new me.bill.fakePlayerPlugin.fakeplayer.BotIdentityCache(this, databaseManager);
    Config.debugDatabase(
        "BotIdentityCache initialised (backend="
            + (databaseManager != null ? (Config.mysqlEnabled() ? "MySQL" : "SQLite") : "YAML")
            + ").");

    remoteBotCache = new me.bill.fakePlayerPlugin.fakeplayer.RemoteBotCache();
    if (Config.isNetworkMode() && databaseManager != null) {
      var remoteRows = databaseManager.getNetworkBotsFromOtherServers();
      for (var row : remoteRows) {
        try {
          java.util.UUID uuid = java.util.UUID.fromString(row.botUuid());
          String display =
              (row.botDisplay() != null && !row.botDisplay().isBlank())
                  ? row.botDisplay()
                  : row.botName();
          remoteBotCache.add(
              new me.bill.fakePlayerPlugin.fakeplayer.RemoteBotEntry(
                  row.serverId(), uuid, row.botName(), display, row.botName(), "", "", -1));
        } catch (Exception ignored) {
        }
      }
      Config.debugNetwork(
          "Remote bot cache pre-populated from DB: " + remoteBotCache.count() + " bot(s).");
    }

    if (Config.isNetworkMode() && databaseManager != null) {
      configSyncManager =
          new me.bill.fakePlayerPlugin.sync.ConfigSyncManager(this, databaseManager);
      configSyncManager.init();
      Config.debugConfigSync(
          "Config sync manager initialized (mode=" + Config.configSyncMode() + ").");
    } else {
      configSyncManager = null;
    }

    fakePlayerManager = new FakePlayerManager(this);
    if (databaseManager != null) fakePlayerManager.setDatabaseManager(databaseManager);
    fakePlayerManager.setIdentityCache(botIdentityCache);

    fakePlayerManager.refreshCleanNamePool();

    fppApi = new me.bill.fakePlayerPlugin.api.impl.FppApiImpl(this, fakePlayerManager);

    // Load persisted despawn snapshots (DB primary, YAML fallback) so bots that were manually
    // despawned before the restart can have their inventory/XP restored on next spawn.
    fakePlayerManager.initDespawnSnapshots();

    chunkLoader = new ChunkLoader(this, fakePlayerManager);
    fakePlayerManager.setChunkLoader(chunkLoader);

    botPersistence = new BotPersistence(this);
    fakePlayerManager.setBotPersistence(botPersistence);

    networkHeartbeat = new me.bill.fakePlayerPlugin.network.NetworkHeartbeatManager(this, fakePlayerManager);
    if (Config.isNetworkMode() && databaseManager != null) {
      networkHeartbeat.start();
    }

    pathfindingService = new PathfindingService();
    commandManager = new CommandManager(this);
    commandManager.register(new SpawnCommand(fakePlayerManager));
    commandManager.register(new DeleteCommand(fakePlayerManager));
    commandManager.register(new ListCommand(this, fakePlayerManager));
    commandManager.register(new TphCommand(fakePlayerManager));
    commandManager.register(new TpCommand(fakePlayerManager));
    xpCommand = new XpCommand(this, fakePlayerManager);
    commandManager.register(xpCommand);
    commandManager.register(new ReloadCommand(this));
    commandManager.register(new InfoCommand(databaseManager, fakePlayerManager));
    commandManager.register(new MigrateCommand(this));
    commandManager.register(
        new me.bill.fakePlayerPlugin.command.BadwordCommand(this, fakePlayerManager));
    commandManager.register(new StatsCommand(fakePlayerManager, databaseManager));
    commandManager.register(new ExtensionCommand(this));
    commandManager.register(new FreezeCommand(fakePlayerManager));
    commandManager.register(
        new me.bill.fakePlayerPlugin.command.RenameCommand(this, fakePlayerManager));
    moveCommand =
        new me.bill.fakePlayerPlugin.command.MoveCommand(
            this, fakePlayerManager, pathfindingService);
    storageStore = new me.bill.fakePlayerPlugin.command.StorageStore(this);
    storageStore.load();
    commandManager.register(moveCommand);
    mineCommand =
        new me.bill.fakePlayerPlugin.command.MineCommand(
            this, fakePlayerManager, storageStore, pathfindingService);
    commandManager.register(mineCommand);
    findCommand =
        new me.bill.fakePlayerPlugin.command.FindCommand(
            this, fakePlayerManager, pathfindingService, mineCommand);
    mineCommand.setFindCommand(findCommand);
    commandManager.register(findCommand);
    commandManager.register(
        new me.bill.fakePlayerPlugin.command.StorageCommand(this, fakePlayerManager, storageStore, pathfindingService));
    placeCommand =
        new me.bill.fakePlayerPlugin.command.PlaceCommand(
            this, fakePlayerManager, storageStore, pathfindingService);
    commandManager.register(placeCommand);
    useCommand =
        new me.bill.fakePlayerPlugin.command.UseCommand(
            this, fakePlayerManager, pathfindingService);
    commandManager.register(useCommand);
    attackCommand =
        new me.bill.fakePlayerPlugin.command.AttackCommand(
            this, fakePlayerManager, pathfindingService);
    commandManager.register(attackCommand);
    followCommand =
        new me.bill.fakePlayerPlugin.command.FollowCommand(
            this, fakePlayerManager, pathfindingService);
    commandManager.register(followCommand);
    sleepCommand =
        new me.bill.fakePlayerPlugin.command.SleepCommand(
            this, fakePlayerManager, pathfindingService);
    commandManager.register(sleepCommand);

    botSettingGui = new BotSettingGui(this, fakePlayerManager);
    inventoryCommand = new InventoryCommand(fakePlayerManager, this, botSettingGui);
    commandManager.register(inventoryCommand);
    commandManager.register(new me.bill.fakePlayerPlugin.command.SetOwnerCommand(this, fakePlayerManager));
    commandManager.register(new me.bill.fakePlayerPlugin.command.SaveCommand(this));

    settingGui = new SettingGui(this);
    commandManager.register(new SettingCommand(settingGui, botSettingGui, fakePlayerManager));
    Config.debugStartup("Commands registered: " + commandManager.getCommands().size() + " total.");

    botPersistence.setMoveCommand(moveCommand);
    botPersistence.setMineCommand(mineCommand);
    botPersistence.setPlaceCommand(placeCommand);
    botPersistence.setUseCommand(useCommand);
    botPersistence.setAttackCommand(attackCommand);
    botPersistence.setFollowCommand(followCommand);
    sleepCommand.setMineCommand(mineCommand);
    sleepCommand.setUseCommand(useCommand);
    sleepCommand.setPlaceCommand(placeCommand);
    sleepCommand.setAttackCommand(attackCommand);
    sleepCommand.setFollowCommand(followCommand);
    sleepCommand.setMoveCommand(moveCommand);
    sleepCommand.setFindCommand(findCommand);

    stopCommand = new me.bill.fakePlayerPlugin.command.StopCommand(fakePlayerManager);
    stopCommand.setMoveCommand(moveCommand);
    stopCommand.setMineCommand(mineCommand);
    stopCommand.setUseCommand(useCommand);
    stopCommand.setPlaceCommand(placeCommand);
    stopCommand.setAttackCommand(attackCommand);
    stopCommand.setFollowCommand(followCommand);
    stopCommand.setFindCommand(findCommand);
    stopCommand.setSleepCommand(sleepCommand);
    commandManager.register(stopCommand);

    var fppCmd = getCommand("fpp");
    if (fppCmd != null) {
      fppCmd.setExecutor(commandManager);
      fppCmd.setTabCompleter(commandManager);
    }

    getServer()
        .getPluginManager()
        .registerEvents(new PlayerJoinListener(this, fakePlayerManager), this);
    getServer()
        .getPluginManager()
        .registerEvents(new PlayerWorldChangeListener(this, fakePlayerManager), this);
    getServer()
        .getPluginManager()
        .registerEvents(new FakePlayerEntityListener(this, fakePlayerManager, chunkLoader), this);
    getServer()
        .getPluginManager()
        .registerEvents(new BotCollisionListener(this, fakePlayerManager), this);

    getServer()
        .getPluginManager()
        .registerEvents(new FakePlayerKickListener(fakePlayerManager), this);

    getServer().getPluginManager().registerEvents(settingGui, this);
    getServer().getPluginManager().registerEvents(botSettingGui, this);
    getServer().getPluginManager().registerEvents(inventoryCommand, this);
    getServer()
        .getPluginManager()
        .registerEvents(
            new me.bill.fakePlayerPlugin.listener.BotSpawnProtectionListener(this), this);
    getServer()
        .getPluginManager()
        .registerEvents(new BotLoginOverrideListener(this, fakePlayerManager), this);
    getServer()
        .getPluginManager()
        .registerEvents(
            new me.bill.fakePlayerPlugin.listener.BotXpPickupListener(this, fakePlayerManager),
            this);

    helpGui = new me.bill.fakePlayerPlugin.gui.HelpGui(this, commandManager);
    getServer().getPluginManager().registerEvents(helpGui, this);
    commandManager.setHelpGui(helpGui);

    extensionLoader = new me.bill.fakePlayerPlugin.extension.ExtensionLoader(this);
    extensionLoader.loadExtensions();

    velocityChannel = new VelocityChannel(this, fakePlayerManager);
    getServer().getMessenger().registerOutgoingPluginChannel(this, VelocityChannel.CHANNEL);
    getServer().getMessenger().registerOutgoingPluginChannel(this, VelocityChannel.PROXY_CHANNEL);
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer()
        .getMessenger()
        .registerIncomingPluginChannel(this, VelocityChannel.CHANNEL, velocityChannel);
    getServer()
        .getMessenger()
        .registerIncomingPluginChannel(this, VelocityChannel.PROXY_CHANNEL, velocityChannel);
    Config.debugNetwork(
        "Plugin messaging channels registered: " + VelocityChannel.CHANNEL + " + " + VelocityChannel.PROXY_CHANNEL + " + BungeeCord.");

    FppScheduler.runSyncRepeating(
        this,
        () -> {
          if (fakePlayerManager.getCount() > 0) {
            fakePlayerManager.validateEntities();
          }
        },
        6000L,
        6000L);

    int configIssues = ConfigValidator.validate();
    if (configIssues > 0) {
      FppLogger.warn("Config validation found " + configIssues + " issue(s) - see above.");
    }

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      try {
        new me.bill.fakePlayerPlugin.util.FppPlaceholderExpansion(this, fakePlayerManager)
            .register();
        Config.debugStartup("PlaceholderAPI detected - placeholders registered.");
      } catch (Exception e) {
        FppLogger.warn("PlaceholderAPI: failed to register expansion - " + e.getMessage());
      }
    }

    worldGuardAvailable = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    if (worldGuardAvailable) {
      Config.debugStartup("WorldGuard detected - bot PvP region protection enabled.");
    }

    worldEditAvailable = Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
    if (worldEditAvailable) {
      Config.debugStartup("WorldEdit detected - --wesel flag enabled for /fpp mine and /fpp place.");
    }

    UpdateChecker.check(this);

    heartbeatSender = new me.bill.fakePlayerPlugin.util.HeartbeatSender(this, fakePlayerManager);
    heartbeatSender.start();

    fppMetrics = new FppMetrics();
    if (Config.metricsEnabled()) {
      try {
        fppMetrics.init(this, fakePlayerManager);
      } catch (Throwable t) {
        FppLogger.error(
            "Metrics: unexpected top-level error - "
                + t.getClass().getName()
                + ": "
                + t.getMessage());
        for (StackTraceElement el : t.getStackTrace()) {
          FppLogger.error("  at " + el);
        }
      }
    } else {
      Config.debugStartup("Metrics disabled in config.yml - skipping FastStats init.");
    }

    String dbLabel =
        databaseManager == null ? "none" : Config.mysqlEnabled() ? "MySQL" : "SQLite (local)";
    String dbState =
        !Config.databaseEnabled() ? "disabled" : (dbOk ? dbLabel : dbLabel + " (failed)");
    int dbSchemaVersion = databaseManager != null ? DatabaseManager.getCurrentSchemaVersion() : 0;

    boolean effectiveSpawnBody = Config.spawnBody();
    boolean effectiveChunkLoading =
        Config.chunkLoadingEnabled() && Config.chunkLoadingRadius() != 0;
    boolean effectiveTaskPersist = Config.persistOnRestart() && databaseManager != null;

    long startupMs = System.currentTimeMillis() - enabledAt;
    int cfgVer = Config.configVersion();
    String configVersion =
        "v" + cfgVer + (cfgVer >= ConfigMigrator.CURRENT_VERSION ? " ✔" : " (migrated)");
    int backupCount = BackupManager.listBackups(this).size();

    FppLogger.printStartupBanner(
        getPluginMeta().getVersion(),
        String.join(", ", getPluginMeta().getAuthors()),
        BotNameConfig.getNames().size(),
        dbState,
        dbSchemaVersion,
        effectiveSpawnBody,
        Config.persistOnRestart(),
        effectiveTaskPersist,
        Bukkit.getPluginManager().getPlugin("LuckPerms") != null,
        worldGuardAvailable,
        effectiveChunkLoading,
        Config.maxBots(),
        fppMetrics.isActive(),
        configVersion,
        backupCount,
        startupMs);

    botPersistence.purgeOrphanedBodiesAndRestore(fakePlayerManager);

    if (velocityChannel != null) {
      FppScheduler.runSyncLater(this, () -> velocityChannel.broadcastResyncRequest(), 10L);
    }

    Config.debugStartup("onEnable complete.");
  }

  @Override
  public void onDisable() {
    Config.debugStartup("onDisable called.");

    int botsRemoved = fakePlayerManager != null ? fakePlayerManager.getCount() : 0;

    if (chunkLoader != null) chunkLoader.releaseAll();

    if (botChatAI != null) botChatAI.cancelAll();

    if (sleepCommand != null) sleepCommand.stopAll();

    if (botPersistence != null && fakePlayerManager != null) {
      if (Config.persistOnRestart()) {
        Config.debugStartup(
            "Saving " + fakePlayerManager.getCount() + " bot(s) for persistence...");
        botPersistence.save(fakePlayerManager.getActivePlayers());
      }
    }

    if (fppApi != null) fppApi.disableAllAddons();
    if (extensionLoader != null) extensionLoader.closeClassLoaders();

    if (velocityChannel != null) {
      velocityChannel.broadcastServerOffline();
    }

    if (fakePlayerManager != null) fakePlayerManager.removeAllSyncFast();

    boolean dbFlushed = false;
    if (databaseManager != null) {
      databaseManager.recordAllShutdown();
      databaseManager.close();
      dbFlushed = true;
    }

    if (heartbeatSender != null) heartbeatSender.stop();
    if (networkHeartbeat != null) networkHeartbeat.stop();

    if (fppMetrics != null) fppMetrics.shutdown();

    getServer().getMessenger().unregisterIncomingPluginChannel(this, VelocityChannel.CHANNEL);
    getServer().getMessenger().unregisterIncomingPluginChannel(this, VelocityChannel.PROXY_CHANNEL);
    getServer().getMessenger().unregisterOutgoingPluginChannel(this, VelocityChannel.CHANNEL);
    getServer().getMessenger().unregisterOutgoingPluginChannel(this, VelocityChannel.PROXY_CHANNEL);
    getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");

    long uptimeMs = System.currentTimeMillis() - enabledAt;
    boolean tasksPersisted = Config.persistOnRestart() && databaseManager != null;
    FppLogger.printShutdownBanner(botsRemoved, dbFlushed, tasksPersisted, botsRemoved, uptimeMs);
  }

  @SuppressWarnings("unused")
  public CommandManager getCommandManager() {
    return commandManager;
  }

  /** Returns the public addon API entry point. Available after {@code onEnable} completes. */
  @SuppressWarnings("unused")
  public me.bill.fakePlayerPlugin.api.FppApi getFppApi() {
    return fppApi;
  }

  /** Internal accessor for subsystems that need the concrete impl (e.g. fireTickHandlers). */
  public me.bill.fakePlayerPlugin.api.impl.FppApiImpl getFppApiImpl() {
    return fppApi;
  }

  @SuppressWarnings("unused")
  public FakePlayerManager getFakePlayerManager() {
    return fakePlayerManager;
  }

  public BotPersistence getBotPersistence() {
    return botPersistence;
  }

  public SettingGui getSettingGui() {
    return settingGui;
  }

  public me.bill.fakePlayerPlugin.gui.BotSettingGui getBotSettingGui() {
    return botSettingGui;
  }

  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  }

  public VelocityChannel getVelocityChannel() {
    return velocityChannel;
  }

  public BotChatController getBotChatAI() {
    return botChatAI;
  }

  public void setBotChatAI(BotChatController botChatAI) {
    this.botChatAI = botChatAI;
  }

  public me.bill.fakePlayerPlugin.fakeplayer.RemoteBotCache getRemoteBotCache() {
    return remoteBotCache;
  }

  public me.bill.fakePlayerPlugin.sync.ConfigSyncManager getConfigSyncManager() {
    return configSyncManager;
  }

  public me.bill.fakePlayerPlugin.fakeplayer.BotIdentityCache getBotIdentityCache() {
    return botIdentityCache;
  }

  public XpCommand getXpCommand() {
    return xpCommand;
  }

  public me.bill.fakePlayerPlugin.command.MoveCommand getMoveCommand() {
    return moveCommand;
  }

  public me.bill.fakePlayerPlugin.command.MineCommand getMineCommand() {
    return mineCommand;
  }

  public me.bill.fakePlayerPlugin.command.PlaceCommand getPlaceCommand() {
    return placeCommand;
  }

  public me.bill.fakePlayerPlugin.command.UseCommand getUseCommand() {
    return useCommand;
  }

  public me.bill.fakePlayerPlugin.command.AttackCommand getAttackCommand() {
    return attackCommand;
  }

  public me.bill.fakePlayerPlugin.command.FollowCommand getFollowCommand() {
    return followCommand;
  }

  public me.bill.fakePlayerPlugin.command.SleepCommand getSleepCommand() {
    return sleepCommand;
  }

  public PathfindingService getPathfindingService() {
    return pathfindingService;
  }

  public me.bill.fakePlayerPlugin.command.StorageStore getStorageStore() {
    return storageStore;
  }

  public me.bill.fakePlayerPlugin.command.InventoryCommand getInventoryCommand() {
    return inventoryCommand;
  }

  public me.bill.fakePlayerPlugin.fakeplayer.SkinManager getSkinManager() {
    return skinManager;
  }

  public void setSkinManager(me.bill.fakePlayerPlugin.fakeplayer.SkinManager skinManager) {
    this.skinManager = skinManager;
  }

  public me.bill.fakePlayerPlugin.fakeplayer.SkinFetchService getSkinFetchService() {
    return skinFetchService != null
        ? skinFetchService
        : me.bill.fakePlayerPlugin.fakeplayer.SkinFetchService.NOOP;
  }

  public void setSkinFetchService(
      me.bill.fakePlayerPlugin.fakeplayer.SkinFetchService skinFetchService) {
    this.skinFetchService =
        skinFetchService != null
            ? skinFetchService
            : me.bill.fakePlayerPlugin.fakeplayer.SkinFetchService.NOOP;
  }

  public me.bill.fakePlayerPlugin.extension.ExtensionLoader getExtensionLoader() {
    return extensionLoader;
  }

  public Component getUpdateNotification() {
    return updateNotificationMessage;
  }

  public void setUpdateNotification(Component c) {
    this.updateNotificationMessage = c;
  }

  private volatile String latestKnownVersion = null;

  private volatile boolean runningBeta = false;

  public String getLatestKnownVersion() {
    return latestKnownVersion;
  }

  public void setLatestKnownVersion(String v) {
    this.latestKnownVersion = v;
  }

  public boolean isRunningBeta() {
    return runningBeta;
  }

  public void setRunningBeta(boolean b) {
    this.runningBeta = b;
  }

  private void ensureDataDirectories() {
    java.io.File root = getDataFolder();
    String[] dirs = {"data", "language", "extensions"};
    for (String dir : dirs) {
      java.io.File d = new java.io.File(root, dir);
      if (!d.exists()) {
        boolean ok = d.mkdirs();
        Config.debugStartup(
            "Created directory: " + d.getPath() + (ok ? " ✔" : " (already exists or failed)"));
      }
    }

    java.io.File extReadme = new java.io.File(root, "extensions/README.txt");
    if (!extReadme.exists()) {
      try (java.io.PrintWriter w = new java.io.PrintWriter(extReadme)) {
        w.println("# FakePlayerPlugin - Extensions Folder");
        w.println("#");
        w.println("# Drop extension JAR files here to load them automatically.");
        w.println("#");
        w.println("# Requirements:");
        w.println(
            "#   - JAR must contain a class implementing me.bill.fakePlayerPlugin.api.FppExtension");
        w.println("#   - That class must have a public no-arg constructor");
        w.println("#");
        w.println("# Run /fpp reload or restart the server after adding or removing extensions.");
      } catch (java.io.IOException e) {
        Config.debugStartup("Could not write extensions/README.txt: " + e.getMessage());
      }
    }
  }
}
