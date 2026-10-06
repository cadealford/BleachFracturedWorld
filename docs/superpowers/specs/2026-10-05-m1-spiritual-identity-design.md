# M1 Spiritual Identity and Debug Console Design

## Goal

Deliver the first playable spiritual identity loop: a player chooses Shinigami or Quincy once, retains that identity and a 100/100 spiritual-energy baseline through death, reconnect, and restart, and sees the authoritative energy value in-game. Provide an in-mod development console that makes this loop repeatable without giving the client authority over profile state.

## Scope

M1 adds player identity, energy persistence, authoritative selection, synchronization, a minimal HUD, and a development-only debug console reached from the pause screen. The console can inspect the owning player's profile, invoke ordinary path selection, reset the profile for another test, and set energy to zero, half, or maximum. It does not add abilities, sword items, projectiles, combat integrations, progression rewards, or a production path-selection GUI.

## Player profile

Store an immutable `PlayerProfile` as a persisted NeoForge data attachment on each player. Its fields are:

| Field | Type | Initial value | Rule |
|---|---|---:|---|
| `path` | `SpiritualPath` | `UNCHOSEN` | May transition exactly once to `SHINIGAMI` or `QUINCY` in M1. |
| `currentEnergy` | `int` | 100 | Must remain within `0..maxEnergy`. |
| `maxEnergy` | `int` | 100 | Fixed in M1; later milestones own progression changes. |
| `mastery` | `int` | 0 | Fixed in M1; later milestones own progression changes. |
| `revision` | `long` | 0 | Increments for every accepted profile mutation. |

Use an attachment codec for persistence and `copyOnDeath` so the profile carries over on death. Mutations replace the attachment value through `setData`; no code may mutate a cached profile instance. Malformed serialized values must be rejected or normalized to the safe default rather than creating invalid energy bounds.

## Server authority and command

Register `/bwf choose <shinigami|quincy>` on the server. It is player-only and has no permission requirement beyond being a connected player.

`ProfileService.choosePath(ServerPlayer, SpiritualPath)` is the single mutation boundary. It accepts only `SHINIGAMI` or `QUINCY`, rejects `UNCHOSEN`, and rejects a profile whose path is already selected. On success it preserves the baseline energy and mastery, increments `revision`, persists the replacement profile, sends success feedback, and requests a client snapshot. On rejection it changes no state and sends a reason-bearing failure message.

The ordinary player-facing selection route in M1 is the command. The development console may request the same selection operation through a bounded client payload, but both routes terminate at the same server-authoritative `ProfileService.choosePath` boundary.

The debug console is the only exception to the command-only user interface, not to server authority. A bounded debug request identifies an action, never a player UUID or replacement profile. The connection supplies the actor. The server checks `DebugAccessPolicy`, maps the action to `ProfileService` or `DebugProfileService`, logs the result, and sends a fresh authoritative snapshot.

Ordinary selection still calls `ProfileService.choosePath`. Debug reset and energy changes use a separate `DebugProfileService`, so later access restrictions can disable debug mutation without changing production progression rules. Every accepted debug mutation replaces the immutable attachment value and increments `revision`; a rejected action changes nothing.

## Client synchronization and HUD

Register a clientbound `ProfileSnapshotPayload` with a fixed network protocol version. The payload contains only the profile fields needed for presentation: path, current energy, max energy, mastery, and revision. It is sent to the owning player after login, respawn, and an accepted path selection.

The client payload handler updates an owner-only `ClientProfileCache`. A client overlay reads that cache and shows `Spiritual Energy: <current>/<max>` only after a path has been selected. Common/server code must not reference Minecraft client classes; the payload's client handler and overlay registration stay in the client package.

## Development debug console

Escape keeps its normal Minecraft behavior. After a `PauseScreen` initializes, a client-only event listener adds a `BWF Debug` button. The button opens a standalone `BwfDebugScreen` with the pause screen as its parent; Escape or Done returns to the pause screen.

The screen shows the latest authoritative path, current/max energy, mastery, revision, and the last debug result. Its M1 controls are:

- `Choose Shinigami`
- `Choose Quincy`
- `Reset Profile`
- `Energy: 0`
- `Energy: Half`
- `Energy: Full`
- `Refresh`

Buttons send an enum-like `DebugActionRequestPayload`; they do not optimistically edit the client cache. Selection buttons use the normal one-time selection service and therefore reject a duplicate choice. Reset returns the profile to `UNCHOSEN/100/100/0` with a revision greater than the prior revision, allowing another real selection test. Energy controls preserve path, maximum energy, and mastery while setting a bounded current value and incrementing revision.

`DebugAccessPolicy` is a server-owned seam. During active development its initial implementation permits connected players when the debug-tools configuration is enabled. The development default is enabled so the current test loop is usable. A later release-hardening task will default it off and add operator or environment restrictions. Hiding the button is presentation only; every request is independently checked by the server.

The server logs actor UUID, action, accepted/rejected outcome, reason, and resulting revision. It does not log unrelated private profile data. Unknown payload values, unavailable players, disabled access, and malformed requests are rejected without mutation.

## Failure handling

- A duplicate command is rejected without modifying the profile or revision.
- A non-player command source receives a command failure rather than a profile write.
- Missing profile data materializes the safe default profile.
- A mismatched mod protocol is rejected during login by NeoForge's payload registrar.
- A missing client snapshot renders no spiritual-energy HUD rather than stale or fabricated values.
- A debug request never names another player and cannot mutate another profile.
- A denied or unknown debug action returns a reason-bearing result without changing state.
- The debug screen renders disconnected or missing-snapshot state explicitly and disables mutation controls.

## Verification

Add NeoForge GameTests for default profile creation, an accepted first selection, rejected repeat selection, debug reset, bounded energy presets, denied debug access, and snapshot dispatch. Tests must assert the exact path, energy, mastery, and expected revision behavior. Pure payload/action decoding tests may be ordinary unit tests when they do not require a running game.

Run a dedicated development server for a manual persistence pass: open the debug console, choose a path, change energy, die and respawn, disconnect/reconnect, restart the server, then verify the same profile and HUD state. Use Reset Profile and confirm an ordinary second selection can be tested while direct duplicate selection still rejects. Run the packaged jar in a clean 1.21.1 NeoForge instance before accepting M1. Record the jar set, commands, logs, screenshots, and results in `MODLOG.md`.

## Non-goals and follow-up

M2 owns energy consumption by gameplay, combat admission, swords, projectiles, and rewards. M3 owns mastery growth, forms, upkeep, cooldowns, and release progression. A graphical production path-selection screen can replace or supplement the command after M1's server contract is proven. Release-grade debug authorization and removal from ordinary player builds are required before public distribution, but do not block the private M1 development loop.
