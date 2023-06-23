For Minecraft 1.19.4, Fabric API 0.78.0 or newer

### Changes
- Updated to Minecraft 1.19.4
- Automation commands no longer take multiple seconds to take effect
- Improved block entity performance (including in codespaces)
- The LagSlayer overlay now has a shadow (#33)
- Improvements to Side Chat
- The mod is now more lenient with Cloth Config compatibility
- Removed Partner Bracket and Quick Var Scope (as they were added to DF in the new patch)
- The mod now raises a warning when used with Optifabric (Optifine is not supported)

### Fixes
- The copy button in /colors no longer freezes the screen
- `/locate` messages should no longer appear in chat unexpectedly on occasion
- Messages (including chat messages and especially `/locate`-ing oneself) can no longer be hidden when they shouldn't be
- The "Recode" button in the Options menu is now compatible with most mods

### Known issues
- Auto `/chat local` and similar automation commands run in support sessions (this will be fixed very soon! but for now you can disable the setting or just live with the bug)
- Newer code actions still don't have lore overlays (the action dump is outdated)
- The current Options menu can clip the edge of the screen on large GUI scales

### Technical
- Updated to Kotlin 1.8.0
- `/locate` is no longer sent by the client every 5 seconds
- A million other internal changes/improvements (unsure about something you see? ask!)