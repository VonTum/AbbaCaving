# AbbaCaving
Simple Abba managing plugin

This is a plugin designed to make it easier to create and manage the popular gametype *AbbaCaving*. This gametype consists of sending the players down a cave with Silk Touch picks. Each ore is assigned a certain number of points. The player who gathers the most points gets all the ores of all the players combined. 


## Features
- Quickly create Abba Games
- Players can join these games and will get teleported there
- If a player dies they can quickly rejoin the game
- At the end of the game, all players are teleported back and their scores are counted

## Usage
- In order to play you must first create a game using `/abba create [name]`
- The game's spawn will be set where you run the command. 
- Once the game is created, it must be opened for players to join using `/abba open [name]`
- The game can be closed using `/abba close`
- To join a game, use `/abba join [name]`, this command is enabled for all players by default.
- To configure the game, use `/abba config <name> ...`
- Once the players have joined, use `/abba start [name]` to start the game
- To remove a game, use `/abba remove [name]`

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
