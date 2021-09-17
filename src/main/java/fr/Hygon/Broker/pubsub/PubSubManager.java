package fr.Hygon.Broker.pubsub;

import fr.Hygon.Broker.Broker;
import io.netty.channel.ChannelHandlerContext;
import fr.Hygon.Broker.packets.MessagePacket;
import java.util.ArrayList;
import java.util.UUID;

public class PubSubManager {
    private static final ArrayList<PubSubClient> clients = new ArrayList<>();

    public static void registerClient(ChannelHandlerContext ctx, UUID uuid) {
        clients.add(new PubSubClient(ctx, uuid));
    }

    public static void registerChannel(UUID uuid, String channel) {
        for(PubSubClient pubSubClients : clients) {
            if(pubSubClients.getUUID().equals(uuid)) {
                pubSubClients.registerChannel(channel);
                break;
            }
        }
    }

    public static void unregisterChannel(UUID uuid, String channel) {
        for(PubSubClient pubSubClients : clients) {
            if(pubSubClients.getUUID().equals(uuid)) {
                pubSubClients.unregisterChannel(channel);
                break;
            }
        }
    }

    public static void diffuseMessage(String channel, byte[] message) {
        for(PubSubClient pubSubClients : clients) {
            if(pubSubClients.getChannels().contains(channel)) {
                Broker.writePacket(pubSubClients.getConnection(), new MessagePacket(channel, message));
            }
        }
    }

    protected static class PubSubClient {
        private final ChannelHandlerContext connection;
        private final UUID uuid;
        private final ArrayList<String> channels = new ArrayList<>();

        protected PubSubClient(ChannelHandlerContext connection, UUID uuid) {
            this.connection = connection;
            this.uuid = uuid;
        }

        public ChannelHandlerContext getConnection() {
            return connection;
        }

        public UUID getUUID() {
            return uuid;
        }

        public ArrayList<String> getChannels() {
            return channels;
        }

        public void registerChannel(String channel) {
            channels.add(channel);
        }

        public void unregisterChannel(String channel) {
            channels.remove(channel);
        }
    }
}
