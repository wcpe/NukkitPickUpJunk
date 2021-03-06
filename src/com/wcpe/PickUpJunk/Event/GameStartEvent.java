package com.wcpe.PickUpJunk.Event;


import java.util.List;

import com.wcpe.PickUpJunk.Obj.GameObj;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class GameStartEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public GameStartEvent(List<Player> playerlist, com.wcpe.PickUpJunk.Obj.GameObj gameObj, String gameName) {
		super();
		this.playerlist = playerlist;
		GameObj = gameObj;
		GameName = gameName;
	}

	private List<Player> playerlist;
	private GameObj GameObj;
	private String GameName;
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
	 * @return gameObj
	 */
	public final GameObj getGameObj() {
		return GameObj;
	}

	/**
	 * @param gameObj 要设置的 gameObj
	 */
	public final void setGameObj(GameObj gameObj) {
		GameObj = gameObj;
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
}