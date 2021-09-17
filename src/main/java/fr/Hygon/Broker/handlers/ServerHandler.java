package fr.Hygon.Broker.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import fr.Hygon.Broker.packets.Packet;
import fr.Hygon.Broker.packets.Packets;

import java.util.UUID;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf inBuffer = (ByteBuf) msg;

        int packetID = inBuffer.readInt();
        Packet packet = Packets.getPacketByID(packetID);
        if(packet == null) {
            System.err.println("Received unknown packet with id " + packetID);
            return;
        }

        long mostSignificantBits = inBuffer.readLong();
        long leastSignificantBits = inBuffer.readLong();

        UUID clientUUID = new UUID(mostSignificantBits, leastSignificantBits);
        packet.read(ctx, new UUID(mostSignificantBits, leastSignificantBits), inBuffer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
