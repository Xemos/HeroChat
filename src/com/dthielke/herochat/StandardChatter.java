package com.dthielke.herochat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class StandardChatter implements Chatter {

    private final Player player;
    private Set<Channel> channels = new HashSet<Channel>();
    private Set<Channel> bans = new HashSet<Channel>();
    private Set<Channel> mutes = new HashSet<Channel>();
    private Channel activeChannel;

    public StandardChatter(Player player) {
        this.player = player;
    }

    @Override
    public boolean addChannel(Channel channel) {
        if (channels.contains(channel))
            return false;

        channels.add(channel);
        if (!channel.isMember(this)) {
            channel.addMember(this);
        }

        return true;
    }

    @Override
    public Result canBan(Channel channel) {
        if (!player.hasPermission(Permission.BAN.form(channel)))
            return Result.NO_PERMISSION;

        return Result.ALLOWED;
    }

    @Override
    public Result canJoin(Channel channel) {
        if (channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.JOIN.form(channel)))
            return Result.NO_PERMISSION;

        if (isBanned(channel))
            return Result.BANNED;

        return Result.ALLOWED;
    }

    @Override
    public Result canKick(Channel channel) {
        if (!player.hasPermission(Permission.KICK.form(channel)))
            return Result.NO_PERMISSION;

        return Result.ALLOWED;
    }

    @Override
    public Result canLeave(Channel channel) {
        if (!channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.LEAVE.form(channel)))
            return Result.NO_PERMISSION;

        return Result.ALLOWED;
    }

    @Override
    public Result canMute(Channel channel) {
        if (!player.hasPermission(Permission.MUTE.form(channel)))
            return Result.NO_PERMISSION;

        return Result.ALLOWED;
    }

    @Override
    public Result canSpeak(Channel channel) {
        if (!channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.SPEAK.form(channel)))
            return Result.NO_PERMISSION;

        if (isMuted(channel))
            return Result.MUTED;

        if (!channel.hasWorld(player.getWorld()))
            return Result.BAD_WORLD;

        return Result.ALLOWED;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;

        if (other == null)
            return false;

        if (!(other instanceof Chatter))
            return false;

        return player.equals(((Chatter) other).getPlayer());
    }

    @Override
    public Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    public Set<Channel> getBans() {
        return bans;
    }

    @Override
    public Set<Channel> getChannels() {
        return channels;
    }

    @Override
    public Set<Channel> getMutes() {
        return mutes;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean hasChannel(Channel channel) {
        return channels.contains(channel);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    @Override
    public boolean isBanned(Channel channel) {
        return bans.contains(channel);
    }

    @Override
    public boolean isMuted(Channel channel) {
        return mutes.contains(channel);
    }

    @Override
    public boolean removeChannel(Channel channel) {
        if (!channels.contains(channel))
            return false;

        channels.remove(channel);
        if (channel.isMember(this)) {
            channel.removeMember(this);
        }

        return true;
    }

    @Override
    public boolean setActiveChannel(Channel channel) {
        if (!channels.contains(channel))
            return false;

        activeChannel = channel;
        return true;
    }

    @Override
    public void setBanned(Channel channel, boolean banned) {
        if (banned) {
            if (!bans.contains(channel)) {
                bans.add(channel);
            }
        } else {
            if (bans.contains(channel)) {
                bans.remove(channel);
            }
        }
    }

    @Override
    public void setMuted(Channel channel, boolean muted) {
        if (muted) {
            if (!mutes.contains(channel)) {
                mutes.add(channel);
            }
        } else {
            if (mutes.contains(channel)) {
                mutes.remove(channel);
            }
        }
    }

    @Override
    public boolean isInRange(Chatter other, int distance) {
        return player.getLocation().distanceSquared(other.getPlayer().getLocation()) <= distance;
    }

}
