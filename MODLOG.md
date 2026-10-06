# Bleach: Fractured World development log

## 2026-10-06 — M1 resumption audit

### Environment

- Workspace: `/mnt/c/Users/squid/Software dev/BleachFracturedWorld`
- Branch: `main`
- Minecraft: 1.21.1
- NeoForge: 21.1.252
- ModDevGradle: 2.0.148
- Parchment: 2024.11.17
- Mod: `bleachfracturedworld` 0.1.0
- Gradle toolchain: Eclipse Temurin JDK 21.0.12.1+1-LTS
- Shell JVM: Ubuntu JDK 17.0.20.1; not the configured compilation toolchain

### Repository state found

- M1 design and implementation-plan documents existed but were untracked.
- Product source was still the NeoForge example template.
- No M1 profile, service, command, payload, HUD, debug console, or GameTests existed.
- No earlier `MODDING_PLAN.md` or `MODLOG.md` existed.
- Tracked working-tree changes were line-ending churn; `git diff --ignore-space-at-eol` showed no semantic tracked changes.
- The checked-out `gradlew` had CRLF endings and could not execute under WSL. The committed LF form was streamed to Bash for baseline verification without changing the working file.

### Baseline verification

- Command: `git show HEAD:gradlew | bash -s -- build`
- Result: `BUILD SUCCESSFUL` in 12m 20s after a cold NeoForge/Minecraft artifact setup.
- Tasks: 5 actionable, 1 executed, 4 up-to-date.
- Tests: `test NO-SOURCE`; the successful build is not M1 gameplay evidence.
- Artifact present: `build/libs/bleachfracturedworld-0.1.0.jar`.
- Prior `run/logs/latest.log` showed an integrated server reaching `Done` on October 5, 2026, but also included third-party mod errors. It is not a clean dedicated-server or M1 acceptance result.

### Decisions

- Complete M1 and the debug console as one vertical slice.
- Keep Escape's normal pause behavior and add a `BWF Debug` pause-screen button.
- Keep all profile mutation server-authoritative.
- Separate development-only reset/energy operations from ordinary progression services.
- Include a server-side `DebugAccessPolicy` seam now; tighten restrictions before public distribution.

### Next step

Update and review the combined written M1/debug-console specification, then revise the implementation plan and execute it test-first.

## 2026-10-06 — Restore the contiguous dependency profile

### Finding

- M1 temporarily routed `runClient` to `run/m1-client` and `runServer` to `run/m1-server`.
- The authoritative dependency stack remained in `run/mods/`, so the M1 client loaded only Bleach Fractured World, Minecraft, and NeoForge.
- The prior shared-profile log at `run/logs/latest.log` proves the combined stack loaded Epic Fight 21.17.3.1, Epic Fight - Invincible 21.15.8.2, Weapons of Miracles 2.0.178, MineColonies 1.1.1403, WarNTaxes 5.0.9, Immersive Portals 6.0.7, and their supporting mods together.

### Decision

- All normal client and server development uses the shared `run/` profile and complete `run/mods/` stack across every milestone.
- Milestone-specific gameplay profiles are prohibited because they conceal cross-mod compatibility failures.
- `run/gametest/` remains isolated only for disposable automated GameTest worlds and logs; it is not a manual gameplay or acceptance profile.
- Existing mods, configurations, saves, and worlds remain in place. No files are moved or deleted.

### Verification

- Shared-profile routing check passed: normal client and server use `run/`; GameTest uses `run/gametest/`.
- `./gradlew build --project-cache-dir /tmp/bwf-gradle-project-cache --no-daemon`: `BUILD SUCCESSFUL` in 31 seconds.
- `./gradlew runGameTestServer --project-cache-dir /tmp/bwf-gradle-project-cache --no-daemon`: all 23 required tests passed and the test server shut down cleanly.
- `gradlew.bat runClient --no-daemon`: Windows client resolved `GAMEDIR` to `run`, `MODSDIR` to `run/mods`, loaded the 18-entry combined mod list, completed resource reload, initialized Epic Fight, and started the sound engine without a fatal startup error.
- The combined stack emits existing third-party missing-icon, missing-subtitle/sound, and optional WaveyCapes-layer diagnostics. These did not stop startup and are retained as compatibility observations for later triage.
- Two byte-identical MineColonies jars are present in `run/mods/` (`minecolonies-1.1.1403-1.21.1.jar` and the `(1)` copy), both SHA-256 `c8d4373ffc8a76638c8edf2bb6716f9209599032209c48d0d559b787f69a0561`. NeoForge selected one copy during this launch. Neither file was removed because dependency-folder cleanup requires an explicit user request.

## 2026-10-06 — Stable simultaneous client/server profiles

### Finding

- Sharing one game directory exposed client/server log and mutable-file contention during simultaneous testing.
- A server launch at 02:01 failed only because port 25565 was already owned by stale WSL Java PID 24513. Every dependency completed discovery and loading before the bind failure; the later shutdown exception was secondary to incomplete server initialization.

### Decision

- Keep `run/mods/` as the single canonical dependency collection.
- Use stable, non-milestone `run/client/` and `run/server/` process profiles.
- `syncClientDevelopmentMods` and `syncServerDevelopmentMods` mirror every canonical jar into the corresponding generated profile before launch.
- The profiles keep separate logs, worlds, saves, and mutable configuration and may therefore run simultaneously.
- The user exclusively performs all client/server launches, process management, and in-game testing. Agent verification is limited to code/configuration inspection, compilation, and non-game unit tests.
