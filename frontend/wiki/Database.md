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
    database: "fpp"
    username: "root"
    password: "secure_password"
    use-ssl: false
    pool-size: 5
    connection-timeout: 30000
```

Each backend server **must have a unique `server-id`** (e.g., `survival`, `skyblock`, `lobby`).

## What Is Stored

- Bot identities and metadata
- Bot session history (for `/fpp info`)
- Active bot locations and tasks (when persistence is on)
- Config sync data (if enabled)

## Database Migrations

The plugin automatically creates tables and runs schema migrations on startup. Current schema version: **v22**.

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

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Database could not be initialised" | Check MySQL credentials and network connectivity |
| Slow `/fpp info` queries | Lower `database.session-history.max-rows` |
| MySQL connection drops | Increase `mysql.pool-size` or check server timeout settings |
