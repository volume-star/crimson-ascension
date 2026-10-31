# Crimson Ascension QA Checklist

## Automated

```powershell
.\gradlew.bat clean build
```

- Java compilation succeeds.
- Cultivation state Codec and network Codec checks pass.
- The final JAR contains the custom logo and texture assets.

## Core

- Dagger deals 7 base damage and applies Bleeding.
- Bleeding damage can kill and still credit the player.
- Vial capacity is 100 and cannot duplicate blood.
- Blood Condensation Altar exchanges 10 body blood qi for 1 refined blood.
- The altar UI allows selecting the exact refined blood amount.
- The altar displays current blood qi, refined blood, and required breakthrough refined blood.
- Blood Puppet and Blood Fiend spawn eggs appear in the Crimson Ascension creative tab.
- Using each spawn egg creates the correct named mob with a randomly rolled valid realm.
- Blood Fiend spawn eggs always produce a realm from `3` to `6`.
- Spawn eggs are consumed outside creative mode and retained in creative mode.
- Blood qi capacity is 100 at Mortal Body and increases by 100 per realm.
- Breakthrough consumes `5 × (realm + 1)` refined blood.
- Breakthrough succeeds based only on refined blood.
- A failed breakthrough does not consume resources.
- Remaining refined blood is kept after a successful breakthrough.
- Spending blood qi never changes the refined blood maximum.
- Mortal Body and Blood Drawing do not passively regenerate blood qi.
- Blood Condensation and Bone Refinement regenerate blood qi without passive healing.
- Blood Core through Blood Immortal consume blood qi to restore missing health.
- Passive healing never consumes blood qi while health is full.
- Maximum health increases by the configured amount per realm.
- Cultivation state and vault slots survive death and world reload.

## Monster Realms

- Every newly spawned hostile monster receives a realm from `0` to `6`.
- Realm assignment is random and independent of player realm.
- Realm names are visible after the monster's base name.
- Higher realms have higher health, attack damage, movement speed, and armor.
- Reloading a chunk does not repeatedly multiply attributes.
- Reloading a chunk or changing player realm does not reroll an existing monster's realm.
- `/crimson monster realm get @e[type=zombie,limit=1]` reports the selected monster's realm.
- `/crimson monster realm set 4 @e[type=zombie,limit=1]` changes its realm and stats immediately.
- Blood Puppets retain their dagger and custom base stats after realm scaling.
- The Blood Fiend rolls only realms `3-6`.
- A Blood Fiend below realm three is raised into `3-6` when loaded.
- Setting a Blood Fiend below realm three through the command clamps it to realm three.

## UI

- `H` opens Cultivation Status and path buttons are visible.
- `V` opens Blood Vault and slots persist after reopening.
- `R` and `Shift+R` cast techniques with configured costs and cooldowns.
- HUD remains readable at GUI scales 2 through 4.
- HUD contains compact red health, blue blood qi, and yellow refined-blood bars.
- HUD uses substantially less vertical space than the original three-row display.
- The player's current realm name appears immediately left of the red health bar.
- Red and blue bars display current/maximum values inside the bars.
- The yellow bar contains no numeric text; refined blood remains visible only in the altar.

## Paths

- Blood Step does not pass through solid walls.
- Blood Burst damages mobs but not other players.
- Bone Armor applies resistance and absorption.
- Bone Armor lasts about 7 seconds.
- Raised Blood Puppets are hostile and carry a dagger.
- Summoned Blood Puppets disappear after about 30 seconds.
- Ghost Veil applies invisibility and speed.
- Ghost Veil lasts about 6 seconds.
- Soul Snare does not affect other players.
- All three passives trigger at their documented rates.

## World Events

- Bone Rune Formation rejects vials below the configured blood cost.
- Blood Moon starts and ends once per night transition.
- Blood Moon spawn cap and chance respect server configuration.
- Blood Fiend creates a boss bar and removes it on death.
- Blood Moon packets and atmosphere effects stop after dawn.

## Multiplayer

- Player cultivation data remains private.
- Vault inventory data remains private.
- Boss bars and Blood Moon messages are visible to all players in the Overworld.
- Reconnecting does not duplicate items or reset selected paths.

## Release

- Dedicated server starts to `Done`.
- Client loads without missing model or texture errors.
- `crimson_ascension-1.0.0.jar` is generated under `build/libs`.
- Test world backups are retained separately from release builds.
