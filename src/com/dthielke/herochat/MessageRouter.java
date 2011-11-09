package com.dthielke.herochat;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class MessageRouter {
    
    public static void route(PlayerChatEvent event) {
        Chatter sender = HeroChat.getChatterManager().getChatter(event.getPlayer());
        Channel channel = sender.getActiveChannel();
        
        Set<Player> recipients = event.getRecipients();
        Set<Chatter> intendedRecipients = channel.getMembers();

        for (Iterator<Player> iter = recipients.iterator(); iter.hasNext();) {
            Chatter recipient = HeroChat.getChatterManager().getChatter(iter.next());
            if (!intendedRecipients.contains(recipient)) {
                iter.remove();
                continue;
            }
            
            if (channel.isLocal() && !sender.isInRange(recipient, channel.getDistance()))
                iter.remove();
        }
        
        format(channel, event);
    }
    
    public static void format(Channel channel, PlayerChatEvent event) {
        String format = channel.getFormat();
        format = format.replace("#name", channel.getName());
        format = format.replace("#nick", channel.getNick());
        format = format.replace("#sender", event.getPlayer().getDisplayName());
        format = format.replace("&", "\u00a7");
        event.getfo
    }

}
