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
