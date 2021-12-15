package fr.hygon.broker.packets;

import io.netty.buffer.ByteBuf;

public enum Packets {
    VELOCITY_UPDATE_KEY(0, VelocityKeyUpdatePacket.class),
    SERVER_STATUS_UPDATE(1, ServerStatusPacket.class);

    private final int packetID;
    private final Class<? extends Packet> packet;

    Packets(int packetID, Class<? extends Packet> packet) {
        this.packetID = packetID;
        this.packet = packet;
    }

    public int getPacketID() {
        return packetID;
    }

    public Class<? extends Packet> getPacket() {
        return packet;
    }

    public static Packet getPacketByID(int id, ByteBuf packetContent) {
        for(Packets packets : Packets.values()) {
            if(packets.packetID == id) {
                try {
                    return packets.getPacket().getConstructor(new Class[] {ByteBuf.class}).newInstance(packetContent);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    public static int getIDByPacket(Packet packet) {
        for(Packets packets : Packets.values()) {
            if(packets.getPacket().equals(packet.getClass())) {
                return packets.getPacketID();
            }
        }

        return -1;
    }
}