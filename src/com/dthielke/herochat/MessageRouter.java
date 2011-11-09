package com.dthielke.herochat;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class MessageRouter {

    public static void route(PlayerChatEvent event) {
        Chatter sender = HeroChat.getChatterManager().getChatter(event.getPlayer());
        if (sender == null) {
            throw new RuntimeException("Chatter (" + event.getPlayer().getName() + ") not found.");
        }

        Channel channel = sender.getActiveChannel();
        if (channel == null) {
            throw new RuntimeException("Active channel for chatter (" + event.getPlayer().getName() + ") not found.");
        }

        // trim the recipient list
        Set<Player> recipients = event.getRecipients();
        Set<Chatter> intendedRecipients = channel.getMembers();
        for (Iterator<Player> iter = recipients.iterator(); iter.hasNext();) {
            Chatter recipient = HeroChat.getChatterManager().getChatter(iter.next());
            if (!intendedRecipients.contains(recipient)) {
                iter.remove();
            } else if (channel.isLocal() && !sender.isInRange(recipient, channel.getDistance())) {
                iter.remove();
            }
        }

        // apply channel formatting
        format(channel, event);
    }

    public static void format(Channel channel, PlayerChatEvent event) {
        // default minecraft format is <%1$s> %2$s

        String format = channel.getFormat();
        format = format.replace("#name", channel.getName());
        format = format.replace("#nick", channel.getNick());
        format = format.replace("#sender", "%1$s");
        format = format.replace("#msg", "%2$s");
        format = format.replace("&", "\u00a7");
        event.setFormat(format);
    }

}
