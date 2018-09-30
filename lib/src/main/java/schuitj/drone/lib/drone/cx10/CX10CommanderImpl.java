package schuitj.drone.lib.drone.cx10;

import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.net.CommandConnection;
import schuitj.drone.lib.drone.net.TransportConnection;
import schuitj.drone.lib.util.ByteUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class CX10CommanderImpl implements CX10Commander {
    private static final String HOST = "172.16.10.1";

    private static final int TRANSPORT_CONNECTION_PORT = 8888;
    private static final int VIDEO_STREAM_PORT = 8889;
    private static final int VIDEO_RECORD_PORT = 8890;
    private static final int COMMAND_CONNECTION_PORT = 8895;
    private static final int HEART_BEAT_SLEEP_MS = 5000;

    private TransportConnection transportConnection;
    private CommandConnection commandConnection;

    private TransportConnection getTransportConnection() throws IOException {
        if(this.transportConnection == null) {
            this.transportConnection = new TransportConnection(HOST, TRANSPORT_CONNECTION_PORT);

            log.debug("sending handshake");
            transportConnection.send(this.loadBinaryResource("handshake1.bin"));
            transportConnection.receive(106);
            transportConnection.send(this.loadBinaryResource("handshake2.bin"));
            transportConnection.receive(106);
            transportConnection.send(this.loadBinaryResource("handshake3.bin"));
            transportConnection.receive(170);
            transportConnection.send(this.loadBinaryResource("handshake4.bin"));
            transportConnection.receive(106);
            transportConnection.send(this.loadBinaryResource("handshake5.bin"));
            transportConnection.receive(106);

            log.debug("sending heartbeat");
            transportConnection.send(this.loadBinaryResource("heartbeat.bin"));
            transportConnection.receive(106);
        }

        return transportConnection;
    }

    private CommandConnection getCommandConnection() throws IOException {
        if(this.commandConnection == null) {
            this.getTransportConnection(); // init
            this.commandConnection = new CommandConnection(HOST, COMMAND_CONNECTION_PORT);
        }

        return this.commandConnection;
    }

    @Override
    public void close() throws IOException {
        if(this.transportConnection != null) {
            this.transportConnection.close();
        }
        if(this.commandConnection != null) {
            this.commandConnection.close();
        }
    }

    @Override
    public void takeOff() throws IOException {
        CX10Command command = new CX10Command();
        command.setTakeOff(true);

        this.getCommandConnection().sendCommand(command);
    }

    @Override
    public void land() throws IOException {
        CX10Command command = new CX10Command();
        command.setLand(true);

        this.getCommandConnection().sendCommand(command);
    }

    @Override
    public void startVideo() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void stopVideo() {
        throw new IllegalStateException("not implemented");
    }

    private byte[] loadBinaryResource(String name) throws IOException {
        try(InputStream inputStream = this.getClass().getResourceAsStream(name)) {
            Objects.requireNonNull(inputStream, "resource not found '" + name + "'");
            return ByteUtils.readAll(inputStream);
        }
    }
}
