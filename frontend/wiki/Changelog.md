# Changelog

## v1.6.6.10 (Current)
- **Velocity & Bungee companions updated to v1.0.1** with config file support (`config.yml`)
- **New companion config option**: `show-bot-names-in-player-sample` — toggle whether bot names appear in the server-list player sample (hover list)
- **Extension data folder fix**: extensions now use their `getName()` as the data folder name instead of the JAR filename
- **FppBotDisplayService API**: new extension service interface (`FppBotDisplayService`) allowing extensions to decorate bot display names dynamically; used by `BotBroadcast` for join/leave messages
- **BotIdentityCache async loading**: DB identity mappings now load asynchronously on startup to reduce blocking time
- **BotIdentityCache debounced YAML saves**: YAML identity saves are now batched and scheduled (1-second debounce) instead of writing synchronously on every new bot
- **Removed stale join-delay/leave-delay config getters**: `Config.joinDelayMin/Max()` and `Config.leaveDelayMin/Max()` removed (config keys were already unused)
- **Startup banner extensions count**: startup banner now shows how many extensions are loaded
- **Deprecation fixes**: replaced deprecated BungeeCord `getServers()` with `getServersCopy()`; replaced deprecated `FixedMetadataValue` with `PersistentDataContainer` + `NamespacedKey`; cleaned up unchecked-operation compiler warnings
- **Legal pages added**: new `frontend/legal/` section with copyright notice, extension policy, privacy policy, and terms of service
- Plugin authors updated to `F_PP` and `Kyttu`
- Wiki version references updated across docs

## v1.6.6.9
- Fall damage implemented (configurable via `combat.fall-damage`)
- Fall damage tracking fixed in `FakePlayerManager` tick loop
- Skin injector fixes for skin extension compatibility
- Config migrator improvements (v71→v72 cleanup)
- Extension bundle support
- Config YML extension removal & migration handling
- Further API additions for extensions
- Database/config migration improvements
- Wiki updated with new marketplace links for extensions and proxy companions

## v1.6.6.8
- **Spoofing features moved to `fpp-spoof.jar` extension** — fake chat, AI conversations, swap system, peak-hours scheduler, ping command, bot groups, and stored commands are no longer in core; they now ship as the `fpp-spoof.jar` extension
- **PvE Smart Attack Mode**: tri-state per-bot setting (OFF / ON_NO_MOVE / ON_MOVE)
- Hunt mode (`--hunt`) for roaming mob hunting
- New commands: `/fpp save`, `/fpp setowner`
- Per-bot overrides: `respawn-on-death`, `auto-eat`, `auto-place-bed`
- `BotSettingGui` overhaul: new PvE tab, Pathfinding tab, share control
- Extension config & resources support (`extension-resources/` in JAR)
- DB schema v22: new columns for PvE, automation, ping, LuckPerms

## v1.6.6.6
- Folia scheduling guard improvements
- Water-path stability fixes
- Spawn grace-period protection

## v1.6.6.2
- BungeeCord companion plugin support
- `AttributeCompat` fix

## v1.6.6
- `/fpp follow` command
- Skin persistence across restarts
- Server-list config additions
- `pathfinding.max-fall` tuning
- DB schema v17

## v1.6.5
- `/fpp ping` command
- `/fpp attack` command
- Permission restructure
- Skin mode rename
- `FlagParser` utility

## Older Versions

For versions prior to v1.6.5, see the Git history:
https://github.com/Pepe-tf/fake-player-plugin/commits/main

---

> **Note:** The built-in ConfigMigrator handles upgrades transparently. Current config version: **71**. Always back up `plugins/FakePlayerPlugin/` before updating to a new major version.
