package me.lennartVH01;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	private static HashSet<Material> transparentBlocks = new HashSet<Material>();
	static{
		transparentBlocks.add(Material.AIR);
		transparentBlocks.add(Material.SIGN);
		transparentBlocks.add(Material.SIGN_POST);
		transparentBlocks.add(Material.WALL_SIGN);
	}
	
	
	
	public Map<UUID, AbbaGame> playerMap = new HashMap<UUID, AbbaGame>();
	
	public List<AbbaGame> ongoingGames = new ArrayList<AbbaGame>();
	
	public final String[] abbaSubCommands = new String[]{"calc", "close", "config", "create", "info", "join", "leave", "list", "open", "reload", "remove", "start"};
	
	public static Main plugin;
	
	public FileConfiguration config;
	
	
	public EventListener evtListener = new EventListener();
	
	@Override
	public void onEnable(){
		ConfigurationSerialization.registerClass(AbbaGame.class);
		
		
		
		config = this.getConfig();
		// Event handler
		evtListener.initialize(this);
		getServer().getPluginManager().registerEvents(evtListener, this);
		
		
		//Config
		this.saveDefaultConfig();
		
		
		
		
		// get Item Point Values
		List<Map<?,?>> itemPointMaps = config.getMapList("ItemValues");
		List<ValueItemPair> valueItemPairs = new ArrayList<ValueItemPair>();
		for(Map<?,?> itemMapGeneric:itemPointMaps){
			
			//Put the torches and pitchforks away, config.getMapList() *should* return Map<String, Object> anyways
			@SuppressWarnings("unchecked")
			Map<String, Object> itemMap = (Map<String, Object>) itemMapGeneric;
			
			if(itemMap.get("Value") == null)
				continue;
			int value = (int) itemMap.remove("Value");
			ItemStack stack = ItemStack.deserialize(itemMap);
			stack.setAmount(0);
			valueItemPairs.add(new ValueItemPair(stack, value));
		}
		
		AbbaTools.initialize(this, valueItemPairs);
		
		
	}
	@Override
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if(cmdLabel.equalsIgnoreCase("abba")){
			if(args.length == 0){
				args = new String[]{"help"};
			}
			switch(args[0].toLowerCase()){
			case "join":
				if(sender instanceof Player){
					if(sender.hasPermission("AbbaCaving.join")){
						Player p = (Player) sender;
						AbbaGame game;
						
						if(args.length >= 2){
							game = AbbaTools.getAbbaGame(args[1]);
						}else{
							game = AbbaTools.getAbbaGame();
						}
						if(game == null){
							sender.sendMessage("§cGame not found!");
							return false;
						}
						if(!(game.isOpen() || p.hasPermission("AbbaCaving.joinClosed"))){
							p.sendMessage("§cThis game is closed!");
							return false;
						}
						if(!(game.hasRoom() || p.hasPermission("AbbaCaving.joinFull"))){
							p.sendMessage("§cThis game is full!");
							return false;
						}
						
						if(!p.hasPermission("AbbaCaving.canCarryContraband")){
							ItemStack[] contraband = AbbaTools.getContraband(p.getInventory());
							if(contraband != null && contraband.length >= 1){
								p.sendMessage("§cYou cannot carry " + contraband[0].getType().toString() + " with you on an abba game!");
								return false;
							}
						}
						
						AbbaGame oldGame = playerMap.get(p.getUniqueId());
						if(oldGame != null){
							if(p.hasPermission("AbbaCaving.leave")){
								oldGame.removePlayer(p);
								playerMap.remove(p.getUniqueId());
								p.sendMessage("Left game \"" + oldGame.getName() + "\"");
							}else{
								p.sendMessage("§cYou don't have permission to leave the current game!");
							}
						}
						
						game.addPlayer(p);
						playerMap.put(p.getUniqueId(), game);
						p.sendMessage("Joined game \"" + game.getName() + "\"");
						
						p.teleport(game.getSpawn());
						p.setGameMode(GameMode.SURVIVAL);
						return true;
					}else{
						sender.sendMessage(Messages.noPermissionError);
						return false;
					}
				}else{
					sender.sendMessage(Messages.mustBeInGameError);
					return false;
				}
				
				
				
			case "leave":
				if(sender instanceof Player){
					if(sender.hasPermission("AbbaCaving.leave")){
						Player p = (Player) sender;
						AbbaGame game = playerMap.get(p.getUniqueId());
						game.removePlayer(p);
						playerMap.remove(p.getUniqueId());
						p.sendMessage("Left game \"" + game.getName() + "\"");
						return true;
					}else{
						sender.sendMessage(Messages.noPermissionError);
						return false;
					}
					
				}else{
					sender.sendMessage(Messages.mustBeInGameError);
					return false;
				}
				
				
				
			case "info":
				if(sender.hasPermission("AbbaCaving.info")){
					AbbaGame game;
					if(args.length >= 2){
						game = AbbaTools.getAbbaGame(args[1]);
						if(game == null){
							sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
							return false;
						}
					}else{
						game = AbbaTools.getAbbaGame();
						if(game == null){
							sender.sendMessage("§cNo Games found!");
							return false;
						}
					}
					sender.sendMessage("Game \"" + game.getName() + "\" " + (game.isOpen() ? "§aOpen":"§cClosed"));
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				break;
				
				
				
			case "list":
				if(sender.hasPermission("AbbaCaving.list")){
					sender.sendMessage("Games:");
					for(AbbaGame g:AbbaTools.getGames()){
						if(g.isOpen()){
							sender.sendMessage("- §a" + g.getName() + " (" + g.getPlayerCount() + "/" + g.getMaxPlayers() + ")");
						}else{
							sender.sendMessage("- §7§o" + g.getName() + " (" + g.getPlayerCount() + "/" + g.getMaxPlayers() + ")");
						}
					}
					return true;
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				
				
				
			case "calc":
				if(sender.hasPermission("AbbaCaving.calc")){
					Player calcPlayer;
					if(args.length >= 2){
						calcPlayer = getServer().getPlayer(args[1]);
						if(calcPlayer == null){
							sender.sendMessage("§cUnable to find player \"" + args[1] + "\"");
							return false;
						}
					}else{
						if(sender instanceof Player){
							calcPlayer = (Player) sender;
						}else{
							sender.sendMessage("usage: /abba calc <Player>");
							return false;
						}
					}
					
					CalculatedScore score = AbbaTools.calcScore(calcPlayer.getInventory());
					for(int i = 0; i < score.size(); i++){
						if(score.getItemCount(i) != 0)
							sender.sendMessage(score.getItemCount(i) + "x" + score.getItemStack(i).getType().toString() + ": " + score.getItemPoints(i));
					}
					sender.sendMessage("§aTotal Score: " + score.total);
					
					
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				
				
				
				break;
				
				
			//Admin commands
			case "calcscores":
				if(sender.hasPermission("AbbaCaving.calcScores")){
					AbbaGame game;
					if(args.length >= 2){
						game = AbbaTools.getAbbaGame(args[1]);
					}else{
						game = AbbaTools.getAbbaGame();
					}
					if(game.getState() != GameState.FINISHED){
						game.calcScores();
						
					}else{
						sender.sendMessage("§cGame not finished yet!");
						return false;
					}
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				break;
			case "create":
				if(args.length <= 5 && !(sender instanceof Player)){
					System.out.println("Must be ingame or specify world and coordinates");
					return false;
				}
				if(sender.hasPermission("AbbaCaving.create")){
					String gameName;
					Location gameSpawn = null;
					
					// set gameName
					if(args.length >= 2){
						gameName = args[1];
					}else{
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						gameName = "Abba" + dateFormat.format(new Date());
						
					}
					
					// set gameSpawn
					if(args.length >= 5){
						double spawnX, spawnY, spawnZ;
						try{
							spawnX = Double.parseDouble(args[2]);
							spawnY = Double.parseDouble(args[3]);
							spawnZ = Double.parseDouble(args[4]);
						}catch(NumberFormatException e){
							return false;
						}
						if(args.length >= 6){
							World w = getServer().getWorld(args[5]);
							if(w != null){
								
								gameSpawn = new Location(w, spawnX, spawnY, spawnZ);
								
							}else{
								sender.sendMessage("§4Unknown world: \"" + args[5] + "\"");
								return false;
							}
						}
					}else{
						if(sender instanceof Player){
							gameSpawn = ((Player) sender).getLocation();
						}else{
							return false;
						}
					}
					
					
					
					while(AbbaTools.getAbbaGame(gameName) != null){
						gameName += "_";
					}
					//create game
					AbbaTools.create(gameName, gameSpawn);
					sender.sendMessage("Successfully created game \"" + gameName + "\"");
					return true;
					
				}else{
					sender.sendMessage(Messages.noPermissionError);
				}
				break;
				
				
				
			case "remove":
				if(sender.hasPermission("AbbaCaving.remove")){
					if(args.length >= 2){
						if(AbbaTools.removeAbbaGame(args[1])){
							sender.sendMessage("Successfully removed game \"" + args[1] + "\"");
							return true;
						}else{
							sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
							return false;
						}
					}else{
						sender.sendMessage("Usage: /abba remove <game>");
						return false;
					}
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				
				
				
			case "open":
				if(sender.hasPermission("AbbaCaving.open")){
					AbbaGame game;
					if(args.length >= 2){
						game = AbbaTools.getAbbaGame(args[1]);
						if(game == null){
							sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
							return false;
						}
					}else{
						game = AbbaTools.getAbbaGame();
						if(game == null){
							sender.sendMessage("§cNo game found");
							return false;
						}
					}
					game.open();
					sender.sendMessage("Opened game \"" + args[1] + "\"");
					return true;
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				
				
				
			case "close":
				if(sender.hasPermission("AbbaCaving.open")){
					AbbaGame game;
					if(args.length >= 2){
						game = AbbaTools.getAbbaGame(args[1]);
						if(game == null){
							sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
							return false;
						}
					}else{
						game = AbbaTools.getAbbaGame();
						if(game == null){
							sender.sendMessage("§cNo game found");
							return false;
						}
					}
					game.close();
					sender.sendMessage("Closed game \"" + args[1] + "\"");
					return true;
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
			
			
			
			//Abba help
			case "reload":
				if(sender.hasPermission("AbbaCaving.reload")){
					sender.sendMessage("Reloading config...");
					this.reloadConfig();
					sender.sendMessage("Reload Complete.");
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				break;
				
				
				
			case "start":
				if(sender.hasPermission("AbbaCaving.start")){
					AbbaGame game;
					if(args.length >= 2){
						game = AbbaTools.getAbbaGame(args[1]);
						if(game == null){
							sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
							return false;
						}
					}else{
						game = AbbaTools.getAbbaGame();
						if(game == null){
							sender.sendMessage("§cNo games found");
							return false;
						}
					}
					switch(game.getState()){
					case WAITING:
						game.start();
						return true;
					case PAUSED:
						break;
					case COUNTDOWN:  //countdown and running both do the same
					case RUNNING:
						sender.sendMessage("§cGame already running!");
						return false;
					case FINISHED:
						break;
					}
					
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				
				
				
			case "config":
				if(sender.hasPermission("AbbaCaving.config")){
					if(args.length >= 2){
						if(args.length >= 3){
							AbbaGame game = AbbaTools.getAbbaGame(args[1]);
							if(game == null){
								sender.sendMessage(Messages.gameNotFoundError);
							}
							switch(args[2].toLowerCase()){
							case "timer":
								
								if(args.length >= 4){
									int newTime;
								
									try{
										newTime = Integer.parseInt(args[3]);
									}catch(NumberFormatException e){
										sender.sendMessage("§cPlease specify a number when you use this command");
										return false;
									}
									if(newTime <= 0){
										sender.sendMessage("§cPlease specify a stricktly positive number");
										return false;
									}
									if(game.getState() == GameState.RUNNING || game.getState() == GameState.PAUSED){
										game.setEndTime(System.currentTimeMillis() + newTime * 1000);
									}else{
										game.setDuration(newTime);
									}
									sender.sendMessage("Time has been updated!");
									return true;
									
								}else{
									sender.sendMessage("§cPlease specify new time");
									return false;
								}
							case "addchest":
								if(sender instanceof Player){
									Block blockInFront = ((Player) sender).getTargetBlock(transparentBlocks, 5);
									if(blockInFront.getState() instanceof Chest){
										for(BlockFace face:new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}){
											Block blockNextTo = blockInFront.getRelative(face);
											if(blockNextTo.getState() instanceof Sign){
												if(AbbaTools.getAbbaGame(args[1]).addChest((Chest) blockInFront.getState(), (Sign) blockNextTo.getState())){
													sender.sendMessage("Chest created");
													return true;
												}else{
													sender.sendMessage("§cChest already in a game!");
													return false;
												}
											}
										}
										
										sender.sendMessage("§cPut a sign next to the chest!");
										return false;
									}else{
										sender.sendMessage("§cYou must look at a chest to register it!");
										return false;
									}
								}else{
									sender.sendMessage("§cMust be ingame to register chests!");
									return false;
								}
							}
						}else{
							sender.sendMessage("§cChoose an option!");
							return false;
						}
					}else{
						sender.sendMessage(Messages.mustSpecifyGameError);
						return false;
					}
				}else{
					sender.sendMessage(Messages.noPermissionError);
					return false;
				}
				
				
				
				break;
			default:
				sender.sendMessage("Usage:\n - §aabba join§r: Joins the Abba Match\n - §aabba leave§f: Leaves current Abba Game\n - §aabba info§f: Displays info about an Abba Match\n - §aabba create§f: Creates an Abba Game at current location\n - §aabba remove§f: Stops game\n - §aabba open§f: Allows players to join the game\n - §aabba close§f: Prevents players from joining");
				return false;
			}
			
		}
		
		
		
		
		return false;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
		List<String> cmds = new ArrayList<String>();
		if(command.getName().equalsIgnoreCase("abba")){
			switch(args.length){
			case 1:
				for(String s:abbaSubCommands){
					if(sender.hasPermission("AbbaCaving." + s) && s.startsWith(args[0])){
						cmds.add(s);
					}
				}
				break;
			case 2:
				for(AbbaGame game:AbbaTools.getGames()){
					if(game.getName().startsWith(args[1])){
						cmds.add(game.getName());
					}
				}
			}
			
		}
		return cmds;
	}
}
