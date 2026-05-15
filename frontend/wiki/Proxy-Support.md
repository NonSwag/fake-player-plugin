# Proxy Support

FPP supports **Velocity** and **BungeeCord** proxy networks via optional companion plugins.

## What You Get

- Cross-server bot visibility in tab lists and player counts
- Remote bot caching across backends
- Config sync across backend servers via shared MySQL

## Companion Plugins

The main `fpp.jar` handles backend server logic. For the proxy layer, download the companion plugins from the [FPP Marketplace](https://mp.fpp.wtf/resources/):

- **Velocity companion** — [FPP — Velocity](https://mp.fpp.wtf/resources/resource/7-fpp---velocity/)
- **BungeeCord companion** — [FPP — BungeeCord](https://mp.fpp.wtf/resources/resource/8-fpp---bungeecord/)

These are also available as optional Maven profiles in the source (`-Pbuild-velocity-companion` / `-Pbuild-bungee-companion`) if you prefer to build them yourself.

## Setup

### 1. Enable Network Mode

On **every backend server**, set:

```yaml
database:
  enabled: true
  mode: "NETWORK"
  server-id: "unique-name"
  mysql-enabled: true
  mysql:
    host: "..."
    port: 3306
    database: "fpp"
    username: "..."
    password: "..."
```

Each server must have a **unique `server-id`**.

### 2. Install Companion Plugin

- **Velocity:** Place the velocity companion JAR in `plugins/`
  - Set `bungeecord-compat-mode: true` in `velocity.toml`
- **BungeeCord:** Place the Bungee companion JAR in `plugins/`

### 3. Reload

Run `/fpp reload` on every backend server after making changes.

## How It Works

- Backends register bots in the shared MySQL database tagged with their `server-id`
- Other backends query the DB and cache remote bot entries
- Plugin messaging channels (`BungeeCord`, custom Velocity channels) sync state changes
- Remote bots appear in tab lists and placeholders but are not physically spawned on the local server

## Notes

- Cross-server bot **TP** and **task control** are not supported
- Remote bots are visual only (tab list + placeholders) on other backends
- The main FPP JAR handles all backend logic; the companion just bridges proxy messaging

## See Also

- [Database](Database) — MySQL setup details
- [Config Sync](Config-Sync) — Push/pull configs across backends
