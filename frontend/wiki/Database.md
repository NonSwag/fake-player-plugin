# Database

FPP supports two database backends: **SQLite** (local, zero-config) and **MySQL** (network / multi-server).

## SQLite (Default)

- **File:** `plugins/FakePlayerPlugin/data/fpp.db`
- **Scope:** Single server only
- **Setup:** None — works out of the box
- **Use when:** You're only running one server or don't need cross-server bot visibility

## MySQL

- **Scope:** Shared across multiple backend servers (Velocity / BungeeCord networks)
- **Setup:** Enable `database.mysql-enabled: true` and fill in credentials
- **Use when:** Running a proxy network with multiple backend servers
- **Required for:** Cross-server bot visibility, network totals, config sync, proxy-merged player counts

### MySQL Config Example

```yaml
database:
  enabled: true
  mode: "NETWORK"
  server-id: "survival"
  mysql-enabled: true
  mysql:
    host: "localhost"
    port: 3306
    database: "fpp_network"
    username: "root"
    password: "secure_password"
    use-ssl: false
    pool-size: 5
    connection-timeout: 30000
```

Each backend server **must have a unique `server-id`** (e.g., `survival`, `skyblock`, `lobby`).

## What Is Stored

### Local Tables (per server)
- **Bot identities and metadata** (`fpp_bot_identities`)
- **Session history** (`fpp_bot_sessions`) for `/fpp info`
- **Active bot locations and tasks** (`fpp_active_bots`) when persistence is on
- **Skin cache** (`fpp_skin_cache`)
- **Extension data** (`fpp_bot_extension_data`)

### Network Tables (shared across all backends)

| Table | Columns | Purpose |
|-------|---------|---------|
| `fpp_network_bots` | bot_uuid, bot_name, bot_display, server_id, spawned_by, world_name, pos_x/y/z, ping, frozen, updated_at | Shared live bot registry. Every backend upserts its bots here every 5 seconds. |
| `fpp_server_heartbeat` | server_id, real_players, bot_count, last_seen | Per-server liveness. Backends heartbeat here; stale servers (60s) are auto-pruned. |
| `fpp_network_tasks` | id, target_bot, source_server, target_server, task_type, task_data, status, result | Cross-server command queue (foundation for future command router). |
| `fpp_config_sync` | config_file, server_id, content_hash, content, pushed_at | Config push/pull across backends. |

### How Network Tables Work

```
Backend A                     Shared MySQL                      Backend B
   |                              |                                  |
   ├─ every 5s:                  |                                  |
   │  INSERT/UPDATE               |                                  |
   │  fpp_network_bots            |  ◀────── reads remote bots ──────┤
   │  fpp_server_heartbeat        |                                  |
   │                              |                                  |
   └─ every 5s: ────────────────▶ |  ◀────── refresh totals ─────────┘
      proxy-pushed NETWORK_STATS  |     (or read from heartbeat)
```

1. Each backend **writes** its live bots to `fpp_network_bots`
2. Each backend **heartbeats** its counts to `fpp_server_heartbeat`
3. Each backend **reads** remote bots from `fpp_network_bots` into `RemoteBotCache`
4. Placeholders read totals from `fpp_server_heartbeat` (or proxy-pushed messages, whichever is freshest)
5. The proxy companion pings backends and pushes `NETWORK_STATS` every 5 seconds

## Database Migrations

The plugin automatically creates tables and runs schema migrations on startup. Current schema version: **v25**.

Key migrations:
- **v25** — Added `fpp_network_bots`, `fpp_server_heartbeat`, `fpp_network_tasks` (proxy-merged architecture)
- **v22** — PvE columns, automation columns, ping columns
- **v17** — LuckPerms group, server_id columns

If you upgrade the plugin, the migrator will add new columns and tables automatically. Always back up your database before major updates.

## Disabling the Database

```yaml
database:
  enabled: false
```

When disabled:
- Persistence still works via YAML files (`data/`)
- No session history in `/fpp info`
- No cross-server support
- No config sync
- No network totals

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Database could not be initialised" | Check MySQL credentials and network connectivity |
| Slow `/fpp info` queries | Lower `database.session-history.max-rows` |
| MySQL connection drops | Increase `mysql.pool-size` or check server timeout settings |
| `%fpp_network_total%` doesn't update | Verify `database.mode: "NETWORK"` and MySQL connectivity; check console for `[NetworkHeartbeat]` errors |
| Duplicate server entries in network totals | Check that each backend has a unique `server-id` |
| Stale server data | Pruning runs automatically every 60s; check `[NetworkHeartbeat]` debug logs |

## MySQL User Permissions

The minimum required MySQL permissions for FPP:

```sql
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, DROP
ON fpp_network.* TO 'fpp_user'@'%';
```

## See Also

- [Proxy Support](Proxy-Support) — Full proxy setup guide
- [Config Sync](Config-Sync) — Push/pull configs across backends
- [Placeholders](Placeholders) — Network-aware placeholder reference
