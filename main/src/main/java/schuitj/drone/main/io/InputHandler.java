package schuitj.drone.main.io;

import schuitj.drone.lib.drone.Drone;

public interface InputHandler<TDrone extends Drone> {
    void setDrone(TDrone drone);

    void start();
}
