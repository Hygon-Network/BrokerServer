package fr.hygon.broker.handlers;

import fr.hygon.broker.Broker;
import fr.hygon.broker.packets.Packet;
import fr.hygon.broker.packets.PacketListener;
import fr.hygon.broker.packets.ServerStatusPacket;
import fr.hygon.broker.packets.VelocityKeyUpdatePacket;

public class PacketListenerImpl implements PacketListener {
    @Override
    public void handleVelocityKeyUpdate(VelocityKeyUpdatePacket velocityKeyUpdatePacket) {
        transmitSamePacket(velocityKeyUpdatePacket);
    }

    @Override
    public void handleServerStatusUpdate(ServerStatusPacket serverStatusPacket) {
        transmitSamePacket(serverStatusPacket);
    }

    private void transmitSamePacket(Packet packet) {
        ServerHandler.getClients().forEach(channelHandlerContext -> Broker.writePacket(channelHandlerContext, packet));
    }
}
