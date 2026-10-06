# M1 Spiritual Identity and Debug Console Implementation Plan

> **Canonical identifier:** This historical M1 plan implements F01.M02 — Persistent spiritual identity and development console. It remains at its original path for stable links. Current status and gates live in the [roadmap](../../ROADMAP.md). All Git/commit steps below are historical instructions and must not be executed unless the user explicitly requests them.

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver a persistent, server-authoritative Shinigami/Quincy identity with synchronized spiritual energy, a HUD, and a pause-screen development console for repeatable testing.

**Architecture:** Store one immutable `PlayerProfile` in a persisted NeoForge player attachment and route every mutation through server services. Versioned directional payloads synchronize owner-only state; client HUD and debug screens render that state but never author it. Debug actions are bounded, access-checked server requests separated from ordinary progression rules.

**Tech Stack:** Minecraft 1.21.1, NeoForge 21.1.252, ModDevGradle 2.0.148, Java 21, Parchment 2024.11.17, NeoForge GameTest.

**Spec:** `docs/superpowers/specs/2026-10-05-m1-spiritual-identity-design.md`

> **Superseding development-profile rule (revised October 6, 2026):** Normal client and server tasks use stable `run/client/` and `run/server/` process profiles. Both receive the complete canonical `run/mods/` stack through prerequisite Gradle sync tasks. Milestone-specific gameplay directories are prohibited. Only the automated GameTest world/log directory is otherwise isolated. The user performs all Git operations; do not execute any commit step in this historical plan unless explicitly requested.

## Global Constraints

- Target exactly Minecraft 1.21.1, NeoForge 21.1.252, Java 21, and Parchment 2024.11.17.
- Keep authoritative profile state server-side in a persisted attachment configured with `copyOnDeath`.
- Initial profile is `UNCHOSEN`, current energy 100, maximum energy 100, mastery 0, revision 0.
- Ordinary selection is one-time and accepts only `SHINIGAMI` or `QUINCY`.
- Every accepted mutation replaces the immutable attachment value and advances revision without overflow.
- Protocol version is `1`; payloads are directional and reveal only the owning player's presentation state.
- Common/server classes must not reference Minecraft client classes.
- Debug requests identify only a bounded action; the network connection supplies the acting player.
- Debug tools default enabled for the private development loop, but every server request passes `DebugAccessPolicy`.
- Client launch or input automation requires separate explicit permission; GameTests, compilation, builds, and headless server checks do not drive player input.

## Review Focus

- A profile at revision `Long.MAX_VALUE` must reject another mutation rather than wrap negative.
- A stale client snapshot must not replace a newer cached snapshot, including after a delayed packet.
- Disconnecting must clear the client cache so another server cannot display the prior profile.
- Disabled debug access must reject every action without mutation or invoking the ordinary profile-snapshot sender; only the reason-bearing debug result may reply.
- Invalid persisted bounds or unknown network action IDs must be rejected rather than materialized as usable state.

## File Structure

- `BleachFracturedWorld.java`: common bootstrap and registry/event wiring only.
- `BleachFracturedWorldClient.java`: client event registration only.
- `config/BwfConfig.java`: development debug-tools server configuration.
- `player/SpiritualPath.java`: stable path identifiers and codec.
- `player/PlayerProfile.java`: immutable validated profile and persistence codec.
- `player/PlayerProfiles.java`: attachment registration.
- `player/ProfileMutationResult.java`: ordinary selection outcome.
- `player/ProfileSnapshotSender.java`: synchronization seam used by services and tests.
- `player/ProfileService.java`: ordinary one-time path mutation boundary.
- `command/SpiritualIdentityCommands.java`: `/bwf choose` registration and feedback.
- `network/ProfileSnapshotPayload.java`: owner-only clientbound profile snapshot.
- `network/BwfNetworking.java`: protocol registration, handlers, and sends.
- `client/ClientProfileCache.java`: revision-aware snapshot cache without Minecraft client imports.
- `client/SpiritualEnergyHud.java`: energy overlay.
- `debug/DebugAction.java`: bounded network action IDs.
- `debug/DebugAccessPolicy.java`: server access seam.
- `debug/DebugActionResult.java`: accepted/rejected debug outcome.
- `debug/DebugProfileService.java`: reset, energy presets, refresh, and delegated selection.
- `network/DebugActionRequestPayload.java`: serverbound action request.
- `network/DebugActionResultPayload.java`: clientbound result plus authoritative snapshot.
- `client/debug/ClientDebugState.java`: latest debug result for presentation.
- `client/debug/DebugScreenModel.java`: client-independent screen state and button availability.
- `client/debug/BwfDebugScreen.java`: standalone development console.
- `client/debug/PauseScreenDebugButton.java`: pause-screen button injection.
- `test/SpiritualIdentityGameTests.java`: deterministic server/domain regression coverage.

