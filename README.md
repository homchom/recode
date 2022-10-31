# recode
![logo](logo.png)

recode is a utility-focused fabric mod for [DiamondFire](https://mcdiamondfire.com/home/) to make development more fun. It is the official successor to [CodeUtilities](https://github.com/CodeUtilities/CodeUtilities-2.0), from which it was cloned.

See the Future plans include removing bloat/unnecessary features, improving existing code, adding new features, and migrating to Kotlin.

Feel free to join the recode Discord server: https://discord.gg/GWxWtcwA2C

## Installation

### GitHub releases (recommended)

Click [here](https://github.com/homchom/recode/releases) to get to the latest release of the mod, then navigate to Assets, and there you can find the mod's .jar file.

### Building manually

If you want to have the most cutting edge recode experience, with some possibility of bugs, or if you want to contribute, you should run the following commands:

- `git clone https://github.com/homchom/recode/`
- If you're on Windows, run `gradlew.bat build`, else run `./gradlew build` (This may take a long time.)

If you encounter any errors during this process, feel free to ask for help in the Discord server.

After you're done you should find the mod's .jar file in `build/libs/` as `recode-[version number].jar`

### GitHub actions

If you don't feel like building the mod, yet still want to have all the benefits (and drawbacks), of having built the mod yourself, you should see the GitHub actions page [here](https://github.com/homchom/recode/actions).

From here you should click on the latest passing workflow, scroll to the bottom of the page and click `Artifacts`.

This should start a download for `Artifacts.zip`, which contains the mod's .jar file as `recode-[version number].jar`

## Features

DiamondFire is a Minecraft server where you can create your own minigames with code. recode seeks to improve the DF experience beyond the limitations of a server plugin. All features in the mod are toggleable to provide everyone with the modular experience they prefer most.

- Utility commands to supercharge your code, such as importing Note Block Studio files as Code Templates
- Automation of frequently used commands, such as `/resetcompact` and `/chat local`
- Discord Rich Presence support
- DiamondFire-specific keybinds
- The ability to hide messages you don't want
- Additional HUDs, such as the LagSlayer HUD to keep your action bar free
- An additional Sided Chat to partition your messages
- And much more!

## Contributing

recode is open source, and the community is welcome to add their own contributions. We only ask that you follow these guidelines:

1. Keep unrelated code additions/changes/fixes to separate pull requests, so they can be separately worked on and merged without merging unfinished code into the main branch.
2. When adding or editing Kotlin code, follow the style guide at https://kotlinlang.org/docs/coding-conventions.html unless you have a good reason not to.

recode currently uses Kotlin version 1.7.0, so you will need to use an IDE that supports it (recommended: IntelliJ with Kotlin plugin v1.7.0).