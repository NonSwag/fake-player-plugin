# Folia Support

FPP is compatible with **Folia** regionised threading out of the box.

## What Works

- Bot spawning and despawning
- Pathfinding and navigation
- Chunk loading (Folia-compatible ticket system)
- Task scheduling (repeating/delayed tasks dispatch to the correct region)
- Database I/O (async safe)
- Plugin messaging (BungeeCord / Velocity channels)
- Per-bot GUIs and settings

## What Does Not Work

- Features that rely on Folia-unsafe synchronous cross-region operations may behave differently. The codebase avoids blocking Folia scheduler threads.

## How It Works

- `FppScheduler` detects Folia at runtime via reflection on `RegionScheduler`.
- Tasks are dispatched to the entity's owning region thread or the location's region thread instead of the global main thread.
- `NmsPlayerSpawner.isFolia()` gates Folia-specific logic throughout the codebase.

## Performance Notes

- On Folia, bot-heavy servers benefit from regionised scheduling because bot tick work is distributed across region threads rather than all running on one main thread.
- Chunk loading still works, but the `mass-disable-threshold` (`chunk-loading.mass-disable-threshold`) auto-releases chunk tickets when bot counts are high to avoid region overload.

## No Extra Configuration Needed

FPP auto-detects Folia. There are no Folia-specific config keys.
