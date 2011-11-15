package com.dthielke.herochat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

public interface Chatter {

    public boolean addChannel(Channel channel);

    public Result canBan(Channel channel);

    public Result canJoin(Channel channel);

    public Result canKick(Channel channel);

    public Result canLeave(Channel channel);

    public Result canModify(Channel channel);

    public Result canMute(Channel channel);

    public Result canSpeak(Channel channel);

    public Channel getActiveChannel();

    public Set<Channel> getChannels();

    public String getName();

    public Player getPlayer();

    public boolean isInRange(Chatter other, int distance);

    public boolean removeChannel(Channel channel);

    public boolean setActiveChannel(Channel channel);

    boolean hasChannel(Channel channel);

    public enum Permission {
        JOIN("join"),
        LEAVE("leave"),
        SPEAK("speak"),
        KICK("kick"),
        BAN("ban"),
        MUTE("mute"),
        MODIFY("modify");

        private static Map<Permission, String> names;
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

        static {
            names = new HashMap<Permission, String>();
            for (Permission permission : Permission.values()) {
                names.put(permission, permission.name);
            }
        }
    }

    public enum Result {
        NO_PERMISSION,
        INVALID,
        BANNED,
        MUTED,
        ALLOWED,
        BAD_WORLD;
    }

}
