package com.dthielke.herochat;

import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.util.Messaging;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {
    private List<String> censors = new ArrayList<String>();

    public void setCensors(List<String> censors) {
        this.censors = censors;
    }

    private String censor(String msg, String censor, boolean customReplacement, String replacement) {
        Pattern pattern = Pattern.compile(censor, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);
        StringBuilder censoredMsg = new StringBuilder();
        while (matcher.find()) {
            String match = matcher.group();
            if (!customReplacement) {
                char[] replaceChars = new char[match.length()];
                Arrays.fill(replaceChars, '*');
                replacement = new String(replaceChars);
            }
            censoredMsg.append(msg.substring(0, matcher.start())).append(replacement);
            msg = msg.substring(matcher.end());
            matcher = pattern.matcher(msg);
        }
        censoredMsg.append(msg);

        return censoredMsg.toString();
    }

    public void handle(PlayerChatEvent event) {
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

        // colorize the message
        if (sender.canColorMessages(channel) == Chatter.Result.ALLOWED)
            event.setMessage(event.getMessage().replaceAll("&([0-9a-fA-F])", "\u00a7$1"));

        // censor the message
        event.setMessage(censor(event.getMessage()));

        // pass it to the channel for additional processing
        channel.processChat(event);
    }

    private String censor(String msg) {
        for (String censor : censors) {
            String[] split = censor.split(";", 2);
            if (split.length == 1) {
                msg = censor(msg, censor, false, "");
            } else {
                msg = censor(msg, split[0], true, split[1]);
            }
        }
        return msg;
    }
}
