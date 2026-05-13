# Changelog

## v1.6.6.9 (Current)
- Fall damage implemented (configurable via `combat.fall-damage`)
- Fall damage tracking fixed in `FakePlayerManager` tick loop
- Skin injector fixes for skin extension compatibility
- Config migrator improvements (v71→v72 cleanup)
- Extension bundle support
- Config YML extension removal & migration handling
- Further API additions for extensions
- Database/config migration improvements

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
