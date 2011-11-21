package com.dthielke.herochat;

import org.bukkit.entity.Player;

import java.util.Set;

public interface Chatter {
    public boolean addChannel(Channel channel, boolean announce);

    public void attachStorage(ChatterStorage storage);

    public Result canBan(Channel channel);

    public Result canJoin(Channel channel);

    public Result canKick(Channel channel);

    public Result canLeave(Channel channel);

    public Result canModify(String setting, Channel channel);

    public Result canMute(Channel channel);

    public Result canRemove(Channel channel);

    public Result canSpeak(Channel channel);

    public Channel getActiveChannel();

    public Set<Channel> getChannels();

    public Set<String> getIgnores();

    public String getName();

    public Player getPlayer();

    public ChatterStorage getStorage();

    boolean hasChannel(Channel channel);

    public boolean isIgnoring(String name);

    public boolean isInRange(Chatter other, int distance);

    public boolean removeChannel(Channel channel, boolean announce);

    public void setActiveChannel(Channel channel);

    public void setIgnore(String name, boolean ignore);

    public enum Permission {
        JOIN("join"),
        LEAVE("leave"),
        SPEAK("speak"),
        KICK("kick"),
        BAN("ban"),
        MUTE("mute"),
        REMOVE("remove"),
        MODIFY_NAME("modify.name"),
        MODIFY_NICK("modify.nick"),
        MODIFY_COLOR("modify.color"),
        MODIFY_DISTANCE("modify.distance"),
        MODIFY_FORMAT("modify.format"),
        MODIFY_SHORTCUT("modify.shortcut");

        private String name;

        private Permission(String name) {
            this.name = name;
        }

        public String form(Channel channel) {
            return "herochat." + name + "." + channel.getName().toLowerCase();
        }

        public String formWildcard() {
            return "herochat." + name + ".*";
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Result {
        NO_PERMISSION,
        INVALID,
        BANNED,
        MUTED,
        ALLOWED,
        BAD_WORLD
    }
}
