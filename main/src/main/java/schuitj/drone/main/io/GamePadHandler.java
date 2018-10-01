package schuitj.drone.main.io;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import schuitj.drone.lib.drone.Drone;

@Slf4j
public class GamePadHandler implements InputHandler {
    private static final float SCALE = 2;

    private Drone drone;

    @Override
    public void setDrone(Drone drone) {
        Validate.notNull(drone);
        this.drone = drone;
    }

    @Override
    public void start() {
        Thread gamepadHandlerThread = new Thread(() -> {
            final ControllerManager controllers = new ControllerManager();
            controllers.initSDLGamepad();

            try {
                while(true) {
                    ControllerState state = controllers.getState(0);
                    
                    if (!state.isConnected) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    drone.setThrottle(state.leftStickY * SCALE);
                    drone.setYaw(state.leftStickX * SCALE);
                    drone.setPitch(state.rightStickY * SCALE);
                    drone.setRoll(state.rightStickX * SCALE);

                    if(state.startJustPressed) {
                        drone.takeOff();
                    }

                    if(state.backJustPressed) {
                        drone.land();
                    }
                }
            } finally {
                controllers.quitSDLGamepad();
            }
        }, "gamepad handler thread");
        gamepadHandlerThread.setDaemon(true);
        gamepadHandlerThread.start();
    }
}
