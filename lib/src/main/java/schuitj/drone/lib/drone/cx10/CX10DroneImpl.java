package schuitj.drone.lib.drone.cx10;

import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.net.CommandConnection;
import schuitj.drone.lib.drone.net.CommandThread;
import schuitj.drone.lib.drone.net.HeartbeatThread;
import schuitj.drone.lib.drone.net.TransportConnection;
import schuitj.drone.lib.util.ByteUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

// TODO: split this class up into smaller parts
@Slf4j
public class CX10DroneImpl implements CX10Drone, Closeable {
    private static final String HOST = "172.16.10.1";
    private static final int TRANSPORT_CONNECTION_PORT = 8888;
    private static final int VIDEO_STREAM_PORT = 8889;
    private static final int VIDEO_RECORD_PORT = 8890;
    private static final int COMMAND_CONNECTION_PORT = 8895;
    private static final int COMMAND_SLEEP_MS = 50;
    private static final int HEART_BEAT_SLEEP_MS = 5000;
    private static final int HEART_BEAT_RESPONSE_SIZE = 106;

    private final CX10Command aggregateCommand = new CX10Command();

    private CommandConnection commandConnection;
    private CommandThread commandThread;

    private TransportConnection transportConnection;
    private HeartbeatThread heartbeatThread;

    public void startTransportConnection() throws IOException {
        if(this.transportConnection != null) {
            throw new IllegalStateException("already started");
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

        byte[] heartbeatPacket = this.loadBinaryResource("heartbeat.bin");
        this.heartbeatThread = new HeartbeatThread(transportConnection, heartbeatPacket, HEART_BEAT_RESPONSE_SIZE, HEART_BEAT_SLEEP_MS);
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
            throw new IllegalStateException("already started");
        }

        this.startTransportConnection();

        this.commandConnection = new CommandConnection(HOST, COMMAND_CONNECTION_PORT);

        this.commandThread = new CommandThread(commandConnection, COMMAND_SLEEP_MS);
        this.commandThread.start();
    }

    @Override
    public void close() throws IOException {
        log.debug("stopping");

        if(this.heartbeatThread != null && this.heartbeatThread.isAlive()) {
            this.heartbeatThread.interrupt();
        }

        if(this.transportConnection != null) {
            this.transportConnection.close();
        }

        if(this.commandThread != null && this.commandThread.isAlive()) {
            this.commandThread.interrupt();
        }

        if(this.commandConnection != null) {
            this.commandConnection.close();
        }
    }

    @Override
    public void setThrottle(float amount) {
        if(amount > 1) amount = 1;
        if(amount < -1) amount = -1;

        aggregateCommand.setThrottle(amount);

        this.commandThread.setNextCommand(aggregateCommand);
    }

    @Override
    public void setPitch(float amount) {
        if(amount > 1) amount = 1;
        if(amount < -1) amount = -1;

        aggregateCommand.setPitch(amount);

        this.commandThread.setNextCommand(aggregateCommand);
    }

    @Override
    public void setYaw(float amount) {
        if(amount > 1) amount = 1;
        if(amount < -1) amount = -1;

        aggregateCommand.setYaw(amount);

        this.commandThread.setNextCommand(aggregateCommand);
    }

    @Override
    public void setRoll(float amount) {
        if(amount > 1) amount = 1;
        if(amount < -1) amount = -1;

        aggregateCommand.setRoll(amount);

        this.commandThread.setNextCommand(aggregateCommand);
    }

    @Override
    public void takeOff() {
        aggregateCommand.setTakeOff(true);
        aggregateCommand.setLand(false);

        this.commandThread.setNextCommand(aggregateCommand);
    }

    @Override
    public void land() {
        aggregateCommand.setLand(true);
        aggregateCommand.setTakeOff(false);

        this.commandThread.setNextCommand(aggregateCommand);
    }

    @Override
    public void startVideo() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void stopVideo() {
        throw new IllegalStateException("not implemented");
    }

    private byte[] loadBinaryResource(String name) {
        try(InputStream inputStream = this.getClass().getResourceAsStream(name)) {
            Objects.requireNonNull(inputStream, "resource not found '" + name + "'");
            return ByteUtils.readAll(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("unable to load resource '" + name + "'", ex);
        }
    }
}
