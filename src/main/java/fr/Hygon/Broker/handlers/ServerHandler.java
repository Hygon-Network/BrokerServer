package fr.Hygon.Broker.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import fr.Hygon.Broker.packets.Packet;
import fr.Hygon.Broker.packets.Packets;
import java.util.UUID;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf packetBuf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        packetBuf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        packetBuf.release();
        packetBuf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        ByteBuf inBuffer = (ByteBuf) message;
        packetBuf.writeBytes(inBuffer);
        inBuffer.release();

        if(packetBuf.readableBytes() >= 4) {
            int packetSize = packetBuf.readInt();
            if(packetBuf.readableBytes() >= packetSize) {
                int packetID = packetBuf.readInt();
                Packet packet = Packets.getPacketByID(packetID);
                if(packet == null) {
                    System.err.println("Received unknown packet with id " + packetID);
                    return;
                }

                long mostSignificantBits = packetBuf.readLong();
                long leastSignificantBits = packetBuf.readLong();

                packet.read(ctx, new UUID(mostSignificantBits, leastSignificantBits), packetBuf);
                packetBuf = ctx.alloc().buffer(4);
            } else {
                packetBuf.resetReaderIndex();
                packetBuf.resetWriterIndex();
            }
        }
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
