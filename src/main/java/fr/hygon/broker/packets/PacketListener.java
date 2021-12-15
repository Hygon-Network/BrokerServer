package fr.hygon.broker.packets;

public interface PacketListener {
    void handleVelocityKeyUpdate(VelocityKeyUpdatePacket velocityKeyUpdatePacket);

    void handleServerStatusUpdate(ServerStatusPacket serverStatusPacket);
}
