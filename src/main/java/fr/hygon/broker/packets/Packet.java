package fr.hygon.broker.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface Packet {
    void write(ByteBuf out);

    void handle(PacketListener listener);
}
