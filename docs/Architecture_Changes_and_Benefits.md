# How this architecture helps development

October 4, 2026 · Companion explanation; separate from the master design

The game vision stays intact. The main changes make its systems easier to build together, preserve between sessions, and test without requiring the entire world to exist first. These are proposed architectural changes, not gameplay already implemented.

| Change | Practical benefit | Example |
|---|---|---|
| One mod with small subsystem boundaries | Keeps the build simple while reducing accidental coupling | A Cero handler does not need to know MineColonies' internal classes |
| One shared ability admission and lifecycle | Every new move inherits costs, cooldowns, interrupts and cleanup | Fixing an energy-check bug fixes every ability using the gate |
| Separate power, reserves and mastery | Preserves distinct builds and meaningful teamwork | A high-output captain can still lose an endurance fight |
| Separate durable records from active effects | Makes death, reconnect and restart behavior deliberate | Bankai ends on logout while its major cooldown remains |
| One owner for guilds, war outcomes and money | Prevents conflicting politics and duplicate payments | A captured outpost is resolved once, even when two mods observe it |
| One restoration owner per operation | Prevents competing resets and item duplication | A robbed depot cannot be restored with its stolen supplies still inside |
| Bounded destruction service | Lets impressive attacks grow without unlimited server work | A large slash queues permitted edits instead of freezing the server |
| Mod-owned routes with optional portal rendering | Keeps realm gameplay usable while visual compatibility is tested | Senkaimon can teleport normally before seamless views are ready |
| Owner-specific Inner World cells | Makes shared themes truly personal | Two players with the same sword family cannot collide during trials |
| Capture leases instead of copied abilities | Gives Bankai theft a clear expiry and recovery path | Copying a medallion creates no second valid stolen release |
| Concrete milestone gates | Gets us back to playable work and replaces speculative schedules | First prove profile persistence, then add one projectile and Hollow |
| Architecture baseline plus a development log | Carries decisions and verified discoveries across chats and computers | The next session starts with the actual last result rather than reconstructing it |

## What Universal Modder adds

The ten skills are present under `C:/Users/squid/.agents/skills/`. They give us a repeatable process: inspect the real APIs, consult existing field notes, implement one slice, verify it in the running game, and record what worked or failed. The earlier installation attempt in this chat failed; the current filesystem and skill catalog now show their presence. This does not establish how they were installed or prove the separate `um` tool and asset services are configured.

For this project, the most immediate gains are Minecraft recon, disciplined reverse engineering, and repeatable in-game verification. The asset skills become useful after placeholder combat works. Their value is fewer repeated investigations and better evidence, not a guarantee that integration becomes automatic.

## What changes in our next working session

We start from the renamed NeoForge starter already on disk. We verify dedicated-server operation and select a minimal combat dependency stack before expanding content. Then we build one saved spiritual profile, one server-approved technique, and one Hollow encounter. That establishes the foundation reused by Shikai, Bankai, Quincy forms, and later Arrancar abilities.

The largest avoided mistake is implementing each spectacular feature with its own rules and discovering later that they disagree about death, damage, protection or ownership. Shared rules give us room for ambitious content without making every new release a fresh infrastructure project.

Risk note: dependency adapters and recovery systems still require real implementation and tests. The document reduces uncertainty; it does not prove compatibility or performance.

Next three steps:

1. Review the proposed defaults and retain the gameplay choices you want.
2. Use the architecture and a development log in the actual repository when implementation resumes.
3. Complete the baseline/profile/combat slice before broad world or content expansion.
