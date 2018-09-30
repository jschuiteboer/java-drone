package schuitj.drone.lib.drone.net;

import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.Command;
import java.io.Closeable;
import java.io.IOException;
import java.net.*;

@Slf4j
public class CommandConnection implements Closeable {
    private final DatagramSocket datagramSocket;
    private final InetAddress host;
    private final int port;

    public CommandConnection(String host, int port) throws IOException {
        log.debug("opening CommandConnection: host={}, port={}", host, port);

        this.datagramSocket = new DatagramSocket();
        this.host = InetAddress.getByName(host);
        this.port = port;
    }

    @Override
    public void close() {
        if(!datagramSocket.isClosed()) {
            log.debug("closing CommandConnection");
            datagramSocket.close();
        }
    }

    public void sendCommand(Command command) throws IOException {
        byte[] data = command.toByteArray();
        DatagramPacket packet = new DatagramPacket(data, 0, data.length, host, port);

        datagramSocket.send(packet);
    }
}
