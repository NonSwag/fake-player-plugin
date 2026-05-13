# Configuration

Main file: `plugins/FakePlayerPlugin/config.yml`

Run `/fpp reload` to apply most changes without restarting.

## Structure

### `config-version`
Managed automatically by the built-in migrator. **Do not edit.**

### `language`
Default: `en`. Points to `plugins/FakePlayerPlugin/language/<lang>.yml`.

---

## 1. Spawning

### `limits`
- `max-bots: 1000` — global cap (`0` = unlimited)
- `user-bot-limit: 1` — default personal limit for `fpp.use` players
- `spawn-presets: [1, 5, 10, 15, 20]` — tab-complete suggestions for `/fpp spawn`

### `spawn-cooldown`
Seconds between `/fpp spawn` uses. `0` = disabled.

### `persistence`
- `enabled: true` — bots save position on shutdown and rejoin on restart

### `join-delay` / `leave-delay`
Stagger spawns/despawns in **ticks** (20 ticks = 1 second).

---

## 2. Appearance

### `bot-name`
- `mode: random` — `random` (generate username) or `pool` (pick from `bot-names.yml`)
- `admin-format: '{bot_name}'` — display name for admin spawns
- `user-format: 'bot-{spawner}-{num}'` — display name for user spawns

### `badword-filter`
- `enabled: true` — block/rename bad names
- `use-global-list: false` — fetch remote profanity list
- `global-list-url: "..."` — remote word list URL
- `global-list-timeout-ms: 5000` — fetch timeout
- `words: []` — inline word list (merged with `bad-words.yml`)
- `whitelist: []` — allowed names even if they match bad words
- `auto-rename: true` — silently rename bad names instead of blocking
- `auto-detection`
  - `enabled: true`
  - `mode: normal` — `off` / `normal` / `strict`

### `bot-interaction`
- `right-click-enabled: true` — right-click opens inventory/executes command
- `shift-right-click-settings: true` — shift+right-click opens bot settings GUI

### `messages`
- `join-message: true` — broadcast join message
- `leave-message: true` — broadcast leave message
- `death-message: true` — broadcast vanilla death message
- `kill-message: false` — broadcast when a real player kills a bot
- `notify-admins-on-join: true` — send compatibility warnings to admins on join

---

## 3. Body & Combat

### `body`
- `enabled: true` — physical entity in the world
- `pushable: true` — players/explosions can push bots
- `damageable: true` — take all damage (if `false`, still takes environmental)
- `pick-up-items: true`
- `pick-up-xp: true`
- `drop-items-on-despawn: false` — `false` = remember inventory on despawn

### `combat`
- `max-health: 20.0` — standard player HP
- `hurt-sound: true`
- `fall-damage`
  - `enabled: true`
  - `safe-distance: 3.0` — blocks before damage starts
  - `multiplier: 1.0` — damage scale

### `death`
- `respawn-on-death: false` — respawn at spawn location after death
- `respawn-delay: 15` — ticks before respawn
- `suppress-drops: false` — `true` = suppress all drops

### `chunk-loading`
- `enabled: true` — keep chunks loaded around bots
- `radius: "auto"` — `"auto"`, `0` = disabled, or fixed number
- `update-interval: 20` — ticks between position checks
- `mass-disable-threshold: 100` — release chunk tickets when active bots exceed this (`0` = never)

### `automation`
Defaults copied to newly spawned bots:
- `auto-eat: true`
- `auto-place-bed: true`
- `auto-milk: true`
- `prevent-bad-omen: true`

---

## 4. AI & Navigation

### `head-ai`
- `enabled: true` — smooth head rotation toward nearest player
- `look-range: 8.0` — detection radius
- `turn-speed: 0.3` — smoothing (0.0 = frozen, 1.0 = instant)
- `tick-rate: 3` — scan every N ticks

### `swim-ai`
- `enabled: true` — automatic upward swimming

### `collision`
- `walk-radius: 0.85` — push radius when walking into a bot
- `walk-strength: 0.22`
- `hit-strength: 0.45`
- `hit-max-horizontal-speed: 0.80`
- `bot-radius: 0.90` — bot-vs-bot separation radius
- `bot-strength: 0.14`
- `max-horizontal-speed: 0.30`

---

## 5. Database & Network

### `database`
- `enabled: true` — `false` = file-only persistence
- `mode: "LOCAL"` — `"LOCAL"` or `"NETWORK"`
- `server-id: "default"` — unique name per backend (NETWORK mode only)
- `mysql-enabled: false`
- `mysql` — host, port, database, username, password, use-ssl, pool-size, connection-timeout
- `location-flush-interval: 30` — seconds between position DB writes
- `session-history.max-rows: 20` — max rows per `/fpp info` query

### `config-sync`
- `mode: "DISABLED"` — `"DISABLED"`, `"MANUAL"`, `"AUTO_PULL"`, `"AUTO_PUSH"`

---

## 9. Performance

### `performance`
- `position-sync-distance: 128.0` — max distance (blocks) for per-tick position-sync packets. `0` = send to all players regardless of distance.

---

## 10. Debug & Logging

### `debug: false`
Master switch. `true` enables all debug categories.

### `logging.debug`
- `startup: false`
- `nms: false`
- `packets: false`
- `network: false`
- `config-sync: false`
- `database: false`

---

## Attack Mob Targeting

### `attack-mob`
- `default-range: 8.0`
- `default-priority: nearest` — `nearest` or `lowest-health`
- `smooth-rotation-speed: 12.0` — degrees per tick
- `retarget-interval: 10` — ticks between scans
- `line-of-sight: true`

---

## Metrics

### `metrics`
- `enabled: true` — anonymous FastStats usage statistics

---

## Migration

The plugin includes a built-in **ConfigMigrator** that:
1. Creates a timestamped backup before any change
2. Automatically upgrades configs when `config-version` is outdated
3. Removes obsolete keys and adds new defaults

Do **not** edit `config-version` manually.
