# FakePlayerPlugin

[![Version](https://img.shields.io/modrinth/v/fake-player-plugin-%28fpp%29?style=flat-square&label=version&color=0079FF&logo=modrinth)](https://modrinth.com/plugin/fake-player-plugin-(fpp))
![MC](https://img.shields.io/badge/Minecraft-1.21.x-0079FF?style=flat-square)
![Platform](https://img.shields.io/badge/platform-Paper-0079FF?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-0079FF?style=flat-square)
[![License: MIT](https://img.shields.io/badge/License-MIT-green?style=flat-square)](https://github.com/Pepe-tf/fake-player-plugin/blob/main/LICENSE)
[![GitHub](https://img.shields.io/badge/GitHub-Open%20Source-181717?style=flat-square&logo=github)](https://github.com/Pepe-tf/fake-player-plugin)
[![Modrinth](https://img.shields.io/badge/Modrinth-FPP-00AF5C?style=flat-square&logo=modrinth)](https://modrinth.com/plugin/fake-player-plugin-(fpp))
[![Discord](https://img.shields.io/badge/Discord-Join%20Server-5865F2?style=flat-square&logo=discord&logoColor=white)](https://discord.gg/QSN7f67nkJ)
[![Wiki](https://img.shields.io/badge/Wiki-fpp.wtf-7B8EF0?style=flat-square)](https://fpp.wtf)
[![GitHub Sponsors](https://img.shields.io/badge/GitHub%20Sponsors-Sponsor-EA4AAA?style=flat-square&logo=githubsponsors&logoColor=white)](https://github.com/sponsors/Pepe-tf)
[![Patreon](https://img.shields.io/badge/Patreon-Support%20FPP-FF424D?style=flat-square&logo=patreon&logoColor=white)](https://www.patreon.com/c/F_PP?utm_medium=unknown&utm_source=join_link&utm_campaign=creatorshare_creator&utm_content=copyLink)

> **Advanced Fake Player Spoofer for Paper 1.21+**
> Create realistic fake players — full tab-list entries, physical in-world bodies, skins, combat, pathfinding, automation, and multi-server proxy support.

---

## ✨ Features

### Core (Ships with `fpp.jar`)

- 🎭 **Realistic Fake Players** — Full tab-list integration, join/leave messages, server count spoofing
- 🏃 **Physical Bodies** — NMS `ServerPlayer` entities with hitboxes, collision, damage, death & respawn
- 🎨 **Skins** — Auto-resolve from Mojang, per-bot skin commands, custom pool support
- 🧭 **Pathfinding & Automation** — A* navigation, follow, roam, find-and-mine, sleep, auto-eat, auto-place-bed
- ⛏️ **Area Mining & Block Placing** — Cuboid region mining (`/fpp mine`) and placement (`/fpp place`) with supply-container restocking
- ⚔️ **PvE Combat** — Per-bot attack settings, hunt mode, melee cooldowns
- ⚙️ **Per-Bot Settings GUI** — Shift+right-click any bot for inventories, pathfinding toggles, PvE settings, and automation overrides
- 💾 **Persistence** — Bot positions, tasks, and inventories survive restarts (YAML or database)
- 🗄️ **Database** — SQLite (local) or MySQL (network / multi-server)
- 🌐 **Proxy Support** — Velocity & BungeeCord companion plugins for cross-server bot visibility
- 🔄 **Config Sync** — Push/pull config across backend servers via shared MySQL
- 📦 **Extension API** — Drop `.jar` files into `plugins/FakePlayerPlugin/extensions/` to load third-party addons
- 🔤 **Random Name Generator** — `bot-name.mode: random` generates realistic Minecraft-style usernames on the fly
- 🚫 **Badword Filter** — Leet-speak normalization, auto-rename, remote word list
- 📊 **PlaceholderAPI** — 29+ placeholders for scoreboards, tab headers, and more
- 🧱 **WorldEdit & WorldGuard** — `--wesel` selection flag for mine/place; region-aware PvP protection
- 🍃 **Folia Support** — Compatible with Folia's regionised threading model out of the box
- 📶 **Simulated Ping** — Tab-list latency display per bot

### Extension (`fpp-spoof.jar`)

Some advanced subsystems require the **`fpp-spoof.jar` extension**:
- 🤖 AI conversations (`/msg` replies with personalities)
- 💬 Fake chat / broadcast messaging
- 🔄 Swap system / peak-hours scheduler
- 👥 Bot groups
- 📶 Ping command (`/fpp ping`)
- 💻 Stored right-click commands (`/fpp cmd`)

---

## 📥 Installation

1. Download `fpp.jar` from [Modrinth](https://modrinth.com/plugin/fake-player-plugin-(fpp)) or build from source.
2. Drop the JAR into your server's `plugins/` folder.
3. Restart the server. The plugin will create `plugins/FakePlayerPlugin/` with configs and data folders.
4. Configure permissions and `plugins/FakePlayerPlugin/config.yml` as needed.
5. Run `/fpp reload` to apply most config changes without restarting.

### Optional Dependencies
- **PlaceholderAPI** — enables placeholder expansion (`%fpp_count%`, `%fpp_total%`, etc.)
- **LuckPerms** — prefix/suffix support and bot group assignment
- **WorldGuard** — bot PvP region protection
- **WorldEdit** — `--wesel` flag for area mining/placing

---

## 🚀 Quick Start

```
# Grant yourself admin access
/lp user <you> permission set fpp.admin true

# Spawn your first bot
/fpp spawn

# Open its settings
shift+right-click the bot entity

# Teleport it to you
/fpp tph <bot>

# Make it follow you
/fpp follow <bot> <player>
```

---

## ⌨️ Commands

All commands are prefixed with `/fpp` (aliases: `fakeplayer`, `fp`).

| Command | Usage | Description | Permission |
|---------|-------|-------------|------------|
| **spawn** | `[amount] [world [x y z]] [--name <name>] [--random-name] [--notp]` | Spawn fake player bots | `fpp.spawn` (admin) / `fpp.spawn.user` (user) |
| **despawn** | `<name> \| all \| --count <n> \| --random [--count <n>]` | Remove bot(s) | `fpp.despawn` |
| **list** | `[page]` | List active bots | `fpp.list` |
| **tph** | `[botname\|all]` | Teleport bot(s) to you | `fpp.tph` |
| **tp** | `[botname]` | Teleport to a bot | `fpp.tp` |
| **xp** | `<bot>` | Collect XP from a bot | `fpp.xp` |
| **move** | `<bot\|all> --to <player> \| --coords <x y z> \| --roam [x,y,z] [radius] \| --stop` | Navigate bot | `fpp.move` |
| **mine** | `<bot> [--once\|--stop\|--pos1\|--pos2\|--start\|--wesel] \| --stop` | Mine blocks | `fpp.mine` |
| **place** | `<bot> [--once\|--stop\|--wesel] \| --stop` | Place blocks | `fpp.place` |
| **use** | `<bot> [--once\|--stop] \| --stop` | Right-click automation | `fpp.use.cmd` |
| **attack** | `<bot> [--mob [type]] [--range <n>] [--stop] \| --hunt [--range <n>] [--stop]` | PvE attack / hunt | `fpp.attack` |
| **follow** | `<bot\|all> <player\|--start> \| <bot\|all> --stop` | Follow a player | `fpp.follow` |
| **find** | `<bot> <block> [--radius <n>] [--count <n>] [--prefer-visible] \| <bot> --stop \| --stop` | Find and mine blocks | `fpp.find` |
| **sleep** | `<bot\|all> <x y z> <radius> \| <bot\|all> --stop` | Auto-sleep at night | `fpp.sleep` |
| **stop** | `[<bot>\|all]` | Cancel active tasks | `fpp.stop` |
| **freeze** | `<bot\|all> [on\|off]` | Freeze/unfreeze | `fpp.freeze` |
| **inventory** | `<bot>` (alias: `inv`) | Open bot inventory | `fpp.inventory` |
| **storage** | `<bot> [storage_name\|--list\|--remove <name>\|--clear]` | Manage supply containers | `fpp.storage` |
| **save** | — | Force-save all bots | `fpp.save` |
| **setowner** | `<bot> <player>` | Transfer ownership | `fpp.setowner` |
| **rename** | `<oldname> <newname>` | Rename a bot | `fpp.rename` |
| **info** | `[bot\|spawner] <name>` | Bot info / session history | `fpp.info` |
| **stats** | — | Plugin statistics | `fpp.stats` |
| **badword** | `<check\|update\|status>` | Manage badword filter | `fpp.badword` |
| **migrate** | `<backup\|status\|config\|lang\|names\|db>` | Backup / migrate data | `fpp.migrate` |
| **reload** | `[all\|config\|lang\|extensions]` | Hot-reload config | `fpp.reload` |
| **settings** | `[bot]` | Open settings GUI | `fpp.settings` |
| **help** | `[page]` | Show help menu | `fpp.help` |

### Quick Examples

```bash
/fpp spawn 5                          # Spawn 5 bots
/fpp spawn --name Steve               # Spawn a bot named "Steve"
/fpp spawn world_nether 100 64 -200   # Spawn in another world
/fpp despawn all                      # Remove all bots
/fpp despawn --random --count 3       # Remove 3 random bots
/fpp move bot1 --to Notch             # Navigate to player
/fpp move bot1 --roam 500,64,200 25   # Roam in 25-block radius
/fpp mine bot1 diamond_ore --wesel    # Mine using WorldEdit selection
/fpp place bot1 --once                # Place one block
/fpp attack bot1 --hunt --range 16    # Hunt mobs
/fpp follow bot1 Notch                # Follow a player
/fpp find bot1 diamond_ore --radius 64 --count 20
/fpp sleep bot1 100 64 200 50         # Set sleep origin
/fpp stop bot1                        # Stop all tasks
/fpp freeze bot1 on                   # Freeze bot
/fpp inv bot1                         # Open inventory
/fpp storage bot1 chest1              # Register container
/fpp rename bot1 builder_01           # Rename bot
/fpp info bot1                        # Show session history
```

---

## 🔐 Permissions

FPP uses a two-tier permission system.

### Wildcards

| Node | Default | Description |
|------|---------|-------------|
| `fpp.admin` | `op` | Full admin access (same as `fpp.op`) |
| `fpp.op` | `op` | Full access to all commands |
| `fpp.use` | `true` | User-tier: spawn (1 bot), tph, xp, info (own bots) |

### Key Nodes

- **Spawn:** `fpp.spawn`, `fpp.spawn.user`, `fpp.spawn.limit.1` through `fpp.spawn.limit.100`
- **Despawn:** `fpp.despawn`, `fpp.despawn.bulk`, `fpp.despawn.own`
- **Movement:** `fpp.move`, `fpp.move.to`, `fpp.move.stop`
- **Automation:** `fpp.mine`, `fpp.place`, `fpp.use.cmd`, `fpp.attack`, `fpp.attack.hunt`, `fpp.find`, `fpp.follow`, `fpp.sleep`, `fpp.stop`
- **Management:** `fpp.freeze`, `fpp.rename`, `fpp.rename.own`, `fpp.inventory`, `fpp.storage`, `fpp.setowner`, `fpp.save`, `fpp.settings`
- **System:** `fpp.reload`, `fpp.migrate`, `fpp.badword`
- **Bypass:** `fpp.bypass.max`, `fpp.bypass.cooldown`
- **Notify:** `fpp.notify` — update notifications on join

### Quick Setup

```bash
# Admin
/lp group admin permission set fpp.admin true

# User
/lp group member permission set fpp.use true

# Custom bot limit (5)
/lp user Alice permission set fpp.spawn.limit.5 true

# Bypass cooldown for VIPs
/lp group vip permission set fpp.bypass.cooldown true

# Hide /fpp from guests
/lp group guest permission set fpp.command false
```

---

## 📊 Placeholders

Requires **PlaceholderAPI**. All prefixed with `%fpp_`.

### Server-Wide

| Placeholder | Description |
|-------------|-------------|
| `%fpp_count%` | Total bots (local + remote) |
| `%fpp_local_count%` | Bots on this server |
| `%fpp_network_count%` | Bots on other proxy servers |
| `%fpp_max%` | Global bot cap (`∞` if unlimited) |
| `%fpp_real%` | Real players online |
| `%fpp_total%` / `%fpp_online%` | Total players (real + bots) |
| `%fpp_frozen%` | Frozen bot count |
| `%fpp_names%` | Comma-separated bot names |
| `%fpp_network_names%` | Remote bot names |
| `%fpp_version%` | Plugin version |

### State

| Placeholder | Description |
|-------------|-------------|
| `%fpp_chat%` | `on` / `off` |
| `%fpp_skin%` | Skin mode |
| `%fpp_body%` / `%fpp_pushable%` / `%fpp_damageable%` / `%fpp_tab%` / `%fpp_ping%` | `on` / `off` |
| `%fpp_max_health%` | Max HP |
| `%fpp_network%` | `on` / `off` (NETWORK mode) |
| `%fpp_server_id%` | Server ID |
| `%fpp_persistence%` | `on` / `off` |
| `%fpp_spawn_cooldown%` | Cooldown seconds |

### Per-World

| Placeholder | Description |
|-------------|-------------|
| `%fpp_count_<world>%` | Bots in world |
| `%fpp_real_<world>%` | Real players in world |
| `%fpp_total_<world>%` | Total in world |

### Player-Relative

| Placeholder | Description |
|-------------|-------------|
| `%fpp_user_count%` | Player's bot count |
| `%fpp_user_max%` | Player's bot limit |
| `%fpp_user_names%` | Player's bot names |
| `%fpp_user_ping%` | First bot's ping |
| `%fpp_user_ping_avg%` | Average ping |

### Per-Bot

| Placeholder | Description |
|-------------|-------------|
| `%fpp_ping_<bot_name>%` | Specific bot's ping |
| `%fpp_ping_all%` | Bot ping or player ping |
| `%fpp_avg_ping%` | Average across all bots |
| `%fpp_player_ping%` | Sender's real ping |

---

## 🗂️ Configuration

Main file: `plugins/FakePlayerPlugin/config.yml`

Key sections:
- `limits` — max bots, user limits, spawn cooldowns
- `persistence` — save/restore bots on restart
- `bot-name` — name sources and formatting
- `badword-filter` — profanity filtering
- `body` — entity settings (pushable, damageable, item pickup)
- `combat` — health, fall damage, hurt sounds
- `death` — respawn behavior
- `chunk-loading` — keep chunks loaded around bots
- `automation` — auto-eat, auto-place-bed, auto-milk, bad-omen prevention
- `head-ai` — smooth head rotation
- `swim-ai` — automatic upward swimming
- `collision` — push radius, strength, separation
- `database` — SQLite / MySQL settings
- `config-sync` — cross-server config push/pull
- `performance` — position-sync distance tuning
- `logging.debug` — per-subsystem debug flags

The plugin includes an **automatic config migrator** (current version: **71**). Do not edit `config-version` manually.

---

## 📚 Documentation

- [Wiki](https://fpp.wtf) — Full documentation
- [Commands](https://fpp.wtf/wiki/Commands) — Command reference
- [Permissions](https://fpp.wtf/wiki/Permissions) — Permission setup
- [Configuration](https://fpp.wtf/wiki/Configuration) — Config tuning
- [Extensions](https://fpp.wtf/wiki/Extensions) — Extension API guide
- [Changelog](https://fpp.wtf/wiki/Changelog) — Version history

---

## 💬 Support

- **Discord:** [Join our server](https://discord.gg/QSN7f67nkJ)
- **Modrinth:** [Download updates](https://modrinth.com/plugin/fake-player-plugin-(fpp))
- **GitHub Issues:** [Report bugs & request features](https://github.com/Pepe-tf/fake-player-plugin/issues)
- **GitHub Sponsors:** [Sponsor development](https://github.com/sponsors/Pepe-tf)
- **Patreon:** [Support FPP](https://www.patreon.com/c/F_PP?utm_medium=unknown&utm_source=join_link&utm_campaign=creatorshare_creator&utm_content=copyLink)

---

## ⚖️ License

MIT License. See [`LICENSE`](https://github.com/Pepe-tf/fake-player-plugin/blob/main/LICENSE).

---

> Made with ❤️ by [Bill_Hub](https://github.com/Pepe-tf)
