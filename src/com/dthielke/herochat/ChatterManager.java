package com.dthielke.herochat;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatterManager {
    private List<Chatter> chatters = new ArrayList<Chatter>();

    public List<Chatter> getChatters() {
        return chatters;
    }

    public boolean addChatter(Chatter chatter) {
        if (chatters.contains(chatter))
            return false;

        chatters.add(chatter);
        return true;
    }

    public Chatter getChatter(Player player) {
        for (Chatter chatter : chatters)
            if (player.equals(chatter.getPlayer()))
                return chatter;
        return null;
    }

    public Chatter getChatter(String name) {
        for (Chatter chatter : chatters)
            if (name.equalsIgnoreCase(chatter.getName()))
                return chatter;

        return null;
    }

    public boolean removeChatter(Chatter chatter) {
        if (!chatters.contains(chatter))
            return false;

        chatters.remove(chatter);
        return true;
    }
}
