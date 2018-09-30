package schuitj.drone.lib.drone.cx10;

import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.net.TransportConnection;
import java.io.IOException;

@Slf4j
public class HeartbeatThread extends Thread {
    private final TransportConnection transportConnection;

    private final byte[] heartbeatPacket;

    private final int responseSize;

    private final int sleepTime;

    public HeartbeatThread(TransportConnection transportConnection, byte[] heartbeatPacket, int responseSize, int sleepTime) {
        this.transportConnection = transportConnection;
        this.heartbeatPacket = heartbeatPacket;
        this.responseSize = responseSize;
        this.sleepTime = sleepTime;

        this.setName("heartbeat");
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                sendHeartBeat();
                Thread.sleep(this.sleepTime);
            } catch (IOException | InterruptedException ex) {
                log.error("Unable to send heartbeat", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    private void sendHeartBeat() throws IOException {
        log.debug("sending heartbeat");

        this.transportConnection.send(this.heartbeatPacket);
        this.transportConnection.receive(this.responseSize);
    }
}
