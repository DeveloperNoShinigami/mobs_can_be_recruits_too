# Mob recruit GUI issue

## Summary
The recruit inventory GUI failed to open for mobs converted with UniversalMobControl. The client attempted to locate the mob using `ModScreens.getControlledMobByUUID`, which required the mob's `RecruitControlled` NBT flag to be present. This flag is written only on the server, so the client could not find the mob and the screen creation returned `null`.

## Resolution
`ModScreens.getControlledMobByUUID` now matches mobs purely by UUID and no longer checks the unsynchronized `RecruitControlled` flag. The server still validates that a mob is controlled before opening its inventory, allowing the GUI to load correctly.

## Preventing similar bugs
When adding data that must be read on both client and server, use `SynchedEntityData` or send a dedicated network packet so the information is synchronized. Avoid relying on persistent NBT values for client-side logic unless those values are explicitly mirrored to the client.
