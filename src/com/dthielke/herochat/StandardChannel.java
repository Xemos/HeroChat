package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class StandardChannel implements Channel {
    public static final String ANNOUNCEMENT_FORMAT = "#color[#nick] #msg";
    public static final String MESSAGE_FORMAT = "#color[#nick] &f#sender#color: #msg";

    private String name;
    private String nick;
    private String format;
    private ChatColor color;
    private int distance;
    private boolean shortcutAllowed;
    private Set<Chatter> members = new HashSet<Chatter>();
    private Set<String> worlds = new HashSet<String>();
    private Set<String> bans = new HashSet<String>();
    private Set<String> mutes = new HashSet<String>();
    private Set<String> moderators = new HashSet<String>();
    private ChannelStorage storage;

    public StandardChannel(String name, String nick) {
        this.name = name;
        this.nick = nick;
        this.color = ChatColor.WHITE;
        this.distance = 0;
        this.shortcutAllowed = false;
        this.format = MESSAGE_FORMAT;
    }

    @Override
    public Set<String> getBans() {
        return bans;
    }

    @Override
    public void setBans(Set<String> bans) {
        this.bans = bans;
    }

    @Override
    public ChatColor getColor() {
        return color;
    }

    @Override
    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public Set<Chatter> getMembers() {
        return members;
    }

    @Override
    public Set<String> getModerators() {
        return moderators;
    }

    @Override
    public void setModerators(Set<String> moderators) {
        this.moderators = moderators;
    }

    @Override
    public Set<String> getMutes() {
        return mutes;
    }

    @Override
    public void setMutes(Set<String> mutes) {
        this.mutes = mutes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public ChannelStorage getStorage() {
        return storage;
    }

    @Override
    public Set<String> getWorlds() {
        return worlds;
    }

    @Override
    public void setWorlds(Set<String> worlds) {
        this.worlds = worlds;
    }

    @Override
    public boolean isShortcutAllowed() {
        return shortcutAllowed;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;

        if (other == null)
            return false;

        if (!(other instanceof Channel))
            return false;

        Channel channel = (Channel) other;
        return name.equalsIgnoreCase(channel.getName()) || name.equalsIgnoreCase(channel.getNick());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.toLowerCase().hashCode());
        result = prime * result + (nick == null ? 0 : nick.toLowerCase().hashCode());
        return result;
    }

    @Override
    public boolean addMember(Chatter chatter, boolean announce) {
        if (members.contains(chatter))
            return false;

        members.add(chatter);
        if (!chatter.hasChannel(this)) {
            chatter.addChannel(this, announce);
        }

        if (announce) {
            announce(chatter.getPlayer().getName() + " has joined the channel.");
        }

        return true;
    }

    @Override
    public void announce(String message) {
        message = MessageHandler.format(this, ANNOUNCEMENT_FORMAT).replace("%2$s", message);
        for (Chatter member : members) {
            member.getPlayer().sendMessage(message);
        }
    }

    @Override
    public void addWorld(String world) {
        if (!worlds.contains(world)) {
            worlds.add(world);
            storage.notify(this);
        }
    }

    @Override
    public void attachStorage(ChannelStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean banMember(Chatter chatter, boolean announce) {
        if (!members.contains(chatter))
            return false;

        if (announce) {
            announce(chatter.getPlayer().getName() + " has been banned.");
        }

        removeMember(chatter, false);
        setBanned(chatter.getPlayer().getName(), true);
        return true;
    }

    @Override
    public boolean removeMember(Chatter chatter, boolean announce) {
        if (!members.contains(chatter))
            return false;

        if (announce) {
            announce(chatter.getPlayer().getName() + " has left the channel.");
        }

        members.remove(chatter);
        if (chatter.hasChannel(this)) {
            chatter.removeChannel(this, announce);
        }

        return true;
    }

    @Override
    public void setBanned(String name, boolean banned) {
        if (banned)
            bans.add(name.toLowerCase());
        else
            bans.remove(name.toLowerCase());
        storage.notify(this);
    }

    @Override
    public boolean hasWorld(String world) {
        return worlds.isEmpty() || worlds.contains(world);
    }

    @Override
    public boolean hasWorld(World world) {
        return worlds.isEmpty() || worlds.contains(world.getName());
    }

    @Override
    public boolean isBanned(String name) {
        return bans.contains(name.toLowerCase());
    }

    @Override
    public boolean isLocal() {
        return distance != 0;
    }

    @Override
    public boolean isMember(Chatter chatter) {
        return members.contains(chatter);
    }

    @Override
    public boolean isModerator(String name) {
        return moderators.contains(name.toLowerCase());
    }

    @Override
    public boolean isMuted(String name) {
        return mutes.contains(name.toLowerCase());
    }

    @Override
    public boolean kickMember(Chatter chatter, boolean announce) {
        if (!members.contains(chatter))
            return false;

        if (announce) {
            announce(chatter.getPlayer().getName() + " has been kicked.");
        }

        removeMember(chatter, false);
        return true;
    }

    @Override
    public void removeWorld(String world) {
        if (worlds.contains(world)) {
            worlds.remove(world);
        }
    }

    @Override
    public void setDistance(int distance) {
        this.distance = distance < 0 ? 0 : distance;
        storage.notify(this);
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
        storage.notify(this);
    }

    @Override
    public void setModerator(String name, boolean moderator) {
        if (moderator)
            moderators.add(name.toLowerCase());
        else
            moderators.remove(name.toLowerCase());
        storage.notify(this);
    }

    @Override
    public void setMuted(String name, boolean muted) {
        if (muted)
            mutes.add(name.toLowerCase());
        else
            mutes.remove(name.toLowerCase());
        storage.notify(this);
    }

    @Override
    public void setName(String name) {
        this.name = name;
        storage.notify(this);
    }

    @Override
    public void setNick(String nick) {
        this.nick = nick;
        storage.notify(this);
    }

    @Override
    public void setShortcutAllowed(boolean shortcutAllowed) {
        this.shortcutAllowed = shortcutAllowed;
        storage.notify(this);
    }
}
