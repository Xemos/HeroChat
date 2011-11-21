package com.dthielke.herochat;

import java.util.Set;

public interface ChannelStorage {
    public void addChannel(Channel channel);

    public void flagUpdate(Channel channel);

    public Channel load(String name);

    public Set<Channel> loadChannels();

    public void removeChannel(Channel channel);

    public void update();

    public void update(Channel channel);
}
