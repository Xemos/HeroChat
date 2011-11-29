package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Set;

public interface Channel {
    public boolean addMember(Chatter chatter, boolean announce);

    public void addWorld(String world);

    public void announce(String message);

    public String applyFormat(String format, String originalFormat);

    public String applyFormat(String format, String originalFormat, Player sender);

    public void attachStorage(ChannelStorage storage);

    public boolean banMember(Chatter chatter, boolean announce);

    public Set<String> getBans();

    public ChatColor getColor();

    public int getDistance();

    public String getFormat();

    public int getMaxMembers();

    public Set<Chatter> getMembers();

    public int getMinMembers();

    public Set<String> getModerators();

    public Set<String> getMutes();

    public String getName();

    public String getNick();

    public String getPassword();

    public ChannelStorage getStorage();

    public Set<String> getWorlds();

    public boolean hasWorld(String world);

    public boolean hasWorld(World world);

    public boolean isBanned(String name);

    public boolean isHidden();

    public boolean isLocal();

    public boolean isMember(Chatter chatter);

    public boolean isModerator(String name);

    public boolean isMuted(String name);

    public boolean isShortcutAllowed();

    public boolean isTransient();

    public boolean kickMember(Chatter chatter, boolean announce);

    public void processChat(PlayerChatEvent event);

    public boolean removeMember(Chatter chatter, boolean announce);

    public void removeWorld(String world);

    public void setBanned(String name, boolean banned);

    public void setBans(Set<String> bans);

    public void setColor(ChatColor color);

    public void setDistance(int distance);

    public void setFormat(String format);

    public void setModerator(String name, boolean moderator);

    public void setModerators(Set<String> moderators);

    public void setMuted(String name, boolean muted);

    public void setMutes(Set<String> mutes);

    public void setNick(String nick);

    public void setPassword(String password);

    public void setShortcutAllowed(boolean shortcutAllowed);

    public void setWorlds(Set<String> worlds);
}
