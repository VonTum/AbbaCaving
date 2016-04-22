package me.lennartVH01;

public class Messages {
	public static final String noPermissionError = "§cYou don't have permission to use this command!";
	public static final String mustBeInGameError = "§cYou must be ingame to use this command!";
	public static final String gameNotFoundError = "§cGame %s not found!";
	public static final String mustSpecifyGameError = "§cYou must specify a game!";
	
	
	public static final String basicHelpMessage = "Usage:\n - §aabba join§f: Joins the Abba Match\n - §aabba leave§f: Leaves current Abba Game\n - §aabba info§f: Displays info about an Abba Match\n - §aabba list§f: Lists all Abba Matches\n - §aabba calc§f: Calculates the score of your current inventory";
	public static final String adminHelpMessage = "Usage:\n - §aabbaadmin create [game]§f: Creates an Abba Game\n - §aabbaadmin remove <game>§f: Removes an Abba Game\n - §aabbaadmin open [game]§f: Allows players to join an Abba Game\n - §aabbaadmin close [game]§f: Prevents players from joining an Abba Game\n - §aabbaadmin start [game]§f: Starts an Abba Game\n - §aabbaadmin calcscores§f: Use to calculate the scores and determine a winner at the end of a Match\n - §aabbaadmin config <game> <timer|addchest> ...§f: Configures an Abba Game\n - §aabbaadmin reload§f: Reloads the config";
	
	public static final String gameFullError = "§cThis game is full!";
	public static final String gameClosedError = "§cThis game is closed!";
	public static final String gameNoChestError = "§cThere aren't enough chests!";
	public static final String gameNotWhiteListedError = "§cYou aren't whitelisted for this game!";
	public static final String gameBlackListedError = "§cYou are blacklisted for this game!";
	
}