---

### Task 1: Establish the GameTest harness and persistent profile

**Files:**
- Modify: `.gitattributes`
- Modify: `.gitignore`
- Modify: `build.gradle`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/BleachFracturedWorld.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/BleachFracturedWorldClient.java`
- Delete: `src/main/java/io/github/cadealford/bleachfracturedworld/Config.java`
- Modify: `src/main/resources/assets/bleachfracturedworld/lang/en_us.json`
- Create: `src/main/resources/data/bleachfracturedworld/structure/empty.nbt`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/test/SpiritualIdentityGameTests.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/player/SpiritualPath.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/player/PlayerProfile.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/player/PlayerProfiles.java`

**Interfaces:**
- Consumes: `BleachFracturedWorld.MODID`, NeoForge `AttachmentType`, `Codec`, `GameTestHelper.makeMockServerPlayerInLevel()`.
- Produces: `SpiritualPath`, `PlayerProfile.unchosen()`, `PlayerProfile.withPath(...)`, `PlayerProfile.withCurrentEnergy(...)`, `PlayerProfiles.PROFILE`.

- [ ] **Step 1: Add deterministic test scaffolding**

Force LF for `gradlew` in `.gitattributes`, configure stable client/server process profiles with a canonical `run/mods/` mirror, keep GameTest world/log output under `run/gametest/`, set `gameTestServer.setForceExit false`, and add a gzipped 1×1×1 empty structure named `bleachfracturedworld:empty`. Annotate the test class with `@GameTestHolder(MODID)` and `@PrefixGameTestTemplate(false)`.

- [ ] **Step 2: Write failing GameTests for profile invariants and attachment defaults**

Add `newProfileHasSafeDefaults`, `invalidProfileBoundsAreRejected`, `codecRejectsInvalidPersistedBounds`, and `attachmentMaterializesSafeDefault`. Assert literal values `UNCHOSEN/100/100/0/0`; invalid current/max energy, negative mastery, negative revision, and null path must reject. Decode a hand-written invalid map through `PlayerProfile.CODEC` with `JsonOps` and assert an error. The attachment test uses a real mock server player and `getData(PlayerProfiles.PROFILE)`.

- [ ] **Step 3: Run the tests and verify RED**

Run: `./gradlew runGameTestServer`

Expected: compilation fails because `SpiritualPath`, `PlayerProfile`, or `PlayerProfiles` does not exist; the wrapper itself must execute successfully.

- [ ] **Step 4: Implement the immutable profile and attachment**

Create `SpiritualPath` with stable lowercase serialized names and `CODEC`. Create `PlayerProfile(SpiritualPath path, int currentEnergy, int maxEnergy, int mastery, long revision)` with canonical validation, `unchosen()`, `withPath(SpiritualPath,long)`, and `withCurrentEnergy(int,long)`. Its codec rejects invalid decoded data. Register `PlayerProfiles.PROFILE` with default supplier, codec serialization, and `copyOnDeath()`.

- [ ] **Step 5: Remove NeoForge example gameplay surface and wire the attachment registry**

Remove example blocks, items, tab, config, and greeting logs. Keep only mod ID/logger/bootstrap responsibilities and register the attachment deferred register on the mod event bus. Replace example translations with an empty JSON object until later tasks add real keys.

- [ ] **Step 6: Run the GameTests and build to verify GREEN**

Run: `./gradlew runGameTestServer && ./gradlew build`

Expected: all four GameTests pass; build exits 0; no tests are reported as missing.

- [ ] **Step 7: Commit**

```bash
git add .gitattributes .gitignore build.gradle src/main/java src/main/resources
git commit -m "feat: persist spiritual player profiles"
```

### Task 2: Implement ordinary server-authoritative path selection

