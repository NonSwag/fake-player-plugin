# FAQ & Troubleshooting

## General

### Q: What server software is supported?
**A:** Paper 1.21+ (up to 1.21.11). Folia is also supported.

### Q: Does it work on Spigot or CraftBukkit?
**A:** No. FPP uses Paper-specific APIs and NMS Mojang-mapped classes.

### Q: What Java version do I need?
**A:** JDK 21+ for both the server and for building from source.

### Q: Can I use this on a server with ViaVersion?
**A:** Yes, but the server itself must be Paper 1.21+. ViaVersion only affects client versions.

## Bots & Spawning

### Q: Bots are not showing in the tab list.
**A:** Check `config.yml` for tab-list settings. Ensure `body.enabled` and tab list debug are not conflicting. Also verify no other plugin is overriding tab list packets.

### Q: Bots appear but have no skin.
**A:** Check `config.yml` skin settings. If `skin.mode` is `off`, skins are disabled. Set to `auto` or `player`. The Mojang API can also rate-limit; try again later.

### Q: Spawn cooldown is blocking players.
**A:** Set `spawn-cooldown: 0` in `config.yml` or grant `fpp.bypass.cooldown`.

### Q: "Max bots reached" but I have fewer than the limit.
**A:** The limit is both global (`limits.max-bots`) and personal (`fpp.spawn.limit.N`). Check both.

## Tasks & Pathfinding

### Q: Bot stopped mining halfway through.
**A:** The bot may have run out of tools or encountered an unbreakable block. Use `/fpp storage` to set a supply container for restocking.

### Q: Bot is stuck and won't move.
**A:** Try `/fpp stop <bot>` then re-issue the task. Check the pathfinding debug log if enabled. Bots may also get stuck in unloaded chunks; chunk-loading helps but is not guaranteed.

### Q: `/fpp mine --wesel` does nothing.
**A:** WorldEdit must be installed. The sender must have an active WorldEdit selection.

### Q: Follow command stops after a restart.
**A:** Tasks are persisted when `persistence.enabled: true` and the database is active. If using YAML-only persistence, tasks may not survive restarts.

## Database

### Q: Can I use SQLite for a network setup?
**A:** No. SQLite is local-only. Use MySQL for multi-server setups.

### Q: Database connection fails on startup.
**A:** Verify credentials, firewall rules, and that the MySQL user has CREATE/ALTER permissions (schema migrations need them).

## Extensions

### Q: Where do I put extension JARs?
**A:** `plugins/FakePlayerPlugin/extensions/`. Create the folder if it doesn't exist, then `/fpp reload`.

### Q: `fpp-spoof.jar` extension is missing.
**A:** This extension is not part of the core plugin. Download it from the [FPP Marketplace](https://mp.fpp.wtf/resources/resource/9-fpp---spoof/).

## Performance

### Q: Server lag with many bots.
**A:**
- Lower `chunk-loading.radius` or set `mass-disable-threshold` lower
- Reduce `head-ai.tick-rate`
- Increase `performance.position-sync-distance` (or set to `128`)
- Reduce bot count or spawn in batches

### Q: Folia: bots despawn unexpectedly.
**A:** Ensure bot entities are not crossing region boundaries during tasks. This is a known edge case; `/fpp stop` and re-task usually fixes it.

## Building

### Q: Build fails with "cannot find symbol" for NMS classes.
**A:** Ensure `libs/paper-1.21.11-mojang-mapped.jar` exists. This is a system-scoped dependency; the build cannot proceed without it.

### Q: `velocity-companion` or `bungee-companion` build fails.
**A:** These directories are `.gitignored` and may not exist. Only build them if you have the companion source.

## Fall Damage

### Q: Bots take fall damage even with `body.damageable: false`.
**A:** `body.damageable` only controls player/entity damage. Fall damage is governed by `combat.fall-damage.enabled` and is independent. Set `combat.fall-damage.enabled: false` to disable fall damage entirely.
