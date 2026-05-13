# Placeholders

FPP provides **29+ placeholders** via PlaceholderAPI (requires the PlaceholderAPI plugin).

All identifiers are prefixed with `%fpp_`.

## Server-Wide

| Placeholder | Description |
|-------------|-------------|
| `%fpp_count%` | Total bots (local + remote in NETWORK mode) |
| `%fpp_local_count%` | Bots on this server only |
| `%fpp_network_count%` | Bots on other proxy servers |
| `%fpp_max%` | Global bot cap (`∞` if unlimited) |
| `%fpp_real%` | Real players online |
| `%fpp_total%` | Total players (real + bots) |
| `%fpp_online%` | Same as `%fpp_total%` |
| `%fpp_frozen%` | Number of frozen bots |
| `%fpp_names%` | Comma-separated bot names |
| `%fpp_network_names%` | Comma-separated remote bot names |
| `%fpp_version%` | Plugin version string |

## State / Toggle

| Placeholder | Description |
|-------------|-------------|
| `%fpp_chat%` | `on` or `off` (fake chat enabled) |
| `%fpp_skin%` | Current skin mode |
| `%fpp_body%` | `on` or `off` (body enabled) |
| `%fpp_pushable%` | `on` or `off` |
| `%fpp_damageable%` | `on` or `off` |
| `%fpp_tab%` | `on` or `off` (tab list enabled) |
| `%fpp_ping%` | `on` or `off` (ping enabled) |
| `%fpp_max_health%` | Bot max health value |
| `%fpp_network%` | `on` or `off` (NETWORK mode) |
| `%fpp_server_id%` | Current server ID |
| `%fpp_persistence%` | `on` or `off` |
| `%fpp_spawn_cooldown%` | Spawn cooldown seconds |

## Per-World

| Placeholder | Description |
|-------------|-------------|
| `%fpp_count_<world>%` | Bots in specific world |
| `%fpp_real_<world>%` | Real players in specific world |
| `%fpp_total_<world>%` | Total (real + bots) in specific world |

## Player-Relative

| Placeholder | Description |
|-------------|-------------|
| `%fpp_user_count%` | Player's bot count |
| `%fpp_user_max%` | Player's bot limit |
| `%fpp_user_names%` | Comma-separated names of player's bots |
| `%fpp_user_ping%` | Ping of player's first bot |
| `%fpp_user_ping_avg%` | Average ping of player's bots |

## Per-Bot

| Placeholder | Description |
|-------------|-------------|
| `%fpp_ping_<bot_name>%` | Specific bot's ping |
| `%fpp_ping_all%` | If sender is a bot, return bot's ping; otherwise sender's real ping |
| `%fpp_avg_ping%` | Average ping across all local bots |
| `%fpp_player_ping%` | Sender's real player ping |

## Examples

```
# Tab list header
&7Bots: %fpp_count% | Real: %fpp_real% | Total: %fpp_total%

# Scoreboard
'Bot Count': %fpp_count%
'Your Bots': %fpp_user_count% / %fpp_user_max%
```
