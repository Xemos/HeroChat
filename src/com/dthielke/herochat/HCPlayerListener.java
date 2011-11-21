package com.dthielke.herochat;

import org.bukkit.event.player.*;

public class HCPlayerListener extends PlayerListener {
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled())
            return;

        MessageHandler.handle(event);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        HeroChat.getChatterManager().removeChatter(event.getPlayer());
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;

        String input = event.getMessage().substring(1);
        String[] args = input.split(" ");
        Channel channel = HeroChat.getChannelManager().getChannel(args[0]);
        if (channel != null && channel.isShortcutAllowed()) {
            event.setCancelled(true);
            HeroChat.getCommandHandler().dispatch(event.getPlayer(), "ch qm", args);
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        HeroChat.getChatterManager().addChatter(event.getPlayer());
    }
}
