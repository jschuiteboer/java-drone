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
    private static final int HEART_BEAT_RESPONSE_SIZE = 106;

    private final CX10Command command;
    private final Thread commandThread;
    private final byte[] heartbeatPacket;
    private final Thread heartbeatThread;

    private TransportConnection transportConnection;
    private CommandConnection commandConnection;
    private boolean sendCommand;

    public CX10DroneImpl() throws IOException {
        this.command = new CX10Command();

        this.heartbeatPacket = this.loadBinaryResource("heartbeat.bin");

        this.heartbeatThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    log.debug("sending heartbeat");
                    this.transportConnection.send(this.heartbeatPacket);
                    this.transportConnection.receive(HEART_BEAT_RESPONSE_SIZE);

                    Thread.sleep(HEART_BEAT_SLEEP_MS);
                } catch (InterruptedException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }, "cx10 heartbeat");
        this.heartbeatThread.setDaemon(true);

        this.commandThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    if(sendCommand) {
                        log.info("sending command {}", command);
                        commandConnection.sendCommand(command);
                        sendCommand = false;
                    }
                    Thread.sleep(50);
                } catch (InterruptedException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }, "cx10 command");
        this.commandThread.setDaemon(true);
    }

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

        this.commandThread.start();
    }

    @Override
    public void close() throws IOException {
        log.debug("stopping");

        if(this.commandConnection != null) {
            this.commandConnection.close();
        }

        if(this.transportConnection != null) {
            this.transportConnection.close();
        }
    }

    @Override
    public void setThrottle(float amount) {
        this.command.setThrottle(amount);
        this.sendCommand = true;
    }

    @Override
    public void setPitch(float amount) {
        this.command.setPitch(amount);
        this.sendCommand = true;
    }

    @Override
    public void setYaw(float amount) {
        this.command.setYaw(amount);
        this.sendCommand = true;
    }

    @Override
    public void setRoll(float amount) {
        this.command.setRoll(amount);
        this.sendCommand = true;
    }

    @Override
    public void takeOff() {
        this.command.setTakeOff(true);
        this.command.setLand(false);
        this.sendCommand = true;
    }

    @Override
    public void land() {
        this.command.setTakeOff(false);
        this.command.setLand(true);
        this.sendCommand = true;
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
