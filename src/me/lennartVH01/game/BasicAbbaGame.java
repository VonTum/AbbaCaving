package me.lennartVH01.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.lennartVH01.Config;
import me.lennartVH01.Messages;
import me.lennartVH01.Permission;
import me.lennartVH01.util.ChatUtil;
import net.minecraft.server.v1_9_R1.ChatComponentText;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class BasicAbbaGame implements AbbaGame{
	private static final String TIMER_SCORE_NAME = "Timer";
	
	
	
	
	private final Clock clock;
	
	private GameState state = GameState.WAITING;
	private boolean open = false;
	
	private int playerCap;
	private int timeLeft;
	private int duration;
	private String name;
	private Location spawn;
	
	
	private ContrabandScanner scanner;
	private ItemStack[] itemValues;
	
	private List<Player> players;
	
	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective objective = scoreboard.registerNewObjective("AbbaCaving", "dummy");
	private Score timerScore = objective.getScore(TIMER_SCORE_NAME);
	
	
	public BasicAbbaGame(JavaPlugin plugin, String name, Location spawn, int duration, int playerCap, ContrabandScanner scanner, ItemStack[] itemValues){
		clock = new Clock(plugin);
		this.name = name;
		this.spawn = spawn;
		this.duration = duration;
		this.playerCap = playerCap;
		this.scanner = scanner;
		this.itemValues = itemValues;
		if(playerCap == -1){
			players = new ArrayList<Player>();
		}else{
			players = new ArrayList<Player>(playerCap);
		}
	}
	@Override public void destroy(){
		clock.turnOff();
		leaveAll();
	}
	
	//Clock code
	private void clockTickCountdown(){
		if(timeLeft <= 0){
			timeLeft = duration;
			startGame();
		}else{
			broadcast("§c" + timeLeft);
			timeLeft--;
		}
	}
	private void clockTickRunning(){
		if(timeLeft <= 0)
			endGame();
		else{
			timerScore.setScore(timeLeft);
			timeLeft--;
		}
	}
	private void startGame(){
		clock.turnOff();
		
		objective.setDisplayName("Abba Caving");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		broadcast(Messages.gameStart);
		
		
		
		state = GameState.RUNNING;
		clock.turnOn(0, 20, this::clockTickRunning);
	}
	private void endGame(){
		clock.turnOff();
		tpAll(spawn);
		broadcast(Messages.gameEnded);
		state = GameState.FINISHED;
		
		scoreboard.resetScores(TIMER_SCORE_NAME);
		objective.setDisplayName("Scores");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		List<AbbaScore> scores = tally();
		
		int[] totalItems = new int[itemValues.length];
		
		for(AbbaScore score:scores){
			for(int i = 0; i < itemValues.length; i++){
				totalItems[i] += score.count[i];
			}
		}
		StringBuilder msgBuilder = new StringBuilder();
		for(int i = 0; i < itemValues.length; i++){
			msgBuilder.append(ChatUtil.getName(itemValues[i]) + ": " + totalItems[i]);
		}
		
		
		
		for(int i = 0; i < scores.size(); i++){
			AbbaScore score = scores.get(i);
			objective.getScore(score.player.getName()).setScore(score.total);
			
		}
	}
	
	
	
	@Override
	public boolean join(final Player p){
		if(!Permission.JOIN_CLOSED.has(p) && !open){
			p.sendMessage(Messages.errorGameClosed);
			return false;
		}
		if(playerCap >= 0 && !Permission.JOIN_FULL.has(p) && players.size() >= playerCap){
			p.sendMessage(Messages.errorGameFull);
			return false;
		}
		if(Config.scanContraband){
			List<ItemStack> illegalItems = scanner.getContraband(p.getInventory());
			if(!illegalItems.isEmpty()){
				p.sendMessage(Messages.errorContraband);
				for(ItemStack stack:illegalItems){
					IChatBaseComponent chatStack = ChatUtil.stackToChat(stack);
					IChatBaseComponent chatText = new ChatComponentText("§c - ");
					chatText.addSibling(chatStack);
					
					ChatUtil.send(p, chatText);
					
				}
				return false;
			}
		}
		
		
		//SUCCESS
		
		broadcast(String.format(Messages.playerJoin, p.getName()));
		p.sendMessage(String.format(Messages.commandJoinSuccess, this.getName()));
		
		players.add(p);
		p.setScoreboard(scoreboard);
		
		p.teleport(spawn);
		p.setGameMode(GameMode.SURVIVAL);
		
		return true;
	}
	
	@Override public void leave(final Player p){
		players.remove(p);
		playerLeaveCleanup(p);
	}
	@Override public void leaveAll(){
		for(Iterator<Player> iter = players.iterator(); iter.hasNext(); ){
			Player p = iter.next();
			playerLeaveCleanup(p);
			iter.remove();
		}
	}
	private void playerLeaveCleanup(Player p){
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		
		broadcast(String.format(Messages.playerLeave, p.getName()));
		p.sendMessage(String.format(Messages.commandLeaveSuccess, this.getName()));
	}
	
	
	
	
	//TODO SERIALIZATION
	@Override
	public Map<String, Object> serialize(){
		return null;
		
	}
	public static AbbaGame deserialize(Map<String, Object> inputMap){
		return null;
	}
	
	
	//SETTERS GETTERS
	@Override public boolean start(final CommandSender sender){
		switch(state){
		case WAITING:
			boolean canStart = true;
			if(sender instanceof Player){
				StringBuilder messageBuilder = new StringBuilder("[");
				for(Player p:players){
					List<ItemStack> contraband = scanner.getContraband(p.getInventory());
					if(!contraband.isEmpty()){
						if(canStart){
							canStart = false;
							sender.sendMessage(Messages.commandStartErrorContraband);
						}
						messageBuilder.append("{\"text\":\"" + p.getName() + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[");
						for(int i = 0; i < contraband.size(); i++){
							messageBuilder.append("{\"translate\":\"" + ChatUtil.getName(contraband.get(i)) + "\"},\"\\n\",");
							
						}
						messageBuilder.setCharAt(messageBuilder.length() - 1, ']');
						messageBuilder.append("}},");
					}
				}
				if(!canStart){
					messageBuilder.delete(messageBuilder.length() - 9, messageBuilder.length() - 4);
					messageBuilder.setCharAt(messageBuilder.length() - 1, ']');
					System.out.println(messageBuilder.toString());
					ChatUtil.send((Player) sender, ChatUtil.fromRawJSON(messageBuilder.toString()));
				}
			}else{
				for(Player p:players){
					if(scanner.hasContraband(p.getInventory())){
						if(canStart){
							canStart = false;
							sender.sendMessage(Messages.commandStartErrorContraband);
						}
						sender.sendMessage(p.getName());
					}
				}
			}
			if(canStart){
				broadcast(Messages.gameCountdown);
				state = GameState.COUNTDOWN;
				timeLeft = 5;
				clock.turnOn(20, 20, this::clockTickCountdown);
				return true;
			}
			return false;
		case PAUSED:
			startGame();
			return true;
		case COUNTDOWN:
		case RUNNING:
			sender.sendMessage(Messages.commandStartErrorRunning);
			return false;
		case FINISHED:
		case CONCLUDED:
			sender.sendMessage(Messages.commandStartErrorFinished);
			return false;
		default:
			return false;
		}
	}
	@Override public boolean pause(final CommandSender sender){
		clock.turnOff();
		switch(state){
		case COUNTDOWN:
			timeLeft = duration;
			state = GameState.WAITING;
			for(Player p:players){
				p.sendMessage(Messages.gamePause);
			}
			sender.sendMessage(String.format(Messages.commandPauseSuccess, name));
			return true;
		case RUNNING:
			sender.sendMessage(String.format(Messages.commandPauseSuccess, name));
			state = GameState.PAUSED;
			broadcast(Messages.gamePause);
			return true;
		default:
			sender.sendMessage(Messages.commandPauseErrorGameNotRunning);
			return false;
		}
	}
	
	private List<AbbaScore> tally(){
		List<AbbaScore> scores = new ArrayList<AbbaScore>(players.size());
		int[] totalItems = new int[itemValues.length];
		for(Player p:players){
			AbbaScore score = new AbbaScore(p, itemValues.length);
			
			
			for(Iterator<ItemStack> iter = p.getInventory().iterator(); iter.hasNext();){
				ItemStack stack = iter.next();
				for(int i = 0; i < itemValues.length; i++){
					if(itemValues[i].isSimilar(stack)){
						score.count[i] += stack.getAmount();
						break;
					}
				}
				scores.add(score);
			}
			
			for(int i = 0; i < itemValues.length; i++){
				score.total += itemValues[i].getAmount() * score.count[i];
				totalItems[i] += score.count[i];
			}
		}
		
		scores.sort((s1, s2) -> {
			return s2.total - s1.total;
		});
		return scores;
	}
	
	
	
	
	@Override public void setOpen(boolean open){this.open = open;}
	@Override public boolean isOpen(){return false;}
	@Override public boolean hasRoom(){return false;}
	@Override public int getPlayerCount(){return players.size();}
	
	@Override public GameState getState(){return state;}
	
	@Override public void setPlayerCap(int playerCap) {this.playerCap = playerCap;}
	@Override public int getPlayerCap(){return playerCap;}
	@Override public List<Player> getPlayers(){return players;}
	
	@Override public String getName(){return name;}
	@Override public Location getSpawn(){return spawn;}
	@Override public int getDuration(){return duration;}
	@Override public void setTimeLeft(int timeLeft){if(state == GameState.WAITING)this.duration = timeLeft; else this.timeLeft = timeLeft;}
	@Override public int getTimeLeft(){return timeLeft;}
	
	@Override public void broadcast(final String message){for(Player p:players) p.sendMessage(message);}
	@Override public void tpAll(final Location loc){for(Player p:players) p.teleport(loc);}
	
	private static class Clock{
		private JavaPlugin plugin;
		private int taskId;
		private boolean clockEnabled = false;
		
		public Clock(JavaPlugin plugin){
			this.plugin = plugin;
		}
		public void turnOn(long delay, long interval, Runnable code){
			if(clockEnabled)
				turnOff();
			taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, code, delay, interval);
			clockEnabled = true;
		}
		public void turnOff(){
			if(clockEnabled){
				plugin.getServer().getScheduler().cancelTask(taskId);
				clockEnabled = false;
			}
		}
	}
	private static class AbbaScore{
		public final int[] count;
		public final Player player;
		public int total;
		
		public AbbaScore(Player player, int size){
			count = new int[size];
			this.player = player;
		}
	}
}
