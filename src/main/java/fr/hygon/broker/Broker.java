package fr.hygon.broker;

import fr.hygon.broker.packets.Packet;
import fr.hygon.broker.packets.Packets;
import fr.hygon.broker.handlers.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class Broker {
    private final int port;

    public static void main(String[] args) throws Exception {
        int port = 9800;

        for(int array = 0; array < args.length; array++) {
            if(args[array].equalsIgnoreCase("--help")) {
                System.out.println("Help:\n        --port - Define the port");
                System.exit(0);
            }
            if(args[array].equalsIgnoreCase("--port")) {
                try {
                    port = Integer.parseInt(args[array + 1]);
                } catch (Exception exception) {
                    System.err.println("Port is not a valid number.");
                    System.exit(1);
                }
            }
        }

        new Broker(port).run();
    }

    public Broker(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress("localhost", port));

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new ServerHandler());
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void writePacket(ChannelHandlerContext channel, Packet packet) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(Packets.getIDByPacket(packet));
        packet.write(byteBuf);

        byte[] packetBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(packetBytes);

        byteBuf.resetReaderIndex();
        byteBuf.resetWriterIndex();

        byteBuf.writeInt(packetBytes.length);
        byteBuf.writeBytes(packetBytes);

        channel.writeAndFlush(byteBuf);
    }
}
