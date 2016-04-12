# AbbaManager
Simple Abba managing plugin

## Usage
In order to play you must first create a game using `/abba create [name]`
The game's spawn will be set where you run the command. 
Once the game is created, it must be opened for players to join using `/abba open [name]`
The game can be closed using `/abba close`
To join a game, use `/abba join [name]`, this command is enabled for all players by default.
To configure the game, use `/abba config <name> ...`
Once the players have joined, use `/abba start [name]` to start the game
To remove a game, use `/abba remove [name]`



## Features
Abba inventory calculator

## Planned Features
- Allow betting on winner of game
- iConomy/Vault/registry integration

## Commands
Command | Description | permission | default
--- | --- | --- | ---
/abba join | Joins an open abba game | AbbaCaving.join | everyone
/abba leave | leaves the current abba game | AbbaCaving.leave | everyone
/abba list | Lists all current games | AbbaCaving.list | everyone
/abba info | Displays info about an abba game | AbbaCaving.info | everyone
/abba calc | calculates current value of inventory | AbbaCaving.calc | everyone
/abba create | creates a new abba game | AbbaCaving.create | OP
/abba remove | Removes an Abba game | AbbaCaving.remove | OP
/abba open | Opens a game so people can join | AbbaCaving.open | OP
/abba close | Closes a game so people can't join | AbbaCaving.close | OP
