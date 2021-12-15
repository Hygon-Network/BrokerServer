package fr.hygon.broker.handlers;

import fr.hygon.broker.packets.PacketListener;
import fr.hygon.broker.packets.Packets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import fr.hygon.broker.packets.Packet;
import java.util.ArrayList;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final ArrayList<ChannelHandlerContext> clients = new ArrayList<>();

    private ByteBuf byteBuffer;
    private final PacketListener packetListener = new PacketListenerImpl();
    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) {
        byteBuffer = channelHandlerContext.alloc().buffer(4);
        clients.add(channelHandlerContext);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        byteBuffer.release();
        byteBuffer = null;

        clients.remove(ctx);
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
                Packet packet = Packets.getPacketByID(packetID, packetBuf);
                if(packet == null) {
                    System.err.println("Received unknown packet with id " + packetID);
                    return;
                }

                packet.handle(packetListener);
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
    }

    public static ArrayList<ChannelHandlerContext> getClients() {
        return clients;
    }
}
