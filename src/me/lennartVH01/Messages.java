package me.lennartVH01;

import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
	/*public static String errorNoPermission = "§cYou don't have permission to use this command!";
	public static String errorMustBeInGame = "§cYou must be ingame to use this command!";
	public static String errorGameNotFound = "§cGame %s not found!";
	*/
	//public static String basicHelpMessage = "Usage:\n - §aabba join§f: Joins the Abba Match\n - §aabba leave§f: Leaves current Abba Game\n - §aabba info§f: Displays info about an Abba Match\n - §aabba list§f: Lists all Abba Matches\n - §aabba calc§f: Calculates the score of your current inventory";
	//public static String adminHelpMessage = "Usage:\n - §aabbaadmin create [game]§f: Creates an Abba Game\n - §aabbaadmin remove <game>§f: Removes an Abba Game\n - §aabbaadmin open [game]§f: Allows players to join an Abba Game\n - §aabbaadmin close [game]§f: Prevents players from joining an Abba Game\n - §aabbaadmin start [game]§f: Starts an Abba Game\n - §aabbaadmin calcscores§f: Use to calculate the scores and determine a winner at the end of a Match\n - §aabbaadmin config <game> <timer|addchest> ...§f: Configures an Abba Game\n - §aabbaadmin reload§f: Reloads the config";
	/*
	public static String errorGameFull = "§cThis game is full!";
	public static String errorGameClosed = "§cThis game is closed!";
	public static String errorNoChest = "§cThere aren't enough chests!";
	public static String errorNotWhitelisted = "§cYou aren't whitelisted for this game!";
	public static String errorBlacklisted = "§cYou are blacklisted for this game!";
	*/
	public static String gameJoinMessage;
	public static String gameLeaveMessage;
	public static String playerJoinMessage;
	public static String playerLeaveMessage;
	
	public static String errorNoGames;
	public static String errorNoPermission;
	public static String errorMustBeInGame;
	public static String errorGameNotFound;
	
	public static String errorGameFull;
	public static String errorGameClosed;
	public static String errorNoChest;
	public static String errorNotWhitelisted;
	public static String errorBlacklisted;
	
	public static String helpJoin;
	public static String helpLeave;
	public static String helpInfo;
	public static String helpList;
	public static String helpCalc;
	
	//admin cmds
	public static String helpCreate;
	public static String helpRemove;
	public static String helpOpen;
	public static String helpClose;
	public static String helpStart;
	public static String helpCalcScores;
	public static String helpReload;
	public static String helpConfig;
	
	public static void reload(FileConfiguration langFile){
		gameJoinMessage = langFile.getString("msg.gameJoin");
		gameLeaveMessage = langFile.getString("msg.gameLeave");
		playerJoinMessage = langFile.getString("msg.playerJoin");
		playerLeaveMessage = langFile.getString("msg.playerLeave");
		
		
		errorNoPermission = langFile.getString("error.noPermission");
		errorMustBeInGame = langFile.getString("error.mustBeInGame");
		errorGameNotFound = langFile.getString("error.mustBeInGame");
		errorNoGames = langFile.getString("error.noGames");
		
		errorGameFull = langFile.getString("error.gameFull");
		errorGameClosed = langFile.getString("error.gameClosed");
		errorNoChest = langFile.getString("error.noChest");
		errorNotWhitelisted = langFile.getString("error.notWhitelisted");
		errorBlacklisted = langFile.getString("error.blacklisted");
		
		helpJoin = langFile.getString("help.join");
		helpLeave = langFile.getString("help.leave");
		helpInfo = langFile.getString("help.info");
		helpList = langFile.getString("help.list");
		helpCalc = langFile.getString("help.calc");
		
		helpCreate = langFile.getString("help.create");
		helpRemove = langFile.getString("help.remove");
		helpOpen = langFile.getString("help.open");
		helpClose = langFile.getString("help.close");
		helpStart = langFile.getString("help.start");
		helpCalcScores = langFile.getString("help.calcscores");
		helpReload = langFile.getString("help.reload");
		helpConfig = langFile.getString("help.config");
		
		//TODO
	}
}
