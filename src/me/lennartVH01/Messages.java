package me.lennartVH01;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.configuration.file.FileConfiguration;

public class Messages{
	private Messages(){}
	
	public static String playerJoin;
	public static String playerLeave;

	public static String gameStart;
	public static String gameCountdown;
	public static String gamePause;
	public static String gameEnded;
	
	public static String errorNoGames;
	public static String errorNoPermission;
	public static String errorNoPendingOperation;
	public static String errorMustBeInGame;
	public static String errorGameNotFound;
	public static String errorGameNotFinished;
	public static String errorPlayerNotFound;
	public static String errorWorldNotFound;
	
	public static String errorGameFull;
	public static String errorGameClosed;
	public static String errorNotWhitelisted;
	public static String errorBlacklisted;
	public static String errorContraband;
	
	public static String errorBreakClaimedChest;
	public static String chestCreateSuccess;
	public static String chestRemoveSuccess;
	
	public static String helpAbba;
	
	public static String helpJoin;
	public static String helpLeave;
	public static String helpInfo;
	public static String helpList;
	public static String helpCalc;
	
	public static String helpCreate;
	public static String helpRemove;
	public static String helpOpen;
	public static String helpClose;
	public static String helpStart;
	public static String helpCalcScores;
	public static String helpReload;
	public static String helpConfig;
	
	public static String commandJoinSuccess;
	public static String commandLeaveSuccess;
	
	public static String commandCreateSuccess;
	public static String commandCreateErrorExists;
	
	public static String commandRemoveSuccess;
	public static String commandRemoveConfirm;
	
	public static String commandOpenSuccess;
	public static String commandCloseSuccess;
	
	public static String commandStartErrorRunning;
	public static String commandStartErrorFinished;
	public static String commandStartErrorContraband;
	
	public static String commandPauseSuccess;
	public static String commandPauseErrorGameNotRunning;
	
	public static String commandConfigHelp;
	public static String commandConfigTimerHelp;
	public static String commandConfigTimerSuccess;
	public static String commandConfigPlayercapHelp;
	public static String commandConfigPlayercapSuccess;
	
	
	
	private static FileConfiguration langFile;
	public static void reload(FileConfiguration langFile){
		Messages.langFile = langFile;
		
		playerJoin = load("msg.playerJoin");
		playerLeave = load("msg.playerLeave");
		
		gameCountdown = load("msg.gameCountdown");
		gameStart = load("msg.gameStart");
		gamePause = load("msg.gamePause");
		gameEnded = load("msg.gameEnd");
		
		errorNoGames = load("error.noGames");
		errorNoPermission = load("error.noPermission");
		errorNoPendingOperation = load("error.noPendingOperation");
		errorMustBeInGame = load("error.mustBeInGame");
		errorGameNotFound = load("error.gameNotFound");
		errorPlayerNotFound = load("error.playerNotFound");
		errorWorldNotFound = load("error.worldNotFound");
		
		errorGameFull = load("error.gameFull");
		errorGameClosed = load("error.gameClosed");
		errorNotWhitelisted = load("error.notWhitelisted");
		errorBlacklisted = load("error.blacklisted");
		errorContraband = load("error.contraband");
		
		errorBreakClaimedChest = load("error.breakClaimedChest");
		chestCreateSuccess = load("chest.create");
		chestRemoveSuccess = load("chest.remove");
		
		helpAbba = load("help.abba");
		
		helpJoin = load("help.join");
		helpLeave = load("help.leave");
		helpInfo = load("help.info");
		helpList = load("help.list");
		helpCalc = load("help.calc");
		
		helpCreate = load("help.create");
		helpRemove = load("help.remove");
		helpOpen = load("help.open");
		helpClose = load("help.close");
		helpStart = load("help.start");
		helpCalcScores = load("help.calcscores");
		helpReload = load("help.reload");
		helpConfig = load("help.config");
		
		commandJoinSuccess = load("command.join.success");
		commandLeaveSuccess = load("command.leave.success");
		
		commandCreateSuccess = load("command.create.success");
		commandCreateErrorExists = load("command.create.error");
		
		commandRemoveSuccess = load("command.remove.success");
		commandRemoveConfirm = load("command.remove.confirm");
		
		commandOpenSuccess = load("command.open.success");
		commandCloseSuccess = load("command.close.success");
		
		commandStartErrorRunning = load("command.start.error.running");
		commandStartErrorFinished = load("command.start.error.finished");
		commandStartErrorContraband = load("command.start.error.contraband");
		
		commandPauseSuccess = load("command.pause.success");
		commandPauseErrorGameNotRunning = load("command.pause.error");
		
		
		commandConfigHelp = load("command.config.help");
		commandConfigTimerHelp = load("command.config.command.timer.help");
		commandConfigTimerSuccess = load("command.config.command.timer.success");
		commandConfigPlayercapHelp = load("command.config.command.playercap.help");
		commandConfigPlayercapSuccess = load("command.config.command.playercap.success");
		
	}
	private static String load(String path){
		String input = langFile.getString(path);
		if(input == null || input.isEmpty()){
			System.out.println("ERROR COULDN'T FIND " + path);
			return "";
		}
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}
