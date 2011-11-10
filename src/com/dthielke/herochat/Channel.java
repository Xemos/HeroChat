package com.dthielke.herochat;

import java.util.Set;

import org.bukkit.World;

public interface Channel {

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

    public void removeWorld(String world);

    public void setDistance(int distance);

    public void setName(String name);

    public void setNick(String nick);

    public String getFormat();

    public void setFormat(String format);

}
