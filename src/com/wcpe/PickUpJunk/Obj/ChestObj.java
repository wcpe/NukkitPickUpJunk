package com.wcpe.PickUpJunk.Obj;

import java.util.List;

import cn.nukkit.level.Location;

public class ChestObj {

	public ChestObj(String name, List<String> id, Location loc) {
		this.name = name;
		this.id = id;
		this.loc = loc;
	}
	private String name;
	private List<String> id;
	private Location loc;
	/**
	 * @return name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param name 要设置的 name
	 */
	public final void setName(String name) {
		this.name = name;
	}
	/**
	 * @return id
	 */
	public final List<String> getId() {
		return id;
	}
	/**
	 * @param id 要设置的 id
	 */
	public final void setId(List<String> id) {
		this.id = id;
	}
	/**
	 * @return loc
	 */
	public final Location getLoc() {
		return loc;
	}
	/**
	 * @param loc 要设置的 loc
	 */
	public final void setLoc(Location loc) {
		this.loc = loc;
	}
	
}
