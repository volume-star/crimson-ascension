# Crimson Ascension 1.0.0

Initial playable release candidate for Minecraft 1.21.1 and NeoForge 21.1.265.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.265 or a compatible 21.1.x release
- Java 21

## Installation

1. Install a compatible NeoForge profile for Minecraft 1.21.1.
2. Place `crimson_ascension-1.0.0.jar` in the instance's `mods` directory.
3. Launch the game with the NeoForge profile.

For a dedicated server, place the same JAR in the server's `mods` directory.
Clients connecting to the server must install the same mod version.

## Included Gameplay

- Realm cultivation from Mortal Body through Blood Immortal.
- Blood qi, refined blood, corruption, breakthroughs, and realm-scaled recovery.
- Bloodletting Dagger, Blood Vial, Blood Condensation Altar, and Bone Rune Formation.
- Blood River, Withered Bone, and Ghost Womb cultivation paths with active techniques.
- Blood Moon encounters, Blood Puppets, and the Blood Fiend boss.
- Persistent random monster realms with realm-scaled attributes.
- Blood Puppet and Blood Fiend spawn eggs.
- Blood Vault interface and compact three-bar HUD.

## Known Limitations

- Blood Puppets and the Blood Fiend currently use configured vanilla Zombie visuals.
- Blood Vault cultivation slots are reserved and accept arbitrary items in this build.
- The project is licensed under the MIT License.

## Verification

The release JAR is produced with:

```powershell
.\gradlew.bat clean build --offline --no-configuration-cache
```

Automated cultivation, Codec, realm-scaling, and resource-pack checks must pass before
uploading the JAR as a GitHub Release asset.
