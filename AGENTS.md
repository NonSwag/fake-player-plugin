# FakePlayerPlugin — Agent Notes

## Build
- **Tool:** Maven only. JDK 21 required (`<release>21</release>`).
- **Command:** `mvn clean package`
- **No tests exist** (`src/test/` absent); `mvn test` is a no-op.
- **Output:** `target/fpp-<version>.jar`, copied to `build/fpp.jar` and `${user.home}/Desktop/dmc/plugins/fpp.jar` (override with `-Ddeploy.dir=…`).

## Architecture
- **Single-module Maven project** for a Paper/Folia Minecraft plugin.
- **Main:** `me.bill.fakePlayerPlugin.FakePlayerPlugin` (see `plugin.yml`).
- **NMS:** Uses Mojang-mapped class names. Compile-time dependency is a **system-scoped** JAR: `libs/paper-1.21.11-mojang-mapped.jar`. Do not remove or rename this file.
- **FastStats:** Bundled as raw binary resources under `src/main/resources/faststats/` and loaded via `URLClassLoader` at runtime. Explicitly excluded from shading to avoid relocation issues.
- **Shaded deps:** `sqlite-jdbc` and `mysql-connector-j` only. All other dependencies are `provided` or `system` scope.

## Code Style
- Google Java Format (`libs/google-java-format-1.25.2-all-deps.jar`) is present but there is no enforced formatter config or pre-commit hook. Keep style consistent with existing files.

## Companion Modules
- `velocity-companion/` and `bungee-companion/` are **gitignored** but currently exist locally as separate Maven projects.
- Optional profiles build them: `-Pbuild-velocity-companion` / `-Pbuild-bungee-companion`.
- These delegate via `exec-maven-plugin`; only activate the profiles when the directories are present, otherwise the build will fail.

## Key Runtime Resources
- `src/main/resources/plugin.yml` — Bukkit plugin descriptor.
- `src/main/resources/velocity-plugin.json` — Velocity proxy descriptor.
- `src/main/resources/config.yml` — Plugin configuration.
- `src/main/resources/language/en.yml` — Messages (MiniMessage format).
- `src/main/resources/bot-names.yml` & `bad-words.yml` — Name lists.

## Important Constraints
- Do **not** text-filter the `faststats/**` JARs in `pom.xml` `<resources>` — they are binary.
- Do **not** shade or relocate FastStats packages.
- Paper API version is `1.21` (max supported up to `1.21.11`).

## Docs
- No root `README.md` build instructions. User-facing docs live in `frontend/wiki/` (Markdown) and are **not** built by Maven.
- Repository: `https://github.com/Pepe-tf/fake-player-plugin.git`.
