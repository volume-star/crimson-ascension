# Crimson Ascension Balance Baseline

## Core Economy

| Value | Default |
|---|---:|
| Maximum blood qi | 100 per realm, from 100 to 700 |
| Blood vial capacity | 100 |
| Blood qi per dagger kill | 1-100, based on target maximum health |
| Blood qi per refined blood | 10 |
| Corruption per refined blood | 1 |
| Refined blood consumed on breakthrough | `(realm + 1) × 5` |
| Maximum-health bonus per realm | 4 |
| Passive blood recovery interval | 40 ticks (2 seconds) |

A normal 20-health hostile mob supplies 20 blood qi, enough for two refined blood and two
corruption. Reaching the first realm requires 100 blood, produces ten refined blood,
and consumes five. Remaining refined blood is not reset.

## Realm Blood Recovery

| Realm | Blood qi per pulse | Healing cost per health |
|---|---:|---:|
| Mortal Body | 0 | — |
| Blood Drawing | 0 | — |
| Blood Condensation | 1 | — |
| Bone Refinement | 2 | — |
| Blood Core | 3 | 8 |
| Yin Spirit | 4 | 10 |
| Blood Immortal | 5 | 12 |

Recovery is applied once every 40 ticks while missing blood qi. From Blood Core onward,
one missing health is restored per pulse when enough blood qi is available. Full health
never consumes blood qi for passive healing.

## Monster Realms

Every newly spawned hostile monster independently rolls a realm from Mortal Body through
Blood Immortal. The roll does not depend on player realm. Once assigned, the realm is
persistent and changes only through the monster realm command. The Blood Fiend rolls
from Bone Refinement through Blood Immortal (`3-6`), and command values below three are
clamped to Bone Refinement.

| Realm | Health | Attack | Speed | Armor |
|---|---:|---:|---:|---:|
| Mortal Body | ×1.00 | ×1.00 | ×1.00 | +0 |
| Blood Drawing | ×1.45 | ×1.25 | ×1.03 | +2 |
| Blood Condensation | ×2.00 | ×1.55 | ×1.06 | +4 |
| Bone Refinement | ×2.65 | ×1.90 | ×1.09 | +6 |
| Blood Core | ×3.40 | ×2.30 | ×1.12 | +9 |
| Yin Spirit | ×4.30 | ×2.75 | ×1.15 | +12 |
| Blood Immortal | ×5.40 | ×3.30 | ×1.18 | +16 |

Multipliers apply only once to each monster's captured base attributes. Blood Puppets
start at 30 health and 5 attack damage; the Blood Fiend starts at its configured health
and 8 attack damage before realm scaling.

## HUD Resource Separation

- The HUD contains compact red, blue, and yellow bars.
- Red shows current health, blue shows blood qi, and yellow shows refined blood.
- Red and blue bars show current and maximum values inside the bars.
- The yellow bar contains no numeric text and shows refined blood toward the current
  breakthrough requirement. Exact refined blood and corruption are shown only in the
  altar interface.

## Techniques

| Slot | Blood cost | Cooldown |
|---|---:|---:|
| Primary | 30 | 8 seconds |
| Secondary | 60 | 18 seconds |

Technique-specific balance:

- Bone Armor: 7 seconds of Resistance II and Absorption II.
- Ghost Veil: 6 seconds of invisibility and Speed II.
- Raised Blood Puppet: 30-second lifetime, 30 health, 5 attack damage.
- Blood Burst: 6 damage in a 6-block radius.
- Soul Snare: Weakness II and Slowness II for 6 seconds.

## World Events

| Value | Default |
|---|---:|
| Blood Moon chance per night | 25% |
| Blood Puppets per Blood Moon | 3 |
| Blood Fiend health | 120 |
| Bone Rune Formation blood cost | 50 |
| Summoned puppet lifetime | 600 ticks |

## Corruption Feedback

- 300 corruption: heartbeat begins.
- 500 corruption: screen-edge blood haze becomes visible.
- Ghost Womb passively removes one corruption every 10 seconds.

All tunable values above that are exposed in `crimson_ascension-common.toml` should be
changed there rather than in source code.
