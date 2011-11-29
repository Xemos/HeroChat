package com.dthielke.herochat;

import com.dthielke.herochat.util.Messaging;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class StandardChatter implements Chatter {
    private final Player player;
    private Channel activeChannel;
    private Channel lastActiveChannel;
    private ChatterStorage storage;
    private Set<Channel> channels = new HashSet<Channel>();
    private Set<String> ignores = new HashSet<String>();
    private boolean muted = false;

    public StandardChatter(ChatterStorage storage, Player player) {
        this.storage = storage;
        this.player = player;
    }

    @Override
    public Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    public Set<Channel> getChannels() {
        return channels;
    }

    @Override
    public Set<String> getIgnores() {
        return ignores;
    }

    @Override
    public Channel getLastActiveChannel() {
        return lastActiveChannel;
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
    public ChatterStorage getStorage() {
        return storage;
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    @Override
    public void setMuted(boolean muted) {
        this.muted = muted;
        storage.flagUpdate(this);
    }

    @Override
    public boolean addChannel(Channel channel, boolean announce) {
        if (channels.contains(channel))
            return false;

        channels.add(channel);
        if (!channel.isMember(this)) {
            channel.addMember(this, announce);
        }

        storage.flagUpdate(this);

        return true;
    }

    @Override
    public void attachStorage(ChatterStorage storage) {
        this.storage = storage;
    }

    @Override
    public Result canBan(Channel channel) {
        if (player.hasPermission(Permission.BAN.form(channel)))
            return Result.ALLOWED;
        if (channel.isModerator(player.getName()) && HeroChat.getChannelManager().checkModPermission(Permission.BAN))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canJoin(Channel channel, String password) {
        if (channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.JOIN.form(channel)))
            return Result.NO_PERMISSION;

        if (channel.isBanned(player.getName()))
            return Result.BANNED;

        if (!password.equals(channel.getPassword()))
            return Result.BAD_PASSWORD;

        return Result.ALLOWED;
    }

    @Override
    public Result canKick(Channel channel) {
        if (player.hasPermission(Permission.KICK.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()) && HeroChat.getChannelManager().checkModPermission(Permission.KICK))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
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
    public Result canModify(String setting, Channel channel) {
        setting = setting.toLowerCase();
        Permission permission;

        if (setting.equals("nick")) {
            permission = Permission.MODIFY_NICK;
        } else if (setting.equals("format")) {
            permission = Permission.MODIFY_FORMAT;
        } else if (setting.equals("distance")) {
            permission = Permission.MODIFY_DISTANCE;
        } else if (setting.equals("color")) {
            permission = Permission.MODIFY_COLOR;
        } else if (setting.equals("shortcut")) {
            permission = Permission.MODIFY_SHORTCUT;
        } else if (setting.equals("password")) {
            permission = Permission.MODIFY_PASSWORD;
        } else {
            return Result.INVALID;
        }

        if (player.hasPermission(permission.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()) && HeroChat.getChannelManager().checkModPermission(permission))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canMute(Channel channel) {
        if (player.hasPermission(Permission.MUTE.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()) && HeroChat.getChannelManager().checkModPermission(Permission.BAN))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canRemove(Channel channel) {
        if (player.hasPermission(Permission.REMOVE.form(channel)))
            return Result.ALLOWED;

        if (channel.isModerator(player.getName()) && HeroChat.getChannelManager().checkModPermission(Permission.REMOVE))
            return Result.ALLOWED;

        return Result.NO_PERMISSION;
    }

    @Override
    public Result canSpeak(Channel channel) {
        if (!channel.isMember(this))
            return Result.INVALID;

        if (!player.hasPermission(Permission.SPEAK.form(channel)))
            return Result.NO_PERMISSION;

        if (muted || channel.isMuted(player.getName()))
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
    public boolean hasChannel(Channel channel) {
        return channels.contains(channel);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    @Override
    public boolean isIgnoring(String name) {
        return ignores.contains(name.toLowerCase());
    }

    @Override
    public boolean isInRange(Chatter other, int distance) {
        Player otherPlayer = other.getPlayer();
        boolean worldCheck = player.getWorld().equals(otherPlayer.getWorld());
        boolean distanceCheck = player.getLocation().distanceSquared(otherPlayer.getLocation()) <= distance * distance;
        return worldCheck && distanceCheck;
    }

    @Override
    public boolean removeChannel(Channel channel, boolean announce) {
        if (!channels.contains(channel))
            return false;

        channels.remove(channel);
        if (channel.isMember(this)) {
            channel.removeMember(this, announce);
        }

        storage.flagUpdate(this);

        return true;
    }

    @Override
    public void setActiveChannel(Channel channel, boolean announce) {
        if (channel.equals(activeChannel))
            return;

        if (activeChannel != null && !activeChannel.isTransient())
            lastActiveChannel = activeChannel;
        activeChannel = channel;

        if (announce) {
            Messaging.send(player, "Now chatting in $1.", channel.getColor() + channel.getName());
        }

        storage.flagUpdate(this);
    }

    @Override
    public void setIgnore(String name, boolean ignore) {
        if (ignore)
            ignores.add(name.toLowerCase());
        else
            ignores.remove(name.toLowerCase());
        storage.flagUpdate(this);
    }
}
