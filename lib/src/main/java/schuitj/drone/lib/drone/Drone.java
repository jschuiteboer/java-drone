package schuitj.drone.lib.drone;

public interface Drone {
    void setThrottle(int amount);

    void setPitch(int amount);

    void setYaw(int amount);

    void setRoll(int amount);

    void takeOff();

    void land();
}
