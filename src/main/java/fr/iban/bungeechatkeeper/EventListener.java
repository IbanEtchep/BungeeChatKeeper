package fr.iban.bungeechatkeeper;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements Listener {

    private final BungeeChatKeeperPlugin plugin;

    public EventListener(BungeeChatKeeperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.getChatHistory().remove(player.getUniqueId());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        BoundedList<BaseComponent> history = plugin.getChatHistory(player.getUniqueId());

        for (BaseComponent component : history) {
            player.sendMessage(component);
        }
    }

}
