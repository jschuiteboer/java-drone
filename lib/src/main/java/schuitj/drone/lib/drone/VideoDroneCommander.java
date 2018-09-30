package schuitj.drone.lib.drone;

import java.io.Closeable;

public interface VideoDroneCommander extends Closeable {
    void startVideo();

    void stopVideo();
}
