package com.dthielke.herochat;

import com.dthielke.herochat.util.Messaging;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardChannel implements Channel {
    public static final String ANNOUNCEMENT_FORMAT = "{color}[{nick}] {msg}";
    public static final String MESSAGE_FORMAT = "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}";
    private static final Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");

    private final String name;
    private String nick;
    private String format;
    private String password;
    private ChatColor color;
    private int distance;
    private boolean shortcutAllowed;
    private Set<Chatter> members = new HashSet<Chatter>();
    private Set<String> worlds = new HashSet<String>();
    private Set<String> bans = new HashSet<String>();
    private Set<String> mutes = new HashSet<String>();
    private Set<String> moderators = new HashSet<String>();
    private ChannelStorage storage;

    public StandardChannel(ChannelStorage storage, String name, String nick) {
        this.storage = storage;
        this.name = name;
        this.nick = nick;
        this.color = ChatColor.WHITE;
        this.distance = 0;
        this.shortcutAllowed = false;
        this.format = MESSAGE_FORMAT;
        this.password = "";
    }

    @Override
    public Set<String> getBans() {
        return bans;
    }

    @Override
    public void setBans(Set<String> bans) {
        this.bans = bans;
        storage.flagUpdate(this);
    }

    @Override
    public ChatColor getColor() {
        return color;
    }

    @Override
    public void setColor(ChatColor color) {
        this.color = color;
        storage.flagUpdate(this);
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public void setDistance(int distance) {
        this.distance = distance < 0 ? 0 : distance;
        storage.flagUpdate(this);
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
        storage.flagUpdate(this);
    }

    @Override
    public int getMaxMembers() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Set<Chatter> getMembers() {
        return members;
    }

    @Override
    public int getMinMembers() {
        return 0;
    }

    @Override
    public Set<String> getModerators() {
        return moderators;
    }

    @Override
    public void setModerators(Set<String> moderators) {
        this.moderators = moderators;
        storage.flagUpdate(this);
    }

    @Override
    public Set<String> getMutes() {
        return mutes;
    }

    @Override
    public void setMutes(Set<String> mutes) {
        this.mutes = mutes;
        storage.flagUpdate(this);
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
    public void setNick(String nick) {
        this.nick = nick;
        storage.flagUpdate(this);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        storage.flagUpdate(this);
    }

    @Override
    public ChannelStorage getStorage() {
        return storage;
    }

    @Override
    public Set<String> getWorlds() {
        return worlds;
    }

    @Override
    public void setWorlds(Set<String> worlds) {
        this.worlds = worlds;
        storage.flagUpdate(this);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isLocal() {
        return distance != 0;
    }

    @Override
    public boolean isShortcutAllowed() {
        return shortcutAllowed;
    }

    @Override
    public void setShortcutAllowed(boolean shortcutAllowed) {
        this.shortcutAllowed = shortcutAllowed;
        storage.flagUpdate(this);
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean addMember(Chatter chatter, boolean announce) {
        if (members.contains(chatter))
            return false;

        members.add(chatter);
        if (!chatter.hasChannel(this)) {
            chatter.addChannel(this, announce);
        }

        if (announce) {
            announce(chatter.getPlayer().getName() + " has joined the channel.");
        }

        return true;
    }

    @Override
    public void announce(String message) {
        message = applyFormat(ANNOUNCEMENT_FORMAT, "").replace("%2$s", message);
        for (Chatter member : members) {
            member.getPlayer().sendMessage(message);
        }
    }

    @Override
    public void addWorld(String world) {
        if (!worlds.contains(world)) {
            worlds.add(world);
            storage.flagUpdate(this);
        }
    }

    @Override
    public String applyFormat(String format, String originalFormat) {
        format = format.replace("{name}", name);
        format = format.replace("{nick}", nick);
        format = format.replace("{color}", color.toString());
        format = format.replace("{msg}", "%2$s");

        Matcher matcher = msgPattern.matcher(originalFormat);
        if (matcher.matches() && matcher.groupCount() == 3) {
            format = format.replace("{sender}", matcher.group(1) + matcher.group(2) + "%1$s" + matcher.group(3));
        } else {
            format = format.replace("{sender}", "%1$s");
        }

        format = format.replaceAll("&([0-9a-fA-F])", "\u00a7$1");
        return format;
    }

    @Override
    public void attachStorage(ChannelStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean banMember(Chatter chatter, boolean announce) {
        if (!members.contains(chatter))
            return false;

        if (announce) {
            announce(chatter.getPlayer().getName() + " has been banned.");
        }

        removeMember(chatter, false);
        setBanned(chatter.getPlayer().getName(), true);
        return true;
    }

    @Override
    public boolean removeMember(Chatter chatter, boolean announce) {
        if (!members.contains(chatter))
            return false;

        if (announce) {
            announce(chatter.getPlayer().getName() + " has left the channel.");
        }

        members.remove(chatter);
        if (chatter.hasChannel(this)) {
            chatter.removeChannel(this, announce);
        }

        return true;
    }

    @Override
    public void setBanned(String name, boolean banned) {
        if (banned)
            bans.add(name.toLowerCase());
        else
            bans.remove(name.toLowerCase());
        storage.flagUpdate(this);
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.toLowerCase().hashCode());
        result = prime * result + (nick == null ? 0 : nick.toLowerCase().hashCode());
        return result;
    }

    @Override
    public boolean hasWorld(String world) {
        return worlds.isEmpty() || worlds.contains(world);
    }

    @Override
    public boolean isBanned(String name) {
        return bans.contains(name.toLowerCase());
    }

    @Override
    public boolean isMember(Chatter chatter) {
        return members.contains(chatter);
    }

    @Override
    public boolean isModerator(String name) {
        return moderators.contains(name.toLowerCase());
    }

    @Override
    public boolean isMuted(String name) {
        return mutes.contains(name.toLowerCase());
    }

    @Override
    public boolean kickMember(Chatter chatter, boolean announce) {
        if (!members.contains(chatter))
            return false;

        if (announce) {
            announce(chatter.getPlayer().getName() + " has been kicked.");
        }

        removeMember(chatter, false);
        return true;
    }

    @Override
    public void processChat(PlayerChatEvent event) {
        final Player player = event.getPlayer();
        String senderName = player.getName();
        Chatter sender = HeroChat.getChatterManager().getChatter(player);

        // trim the recipient list
        Set<Player> recipients = event.getRecipients();
        for (Iterator<Player> iter = recipients.iterator(); iter.hasNext(); ) {
            Chatter recipient = HeroChat.getChatterManager().getChatter(iter.next());
            if (!members.contains(recipient)) {
                iter.remove();
            } else if (isLocal() && !sender.isInRange(recipient, distance)) {
                iter.remove();
            } else if (!hasWorld(recipient.getPlayer().getWorld())) {
                iter.remove();
            } else if (recipient.isIgnoring(senderName)) {
                iter.remove();
            }
        }

        if (isLocal()) {
            int visibleRecipients = 0;
            for (Player recipient : recipients) {
                if (!HeroChat.getPermissionService().has(player, "herochat.admin.stealth"))
                    visibleRecipients++;
            }

            if (visibleRecipients <= 1) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HeroChat.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        Messaging.send(player, "No one hears you.");
                    }
                }, 1L);
            }
        }

        event.setFormat(applyFormat(format, event.getFormat(), player));
    }

    @Override
    public boolean hasWorld(World world) {
        return worlds.isEmpty() || worlds.contains(world.getName());
    }

    @Override
    public String applyFormat(String format, String originalFormat, Player sender) {
        format = applyFormat(format, originalFormat);
        Chat chat = HeroChat.getChatService();
        format = format.replace("{prefix}", chat.getPlayerPrefix(sender));
        format = format.replace("{suffix}", chat.getPlayerSuffix(sender));
        format = format.replace("{group}", chat.getPrimaryGroup(sender));
        format = format.replace("{world}", sender.getWorld().toString());
        format = format.replace("&", "\u00a7");
        return format;
    }

    @Override
    public void removeWorld(String world) {
        if (worlds.contains(world)) {
            worlds.remove(world);
        }
    }

    @Override
    public void setModerator(String name, boolean moderator) {
        if (moderator)
            moderators.add(name.toLowerCase());
        else
            moderators.remove(name.toLowerCase());
        storage.flagUpdate(this);
    }

    @Override
    public void setMuted(String name, boolean muted) {
        if (muted)
            mutes.add(name.toLowerCase());
        else
            mutes.remove(name.toLowerCase());
        storage.flagUpdate(this);
    }
}