**Files:**
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/player/ProfileMutationResult.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/player/ProfileSnapshotSender.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/player/ProfileService.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/test/SpiritualIdentityGameTests.java`

**Interfaces:**
- Consumes: `PlayerProfiles.PROFILE`, immutable `PlayerProfile`, and `SpiritualPath`.
- Produces: `ProfileMutationResult(Status status, PlayerProfile profile)`, `ProfileSnapshotSender.send(ServerPlayer)`, `ProfileService(ProfileSnapshotSender)`, and `ProfileService.choosePath(ServerPlayer, SpiritualPath)`.

- [ ] **Step 1: Write failing selection-service GameTests**

Add `firstSelectionPersistsAndSynchronizesOnce`, `repeatSelectionRejectsWithoutMutationOrSync`, `unchosenSelectionRejects`, and `revisionOverflowRejects`. Use a real mock server player and a counting `ProfileSnapshotSender`. Assert the exact stored profile, status, revision, and send count.

- [ ] **Step 2: Run the tests and verify RED**

Run: `./gradlew runGameTestServer`

Expected: compilation fails because `ProfileService`, `ProfileMutationResult`, or `ProfileSnapshotSender` does not exist.

- [ ] **Step 3: Implement the mutation result and service**

Define statuses `ACCEPTED`, `INVALID_PATH`, `ALREADY_CHOSEN`, and `REVISION_EXHAUSTED`. `choosePath` rejects without `setData` or snapshot dispatch; success preserves energy/mastery, increments revision once, persists the replacement, and dispatches once.

- [ ] **Step 4: Run the GameTests and verify GREEN**

Run: `./gradlew runGameTestServer`

Expected: all profile and selection tests pass.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/io/github/cadealford/bleachfracturedworld/player src/main/java/io/github/cadealford/bleachfracturedworld/test
git commit -m "feat: add authoritative spiritual path selection"
```

### Task 3: Add profile networking, command feedback, and energy HUD

**Files:**
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/network/ProfileSnapshotPayload.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/network/BwfNetworking.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/player/BwfServices.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/command/SpiritualIdentityCommands.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/client/ClientProfileCache.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/client/SpiritualEnergyHud.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/BleachFracturedWorld.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/BleachFracturedWorldClient.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/test/SpiritualIdentityGameTests.java`
- Modify: `src/main/resources/assets/bleachfracturedworld/lang/en_us.json`

**Interfaces:**
- Consumes: `ProfileService`, `ProfileSnapshotSender`, `PlayerProfile`, and NeoForge payload/event APIs.
- Produces: protocol `1`, `ProfileSnapshotPayload.from(PlayerProfile)`, `BwfNetworking.sendProfileSnapshot(ServerPlayer)`, `BwfServices.PROFILES`, `/bwf choose <shinigami|quincy>`, `ClientProfileCache.accept(...)`, `ClientProfileCache.clear()`.

- [ ] **Step 1: Write failing snapshot/cache GameTests**

Add `snapshotCopiesEveryPresentationField`, `cacheRejectsOlderRevision`, `cacheAcceptsEqualOrNewerRevision`, and `cacheClearRemovesSnapshot`. Expected fields are literal and do not reuse profile conversion helpers.

- [ ] **Step 2: Run the tests and verify RED**

Run: `./gradlew runGameTestServer`

Expected: compilation fails because the payload or cache types do not exist.

- [ ] **Step 3: Implement versioned clientbound profile synchronization**

Implement `ProfileSnapshotPayload` as a `CustomPacketPayload` with bounded codecs for path, current energy, maximum energy, mastery, and revision. Register it through `event.registrar("1").playToClient(...)`. `BwfNetworking.sendProfileSnapshot` uses `PacketDistributor.sendToPlayer`.

- [ ] **Step 4: Implement service composition and synchronization boundaries**

Construct `BwfServices.PROFILES` with `BwfNetworking::sendProfileSnapshot`. Register login and respawn listeners that send only the owning player's snapshot. Register `/bwf choose` and map every service status to translatable feedback without mutating state in the command handler.

- [ ] **Step 5: Implement revision-aware client cache and HUD**

`ClientProfileCache` accepts an incoming snapshot only when no snapshot exists or `incoming.revision() >= current.revision()`, and clears on client logout. Register `SpiritualEnergyHud` above `VanillaGuiLayers.HOTBAR`; render `Spiritual Energy: <current>/<max>` only for a selected path.

- [ ] **Step 6: Run GameTests and build to verify GREEN**

Run: `./gradlew runGameTestServer && ./gradlew build`

Expected: all GameTests pass; common compilation and client compilation succeed; the built jar contains the new payload and client classes.

- [ ] **Step 7: Commit**

```bash
git add src/main/java src/main/resources/assets/bleachfracturedworld/lang/en_us.json
git commit -m "feat: sync spiritual identity to the player HUD"
```

### Task 4: Implement server-authoritative debug actions

**Files:**
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/config/BwfConfig.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/debug/DebugAction.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/debug/DebugAccessPolicy.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/debug/DebugActionResult.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/debug/DebugProfileService.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/network/DebugActionRequestPayload.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/network/DebugActionResultPayload.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/client/debug/ClientDebugState.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/network/BwfNetworking.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/player/BwfServices.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/BleachFracturedWorld.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/test/SpiritualIdentityGameTests.java`

