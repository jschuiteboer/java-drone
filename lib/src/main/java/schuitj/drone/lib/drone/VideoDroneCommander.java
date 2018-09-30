package schuitj.drone.lib.drone;

import java.io.Closeable;
import java.io.IOException;

public interface VideoDroneCommander extends Closeable {
    void startVideo() throws IOException;

    void stopVideo();
}
