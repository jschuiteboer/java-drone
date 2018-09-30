package schuitj.drone.lib.drone.cx10;

import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.net.CommandConnection;
import schuitj.drone.lib.drone.net.TransportConnection;
import schuitj.drone.lib.util.ByteUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class CX10DroneImpl implements CX10Drone, Closeable {
    private static final String HOST = "172.16.10.1";

    private static final int TRANSPORT_CONNECTION_PORT = 8888;
    private static final int VIDEO_STREAM_PORT = 8889;
    private static final int VIDEO_RECORD_PORT = 8890;
    private static final int COMMAND_CONNECTION_PORT = 8895;
    private static final int HEART_BEAT_SLEEP_MS = 5000;

    private TransportConnection transportConnection;
    private CommandConnection commandConnection;

    private HeartbeatThread heartbeatThread;

    private CX10Command lastCommand = new CX10Command();

    public void initTransportConnection() throws IOException {
        if(this.transportConnection != null) {
            return; // already started
        }

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

        this.heartbeatThread = new HeartbeatThread(transportConnection, this.loadBinaryResource("heartbeat.bin"), 106, HEART_BEAT_SLEEP_MS);
        this.heartbeatThread.start();

        // wait for first heartbeat
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void startCommandConnection() throws IOException {
        if(this.commandConnection != null) {
            return; // already started
        }

        this.initTransportConnection();

        this.commandConnection = new CommandConnection(HOST, COMMAND_CONNECTION_PORT);

        new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    log.info("sending command {}", lastCommand);
                    commandConnection.sendCommand(lastCommand);
                    Thread.sleep(50);
                } catch (InterruptedException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
    }

    @Override
    public void close() throws IOException {
        log.debug("stopping");

        if(this.heartbeatThread != null) {
            this.heartbeatThread.interrupt();
        }

        if(this.commandConnection != null) {
            this.commandConnection.close();
        }

        if(this.transportConnection != null) {
            this.transportConnection.close();
        }
    }

    @Override
    public void setThrottle(int amount) {
        this.lastCommand.setThrottle(amount);
    }

    @Override
    public void setPitch(int amount) {
        this.lastCommand.setPitch(amount);
    }

    @Override
    public void setYaw(int amount) {
        this.lastCommand.setYaw(amount);
    }

    @Override
    public void setRoll(int amount) {
        this.lastCommand.setRoll(amount);
    }

    @Override
    public void takeOff() {
        this.lastCommand.setTakeOff(true);
        this.lastCommand.setLand(false);
    }

    @Override
    public void land() {
        this.lastCommand.setTakeOff(false);
        this.lastCommand.setLand(true);
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
