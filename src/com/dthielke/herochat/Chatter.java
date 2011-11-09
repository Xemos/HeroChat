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

    public Result canMute(Channel channel);

    public Result canSpeak(Channel channel);

    public Channel getActiveChannel();

    public Set<Channel> getBans();

    public Set<Channel> getChannels();

    public Set<Channel> getMutes();

    public String getName();

    public Player getPlayer();
    
    public boolean isInRange(Chatter other, int distance);

    public boolean isBanned(Channel channel);

    public boolean isMuted(Channel channel);

    public boolean removeChannel(Channel channel);

    public boolean setActiveChannel(Channel channel);

    public void setBanned(Channel channel, boolean banned);

    public void setMuted(Channel channel, boolean muted);

    boolean hasChannel(Channel channel);

    public enum Result {
        NO_PERMISSION,
        INVALID,
        BANNED,
        MUTED,
        ALLOWED;
    }
    
    public enum Permission {
        JOIN("join"),
        LEAVE("leave"),
        SPEAK("speak"),
        KICK("kick"),
        BAN("ban"),
        MUTE("mute");

        private static Map<Permission, String> names;
        private String name;

        private Permission(String name) {
            this.name = name;
        }

        public String form(Channel channel) {
            return "herochat." + name + "." + channel.getName().toLowerCase();
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

}
