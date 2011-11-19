package com.dthielke.herochat;

public interface ChannelStorage {
    public void addChannel(Channel channel);

    public Channel load(String name);

    public void notify(Channel channel);

    public void removeChannel(Channel channel);

    public void update();

    public void update(Channel channel);
}
