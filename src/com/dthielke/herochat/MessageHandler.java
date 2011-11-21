package com.dthielke.herochat;

import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {
    private static final Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");

    public static void handle(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Chatter sender = HeroChat.getChatterManager().getChatter(player);
        if (sender == null)
            throw new RuntimeException("Chatter (" + event.getPlayer().getName() + ") not found.");

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

        // trim the recipient list
        String senderName = player.getName();
        Set<Player> recipients = event.getRecipients();
        Set<Chatter> intendedRecipients = channel.getMembers();
        for (Iterator<Player> iter = recipients.iterator(); iter.hasNext(); ) {
            Chatter recipient = HeroChat.getChatterManager().getChatter(iter.next());
            if (!intendedRecipients.contains(recipient)) {
                iter.remove();
            } else if (channel.isLocal() && !sender.isInRange(recipient, channel.getDistance())) {
                iter.remove();
            } else if (!channel.hasWorld(recipient.getPlayer().getWorld())) {
                iter.remove();
            } else if (recipient.isIgnoring(senderName)) {
                iter.remove();
            }
        }

        // apply channel formatting
        String format = event.getFormat();
        Matcher matcher = msgPattern.matcher(format);
        if (matcher.groupCount() == 3)
            event.setFormat(format(channel, matcher.group(1) + matcher.group(2), matcher.group(3)));
        else
            event.setFormat(format(channel, "", ""));
    }

    public static String format(Channel channel, String preExtras, String postExtras) {
        return format(channel, channel.getFormat(), preExtras, postExtras);
    }

    public static String format(Channel channel, String format, String preExtras, String postExtras) {
        // default minecraft format is <%1$s> %2$s
        format = format.replace("#name", channel.getName());
        format = format.replace("#nick", channel.getNick());
        format = format.replace("#color", channel.getColor().toString());
        format = format.replace("#sender", preExtras + "%1$s" + postExtras);
        format = format.replace("#msg", "%2$s");
        format = format.replace("&", "\u00a7");
        return format;
    }
}
