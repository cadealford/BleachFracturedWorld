# Bleach: Fractured World modding plan

Updated October 6, 2026.

- Development install: `/mnt/c/Users/squid/Software dev/BleachFracturedWorld` with a generated game directory at `run/`.
- Target: Minecraft Java Edition 1.21.1, NeoForge 21.1.252, ModDevGradle 2.0.148, Parchment 2024.11.17.
- Runtime: managed Java, 64-bit; Gradle uses an auto-provisioned Eclipse Temurin JDK 21 toolchain.
- Anti-cheat / online: no anti-cheat is part of the development route. Development and verification are limited to local/offline worlds and privately controlled servers. Do not use the mod on protected public servers.
- Saves: `run/saves/` or the dedicated lab world's configured directory.
- Config: `run/config/`.
- Logs: `run/logs/latest.log` and the dedicated server's log directory.
- Community route: the official NeoForge mod API and ModDevGradle workspace. Relevant official references are [getting started](https://docs.neoforged.net/docs/1.21.1/gettingstarted/), [data attachments](https://docs.neoforged.net/docs/1.21.1/datastorage/attachments/), [networking](https://docs.neoforged.net/docs/1.21.1/networking/), and [screens](https://docs.neoforged.net/docs/1.21.1/gui/screens/).
- Chosen route: extend the existing NeoForge mod through public loader APIs. M1 uses a persisted player attachment, directional payloads, server event/command handlers, client HUD/screen hooks, and NeoForge GameTests.
- Lab plan: use a fresh 1.21.1 world and separate development profile; back up any existing save before schema or gameplay testing; never test against an ordinary player world. A client launch or automated input session requires explicit user approval.
- Current vertical slice: persisted Shinigami/Quincy identity, synchronized spiritual energy, and a server-authoritative development console accessible from the pause screen.
- Verification gate: GameTests and build first, then dedicated-server persistence, then an approved manual client pass, then a packaged-jar pass in a clean 1.21.1 NeoForge instance.
- Toolkit note: the Universal Modder `um` executable is not currently on `PATH`, so its shared knowledge-base search and backup command were unavailable during this recon. No loader installation or game-folder mutation was performed.
- Unknowns to resolve before M1 acceptance: exact clean lab profile path, packaged-jar client/server pair, final release debug-access policy, and whether the existing third-party development mod set should be excluded from the M1 acceptance profile.

