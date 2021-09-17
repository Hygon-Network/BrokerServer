package fr.Hygon.Broker.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import fr.Hygon.Broker.pubsub.PubSubManager;

import java.util.UUID;

public class RegisterClientPacket implements Packet {
    @Override
    public void read(ChannelHandlerContext ctx, UUID uuid, ByteBuf in) {
        PubSubManager.registerClient(ctx, uuid);
    }

    @Override
    public void write(ByteBuf out) {
    }
}
