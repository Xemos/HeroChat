package com.dthielke.herochat;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerChatListener extends PlayerListener {

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled())
            return;
        
        MessageHandler.handle(event);
    }

}
