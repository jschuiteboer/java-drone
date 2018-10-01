package schuitj.drone.lib.drone;

public interface Drone {
    void setThrottle(float amount);

    void setPitch(float amount);

    void setYaw(float amount);

    void setRoll(float amount);

    void takeOff();

    void land();
}
