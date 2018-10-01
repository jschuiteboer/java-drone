package schuitj.drone.main.io;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.Validate;
import schuitj.drone.lib.drone.Drone;

public class KeyboardHandler implements InputHandler, EventHandler<KeyEvent> {
    private Drone drone;

    @Override
    public void setDrone(Drone drone) {
        Validate.notNull(drone);
        this.drone = drone;
    }

    @Override
    public void start() {
        // not used
    }

    @Override
    public void handle(KeyEvent event) {
        final EventType<KeyEvent> keyEventType = event.getEventType();

        if(keyEventType == KeyEvent.KEY_PRESSED
        || keyEventType == KeyEvent.KEY_RELEASED) {
            float amount = keyEventType == KeyEvent.KEY_PRESSED ? 1 : 0;

            switch(event.getCode()) {
                case W:         drone.setThrottle(amount);  break;
                case S:         drone.setThrottle(-amount); break;
                case A:         drone.setYaw(-amount);      break;
                case D:         drone.setYaw(amount);       break;

                case UP:        drone.setPitch(amount);     break;
                case DOWN:      drone.setPitch(-amount);    break;
                case LEFT:      drone.setRoll(-amount);     break;
                case RIGHT:     drone.setRoll(amount);      break;

                case PAGE_UP:   drone.takeOff();            break;
                case PAGE_DOWN: drone.land();               break;
            }
        }
    }
}
