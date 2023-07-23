For Minecraft 1.20.1 with Fabric API

### Additions
- Added better unicode by Electrosolt (which retextures many unicode symbols in the vanilla style) as a built-in resource pack
- Re-added support session state detection and related features

### Changes
- Updated to Minecraft 1.20.1
- Request timeouts (which should not happen to begin with) are now logged with a toast notification; there are no longer uncaught exceptions with them
- Updated the DiamondFire logo texture (thanks Baconiumo)

### Fixes
- Fixed all currently known causes of request timeouts; so far they have not occured in testing :)
- Fixed `/profile` message detection (again)
- Fixed auto `/lagslayer`
- Other minor state fixes