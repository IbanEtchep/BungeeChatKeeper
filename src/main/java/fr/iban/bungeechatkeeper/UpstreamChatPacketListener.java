package fr.iban.bungeechatkeeper;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.protocol.packet.SystemChat;

public class UpstreamChatPacketListener extends AbstractPacketListener<SystemChat> {

    private final BungeeChatKeeperPlugin plugin;

    protected UpstreamChatPacketListener(BungeeChatKeeperPlugin plugin) {
        super(SystemChat.class, Direction.UPSTREAM, 0);
        this.plugin = plugin;
    }

    @Override
    public void packetReceive(PacketReceiveEvent<SystemChat> event) {
    }

    @Override
    public void packetSend(PacketSendEvent<SystemChat> event) {
        SystemChat packet = event.packet();
        boolean isActionBar = packet.getPosition() == ChatMessageType.ACTION_BAR.ordinal();

        if (event.player().protocolVersion() < ProtocolVersions.MINECRAFT_1_20_2 || isActionBar) {
            return;
        }

        plugin.getChatHistory(event.player().uniqueId()).add(packet.getMessage());
    }
}
