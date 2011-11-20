package com.dthielke.herochat;

public interface ChatterStorage {
    public void addChatter(Chatter chatter);

    public Chatter load(String name);

    public void notify(Chatter chatter);

    public void removeChatter(Chatter chatter);

    public void update();

    public void update(Chatter chatter);
}
