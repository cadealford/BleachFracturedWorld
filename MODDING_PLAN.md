# Bleach: Fractured World modding plan

Updated October 6, 2026.

- Development install: `/mnt/c/Users/squid/Software dev/BleachFracturedWorld`; normal runtime profiles are `run/client/` and `run/server/`.
- Target: Minecraft Java Edition 1.21.1, NeoForge 21.1.252, ModDevGradle 2.0.148, Parchment 2024.11.17.
- Runtime: managed Java, 64-bit; Gradle uses an auto-provisioned Eclipse Temurin JDK 21 toolchain.
- Anti-cheat / online: no anti-cheat is part of the development route. Development and verification are limited to local/offline worlds and privately controlled servers. Do not use the mod on protected public servers.
- Saves: `run/saves/` or the dedicated lab world's configured directory.
- Config: process-local configuration under `run/client/config/` and `run/server/config/`.
- Mods: `run/mods/`. This is the single authoritative development dependency stack for normal client and server launches.
- Logs: `run/client/logs/latest.log` and `run/server/logs/latest.log`; automated GameTests use `run/gametest/logs/`.
- Community route: the official NeoForge mod API and ModDevGradle workspace. Relevant official references are [getting started](https://docs.neoforged.net/docs/1.21.1/gettingstarted/), [data attachments](https://docs.neoforged.net/docs/1.21.1/datastorage/attachments/), [networking](https://docs.neoforged.net/docs/1.21.1/networking/), and [screens](https://docs.neoforged.net/docs/1.21.1/gui/screens/).
- Chosen route: extend the existing NeoForge mod through public loader APIs. F01.M02 uses a persisted player attachment, directional payloads, server event/command handlers, client HUD/screen hooks, and NeoForge GameTests.
- Lab plan: use the stable client/server profiles with the complete mirrored mod stack active. Create disposable worlds under `run/client/saves/` or `run/server/world/` and back them up before schema or destructive gameplay testing; never use an ordinary player world. The user exclusively performs client/server launches and game input.
- Current vertical slice: F01.M02, persisted Shinigami/Quincy identity, synchronized spiritual energy, and a server-authoritative development console accessible from the pause screen. Current status and gates are maintained in the canonical [roadmap](docs/ROADMAP.md).
- Verification gate: agent-run compilation and applicable non-runtime unit checks first; the user then performs client, dedicated-server, persistence, multiplayer, and packaged-jar passes required by the roadmap. The agent does not launch a Minecraft runtime.
- Toolkit note: the Universal Modder `um` executable is not currently on `PATH`, so its shared knowledge-base search and backup command were unavailable during this recon. No loader installation or game-folder mutation was performed.
- Normal development rule: milestones must not create milestone-specific profiles. `run/mods/` is the single canonical dependency collection; Gradle mirrors all jars into stable `run/client/mods/` and `run/server/mods/` before their respective launch tasks. This permits simultaneous client/server testing without sharing worlds, logs, or mutable configuration.
- External infrastructure rule: BWF owns gameplay semantics and consumes third-party technology through BWF-owned services/adapters. The canonical policy is [External Mod Infrastructure Strategy](docs/External_Mod_Infrastructure_Strategy.md). Uninstalled candidates require explicit approval plus compatibility and license verification before entering `run/mods` or build metadata.
- Next implementation milestone: specify F01.M03, server-authoritative combat and Epic Fight integration, while any failures found in the pending F01.M01/F01.M02 user acceptance pass remain release-blocking.
- Unknowns to resolve before release: packaged-jar client/server pair and the final release debug-access policy.

