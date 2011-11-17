package com.dthielke.herochat;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerChatListener extends PlayerListener {

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String input = event.getMessage().substring(1);
        String[] args = input.split(" ");
        Channel channel = HeroChat.getChannelManager().getChannel(args[0]);
        if (channel != null && channel.isQuickMessagable()) {
            event.setCancelled(true);
            HeroChat.getCommandHandler().dispatch(event.getPlayer(), "qm", args);
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled())
            return;

        MessageHandler.handle(event);
    }

}
