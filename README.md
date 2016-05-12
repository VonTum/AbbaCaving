# AbbaCaving
Simple Abba managing plugin

## Usage
`/abba create [gameName]` to create a game at the current location of the player. 
To mark a chest to be used for the game, place a sign on the chest with `[abba]` on the first line and optionally the game name on the second line. When completed, the text on the first line should turn blue. 
Open the game with `/abba open [game]`. This allows players to join the game.
When all players are ready, use `/abba start [game]` to start the countdown. 
After the time is up and all players have deposited their ores in the chests, use `/abba tally [game]` to calculate the scores. 
Once all players have left, remove the game with `/abba remove <game>`

## Commands
Command | Description
--- | ---
/abba join | Joins an open abba game
/abba leave | leaves the current abba game
/abba list | Lists all current games
/abba info | Displays info about an abba game
/abba create | creates a new abba game
/abba remove | Removes an Abba game
/abba open | Opens a game so people can join
/abba close | Closes a game so people can't join
/abba config | Configures an abba game

## Permissions
Permission | Description | default
--- | --- | ---
AbbaCaving.basic | Allows access to basic commands such as `/abba join`
AbbaCaving.admin | Allows access to admin commands
AbbaCaving.joinFull | Allows to join full games | OP
AbbaCaving.joinClosed | Allows to join closed games | OP
