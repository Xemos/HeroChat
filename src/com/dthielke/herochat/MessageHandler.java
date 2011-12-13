package com.dthielke.herochat;

import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class MessageHandler {
    public static void handle(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Chatter sender = HeroChat.getChatterManager().getChatter(player);
        if (sender == null) throw new RuntimeException("Chatter (" + event.getPlayer().getName() + ") not found.");

        Channel channel = sender.getActiveChannel();
        if (channel == null)
            throw new RuntimeException("Active channel for chatter (" + event.getPlayer().getName() + ") not found.");

        // see if the player can speak in the active channel
        Result result = sender.canSpeak(channel);
        switch (result) {
            case INVALID:
                Messaging.send(player, "You must join the channel before you can speak.");
                break;
            case MUTED:
                Messaging.send(player, "You are muted.");
                break;
            case NO_PERMISSION:
                Messaging.send(player, "You do not have permission.");
                break;
            case BAD_WORLD:
                Messaging.send(player, "Channel not available to this world.");
                break;
        }
        if (result != Result.ALLOWED) {
            event.setCancelled(true);
            return;
        }

        if (sender.canColorMessages(channel) == Chatter.Result.ALLOWED)
            event.setMessage(event.getMessage().replaceAll("&([0-9a-fA-F])", "\u00a7$1"));
        channel.processChat(event);
    }
}
