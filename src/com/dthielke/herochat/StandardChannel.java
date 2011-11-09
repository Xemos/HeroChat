package com.dthielke.herochat;

import java.util.HashSet;
import java.util.Set;

public class StandardChannel implements Channel {

    private String name;
    private String nick;
    private String format;
    private int distance;
    private Set<Chatter> members = new HashSet<Chatter>();
    private Set<String> worlds = new HashSet<String>();

    public StandardChannel(String name, String nick) {
        this.name = name;
        this.nick = nick;
        this.distance = 0;
        this.format = "[#nick] #sender: #msg";
    }

    @Override
    public boolean addMember(Chatter chatter) {
        if (members.contains(chatter))
            return false;

        members.add(chatter);
        if (!chatter.hasChannel(this)) {
            chatter.addChannel(this);
        }

        return true;
    }

    @Override
    public void addWorld(String world) {
        if (!worlds.contains(world)) {
            worlds.add(world);
        }
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
    public int getDistance() {
        return distance;
    }

    @Override
    public Set<Chatter> getMembers() {
        return members;
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
    public Set<String> getWorlds() {
        return worlds;
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
    public boolean hasWorld(String world) {
        return worlds.contains(world);
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
    public boolean removeMember(Chatter chatter) {
        if (!members.contains(chatter))
            return false;

        members.remove(chatter);
        if (chatter.hasChannel(this)) {
            chatter.removeChannel(this);
        }

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
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

}
