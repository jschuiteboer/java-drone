package schuitj.drone.lib.drone;

import java.io.Closeable;
import java.io.IOException;

public interface DroneCommander extends Closeable {
    void takeOff() throws IOException;

    void land() throws IOException;
}
