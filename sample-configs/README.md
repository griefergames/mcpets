# MCPets – Sample Configs

Vorlagen für `plugins/MCPets/`. Beim ersten Start legt das Plugin fehlende Dateien auch selbst an; diese Samples sind kommentiert und für den GG-Betrieb vorbereitet.

## Dateien

| Sample | Zielpfad |
|---|---|
| `config.yml` | `plugins/MCPets/config.yml` |
| `blacklist.yml` | `plugins/MCPets/blacklist.yml` |
| `petfoods.yml` | `plugins/MCPets/petfoods.yml` |
| `Pets/example_pet.yml` | `plugins/MCPets/Pets/example_pet.yml` |
| `Categories/example.yml` | `plugins/MCPets/Categories/example.yml` |

Nicht enthalten (werden zur Laufzeit generiert):

- `language.yml` – alle Language-Keys mit Defaults
- `menuIcons.yml` – serialisierte Bukkit-ItemStacks für Menü-Icons

## GG-Hinweise

- **Velocity:** `Velocity.Enabled: false` lassen. Cross-Server Active-Pets laufen über ClusterConnect (Hazelcast), nicht über `mcpets-velocity`.
- **MySQL:** nur nötig, wenn ihr Player-Data (Stats/Inventar/Namen) zentral speichern wollt. Active-Pet-Handoff braucht kein MySQL.
- **PlotSquared-Flags** (nach Deploy von diesem Build):
  - `/plot flag set pets false` – Pets auf dem Plot verboten
  - `/plot flag set pet-damage false` – Pets nicht von Spielern schädigbar  
  Defaults: beide `true`. Außerhalb von Plots: erlaubt.

## MythicMobs

`MythicMob` in der Pet-YAML muss zu einer echten MythicMobs-Mob-ID passen (z. B. euer ModelEngine-Mob).
