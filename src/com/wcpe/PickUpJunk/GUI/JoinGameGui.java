package com.wcpe.PickUpJunk.GUI;


import com.wcpe.PickUpJunk.GUI.InterFace.Gui;

import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowSimple;

public class JoinGameGui extends FormWindowSimple implements Gui {
	public JoinGameGui( String title, String content) {
		super(title, content);
	}

	@Override
	public void onClick(PlayerFormRespondedEvent event) {
	}
}