**Interfaces:**
- Consumes: `BwfServices.PROFILES`, `ProfileSnapshotPayload`, `ProfileSnapshotSender`, and the connection-derived `ServerPlayer`.
- Produces: `DebugAction` IDs for choose/reset/energy/refresh, `DebugProfileService.perform(ServerPlayer, DebugAction)`, serverbound request handling, clientbound `DebugActionResultPayload`, and `ClientDebugState.accept(...)`.

- [ ] **Step 1: Write failing debug-service GameTests**

Add `disabledDebugAccessRejectsWithoutMutationOrSync`, `debugResetRestoresDefaultsAndAdvancesRevision`, `energyPresetsPreservePathMaxAndMastery`, `refreshSynchronizesWithoutAdvancingRevision`, `debugSelectionUsesOrdinaryDuplicateRule`, `debugMutationRevisionOverflowRejects`, `unknownDebugActionIdRejects`, `debugClientStateStoresLatestResult`, and `debugClientStateClearsOnDisconnect`. Assert exact profiles, result codes, and send counts; the client state model must have no Minecraft client imports.

- [ ] **Step 2: Run the tests and verify RED**

Run: `./gradlew runGameTestServer`

Expected: compilation fails because debug domain/service types do not exist.

- [ ] **Step 3: Implement bounded actions, access policy, and debug service**

Define stable numeric IDs for `CHOOSE_SHINIGAMI`, `CHOOSE_QUINCY`, `RESET_PROFILE`, `ENERGY_ZERO`, `ENERGY_HALF`, `ENERGY_FULL`, and `REFRESH`. Unknown IDs return no action. The service checks access first; selection delegates to `ProfileService`; reset preserves monotonic revision while restoring defaults; energy presets preserve path/max/mastery; refresh sends without mutation.

- [ ] **Step 4: Register development configuration and payloads**

Register `debugToolsEnabled` as a server config with default `true`. Register the request as `playToServer` and the result as `playToClient` under protocol `1`. The server handler accepts only `context.player()` when it is a `ServerPlayer`, executes on the server thread, logs actor UUID/action/result/revision, and replies with the authoritative result snapshot. The client result handler updates both `ClientProfileCache` and `ClientDebugState`; disconnect clears both.

- [ ] **Step 5: Run GameTests and build to verify GREEN**

Run: `./gradlew runGameTestServer && ./gradlew build`

Expected: all GameTests pass; malformed/denied cases do not mutate or dispatch; build exits 0.

- [ ] **Step 6: Commit**

```bash
git add src/main/java
git commit -m "feat: add server-authoritative debug actions"
```

### Task 5: Add the pause-screen debug console

