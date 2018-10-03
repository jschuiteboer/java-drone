package schuitj.drone.lib.drone.net;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class HeartbeatThread extends Thread {
    private final TransportConnection transportConnection;
    private final byte[] heartbeatPacket;
    private final int responseSize;
    private final int sleepTime;

    public HeartbeatThread(TransportConnection transportConnection, byte[] heartbeatPacket, int responseSize, int sleepTimeMs) {
        this.setName("heartbeat thread");
        this.setDaemon(true);

        this.transportConnection = transportConnection;
        this.heartbeatPacket = heartbeatPacket;
        this.responseSize = responseSize;
        this.sleepTime = sleepTimeMs;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                log.debug("sending heartbeat");
                this.transportConnection.send(this.heartbeatPacket);
                this.transportConnection.receive(this.responseSize); //TODO: is it required to receive the response?

                Thread.sleep(sleepTime);
            } catch (InterruptedException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
