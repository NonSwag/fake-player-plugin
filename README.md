# FakePlayerPlugin

> Advanced Fake Player Spoofer for Paper 1.21+

A Minecraft Paper plugin that creates realistic fake players — full tab-list entries, physical in-world bodies, skins, combat, pathfinding, automation, and multi-server proxy support.

- **Version:** 1.6.6.9
- **Platform:** Paper / Folia 1.21.x (up to `1.21.11`)
- **Java:** 21+
- **License:** MIT
- **Source:** https://github.com/Pepe-tf/fake-player-plugin

---

## Features

- **Realistic fake players** — tab-list integration, join/leave messages, server count spoofing
- **Physical bodies** — NMS `ServerPlayer` entities with hitboxes, collision, damage, death & respawn
- **Skins** — auto-resolve from Mojang, per-bot skin commands (`/fpp skin`), custom pool support
- **Pathfinding & automation** — A* navigation, follow, roam, find-and-mine, sleep, auto-eat, auto-place-bed
- **Area mining & block placing** — cuboid region mining (`/fpp mine`) and placement (`/fpp place`) with supply-container restocking
- **PvE combat** — per-bot attack settings, hunt mode, melee cooldowns
- **Per-bot settings GUI** — shift+right-click any bot for inventories, pathfinding toggles, PvE settings, and automation overrides
- **Persistence** — bot positions, tasks, and inventories survive restarts (YAML or database)
- **Database** — SQLite (local) or MySQL (network / multi-server)
- **Proxy support** — Velocity & BungeeCord companion plugins for cross-server bot visibility
- **Config sync** — push/pull config across backend servers via shared MySQL
- **Extension API** — drop `.jar` files into `plugins/FakePlayerPlugin/extensions/` to load third-party addons
- **Random name generator** — `bot-name.mode: random` generates realistic Minecraft-style usernames on the fly
- **Badword filter** — leet-speak normalization, auto-rename, remote word list
- **PlaceholderAPI** — 29+ placeholders for scoreboards, tab headers, and more
- **WorldEdit & WorldGuard** — `--wesel` selection flag for mine/place; region-aware PvP protection
- **Folia support** — compatible with Folia's regionised threading model
- **Simulated ping** — tab-list latency display per bot

Some advanced subsystems are implemented as an extension (`fpp-spoof.jar`) rather than core code:
- AI conversations (`/msg` replies, personalities)
- Fake chat / broadcast messaging
- Swap system / peak-hours scheduler
- Bot groups
- Ping command (`/fpp ping`)
- Stored right-click commands (`/fpp cmd`)

---

## Installation

1. Download the plugin JAR from [Modrinth](https://modrinth.com/plugin/fake-player-plugin-(fpp)) or build from source.
2. Drop `fpp.jar` into your server's `plugins/` folder.
3. Restart the server. The plugin will create `plugins/FakePlayerPlugin/` with configs and data folders.
4. Configure permissions and `plugins/FakePlayerPlugin/config.yml` as needed.
5. Run `/fpp reload` to apply most config changes without restarting.

### Optional Dependencies
- **PlaceholderAPI** — enables placeholder expansion (`%fpp_count%`, `%fpp_total%`, etc.)
- **LuckPerms** — prefix/suffix support and bot group assignment
- **WorldGuard** — bot PvP region protection
- **WorldEdit** — `--wesel` flag for area mining/placing

---

## Building

Requires **JDK 21** and **Maven**.

```bash
mvn clean package
```

- Build output: `build/fpp.jar`
- Auto-deploy target (override via `-Ddeploy.dir=…`): `~/Desktop/dmc/plugins/fpp.jar`
- Optional companion plugins: add `-Pbuild-velocity-companion` and/or `-Pbuild-bungee-companion`

The build uses a **system-scoped** Mojang-mapped Paper server JAR at `libs/paper-1.21.11-mojang-mapped.jar`. Do not remove or rename this file.

---

## Commands

| Command | Description |
|---------|-------------|
| `/fpp spawn [count]` | Spawn fake player bots |
| `/fpp despawn <bot\|all>` | Remove bot(s) |
| `/fpp list` | List active bots |
| `/fpp tph <bot\|all>` | Teleport bot(s) to you |
| `/fpp tp <bot>` | Teleport to a bot |
| `/fpp xp <bot>` | Collect XP from a bot |
| `/fpp move <bot> --to <player>` | Navigate bot to a player |
| `/fpp mine <bot>` | Start area mining |
| `/fpp place <bot>` | Start block placement |
| `/fpp use <bot>` | Right-click automation |
| `/fpp attack <bot>` | PvE attack mode |
| `/fpp follow <bot> <player>` | Make bot follow a player |
| `/fpp find <bot> <material>` | Find and mine nearby blocks |
| `/fpp sleep <bot>` | Auto-sleep at night |
| `/fpp stop <bot\|all>` | Cancel all active tasks |
| `/fpp freeze <bot\|all>` | Freeze/unfreeze bot(s) |
| `/fpp inventory <bot>` | Open bot inventory GUI |
| `/fpp skin <bot> <username\|url\|reset>` | Apply or reset a bot's skin |
| `/fpp save` | Force-save all active bots |
| `/fpp setowner <bot> <player>` | Transfer bot ownership |
| `/fpp rename <bot> <new_name>` | Rename a bot |
| `/fpp info [bot]` | Show bot info / session history |
| `/fpp stats` | Show plugin statistics |
| `/fpp badword <check\|update\|status>` | Manage badword filter |
| `/fpp migrate` | Backup, migrate, or convert data |
| `/fpp reload` | Hot-reload config and language files |
| `/fpp settings [bot]` | Open settings GUI |
| `/fpp help` | Show help menu |

See `src/main/resources/plugin.yml` for the full permission tree.

---

## Configuration

Main config: `plugins/FakePlayerPlugin/config.yml`

Key sections:
- `limits` — max bots, user limits, spawn cooldowns
- `persistence` — save/restore bots on restart
- `bot-name` — name sources and formatting
- `badword-filter` — profanity filtering settings
- `body` — entity settings (pushable, damageable, item pickup)
- `combat` — health, fall damage, hurt sounds
- `death` — respawn behavior
- `chunk-loading` — keep chunks loaded around bots
- `automation` — auto-eat, auto-place-bed, auto-milk, bad-omen prevention
- `head-ai` — smooth head rotation toward nearby players
- `swim-ai` — automatic upward swimming
- `collision` — push radius, strength, and separation
- `database` — SQLite / MySQL settings and network mode
- `config-sync` — cross-server config push/pull
- `performance` — position-sync distance tuning
- `logging.debug` — per-subsystem debug flags

The plugin includes an **automatic config migrator** that backs up and upgrades configs across versions. Do not edit `config-version` manually.

---

## Permissions

- `fpp.admin` (or `fpp.op`) — full admin access (default: `op`)
- `fpp.use` — user-tier access: spawn, tph, xp, info (default: `true`)
- `fpp.spawn.limit.<N>` — personal bot limit (1–100)
- `fpp.bypass.max` — bypass global bot cap
- `fpp.bypass.cooldown` — skip spawn cooldown

Full permission list is available in `src/main/resources/plugin.yml`.

---

## Support

- **Discord:** https://discord.gg/RfjEJDG2TM
- **Modrinth:** https://modrinth.com/plugin/fake-player-plugin-(fpp)
- **Issues:** https://github.com/Pepe-tf/fake-player-plugin/issues

---

## License

MIT License. See `LICENSE`.
