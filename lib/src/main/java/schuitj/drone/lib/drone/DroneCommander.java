package schuitj.drone.lib.drone;

import java.io.Closeable;

public interface DroneCommander extends Closeable {
    void setThrottle(int amount);

    void setPitch(int amount);

    void setYaw(int amount);

    void setRoll(int amount);

    void takeOff();

    void land();
}
