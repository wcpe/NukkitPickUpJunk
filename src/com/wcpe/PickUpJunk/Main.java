package com.wcpe.PickUpJunk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.wcpe.PickUpJunk.Event.GameStartEvent;
import com.wcpe.PickUpJunk.GUI.CreateGameGui;
import com.wcpe.PickUpJunk.GUI.JoinGameGui;
import com.wcpe.PickUpJunk.GUI.InterFace.Button;
import com.wcpe.PickUpJunk.GUI.InterFace.Gui;
import com.wcpe.PickUpJunk.Obj.ChestObj;
import com.wcpe.PickUpJunk.Obj.GameObj;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class Main extends PluginBase implements CommandExecutor, Listener {

	private LinkedHashMap<String, GameObj> gamelist = new LinkedHashMap<String, GameObj>();

	private HashMap<String, List<String>> chest = new HashMap<String, List<String>>();

	private List<Player> GamePlayer = new ArrayList<Player>();
	@Override
	public void onDisable() {
		saveData();
		getLogger().info(TextFormat.RED + "PickUpJunk被卸载啦!");
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		loadData();
		getLogger().info(TextFormat.GREEN + "PickUpJunk加载完成啦!");
	}

	@Override
	public void onLoad() {
		getLogger().info(TextFormat.GREEN + "PickUpJunk被加载啦!");
	}

	private void saveData() {
		for (Entry<String, GameObj> obj : gamelist.entrySet()) {
			String gameName = obj.getValue().getGameName();

			int GameStartPlayer = obj.getValue().getGameStartPlayer();
			int GameStartCountDown = obj.getValue().getGameStartCountDown();
			int GameTime = obj.getValue().getGameTime();
			int ClLocationCountDown = obj.getValue().getClLocationCountDown();
			String ClWorld = obj.getValue().getClWorld();

			Location firstLoc = obj.getValue().getFirstLoc();
			Location secondLoc = obj.getValue().getSecondLoc();
			List<String> cllist = new ArrayList<String>();
			List<ChestObj> chestlist = obj.getValue().getChestlist();
			List<String> playertp = new ArrayList<String>();

			File f = new File(this.getDataFolder(), "data.yml");
			Config data = new Config(f);

			data.set(gameName + ".GameStartPlayer", GameStartPlayer);
			data.set(gameName + ".GameStartCountDown", GameStartCountDown);
			data.set(gameName + ".GameTime", GameTime);
			data.set(gameName + ".ClLocationCountDown", ClLocationCountDown);

			data.set(gameName + ".ClWorld", ClWorld);

			data.set(gameName + ".firstLoc", (int) firstLoc.getX() + ";" + (int) firstLoc.getY() + ";"
					+ (int) firstLoc.getZ() + ";" + firstLoc.getLevel().getName());
			data.set(gameName + ".secondLoc", (int) secondLoc.getX() + ";" + (int) secondLoc.getY() + ";"
					+ (int) secondLoc.getZ() + ";" + secondLoc.getLevel().getName());

			for (Location l : obj.getValue().getCllist()) {
				cllist.add((int) l.getX() + ";" + (int) l.getY() + ";" + (int) l.getZ() + ";" + l.getLevel().getName());
			}
			data.set(gameName + ".cllist", cllist);
			for (ChestObj o : chestlist) {
				String name = o.getName();
				Location loc = o.getLoc();
				List<String> id = o.getId();
				String key = (int) loc.getX() + ";" + (int) loc.getY() + ";" + (int) loc.getZ() + ";"
						+ loc.getLevel().getName();
				data.set(gameName + ".chest." + name + ".Loc", key);
				data.set(gameName + ".chest." + name + ".Item", id);
			}
			for (Location l : obj.getValue().getPlayertp()) {
				playertp.add(
						(int) l.getX() + ";" + (int) l.getY() + ";" + (int) l.getZ() + ";" + l.getLevel().getName());
			}
			data.set(gameName + ".playertp", playertp);
			data.save();
		}

	}

	private void loadData() {
		for (String st : this.getConfig().getSection("Chest").getKeys(false)) {
			chest.put(st, this.getConfig().getStringList("Chest." + st + ".Item"));
		}

		File f = new File(this.getDataFolder(), "data.yml");
		Config data = new Config(f);
		for (String obj : data.getKeys(false)) {
			if (data.exists(obj + ".firstLoc")) {
				int GameStartPlayer = data.getInt(obj + ".GameStartPlayer");
				int GameStartCountDown = data.getInt(obj + ".GameStartCountDown");
				int GameTime = data.getInt(obj + ".GameTime");
				int ClLocationCountDown = data.getInt(obj + ".ClLocationCountDown");
				String ClWorld = data.getString(obj + ".ClWorld");
				String firstLockey = data.getString(obj + ".firstLoc");
				String secondLockey = data.getString(obj + ".secondLoc");
				List<String> cllistkey = data.getStringList(obj + ".cllist");
				List<String> playertpkey = data.getStringList(obj + ".playertp");
				List<Location> cllist = new ArrayList<Location>();
				List<Location> playertp = new ArrayList<Location>();
				List<ChestObj> chestlist = new ArrayList<ChestObj>();
				for (String s : data.getSection(obj + ".chest").getKeys(false)) {
					String name = s;
					String lockey = data.getString(obj + ".chest." + s + ".Loc");
					String[] locsp = lockey.split(";");
					Location loc = new Location(Double.valueOf(locsp[0]), Double.valueOf(locsp[1]),
							Double.valueOf(locsp[2]), getServer().getLevelByName(locsp[3]));
					List<String> item = data.getStringList(obj + ".chest." + s + ".Item");
					chestlist.add(new ChestObj(name, item, loc));
				}
				String[] firstlocsp = firstLockey.split(";");
				Location firstLoc = new Location(Double.valueOf(firstlocsp[0]), Double.valueOf(firstlocsp[1]),
						Double.valueOf(firstlocsp[2]), getServer().getLevelByName(firstlocsp[3]));
				String[] secondlocsp = secondLockey.split(";");
				Location secondLoc = new Location(Double.valueOf(secondlocsp[0]), Double.valueOf(secondlocsp[1]),
						Double.valueOf(secondlocsp[2]), getServer().getLevelByName(secondlocsp[3]));
				for (String s : cllistkey) {
					String[] cllocsp = s.split(";");
					cllist.add(new Location(Double.valueOf(cllocsp[0]), Double.valueOf(cllocsp[1]),
							Double.valueOf(cllocsp[2]), getServer().getLevelByName(cllocsp[3])));
				}
				for (String s : playertpkey) {
					String[] playertplocsp = s.split(";");
					playertp.add(new Location(Double.valueOf(playertplocsp[0]), Double.valueOf(playertplocsp[1]),
							Double.valueOf(playertplocsp[2]), getServer().getLevelByName(playertplocsp[3])));
				}
				gamelist.put(obj,
						new GameObj(this, GameStartPlayer, GameStartCountDown, GameTime, ClLocationCountDown, ClWorld,
								obj, firstLoc, secondLoc, cllist, chestlist, playertp, new ArrayList<Player>(),
								new ArrayList<Player>(), true, false, false));

			}
		}
	}

	boolean setGameStart = false;
	boolean setGameOneOver = false;
	boolean setGameTwoOver = false;
	boolean setClListOver = false;

	boolean setChestStart = false;
	boolean setChestOver = false;
	boolean setPlayertpStart = false;
	boolean setPlayertpOver = false;

	@SuppressWarnings("serial")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§a宁必须是玩家 !");
			return true;
		}
		Player p = (Player) sender;

		if (args.length == 1) {
			if (args[0].equals("list")) {
				p.sendMessage("§e¤§a当前等待的玩家§e¤");
				for (Entry<String, GameObj> obj : gamelist.entrySet()) {
					List<String> a = new ArrayList<String>();
					for (Player b : obj.getValue().getPlayerlist()) {
						a.add(b.getName());
					}
					p.sendMessage("§a游戏名称§e¤§c" + obj.getValue().getGameName());
					p.sendMessage("§a玩家§e¤§c" + a);
				}
				return true;
			} else if (args[0].equals("join")) {
				JoinGameGui ha = new JoinGameGui("加入游戏", "选择一个房间 点击开始吧");
				for (Entry<String, GameObj> o : getGamelist().entrySet()) {
					ha.addButton(new Button("§a游戏房间:§e " + o.getValue().getGameName() + " §9§l§m+---- §b§l单击加入游戏",
							new ElementButtonImageData("path", "textures/items/clock_item.png")));
				}

				p.showFormWindow(ha);
				return true;
			} else if (args[0].equals("setgame")) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				p.sendMessage("§a开始设置游戏区域坐标");

				CreateGameGui createGameGui = new CreateGameGui("开始设置PickUpJunk游戏房间", new ArrayList<Element>() {
					{
						add(new ElementInput("请输入游戏名称"));
						add(new ElementInput("请输入多少玩家开始游戏 纯数字"));
						add(new ElementInput("请输入游戏即将开始倒计时 纯数字"));
						add(new ElementInput("请输入游戏时间 纯数字"));
						add(new ElementInput("请输入撤离点撤离倒计时 纯数字"));
						add(new ElementInput("请输入撤离返回的世界"));
					}
				});

				p.showFormWindow(createGameGui);
				settingplayer = p;
				setGameStart = true;

			} else {
				p.sendMessage("§4用法错误 正确食用方式~ ");
				p.sendMessage("§a加入游戏:/puj join <游戏名称>");
				p.sendMessage("§a离开游戏:/puj leave <游戏名称>");
				p.sendMessage("§a查询游戏玩家:/puj list");
			}
		} else if (args.length == 2) {
			if (args[0].equals("join") && args[1] != null) {
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏 请联系管理员");
					return true;
				}
				if (gameObj.isGameStart()) {
					p.sendMessage("§a游戏已经开始！请等待至结束");
					return true;
				}
				if (gameObj.getPlayerlist().contains(p) || GamePlayer.contains(p)) {
					p.sendMessage("§c您当前已经加入游戏 输入/puj list 可查看多少玩家正在等待");
					return true;
				}
				if (!gameObj.isGameSetOver()) {
					p.sendMessage("§a游戏场地未设置");
					return true;
				}

				gameObj.getPlayerlist().add(p);
				GamePlayer.add(p);
				for (Player pl : gameObj.getPlayerlist()) {
					pl.sendMessage("§a加入游戏成功 ,当前§e" + gameObj.getPlayerlist().size() + "§b/"
							+ gameObj.getGameStartPlayer() + "§a名玩家");
				}

				if (gameObj.getGameStartPlayer() == gameObj.getPlayerlist().size()) {
					for (Player pl : gameObj.getPlayerlist()) {
						pl.sendMessage("§a当前§e" + gameObj.getPlayerlist().size() + "§b/" + gameObj.getGameStartPlayer()
								+ "§a名玩家");
						pl.sendMessage("§a游戏即将开始 倒计时§e" + gameObj.getGameStartCountDown() + "§a秒");
					}
					new Thread(() -> {
						try {
							for (int t = gameObj.getGameStartCountDown(); t >= 0; t--) {
								Thread.sleep(1000);
								if (t == gameObj.getGameStartCountDown() || t == t / 2 || t == 5 || t == 4 || t == 3
										|| t == 2 || t == 1 || t == 0) {
									if (gameObj.getPlayerlist().isEmpty()) {
										getServer().broadcastMessage("§a游戏玩家全部离开，游戏结束!");
										return;
									}

									for (Player pl : gameObj.getPlayerlist()) {
										pl.sendMessage("§c倒计时" + "§e" + t + "§c秒.....");
									}

									if (t == 0) {
										gameObj.setGameStart(true);
										getServer().getPluginManager().registerEvents(gameObj, this);
										getServer().getPluginManager().callEvent(new GameStartEvent(
												gameObj.getPlayerlist(), gameObj, gameObj.getGameName()));

										List<Location> aaa = new ArrayList<Location>();
										aaa.addAll(gameObj.getPlayertp());
										for (Player pl : gameObj.getPlayerlist()) {
											pl.sendMessage("§a游戏开始 祝你好运");
											Location location = aaa.get((int) (Math.random() * aaa.size()));
											pl.teleport(location);
											gameObj.getMap().put(pl.getName(), false);
											aaa.remove(location);
										}

									}
								}
							}
						} catch (InterruptedException e) {
						}
					}).start();
				}
				return true;
			} else if (args[0].equals("leave") && args[1] != null) {
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏 请联系管理员");
					return true;
				}
				if (!gameObj.isGameSetOver()) {
					p.sendMessage("§a游戏场地未设置");
					return true;
				}
				if (gameObj.getPlayerlist().contains(p) || GamePlayer.contains(p)) {
					gameObj.getPlayerlist().remove(p);
					GamePlayer.remove(p);
					p.sendMessage("§a离开游戏成功 ,当前§e " + gameObj.getPlayerlist().size() + "§a名玩家正在等待");
				} else {
					p.sendMessage("§4你没有加入该游戏！！");
				}
				return true;
			} else if (args[0].equals("setgameone") && args[1] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setGameStart) {
					gameObj.setFirstLoc(p.getLocation());
					p.sendMessage("§a成功设置游戏区域第一个点");
					p.sendMessage("§a请站到第二个点输入§e/puj setgametwo <游戏名称>");
					setGameOneOver = true;
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame <游戏名称>§a开始设置点");
				}
			} else if (args[0].equals("setgametwo") && args[1] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setGameOneOver) {
					if (!p.getLocation().getLevel().equals(gameObj.getFirstLoc().getLevel())) {
						p.sendMessage("§4两点请在同一世界设置");
						return false;
					}
					gameObj.setSecondLoc(p.getLocation());
					p.sendMessage("§a成功设置游戏区域第二个点");
					p.sendMessage("§a成功设置完毕游戏区域 请开始设置撤离点坐标");
					p.sendMessage("§a请输入§e/setcl <游戏名称> §a进行设置撤离点");
					setGameTwoOver = true;
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame开始设置点");
				}
			} else if (args[0].equals("setclover") && args[1] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setClListOver) {
					p.sendMessage("§a成功结束设置撤离点");
					p.sendMessage("§a输入§e/puj setchest 箱子id §a进行设置");
					setChestStart = true;
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame开始设置点");
				}
			} else if (args[0].equals("setchestover") && args[1] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setChestOver) {
					p.sendMessage("§a成功结束设置箱子");
					p.sendMessage("§a输入§e/puj setplayertp <游戏名称>§a设置玩家进入游戏传送的位置");
					setPlayertpStart = true;
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame开始设置点");
				}
			} else if (args[0].equals("setplayertpover") && args[1] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setPlayertpOver) {
					gameObj.setGameSetOver(true);
					p.sendMessage("§a成功结束设置玩家传送点");
					p.sendMessage("§e设置§ePickUpJunk完毕");
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame开始设置点");
				}
			} else if (args[0].equals("setcl") && args[1] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setGameTwoOver) {
					if (!p.getLocation().getLevel().equals(gameObj.getFirstLoc().getLevel())) {
						p.sendMessage("§4撤离点请在与游戏区域同一世界设置");
						return false;
					}
					gameObj.getCllist().add(p.getLocation());
					p.sendMessage("§a撤离点" + args[1] + "位置设置成功！！！");
					p.sendMessage("§a如果设置完所有撤离点请输入§e/puj setclover §a结束设置撤离点");
					setClListOver = true;
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame开始设置点");
				}
			} else if (args[0].equals("setplayertp") && args[1] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[1]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setPlayertpStart) {
					if (!p.getLocation().getLevel().equals(gameObj.getFirstLoc().getLevel())) {
						p.sendMessage("§4传送点请在与游戏区域同一世界设置");
						return false;
					}
					gameObj.getPlayertp().add(p.getLocation());
					p.sendMessage("§a传送点§e" + args[1] + "§a位置设置成功 ~");
					p.sendMessage("§a如果设置完玩家传送点请输入§e/puj setplayertpover §a结束设置玩家传送点");
					setPlayertpOver = true;
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame开始设置点");
				}
			} else {
				p.sendMessage("§4用法错误 正确食用方式~ ");
				p.sendMessage("§a加入游戏:/puj join <游戏名称>");
				p.sendMessage("§a离开游戏:/puj leave <游戏名称>");
				p.sendMessage("§a查询游戏玩家:/puj list");
			}
		} else if (args.length == 3) {
			if (args[0].equals("setchest") && args[1] != null && args[2] != null) {
				if (!p.hasPermission("PickUpJunk.admin")) {
					p.sendMessage("§4宁有权限？？？");
					return true;
				}
				GameObj gameObj = gamelist.get(args[2]);
				if (gameObj == null) {
					p.sendMessage("§c未知游戏名称");
					return true;
				}
				if (setChestStart) {
					if (!p.getLocation().getLevel().equals(gameObj.getFirstLoc().getLevel())) {
						p.sendMessage("§4箱子请在与游戏区域同一世界设置");
						return false;
					}
					if (chest.containsKey(args[1])) {
						gameObj.getChestlist().add(new ChestObj(args[1], chest.get(args[1]), p.getLocation()));
						p.sendMessage("§a箱子§e" + args[1] + "§a位置设置成功 <可重复设置同一个箱子到不同位置>~");
						p.sendMessage("§a如果设置完箱子请输入§e/puj setchestover §a结束设置箱子");
						setChestOver = true;
					} else {
						p.sendMessage("§c没有这个箱子 请检查配置");
					}
				} else {
					p.sendMessage("§4当前暂未开启设置区域 请输入/puj setgame开始设置点");
				}
			}
		} else {
			p.sendMessage("§4用法错误 正确食用方式~ ");
			p.sendMessage("§a加入游戏:/puj join <游戏名称>");
			p.sendMessage("§a离开游戏:/puj leave <游戏名称>");
			p.sendMessage("§a查询游戏玩家:/puj list");
		}
		return true;
	}

	private Player settingplayer;

	private List<Object> result = new ArrayList<Object>();

	@EventHandler
	public void click(PlayerFormRespondedEvent e) {
		if (e.getWindow() instanceof Gui) {
			JSONObject j1 = new JSONObject(e.getWindow().getJSONData());
			JSONObject j2 = j1.getJSONObject("response");
			int c = 0;
			for (Entry<String, GameObj> o : getGamelist().entrySet()) {
				if (j2.getInt("clickedButtonId") == c++) {
					getServer().dispatchCommand(e.getPlayer(), "puj join " + o.getValue().getGameName());
					return;
				}
			}
		}
	}

	@EventHandler
	public void setgame(PlayerFormRespondedEvent e) {
		if (setGameStart && e.getPlayer().equals(settingplayer)) {
			String jsonData = e.getWindow().getJSONData();
			JSONObject j1 = new JSONObject(jsonData);
			if (j1.getBoolean("closed")) {
				return;
			}
			JSONObject j2 = j1.getJSONObject("response");
			if (!j2.getString("title").equals("开始设置PickUpJunk游戏房间")) {
				return;
			}

			JSONObject j3 = j2.getJSONObject("responses");
			try {
				result.add(j3.get("0"));
				result.add(Integer.valueOf((String) j3.get("1")));
				result.add(Integer.valueOf((String) j3.get("2")));
				result.add(Integer.valueOf((String) j3.get("3")));
				result.add(Integer.valueOf((String) j3.get("4")));
				result.add(j3.get("5"));
			} catch (NumberFormatException ee) {
				e.getPlayer().sendMessage("§a请确认你输入的是否为数字 请输入§e/puj setgame §a重新打开窗口!");
				return;
			}

			gamelist.put((String) result.get(0),
					new GameObj(this, (int) result.get(1), (int) result.get(2), (int) result.get(3),
							(int) result.get(4), (String) result.get(5), (String) result.get(0), null, null,
							new ArrayList<Location>(), new ArrayList<ChestObj>(), new ArrayList<Location>(),
							new ArrayList<Player>(), new ArrayList<Player>(), false, false, false));
			e.getPlayer().sendMessage("§a第一步 站到第一个点输入§e/puj setgameone <游戏名称>");
			settingplayer = null;
		}
	}

	public boolean isInAABB(Location needGo, Location AA, Location BB) {
		int xMax = (int) (AA.getX() > BB.getX() ? AA.getX() : BB.getX());
		int xMin = (int) (AA.getX() > BB.getX() ? BB.getX() : AA.getX());
		int yMax = (int) (AA.getY() > BB.getY() ? AA.getY() : BB.getY());
		int yMin = (int) (AA.getY() > BB.getY() ? BB.getY() : AA.getY());
		int zMax = (int) (AA.getZ() > BB.getZ() ? AA.getZ() : BB.getZ());
		int zMin = (int) (AA.getZ() > BB.getZ() ? BB.getZ() : AA.getZ());
		return (needGo.getX() >= xMin) && (needGo.getX() <= xMax) && (needGo.getY() >= yMin) && (needGo.getY() <= yMax)
				&& (needGo.getZ() >= zMin) && (needGo.getZ() <= zMax);
	}

	/**
	 * @return gamelist
	 */
	public final LinkedHashMap<String, GameObj> getGamelist() {
		return gamelist;
	}

	/**
	 * @param gamelist 要设置的 gamelist
	 */
	public final void setGamelist(LinkedHashMap<String, GameObj> gamelist) {
		this.gamelist = gamelist;
	}

	/**
	 * @return chest
	 */
	public final HashMap<String, List<String>> getChest() {
		return chest;
	}

	/**
	 * @param chest 要设置的 chest
	 */
	public final void setChest(HashMap<String, List<String>> chest) {
		this.chest = chest;
	}

	/**
	 * @return gamePlayer
	 */
	public final List<Player> getGamePlayer() {
		return GamePlayer;
	}

	/**
	 * @param gamePlayer 要设置的 gamePlayer
	 */
	public final void setGamePlayer(List<Player> gamePlayer) {
		GamePlayer = gamePlayer;
	}

	/**
	 * @return setGameStart
	 */
	public final boolean isSetGameStart() {
		return setGameStart;
	}

	/**
	 * @param setGameStart 要设置的 setGameStart
	 */
	public final void setSetGameStart(boolean setGameStart) {
		this.setGameStart = setGameStart;
	}

	/**
	 * @return setGameOneOver
	 */
	public final boolean isSetGameOneOver() {
		return setGameOneOver;
	}

	/**
	 * @param setGameOneOver 要设置的 setGameOneOver
	 */
	public final void setSetGameOneOver(boolean setGameOneOver) {
		this.setGameOneOver = setGameOneOver;
	}

	/**
	 * @return setGameTwoOver
	 */
	public final boolean isSetGameTwoOver() {
		return setGameTwoOver;
	}

	/**
	 * @param setGameTwoOver 要设置的 setGameTwoOver
	 */
	public final void setSetGameTwoOver(boolean setGameTwoOver) {
		this.setGameTwoOver = setGameTwoOver;
	}

	/**
	 * @return setClListOver
	 */
	public final boolean isSetClListOver() {
		return setClListOver;
	}

	/**
	 * @param setClListOver 要设置的 setClListOver
	 */
	public final void setSetClListOver(boolean setClListOver) {
		this.setClListOver = setClListOver;
	}

	/**
	 * @return setChestStart
	 */
	public final boolean isSetChestStart() {
		return setChestStart;
	}

	/**
	 * @param setChestStart 要设置的 setChestStart
	 */
	public final void setSetChestStart(boolean setChestStart) {
		this.setChestStart = setChestStart;
	}

	/**
	 * @return setChestOver
	 */
	public final boolean isSetChestOver() {
		return setChestOver;
	}

	/**
	 * @param setChestOver 要设置的 setChestOver
	 */
	public final void setSetChestOver(boolean setChestOver) {
		this.setChestOver = setChestOver;
	}

	/**
	 * @return setPlayertpStart
	 */
	public final boolean isSetPlayertpStart() {
		return setPlayertpStart;
	}

	/**
	 * @param setPlayertpStart 要设置的 setPlayertpStart
	 */
	public final void setSetPlayertpStart(boolean setPlayertpStart) {
		this.setPlayertpStart = setPlayertpStart;
	}

	/**
	 * @return setPlayertpOver
	 */
	public final boolean isSetPlayertpOver() {
		return setPlayertpOver;
	}

	/**
	 * @param setPlayertpOver 要设置的 setPlayertpOver
	 */
	public final void setSetPlayertpOver(boolean setPlayertpOver) {
		this.setPlayertpOver = setPlayertpOver;
	}

}