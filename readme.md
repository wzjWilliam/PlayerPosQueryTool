# PlayerPosQueryTool
## Description
This is a simple mod that allows you to query the coordinates of the specified player by command. It supports querying multiple players at the same time.
## Supported Minecraft Versions
- 1.20.4 Fabric
## Command Usage
### Syntax
1. `/playerpos query <players>`
Query the coordinates of the specified players. You can specify multiple players separated by spaces.

2. `/playerpos config <option> [value]`
Get or set the configuration options for the mod. The available options are:
   - `broadcastToOps`: Whether to broadcast the coordinates to operators. Default is `false`.
   - `requestedOpLevel`: Whether the player must be an operator to use the command. Default is `true`.

### Examples
1. `/playerpos query Tank`
    - This command will return the coordinates of Tank.

2. `/playerpos query @a`
    - This command will return the coordinates of all players.

3. `/playerpos config broadcastToOps true`
    - This command will set the configuration option to broadcast coordinates to operators.

4. `/playerpos config requestedOpLevel`
    - This command will return the current setting for whether the player must be an operator to use the command.

