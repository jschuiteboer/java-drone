package schuitj.drone.lib.drone.net;

import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.util.ByteUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class TransportConnection implements Closeable {
    private final Socket socket;

    public TransportConnection(String host, int port) throws IOException {
        log.debug("opening TransportConnection: host={}, port={}", host, port);

        InetAddress address = InetAddress.getByName(host);
        this.socket = new Socket(address, port);
    }

    @Override
    public void close() throws IOException {
        if(!socket.isClosed()) {
            log.debug("closing TransportConnection");
            socket.close();
        }
    }

    public void send(byte[] bytes) throws IOException {
        final OutputStream outputStream = socket.getOutputStream();

        if(log.isDebugEnabled()) {
            log.debug("sending {} bytes", bytes.length);
        } else if(log.isTraceEnabled()) {
            log.trace("sending {} bytes:\n    {}", bytes.length, ByteUtils.toHexString(bytes));
        }
        outputStream.write(bytes);
    }

    public byte[] receive(int size) throws IOException {
        final InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[size];

        inputStream.read(bytes);

        if(log.isDebugEnabled()) {
            log.debug("received {} bytes", bytes.length);
        } else if(log.isTraceEnabled()) {
            log.trace("received {} bytes:\n    {}", bytes.length, ByteUtils.toHexString(bytes));
        }

        return bytes;
    }
}
