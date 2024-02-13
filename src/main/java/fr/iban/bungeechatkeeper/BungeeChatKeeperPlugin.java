package fr.iban.bungeechatkeeper;

import dev.simplix.protocolize.api.Protocolize;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BungeeChatKeeperPlugin extends Plugin implements Listener {

    private int MAX_MESSAGE_COUNT;
    private Map<UUID, BoundedList<BaseComponent>> chatHistory;
    private Configuration configuration;
    private DownstreamChatPacketListener downstreamChatPacketListener;
    private UpstreamChatPacketListener upstreamChatPacketListener;

    @Override
    public void onEnable() {
        chatHistory = new HashMap<>();
        saveDefaultConfig();
        loadConfig();

        MAX_MESSAGE_COUNT = configuration.getInt("max-message-count", 100);
        
        Protocolize.listenerProvider().registerListener(downstreamChatPacketListener = new DownstreamChatPacketListener(this));
        Protocolize.listenerProvider().registerListener(upstreamChatPacketListener = new UpstreamChatPacketListener(this));

        getProxy().getPluginManager().registerListener(this, new EventListener(this));
    }

    @Override
    public void onDisable() {
        Protocolize.listenerProvider().unregisterListener(downstreamChatPacketListener);
        Protocolize.listenerProvider().unregisterListener(upstreamChatPacketListener);
    }

    public BoundedList<BaseComponent> getChatHistory(UUID uuid) {
        return chatHistory.computeIfAbsent(uuid, k -> new BoundedList<>(MAX_MESSAGE_COUNT));
    }

    public Map<UUID, BoundedList<BaseComponent>> getChatHistory() {
        return chatHistory;
    }

    public void loadConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().severe("Error while loading config file.");
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().severe("Error while saving config file.");
        }
    }

    public void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                getLogger().severe("Error while saving default config file.");
            }
        }
    }

}
