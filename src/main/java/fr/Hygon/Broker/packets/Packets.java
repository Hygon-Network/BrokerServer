package fr.Hygon.Broker.packets;

public enum Packets {
    REGISTER_CLIENT_PACKET(0, RegisterClientPacket.class),
    CHANNEL_PACKET(1, ChannelPacket.class),
    MESSAGE_PACKET(2, MessagePacket.class);

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

    public static Packet getPacketByID(int id) {
        for(Packets packets : Packets.values()) {
            if(packets.packetID == id) {
                try {
                    return packets.getPacket().getConstructor().newInstance();
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