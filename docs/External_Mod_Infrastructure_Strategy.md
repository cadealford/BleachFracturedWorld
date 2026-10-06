# External Mod Infrastructure Strategy

Updated October 6, 2026.

## Purpose

Bleach: Fractured World owns Bleach gameplay rules while mature third-party projects may provide generic Minecraft infrastructure. Gameplay code depends on Fractured World contracts; adapters translate those contracts to external APIs.

The governing rule is:

> Depend when possible, extend when necessary, and fork only when control becomes strategically valuable.

This document records architectural intent and candidate technologies. It does not prove that an uninstalled project supports Minecraft 1.21.1, NeoForge 21.1.252, the project's license/distribution needs, or the combined mod stack. Those questions require a dated compatibility investigation before dependency installation or roadmap commitment.

## Ownership boundary

Fractured World owns:

- Spiritual identity, reserves, power, mastery, and progression
- Zanpakuto, Shikai, Bankai, Resurreccion, masks, and Quincy techniques
- Ability admission, costs, cooldowns, targeting, damage policy, and rewards
- Hollow and faction behavior semantics
- Senkaimon, Garganta, realm-access, and recovery rules
- Settlement meaning, protector contracts, guild politics, raids, and companions

External infrastructure may provide:

- General combat animation, stance, combo, dodge, and collision mechanisms
- Generic VFX rendering
- Portal rendering and cross-dimension presentation
- Custom entity/object animation
- AI scheduling primitives
- Settlement simulation
- Extra equipment-slot mechanisms
- Rapid modpack prototyping

An external system never becomes authoritative for BWF progression, permissions, energy, rewards, travel admission, or damage policy merely because it supplies presentation or execution infrastructure.

## Dependency categories

| Category | Policy | Typical examples |
|---|---|---|
| A — Normal dependency | Use supported APIs. Do not fork except for an extraordinary, separately approved reason. | MineColonies; candidate GeckoLib, SmartBrainLib, Curios |
| B — Extension/adapter | Hide the dependency behind a substantial BWF-owned service or adapter. | Epic Fight, Immersive Portals; candidate AAA Particles backend |
| C — Potential fork | Begin as a dependency/adapter. Evaluate a fork only after representative prototypes demonstrate repeated blocking limitations. | AAA Particles first; Immersive Portals possible; Epic Fight last resort |

Categories describe intended governance, not current installation state.

## Integration service boundaries

Gameplay subsystems consume BWF interfaces rather than third-party classes:

| BWF boundary | Responsibility | Possible backend |
|---|---|---|
| `CombatService` / `CombatAdapter` | Combat mode, timing, animation, hit observation, weapon profiles | Epic Fight |
| `VFXService` | Projectiles, beams, bursts, auras, trails, areas, transformations, environment effects | Vanilla fallback; AAA Particles candidate |
| `PortalService` / `PortalBackend` | Portal presentation and traversal callbacks for BWF-owned routes | Ordinary teleport fallback; Immersive Portals |
| `AIService` | Reusable sensing and behavior scheduling for BWF-owned brains | Vanilla AI; SmartBrainLib candidate |
| `AnimationService` | Custom entity and object animation | Vanilla models; GeckoLib candidate |
| `SettlementService` / `ColonyAdapter` | Colony lookup, permissions, metrics, patronage, and supported mutations | MineColonies |
| `EquipmentService` | Spiritual equipment slots and eligibility | Vanilla/data components; Curios candidate |

Third-party-specific classes remain in adapter packages such as `combat/epicfight`, `vfx/aaaparticles`, `portal/immersiveportals`, `npc/smartbrain`, `animation/geckolib`, `colony/minecolonies`, and `equipment/curios`. Common gameplay packages do not import those APIs directly.

## Current and candidate stack

| Project | Intended role | Current posture |
|---|---|---|
| Epic Fight | Primary player combat framework | Installed; Category B; F01.M03 integration target; do not fork initially |
| Immersive Portals | Optional portal rendering and spatial effects | Installed; Category B; ordinary teleport remains functional fallback |
| MineColonies | Human settlement simulation | Installed; Category A; do not fork |
| War 'N Taxes | Economy, war, and siege functionality where compatible | Installed; adapter and single-authority decisions required before F08 |
| AAA Particles | Reusable advanced VFX backend | Candidate, not currently installed or verified; strongest fork candidate after prototypes |
| GeckoLib | Custom entity and object animation | Candidate, not currently installed or verified; Category A |
| SmartBrainLib | Mob/NPC AI scheduling | Candidate, not currently installed or verified; Category A |
| Curios | Spiritual equipment slots | Candidate, not currently installed or verified; Category A |
| KubeJS | Temporary recipes, loot, spawn, and balance prototypes | Optional candidate; never the foundation of core gameplay |
| Player Animator | Additional player-animation system | Excluded from the required stack unless Epic Fight cannot satisfy a reviewed requirement |

