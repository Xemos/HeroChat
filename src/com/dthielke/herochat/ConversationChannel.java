package com.dthielke.herochat;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

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
        }, "convo" + memberOne.getName() + memberTwo.getName(), "convo" + memberTwo.getName() + memberOne.getName());
        addMember(memberOne, false);
        addMember(memberTwo, false);
        setFormat("&d#convoaddress #convopartner&d: #msg");
        channelCount++;
    }

    @Override
    public boolean addMember(Chatter chatter, boolean announce) {
        return getMembers().size() < getMinMembers() && super.addMember(chatter, false);
    }

    public ConversationChannel(ChannelStorage storage, String name, String nick) {
        super(storage, name, nick);
    }

    @Override
    public Set<String> getBans() {
        return new HashSet<String>();
    }

    @Override
    public void setBans(Set<String> bans) {}

    @Override
    public int getDistance() {
        return 0;
    }

    @Override
    public int getMaxMembers() {
        return 2;
    }

    @Override
    public int getMinMembers() {
        return 2;
    }

    @Override
    public Set<String> getModerators() {
        return new HashSet<String>();
    }

    @Override
    public void setModerators(Set<String> moderators) {}

    @Override
    public Set<String> getMutes() {
        return new HashSet<String>();
    }

    @Override
    public void setMutes(Set<String> mutes) {}

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public void setPassword(String password) {}

    @Override
    public Set<String> getWorlds() {
        return new HashSet<String>();
    }

    @Override
    public void setWorlds(Set<String> worlds) {}

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isShortcutAllowed() {
        return false;
    }

    @Override
    public void setShortcutAllowed(boolean shortcutAllowed) {}

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public void setNick(String nick) {}

    @Override
    public void addWorld(String world) {}

    @Override
    public boolean banMember(Chatter chatter, boolean announce) {
        return false;
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
    public boolean isModerator(String name) {
        return false;
    }

    @Override
    public boolean isMuted(String name) {
        return false;
    }

    @Override
    public boolean kickMember(Chatter chatter, boolean announce) {
        return false;
    }

    @Override
    public void processChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String senderName = player.getName();
        Chatter sender = HeroChat.getChatterManager().getChatter(player);

        event.setCancelled(true);

        String format = getFormat();
        format = format.replace("#msg", event.getMessage());

        for (Chatter member : getMembers()) {
            if (!member.isIgnoring(senderName)) {
                Player memberPlayer = member.getPlayer();
                memberPlayer.sendMessage(applyFormat(format, event.getFormat(), player, memberPlayer));
            }
        }
    }

    public String applyFormat(String format, String originalFormat, Player sender, Player recipient) {
        format = super.applyFormat(format, originalFormat, sender);
        if (sender.equals(recipient)) {
            format = format.replace("#convoaddress", "To");
            for (Chatter member : getMembers()) {
                if (!member.getPlayer().equals(sender)) {
                    format = format.replace("#convopartner", member.getPlayer().getName());
                    break;
                }
            }
        } else {
            format = format.replace("#convoaddress", "From");
            format = format.replace("#convopartner", sender.getDisplayName());
        }
        return format;
    }

    @Override
    public boolean removeMember(Chatter chatter, boolean announce) {
        if (super.removeMember(chatter, false)) {
            int count = getMembers().size();
            if (count < getMinMembers() && count > 0) {
                Chatter otherMember = getMembers().iterator().next();
                removeMember(otherMember, false);
                if (otherMember.getActiveChannel().equals(this)) {
                    otherMember.setActiveChannel(otherMember.getLastActiveChannel(), true);
                }
            }
            HeroChat.getChannelManager().removeChannel(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeWorld(String world) {}

    @Override
    public void setBanned(String name, boolean banned) {}

    @Override
    public void setModerator(String name, boolean moderator) {}

    @Override
    public void setMuted(String name, boolean muted) {}
}
