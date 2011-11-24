package com.dthielke.herochat;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Set;

public interface Channel {
    // NOTE TO FUTURE SELF:
    // You may be wondering why the member functions operate on chatter objects
    // while ban, mute, and moderator functions operate on strings. A player is
    // only an active member of a channel when they are online. This restriction
    // guarantees we will always have a chatter object available for each player
    // in a channel. Ban, mute and mod statuses, however, should be modifiable
    // regardless whether a player is online and therefore we don't have the
    // same guarantee. In this case, the best thing we have to work with are
    // player names.

    public boolean isTransient();

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

    public Set<Chatter> getMembers();

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

    public int getMinMembers();

    public int getMaxMembers();
}
