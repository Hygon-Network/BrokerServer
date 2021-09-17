package fr.Hygon.Broker.packets;

import fr.Hygon.Broker.pubsub.PubSubManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ChannelPacket implements Packet {
    @Override
    public void read(ChannelHandlerContext ctx, UUID uuid, ByteBuf in) {
        int action = in.readInt(); // 0 = register, 1 = unregister;
        int channelLength = in.readInt();
        String channel = (String) in.readCharSequence(channelLength, StandardCharsets.UTF_8);

        System.out.println("action: " + action + " channelength: " + channelLength + " channel: " + channel);
        switch (action) {
            case 0 -> PubSubManager.registerChannel(uuid, channel);
            case 1 -> PubSubManager.unregisterChannel(uuid, channel);
        }
    }

    @Override
    public void write(ByteBuf out) {
        
    }
}
