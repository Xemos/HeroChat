package com.dthielke.herochat;

import org.bukkit.ChatColor;

public interface ChannelStorage {
    public void addWorld(Channel channel, String world);

    public void removeWorld(Channel channel, String world);

    public void setBanned(Channel channel, String name, boolean banned);

    public void setColor(Channel channel, ChatColor color);

    public void setDistance(Channel channel, int distance);

    public void setFormat(Channel channel, String format);

    public void setModerator(Channel channel, String name, boolean moderator);

    public void setMuted(Channel channel, String name, boolean muted);

    public void setName(Channel channel, String name);

    public void setNick(Channel channel, String nick);

    public void setShortcutAllowed(Channel channel, boolean shortcutAllowed);
}
