package schuitj.drone.main.io;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import schuitj.drone.lib.drone.Drone;

@Slf4j
public class GamePadHandler {
    private static final float DEFAULT_SCALE = 1;
    private static final float DEFAULT_DEAD_ZONE = 0.15f;

    private final Thread gamePadHandlerThread;

    private float scale = DEFAULT_SCALE;
    private float deadZone = DEFAULT_DEAD_ZONE;

    public GamePadHandler(Drone drone) {
        Validate.notNull(drone);

        this.gamePadHandlerThread = new Thread(() -> {
            final ControllerManager controllers = new ControllerManager();
            controllers.initSDLGamepad();

            try {
                while(!Thread.currentThread().isInterrupted()) {
                    ControllerState state = controllers.getState(0);

                    if (!state.isConnected) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    drone.setThrottle(this.parseAxis(state.leftStickY));
                    drone.setYaw(this.parseAxis(state.leftStickX));
                    drone.setPitch(this.parseAxis(state.rightStickY));
                    drone.setRoll(this.parseAxis(state.rightStickX));

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
        gamePadHandlerThread.setDaemon(true);
    }

    public void setScale(float scale) {
        if(scale < 1) {
            throw new IllegalArgumentException("Invalid value for scale: " + scale);
        }
        this.scale = scale;
    }

    public void setDeadZone(float deadZone) {
        Validate.inclusiveBetween(0f, 1f, deadZone);
        this.deadZone = deadZone;
    }

    public void start() {
        this.gamePadHandlerThread.start();
    }

    public void stop() {
        this.gamePadHandlerThread.interrupt();
    }

    private float parseAxis(float inputValue) {
        boolean negate = false;
        if(inputValue < 0) {
            negate = true;
            inputValue = -inputValue;
        }

        inputValue = (inputValue * (1 + deadZone)) - deadZone;

        inputValue = inputValue * scale;

        if(inputValue > 1) {
            inputValue = 1;
        }

        if(negate) {
            inputValue = -inputValue;
        }

        return inputValue;
    }
}
