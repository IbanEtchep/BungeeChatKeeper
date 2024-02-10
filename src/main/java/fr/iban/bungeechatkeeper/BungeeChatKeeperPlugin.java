package fr.iban.bungeechatkeeper;

import dev.simplix.protocolize.api.Protocolize;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BungeeChatKeeperPlugin extends Plugin implements Listener {

    private Map<UUID, BoundedList<BaseComponent>> chatHistory;

    private DownstreamChatPacketListener downstreamChatPacketListener;
    private UpstreamChatPacketListener upstreamChatPacketListener;

    @Override
    public void onEnable() {
        chatHistory = new HashMap<>();
        Protocolize.listenerProvider().registerListener(downstreamChatPacketListener = new DownstreamChatPacketListener(this));
        Protocolize.listenerProvider().registerListener(upstreamChatPacketListener = new UpstreamChatPacketListener(this));
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        Protocolize.listenerProvider().unregisterListener(downstreamChatPacketListener);
        Protocolize.listenerProvider().unregisterListener(upstreamChatPacketListener);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        chatHistory.remove(player.getUniqueId());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        BoundedList<BaseComponent> history = getChatHistory(player.getUniqueId());

        for (BaseComponent component : history) {
            player.sendMessage(component);
        }
    }

    public BoundedList<BaseComponent> getChatHistory(UUID uuid) {
        return chatHistory.computeIfAbsent(uuid, k -> new BoundedList<>(50));
    }
}
