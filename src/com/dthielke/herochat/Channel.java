package com.dthielke.herochat;

import java.util.Set;

import org.bukkit.World;

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

    public boolean addMember(Chatter chatter);

    public void addWorld(String world);

    public int getDistance();

    public Set<Chatter> getMembers();

    public String getName();

    public String getNick();

    public Set<String> getWorlds();

    public boolean hasWorld(String world);

    public boolean hasWorld(World world);

    public boolean isLocal();

    public boolean isMember(Chatter chatter);

    public boolean removeMember(Chatter chatter);

    public boolean isBanned(String name);

    public boolean isMuted(String name);

    public boolean isModerator(String name);

    public void setBanned(String name, boolean banned);

    public void setMuted(String name, boolean muted);

    public void setModerator(String name, boolean moderator);

    public void removeWorld(String world);

    public void setDistance(int distance);

    public void setName(String name);

    public void setNick(String nick);

    public String getFormat();

    public void setFormat(String format);

}
