package fr.Hygon.Broker.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import fr.Hygon.Broker.pubsub.PubSubManager;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MessagePacket implements Packet {
    private final String channel;
    private final byte[] message;

    public MessagePacket(String channel, byte[] message) {
        this.channel = channel;
        this.message = message;
    }

    public MessagePacket() {
        channel = null;
        message = null;
    }

    @Override
    public void read(ChannelHandlerContext ctx, UUID uuid, ByteBuf in) {
        int channelLength = in.readInt();
        String channel = (String) in.readCharSequence(channelLength, StandardCharsets.UTF_8);
        byte[] message = new byte[in.readableBytes()];
        in.readBytes(message);

        PubSubManager.diffuseMessage(channel, message);
    }

    @Override
    public void write(ByteBuf out) {
        if(channel == null) {
            throw new NullPointerException("Channel cannot be null");
        }
        if(message == null) {
            throw new NullPointerException("Message cannot be null");
        }

        out.writeInt(channel.length());
        out.writeCharSequence(channel, StandardCharsets.UTF_8);
        out.writeBytes(Unpooled.wrappedBuffer(message));
    }
}
