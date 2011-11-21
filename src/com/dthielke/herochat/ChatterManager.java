package com.dthielke.herochat;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChatterManager {
    private Map<Player, Chatter> chatters = new HashMap<Player, Chatter>();
    private ChatterStorage storage;

    public ChatterStorage getStorage() {
        return storage;
    }

    public void setStorage(ChatterStorage storage) {
        this.storage = storage;
    }

    public void addChatter(Player player) {
        if (chatters.containsKey(player))
            return;

        Chatter chatter = storage.load(player.getName());
        storage.addChatter(chatter);
        chatters.put(player, chatter);
    }

    public void addChatter(Chatter chatter) {
        chatters.put(chatter.getPlayer(), chatter);
        storage.addChatter(chatter);
    }

    public Chatter getChatter(Player player) {
        return chatters.get(player);
    }

    public Chatter getChatter(String name) {
        for (Chatter chatter : chatters.values())
            if (name.equalsIgnoreCase(chatter.getName()))
                return chatter;

        return null;
    }

    public Collection<Chatter> getChatters() {
        return chatters.values();
    }

    public void removeChatter(Chatter chatter) {
        chatters.remove(chatter.getPlayer());
        storage.removeChatter(chatter);
        for (Channel channel : chatter.getChannels().toArray(new Channel[0]))
            channel.removeMember(chatter, true);
    }

    public void removeChatter(Player player) {
        removeChatter(chatters.get(player));
    }
}
