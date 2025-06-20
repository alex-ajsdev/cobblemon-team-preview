# Cobblemon Team Preview

A server-side Fabric mod for Minecraft **1.21.1** that adds a customizable **Team Preview** UI before Cobblemon battles begin. Inspired by competitive formats like Pokémon Showdown, this mod enhances PvP by letting players view their opponent’s team and choose the order of their Pokémon before a battle begins.

## ✨ Features

- Interactive GUI where players order their Pokémon before battle
- Applies chosen team order at the start of the battle
- Timeout and cancel options for inactive players
- Players can opt-out via `/teampreview toggle`
- Server-wide toggle with `/teampreview toggle global`
- Fully configurable timeout duration
- Seamless integration with the **Cobblemon battle flow**

## 📦 Requirements

- [Fabric Loader 0.116.0+1.21.1](https://fabricmc.net/)
- Minecraft **1.21.1**
- [Fabric API 0.116.0+1.21.1](https://modrinth.com/mod/fabric-api)
- [Cobblemon 1.6.1](https://cobblemon.mod.io/)
- [SGUI 1.6.1+1.21.1](https://github.com/Patbox/sgui/releases/tag/1.6.1%2B1.21.1) (for GUI rendering)

## 🔧 Configuration

A config file is generated at `config/teampreview.json` on first run.
- `timeout`: Duration in **seconds** before the GUI automatically cancels if the player doesn't respond.

## 🔨 Commands

| Command                        | Description                                   | Permission |
|-------------------------------|-----------------------------------------------|------------|
| `/teampreview toggle`         | Enables/disables team preview for the player  | All        |
| `/teampreview toggle global`  | Toggles team preview for everyone (globally)  | OP (level 4) |

## 🚀 Installation

1. Download the latest `.jar` from [Releases](https://github.com/yourname/cobblemon-team-preview/releases)
2. Place it in your `mods/` folder alongside:
    - `Cobblemon`
    - `Fabric API`
    - `SGUI 1.6.1+1.21.1`
3. Start the server

## 🧠 How It Works

When a battle is initiated:
1. The mod intercepts the default start flow.
2. Each player is shown a GUI to select their Pokémon order.
3. The battle starts once both players confirm.
4. The team order is applied, influencing which Pokémon lead.

## 🛠️ Development

This mod is open-source! Contributions are welcome.

```bash
git clone https://github.com/ajsdev/cobblemon-team-preview.git
cd cobblemon-team-preview
./gradlew build
```

## 📝 License

This project is licensed under the [MIT License](LICENSE).

## ❤️ Credits

- Built on the Cobblemon API
- GUI powered by [SGUI](https://github.com/Patbox/sgui)
- Thanks to the Cobblemon devs for enabling competitive Pokémon in Minecraft
