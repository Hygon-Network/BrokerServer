package fr.hygon.broker.packets;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerStatusPacket implements Packet {
    private final String server;
    private final Status status;

    public ServerStatusPacket(String server, Status status) {
        this.server = server;
        this.status = status;
    }


    public ServerStatusPacket(ByteBuf buffer) {
        int serverLength = buffer.readInt();
        server = (String) buffer.readCharSequence(serverLength, StandardCharsets.UTF_8);

        byte statusId = buffer.readByte();
        status = Arrays.stream(Status.values()).filter(statusIteration -> statusIteration.status == statusId).findFirst().orElse(Status.WAITING_PLAYERS);
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(server.length());
        out.writeCharSequence(server, StandardCharsets.UTF_8);

        out.writeByte(status.status);
    }

    @Override
    public void handle(PacketListener listener) {
        listener.handleServerStatusUpdate(this);
    }

    public enum Status {
        STARTING((byte) 0), WAITING_PLAYERS((byte) 1), IN_GAME((byte) 2), STOPPING((byte) 3);

        private final byte status;

        Status(byte status) {
            this.status = status;
        }
    }
}
