package fr.Hygon.Broker.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public interface Packet {
    void read(ChannelHandlerContext ctx, UUID uuid, ByteBuf in);

    void write(ByteBuf out);
}