**Files:**
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/client/debug/DebugScreenModel.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/client/debug/BwfDebugScreen.java`
- Create: `src/main/java/io/github/cadealford/bleachfracturedworld/client/debug/PauseScreenDebugButton.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/network/BwfNetworking.java`
- Modify: `src/main/java/io/github/cadealford/bleachfracturedworld/BleachFracturedWorldClient.java`
- Modify: `src/main/resources/assets/bleachfracturedworld/lang/en_us.json`

**Interfaces:**
- Consumes: `ClientProfileCache`, `DebugActionRequestPayload`, `DebugActionResultPayload`, `ScreenEvent.Init.Post`, and `PauseScreen`.
- Produces: `DebugScreenModel.from(...)`, `BwfDebugScreen(Screen parent)`, and a `BWF Debug` pause-screen button.

- [ ] **Step 1: Write failing screen-model GameTests**

Add `debugScreenModelDisablesActionsWithoutSnapshot` and `debugScreenModelExposesAuthoritativeProfile`. The model must derive literal presentation values and action availability from connection state, `ClientProfileCache`, and `ClientDebugState` without Minecraft client imports.

- [ ] **Step 2: Run the tests and verify RED**

Run: `./gradlew runGameTestServer`

Expected: compilation fails because `DebugScreenModel` does not exist.

- [ ] **Step 3: Implement the pure debug screen model**

Implement `DebugScreenModel.from(...)` so disconnected or missing-snapshot state disables mutations, while a connected authoritative snapshot exposes path, energy, mastery, revision, and the latest result.

- [ ] **Step 4: Implement the standalone debug screen**

Render path, current/max energy, mastery, revision, connection/snapshot status, and the last result. Add buttons for the seven M1 actions. Buttons send only `DebugActionRequestPayload`; mutation controls are disabled while disconnected or before a snapshot exists. Escape and Done return to the parent pause screen.

- [ ] **Step 5: Inject the pause-screen button**

Listen to `ScreenEvent.Init.Post` only on the physical client. When `event.getScreen()` is a `PauseScreen` for an active world connection, add one `BWF Debug` button that opens `BwfDebugScreen`; do not replace Escape behavior or alter other screens.

- [ ] **Step 6: Run GameTests and build to verify GREEN**

Run: `./gradlew runGameTestServer && ./gradlew build`

Expected: all GameTests pass; build exits 0; no client class is loaded by the GameTest server.

- [ ] **Step 7: Commit**

```bash
git add src/main/java src/main/resources/assets/bleachfracturedworld/lang/en_us.json
git commit -m "feat: add pause-screen debug console"
```

### Task 6: Verify the combined M1 slice and record evidence

**Files:**
- Modify: `MODLOG.md`
- Modify: `docs/Bleach_Fractured_World_Architecture.md`

**Interfaces:**
- Consumes: completed profile, command, synchronization, HUD, debug actions, and debug screen.
- Produces: reproducible automated evidence, a headless dedicated-server result, and a clearly separated manual-client checklist/result.

- [ ] **Step 1: Run the complete automated verification**

Run: `./gradlew clean runGameTestServer && ./gradlew build`

Expected: every GameTest passes from a clean build; build exits 0; generated jar is `build/libs/bleachfracturedworld-0.1.0.jar`.

- [ ] **Step 2: Run a headless dedicated-server class-loading check**

The user starts `runServer` in the stable `run/server/` profile with the complete mirrored dependency stack, waits for `Done`, issues `stop` through the server console, and inspects the log. If EULA acceptance is required, the user decides whether to accept it.

Expected: server starts and stops cleanly with no client-class loading error from `bleachfracturedworld`.

- [ ] **Step 3: Record automated evidence and remaining manual checks**

Record commands, versions, artifact hash, test counts, log paths, warnings, and observed results in `MODLOG.md`. Mark M1 as implemented but not fully accepted until the permitted manual client scenario proves pause-screen UI, HUD, death, reconnect, restart, and packaged-jar behavior.

- [ ] **Step 4: Perform the manual client scenario only after explicit permission**

The user uses the stable `run/client/` profile and its complete mirrored dependency stack with a fresh backed-up 1.21.1 lab world. Verify pause-screen entry, both ordinary and debug selection paths, duplicate rejection, reset, energy presets, HUD, death/respawn, reconnect, restart, packaged-jar operation, and a clear rejection for a deliberately mismatched protocol jar. The agent does not launch or control the game.

- [ ] **Step 5: Update milestone status from observed evidence**

If every M1 acceptance check passes with the complete shared dependency stack active, mark M1 accepted in the architecture table and record the combined profile/jar. Otherwise leave it implemented-but-unaccepted and list each missing or failed check.

- [ ] **Step 6: Commit**

```bash
git add MODLOG.md docs/Bleach_Fractured_World_Architecture.md
git commit -m "docs: record m1 verification evidence"
```
