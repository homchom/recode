For Minecraft 1.20.2, Fabric API 0.90.7 or newer

### Additions
- Completely overhauled Syntax Highlighting, which now highlights the chat input box directly
- Syntax Highlighting now includes MiniMessage highlighting

### Changes
- Updated to Minecraft 1.20.2
- Formatting changes and fixes to Chest Preview (#60, thanks Electric131)

### Fixes
- Holding Game Values can no longer crash the game (thanks ShadowEmpress_)
- Kicks for "out-of-order chat packets" are now much less common
- Message stacking once again works for multi-line messages
- Code Search is now compatible with Sodium 0.5 (for real this time!)
- The check for DiamondFire IP addresses is now case-insensitive
- `/dfgive clipboard` now trims extraneous whitespace (#61, thanks Electric131)
- More obscure fixes to location overlay and state-related features (thanks ShadowEmpress_)

### Removals
- Removed `/schem`
- Removed `/gradient` in favor of `<gradient>`