No candidate becomes part of `run/mods/`, build metadata, or the required pack without explicit user approval and a compatibility/license check.

## VFX architecture and evaluation

`VFXService` exposes reusable effect families instead of one renderer per ability:

- Projectile
- Beam
- Burst
- Aura
- Trail
- Area
- Transformation
- Environmental

Ability definitions select an effect family and server-authorized parameters. The backend renders presentation only; it never decides whether an attack landed, how much damage it dealt, whether terrain changed, or whether energy was spent.

F11 should evaluate a candidate AAA Particles adapter with four representative prototypes:

| Prototype | Infrastructure exercised |
|---|---|
| Cero | Charge sphere, beam, impact |
| Getsuga Tensho | Moving slash/projectile geometry and impact |
| Quincy Arrow | Fast projectile, trail, repeated spawning |
| Spiritual Pressure | Persistent player-attached aura with scalable intensity |

The experiment asks whether the four effects share a clean reusable BWF abstraction. It is not permission to implement those complete gameplay abilities early. F01 combat authority and the relevant path features still own their damage, costs, unlocks, and lifecycle.

## Portal, animation, AI, settlement, and equipment policy

- `PortalService` owns route IDs, admission, safe destinations, recovery, and ordinary teleport fallback. Immersive Portals supplies optional rendering/spatial behavior only.
- Epic Fight owns player combat execution mechanisms. BWF combat profiles and form state determine which styles, modifiers, and actions are permitted.
- GeckoLib may animate custom entities, objects, weapons, masks, and constructs; it does not replace Epic Fight's player-combat responsibility.
- SmartBrainLib may schedule BWF-defined Hollow, Arrancar, boss, and companion behaviors. BWF owns their goals, targeting policy, factions, and rewards.
- MineColonies remains owner of settlement simulation. BWF layers prosperity, spiritual events, attacks, patronage, allegiance, and protector contracts through supported boundaries.
- Curios may expose extra slots for crosses, badges, masks, charms, seals, artifacts, or insignia. BWF owns eligibility and progression rules.
- KubeJS prototypes are disposable experiments. Proven core mechanics move into the Java mod when permanence, authority, persistence, or maintainability requires it.

## Fork decision gate

Evaluate in this order:

1. Can the supported API implement the required behavior?
2. Can a BWF addon or adapter implement it without unstable internal access?
3. Can a practical upstream contribution add the missing extension point?
4. Is the blocked behavior strategically important enough to justify owning upstream maintenance?
5. Do the license, asset, attribution, distribution, security, and migration costs remain acceptable?

Only an approved “yes” to the final two questions permits a fork investigation. Convenience, source availability, or one awkward API call is not sufficient.

Fork priority:

1. AAA Particles — strongest candidate, but only after the four-effect F11 experiment.
2. Immersive Portals — only if essential F07 spatial behavior cannot use public APIs and fallback travel is insufficient.
3. Epic Fight — last resort if essential combat behavior remains impossible after adapter and upstream options.

MineColonies, GeckoLib, SmartBrainLib, Curios, and KubeJS are not fork candidates under the current strategy. A fork must keep proprietary/restricted upstream assets out of BWF and requires a separate legal/redistribution review.

## Delivery order

Infrastructure is introduced one proven vertical slice at a time; the project does not install every candidate in advance.

1. Continue F01.M03 with the narrow Epic Fight adapter and debug combat probe.
2. Add path combat and encounter slices through F01.M04-F01.M07.
3. During F11 planning, verify AAA Particles compatibility and design `VFXService` with a vanilla fallback.
4. Prototype Cero, Quincy Arrow, Getsuga Tensho, and Spiritual Pressure presentation against that service.
5. Record API limitations, performance, combined-stack behavior, license findings, and maintenance cost.
6. Decide whether the AAA Particles adapter is sufficient; a fork is a separate approval.
7. Validate `PortalService` and the Immersive Portals backend when F07 approaches readiness.

This ordering incorporates the infrastructure strategy without displacing the approved F01 roadmap or confusing presentation prototypes with authoritative gameplay implementation.
