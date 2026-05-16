# Changelog

## v1.6.6.10.1 (Current)

### Attribution & Author Updates
- Hardcoded original author updated from `el_pepes` to `F_PP` across codebase

### FastStats Metrics System Overhaul
- **ErrorTracker** — context-aware error tracking via FastStats API
- **Debug toggle** — `metrics.debug` option in `config.yml` (default `false`)
- **onFlush callback** — logs at debug level when metrics are flushed to FastStats
- **New metrics added**: `active_features` (string array), feature flags, installed plugins (LuckPerms, PlaceholderAPI, WorldGuard, WorldEdit, NameTag), server info, PvE settings, automation toggles
- **trackError() helpers** — two public overloads (`Throwable` and `String`) for external error reporting
- Added `getFppMetrics()` public getter on `FakePlayerPlugin.java`

### Bug Fixes
- **FakeChannelPipeline deprecation warning** — added `@SuppressWarnings("deprecation")` to suppress unavoidable Netty `ChannelPipeline` API deprecation warnings for `EventExecutorGroup` overloads
- **PluginRemapper duplicate entries** — `pom.xml` now properly excludes Mojang-mapped `paper-server` NMS classes from shaded JAR, fixing Paper 1.21.11 runtime remapping crash
- **SQLite AUTO_INCREMENT syntax** — split `fpp_network_tasks` table creation into SQLite (`INTEGER PRIMARY KEY AUTOINCREMENT`) and MySQL (`BIGINT AUTO_INCREMENT`) variants, fixing `SQLITE_ERROR near "AUTO_INCREMENT": syntax error`

### Deprecations & Removals
- None

---

## v1.6.6.10

**Requires MySQL for cross-server features.**

### Network Architecture  
**Proxy-merged database** — all backends share live bot registry and player counts via MySQL.
- Schema v25: `fpp_network_bots`, `fpp_server_heartbeat`, `fpp_network_tasks`
- **NetworkHeartbeatManager** — publishes local bots / reads remote bots every 5s, stale pruning every 60s
- Proxy companions (Velocity + Bungee) push `NETWORK_STATS` to all backends independently of players
- `RemoteBotCache` now survives restarts via DB (no longer messaging-only)

### PlaceholderAPI — 70+ placeholders  
New cross-server placeholders: `%fpp_network_total%`, `%fpp_network_real%`, `%fpp_network_bots%`  
Also added: server performance, extensions, 30+ config toggles, player-relative per-world, per-bot dynamic lookups.

### Extension System  
- `/fpp extension` bare command → marketplace link  
- `/fpp extension --list` → loaded extensions detail table  
- Extension data folders fixed (`getName()` instead of JAR filename)

### Deprecations & Fixes  
- `getServers()` → `getServersCopy()`, `FixedMetadataValue` → `PersistentDataContainer`, unchecked warnings cleaned
- Startup banner shows extension count  
- Authors updated to `F_PP` and `Kyttu`

### Legal  
Added `frontend/legal/` pages (copyright, extension policy, privacy, ToS)

---

## v1.6.6.9
- Fall damage tracking + config
- Skin injector fixes
- Config migrator v71→v72
- Extension bundles, API additions
- Wiki marketplace links

## v1.6.6.8
- Spoofing moved to `fpp-spoof.jar` extension (chat, AI, swap, peak-hours, ping, groups, stored cmds)
- PvE Smart Attack Mode (OFF / ON_NO_MOVE / ON_MOVE)
- `/fpp save`, `/fpp setowner`
- Per-bot overrides: respawn-on-death, auto-eat, auto-place-bed
- BotSettingGui PvE + Pathfinding tabs, share control
- DB schema v22: PvE, automation, ping, LuckPerms

## v1.6.6.6
- Folia scheduling guards
- Water-path stability fixes
- Spawn grace-period protection

## v1.6.6.2
- BungeeCord companion plugin support
- `AttributeCompat` fix

## v1.6.6
- `/fpp follow`
- Skin persistence
- Server-list config additions
- DB schema v17

## v1.6.5
- `/fpp ping`
- `/fpp attack`
- Permission restructure
- Skin mode rename
- `FlagParser` utility

## Older Versions
https://github.com/Pepe-tf/fake-player-plugin/commits/main

---

> **Note:** The built-in ConfigMigrator handles upgrades transparently. Current config version: **72**. Always back up `plugins/FakePlayerPlugin/` before major updates.
