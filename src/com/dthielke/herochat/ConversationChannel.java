package com.dthielke.herochat;

import com.dthielke.herochat.util.Messaging;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ConversationChannel extends StandardChannel {
    private static int channelCount = 0;

    public ConversationChannel(Chatter memberOne, Chatter memberTwo) {
        super(new ChannelStorage() {
            @Override
            public void addChannel(Channel channel) {}

            @Override
            public void flagUpdate(Channel channel) {}

            @Override
            public Channel load(String name) {
                return null;
            }

            @Override
            public Set<Channel> loadChannels() {
                return new HashSet<Channel>();
            }

            @Override
            public void removeChannel(Channel channel) {}

            @Override
            public void update() {}

            @Override
            public void update(Channel channel) {}
        }, "convo" + channelCount, "convo" + channelCount);
        addMember(memberOne, false);
        addMember(memberTwo, false);
        channelCount++;
    }

    @Override
    public boolean addMember(Chatter chatter, boolean announce) {
        return getMembers().size() < 2 && super.addMember(chatter, false);
    }

    public ConversationChannel(ChannelStorage storage, String name, String nick) {
        super(storage, name, nick);
    }

    @Override
    public void addWorld(String world) {}

    @Override
    public String applyFormat(String format, String originalFormat, Player sender) {
        format = super.applyFormat(format, originalFormat, sender);
        for (Chatter member : getMembers()) {
            if (!member.getName().equals(sender.getName())) {
                format = format.replace("#recipient", sender.getDisplayName());
            }
        }
        return format;
    }

    @Override
    public boolean banMember(Chatter chatter, boolean announce) {
        return false;
    }

    @Override
    public Set<String> getBans() {
        return new HashSet<String>();
    }

    @Override
    public int getDistance() {
        return 0;
    }

    @Override
    public int getMaxMembers() {
        return 2;
    }

    @Override
    public Set<String> getModerators() {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getMutes() {
        return new HashSet<String>();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public Set<String> getWorlds() {
        return new HashSet<String>();
    }

    @Override
    public boolean hasWorld(World world) {
        return true;
    }

    @Override
    public boolean isBanned(String name) {
        return false;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isModerator(String name) {
        return false;
    }

    @Override
    public boolean isMuted(String name) {
        return false;
    }

    @Override
    public boolean isShortcutAllowed() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public boolean kickMember(Chatter chatter, boolean announce) {
        return false;
    }

    @Override
    public boolean removeMember(Chatter chatter, boolean announce) {
        if (super.removeMember(chatter, false)) {
            int count = getMembers().size();
            if (count < getMinMembers() && count > 0) {
                Chatter otherMember = getMembers().iterator().next();
                removeMember(otherMember, false);
                otherMember.setActiveChannel(HeroChat.getChannelManager().getDefaultChannel());
                otherMember.setActiveChannel(otherMember.getLastActiveChannel());
                Messaging.send(otherMember.getPlayer(), "Now chatting in $1.", otherMember.getLastActiveChannel().getName());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getMinMembers() {
        return 2;
    }

    @Override
    public void removeWorld(String world) {}

    @Override
    public void setBanned(String name, boolean banned) {}

    @Override
    public void setBans(Set<String> bans) {}

    @Override
    public void setModerator(String name, boolean moderator) {}

    @Override
    public void setModerators(Set<String> moderators) {}

    @Override
    public void setMuted(String name, boolean muted) {}

    @Override
    public void setMutes(Set<String> mutes) {}

    @Override
    public void setNick(String nick) {}

    @Override
    public void setPassword(String password) {}

    @Override
    public void setShortcutAllowed(boolean shortcutAllowed) {}

    @Override
    public void setWorlds(Set<String> worlds) {}
}
