package fr.hygon.broker.packets;

import io.netty.buffer.ByteBuf;

public class VelocityKeyUpdatePacket implements Packet {
    private final byte[] velocityKey;

    public VelocityKeyUpdatePacket(byte[] velocityKey) {
        this.velocityKey = velocityKey;
    }

    public VelocityKeyUpdatePacket(ByteBuf buffer) {
        int keySize = buffer.readInt();
        velocityKey = new byte[keySize];
        buffer.readBytes(velocityKey);
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(velocityKey.length);
        buffer.writeBytes(velocityKey);
    }

    @Override
    public void handle(PacketListener listener) {
        listener.handleVelocityKeyUpdate(this);
    }

    public byte[] getVelocityKey() {
        return velocityKey;
    }
}
