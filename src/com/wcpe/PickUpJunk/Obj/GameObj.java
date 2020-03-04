package com.wcpe.PickUpJunk.Obj;

import java.util.HashMap;
import java.util.List;

import com.wcpe.PickUpJunk.Main;
import com.wcpe.PickUpJunk.Event.GameOverEvent;
import com.wcpe.PickUpJunk.Event.GameStartEvent;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public class GameObj implements Listener {
	public GameObj(Main a, int gameStartPlayer, int gameStartCountDown, int gameTime, int clLocationCountDown,
			String clWorld, String gameName, Location firstLoc, Location secondLoc, List<Location> cllist,
			List<ChestObj> chestlist, List<Location> playertp, List<Player> playerlist, List<Player> deathlist,
			boolean gameSetOver, boolean gameStart, boolean gameOver) {
		this.a = a;
		this.GameStartPlayer = gameStartPlayer;
		this.GameStartCountDown = gameStartCountDown;
		this.GameTime = gameTime;
		this.ClLocationCountDown = clLocationCountDown;
		this.ClWorld = clWorld;
		this.GameName = gameName;
		this.FirstLoc = firstLoc;
		this.SecondLoc = secondLoc;
		this.Cllist = cllist;
		this.Chestlist = chestlist;
		this.Playertp = playertp;
		this.playerlist = playerlist;
		this.deathlist = deathlist;
		this.GameSetOver = gameSetOver;
		this.GameStart = gameStart;
		this.GameOver = gameOver;
	}

	private Main a;
	private int GameStartPlayer;
	private int GameStartCountDown;
	private int GameTime;
	private int ClLocationCountDown;
	private String ClWorld;
	private String GameName;
	private Location FirstLoc;
	private Location SecondLoc;
	private List<Location> Cllist;
	private List<ChestObj> Chestlist;
	private List<Location> Playertp;
	private List<Player> playerlist;
	private List<Player> deathlist;
	private boolean GameSetOver;
	private boolean GameStart;

	private boolean GameOver;

	Thread t = new Thread(() -> {
		try {
			for (int tt = GameTime; tt >= 0; tt--) {
				if (!GameOver) {
					Thread.sleep(1000);
					if (tt == GameTime || tt == tt / 2 || tt == 5 || tt == 4 || tt == 3 || tt == 2 || tt == 1
							|| tt == 0) {
						for (Player pl : getPlayerlist()) {
							pl.sendTitle("§c倒计时§e" + tt + "§c秒....., ", "§a游戏即将结束，游戏结束还未撤离直接死亡");
						}

						if (tt == 0) {
							for (Player pl : getPlayerlist()) {
								pl.sendMessage("§4时间已到 再见");
								pl.setHealth(0.0F);
								pl.extinguish();
								pl.scheduleUpdate();
								continue;
							}
							a.getGamePlayer().clear();
							setGameStart(false);
							getDeathlist().clear();
							getPlayerlist().clear();
							a.getServer().getPluginManager()
									.callEvent(new GameOverEvent(getGameName(), getPlayerlist(), this));

						}
					}
				}
			}
		} catch (InterruptedException | java.util.ConcurrentModificationException e) {
		}

	});
	/**
	 * 撤离冷却
	 **/
	private HashMap<String, Boolean> map = new HashMap<String, Boolean>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@EventHandler
	public void gameStart(GameStartEvent e) {
		GameOver = false;
		if (e.getGameName().equals(GameName)) {
			for (ChestObj chestObj : e.getGameObj().getChestlist()) {
				chestObj.getName();
				List<String> id = chestObj.getId();
				Location loc = chestObj.getLoc();
				CompoundTag nbt = new CompoundTag().putList(new ListTag("Items")).putString("id", "Chest")
						.putInt("x", (int) loc.x).putInt("y", (int) loc.y).putInt("z", (int) loc.z);
				loc.level.setBlockAt((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), 54, 0);
				BlockEntityChest chest = new BlockEntityChest(loc.getChunk(), nbt);
				for (String s : id) {
					String[] split = s.split(";");
					String[] split2 = split[0].split(":");
					double a = (Math.random() * 100 + 1);
					if (a <= Integer.valueOf(split[1])) {
						chest.getInventory().addItem(new Item(Integer.valueOf(split2[0]), Integer.valueOf(split2[1])));
					}
				}
			}
			t.start();
			a.getServer().broadcastMessage("§a游戏§e" + e.getGameName() + "§a开始了~");
		}

	}

	@EventHandler
	public void gameOver(GameOverEvent e) {
		if (e.getGameName().equals(GameName)) {
			GameOver = true;
			for (ChestObj chestObj : e.getGameObj().getChestlist()) {
				Location loc = chestObj.getLoc();
				loc.level.setBlockAt((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), 0, 0);
			}
			a.getServer().broadcastMessage("§a游戏§e" + e.getGameName() + "§c结束！");
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent e) {

		if (GameStart() && playerlist.contains(e.getEntity())) {
			e.setKeepInventory(false);
			deathlist.add(e.getEntity());
			for (Player pl : playerlist) {
				pl.sendMessage("§c玩家§e" + e.getEntity().getName() + "§c由于死亡被淘汰！！！");
				pl.sendTitle("§c玩家§e" + e.getEntity().getName(), "§c由于死亡被淘汰！！！");
			}
			playerlist.remove(e.getEntity());
			a.getGamePlayer().remove(e.getEntity());
			if (!playerlist.isEmpty() && playerlist != null) {

			} else {
				GameStart = false;
				deathlist.clear();
				playerlist.clear();
				a.getGamePlayer().clear();
				a.getServer().getPluginManager().callEvent(new GameOverEvent(GameName, playerlist, this));
			}

		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void move(PlayerMoveEvent e) {
		/**
		 * 判断角度
		 **/
		if ((e.getFrom().getX() == e.getTo().getX()) && (e.getFrom().getY() == e.getTo().getY())
				&& (e.getFrom().getX() == e.getTo().getX())) {
			return;
		}

		Thread tt = new Thread(() -> {
			try {
				for (int t = ClLocationCountDown; t >= 0; t--) {
					if (deathlist.contains(e.getPlayer())) {
						return;
					}
					if (!playerlist.contains(e.getPlayer())) {
						return;
					}

					if (!map.get(e.getPlayer().getName())) {
						return;
					}
					Thread.sleep(1000);
					if (t == ClLocationCountDown || t == t / 2 || t == 5 || t == 4 || t == 3 || t == 2 || t == 1
							|| t == 0) {
						e.getPlayer().sendTitle("§a开始撤离，请勿移动", "§c倒计时" + "§e" + t + "§c秒.....");
						if (t == 0) {

							e.getPlayer().teleport(a.getServer().getLevelByName(ClWorld).getSpawnLocation());
							for (Player pl : playerlist) {
								pl.sendMessage("§a玩家§e" + e.getPlayer().getName() + "§a成功撤离！！！");
								pl.sendTitle("§a玩家§e" + e.getPlayer().getName(), "§a撤离成功");
							}
							playerlist.remove(e.getPlayer());
							if (playerlist.isEmpty()) {

								GameStart = false;
								deathlist.clear();
								playerlist.clear();
								a.getGamePlayer().remove(e.getPlayer());
								a.getServer().getPluginManager()
										.callEvent(new GameOverEvent(GameName, playerlist, this));
							}
						}
					}
				}
			} catch (InterruptedException ee) {
				ee.printStackTrace();
			}
		});

		if (GameStart && playerlist.contains(e.getPlayer())) {
			double distanceA = e.getPlayer().getLocation().distance(getFirstLoc());
			double distanceB = e.getPlayer().getLocation().distance(getSecondLoc());
			if ((distanceA < 4 || distanceB < 4)) {
				if (!map.get(e.getPlayer().getName())) {
					tt.start();
					map.put(e.getPlayer().getName(), true);
				}
			} else {
				tt.stop();
				map.put(e.getPlayer().getName(), false);
			}
		}

		if (GameStart() && !a.isInAABB(e.getTo(), getFirstLoc(), getSecondLoc())) {
			e.getPlayer().teleport(e.getFrom());
		}

	}

	@EventHandler
	public void disCom(PlayerCommandPreprocessEvent e) {
		if (e.getPlayer().hasPermission("PickUpJunk.admin"))
			return;
		if (GameStart && playerlist.contains(e.getPlayer())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§4游戏状态下禁止使用指令！！！");
		}
	}

	public boolean GameStart() {
		if (GameSetOver && GameStart) {
			return true;
		}
		return false;
	}

	/**
	 * @return gameName
	 */
	public final String getGameName() {
		return GameName;
	}

	/**
	 * @param gameName 要设置的 gameName
	 */
	public final void setGameName(String gameName) {
		GameName = gameName;
	}

	/**
	 * @return firstLoc
	 */
	public final Location getFirstLoc() {
		return FirstLoc;
	}

	/**
	 * @param firstLoc 要设置的 firstLoc
	 */
	public final void setFirstLoc(Location firstLoc) {
		FirstLoc = firstLoc;
	}

	/**
	 * @return secondLoc
	 */
	public final Location getSecondLoc() {
		return SecondLoc;
	}

	/**
	 * @param secondLoc 要设置的 secondLoc
	 */
	public final void setSecondLoc(Location secondLoc) {
		SecondLoc = secondLoc;
	}

	/**
	 * @return cllist
	 */
	public final List<Location> getCllist() {
		return Cllist;
	}

	/**
	 * @param cllist 要设置的 cllist
	 */
	public final void setCllist(List<Location> cllist) {
		Cllist = cllist;
	}

	/**
	 * @return chestlist
	 */
	public final List<ChestObj> getChestlist() {
		return Chestlist;
	}

	/**
	 * @param chestlist 要设置的 chestlist
	 */
	public final void setChestlist(List<ChestObj> chestlist) {
		Chestlist = chestlist;
	}

	/**
	 * @return playertp
	 */
	public final List<Location> getPlayertp() {
		return Playertp;
	}

	/**
	 * @param playertp 要设置的 playertp
	 */
	public final void setPlayertp(List<Location> playertp) {
		Playertp = playertp;
	}

	/**
	 * @return playerlist
	 */
	public final List<Player> getPlayerlist() {
		return playerlist;
	}

	/**
	 * @param playerlist 要设置的 playerlist
	 */
	public final void setPlayerlist(List<Player> playerlist) {
		this.playerlist = playerlist;
	}

	/**
	 * @return deathlist
	 */
	public final List<Player> getDeathlist() {
		return deathlist;
	}

	/**
	 * @param deathlist 要设置的 deathlist
	 */
	public final void setDeathlist(List<Player> deathlist) {
		this.deathlist = deathlist;
	}

	/**
	 * @return gameSetOver
	 */
	public final boolean isGameSetOver() {
		return GameSetOver;
	}

	/**
	 * @param gameSetOver 要设置的 gameSetOver
	 */
	public final void setGameSetOver(boolean gameSetOver) {
		GameSetOver = gameSetOver;
	}

	/**
	 * @return gameStart
	 */
	public final boolean isGameStart() {
		return GameStart;
	}

	/**
	 * @param gameStart 要设置的 gameStart
	 */
	public final void setGameStart(boolean gameStart) {
		GameStart = gameStart;
	}

	/**
	 * @return a
	 */
	public final Main getA() {
		return a;
	}

	/**
	 * @param a 要设置的 a
	 */
	public final void setA(Main a) {
		this.a = a;
	}

	/**
	 * @return map
	 */
	public final HashMap<String, Boolean> getMap() {
		return map;
	}

	/**
	 * @param map 要设置的 map
	 */
	public final void setMap(HashMap<String, Boolean> map) {
		this.map = map;
	}

	/**
	 * @return gameStartPlayer
	 */
	public final int getGameStartPlayer() {
		return GameStartPlayer;
	}

	/**
	 * @param gameStartPlayer 要设置的 gameStartPlayer
	 */
	public final void setGameStartPlayer(int gameStartPlayer) {
		GameStartPlayer = gameStartPlayer;
	}

	/**
	 * @return gameStartCountDown
	 */
	public final int getGameStartCountDown() {
		return GameStartCountDown;
	}

	/**
	 * @param gameStartCountDown 要设置的 gameStartCountDown
	 */
	public final void setGameStartCountDown(int gameStartCountDown) {
		GameStartCountDown = gameStartCountDown;
	}

	/**
	 * @return gameTime
	 */
	public final int getGameTime() {
		return GameTime;
	}

	/**
	 * @param gameTime 要设置的 gameTime
	 */
	public final void setGameTime(int gameTime) {
		GameTime = gameTime;
	}

	/**
	 * @return clLocationCountDown
	 */
	public final int getClLocationCountDown() {
		return ClLocationCountDown;
	}

	/**
	 * @param clLocationCountDown 要设置的 clLocationCountDown
	 */
	public final void setClLocationCountDown(int clLocationCountDown) {
		ClLocationCountDown = clLocationCountDown;
	}

	/**
	 * @return clWorld
	 */
	public final String getClWorld() {
		return ClWorld;
	}

	/**
	 * @param clWorld 要设置的 clWorld
	 */
	public final void setClWorld(String clWorld) {
		ClWorld = clWorld;
	}

	/**
	 * @return gameOver
	 */
	public final boolean isGameOver() {
		return GameOver;
	}

	/**
	 * @param gameOver 要设置的 gameOver
	 */
	public final void setGameOver(boolean gameOver) {
		GameOver = gameOver;
	}

	/**
	 * @return t
	 */
	public final Thread getT() {
		return t;
	}

	/**
	 * @param t 要设置的 t
	 */
	public final void setT(Thread t) {
		this.t = t;
	}

	@Override
	public String toString() {
		return "GameObj [a=" + a + ", GameStartPlayer=" + GameStartPlayer + ", GameStartCountDown=" + GameStartCountDown
				+ ", GameTime=" + GameTime + ", ClLocationCountDown=" + ClLocationCountDown + ", ClWorld=" + ClWorld
				+ ", GameName=" + GameName + ", FirstLoc=" + FirstLoc + ", SecondLoc=" + SecondLoc + ", Cllist="
				+ Cllist + ", Chestlist=" + Chestlist + ", Playertp=" + Playertp + ", playerlist=" + playerlist
				+ ", deathlist=" + deathlist + ", GameSetOver=" + GameSetOver + ", GameStart=" + GameStart
				+ ", GameOver=" + GameOver + "]";
	}

}