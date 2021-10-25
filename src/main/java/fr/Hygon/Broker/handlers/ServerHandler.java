package fr.Hygon.Broker.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import fr.Hygon.Broker.packets.Packet;
import fr.Hygon.Broker.packets.Packets;
import java.util.UUID;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf byteBuffer;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        byteBuffer = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        byteBuffer.release();
        byteBuffer = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        ByteBuf inBuffer = (ByteBuf) message;
        byteBuffer.writeBytes(inBuffer);
        inBuffer.release();

        if(byteBuffer.readableBytes() >= 4) {
            int packetSize = byteBuffer.readInt();
            if(byteBuffer.readableBytes() >= packetSize) {
                ByteBuf packetBuf = ctx.alloc().buffer(packetSize);
                byteBuffer.readBytes(packetBuf);

                int packetID = packetBuf.readInt();
                Packet packet = Packets.getPacketByID(packetID);
                if(packet == null) {
                    System.err.println("Received unknown packet with id " + packetID);
                    return;
                }

                long mostSignificantBits = packetBuf.readLong();
                long leastSignificantBits = packetBuf.readLong();

                packet.read(ctx, new UUID(mostSignificantBits, leastSignificantBits), packetBuf);
            } else {
                byteBuffer.resetReaderIndex();
                byteBuffer.resetWriterIndex();
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
