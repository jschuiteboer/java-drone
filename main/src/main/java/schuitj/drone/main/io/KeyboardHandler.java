package schuitj.drone.main.io;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.Validate;
import schuitj.drone.lib.drone.Drone;

public class KeyboardHandler implements EventHandler<KeyEvent> {
    private final Drone drone;
    private final Scene scene;

    public KeyboardHandler(Drone drone, Scene scene) {
        Validate.notNull(drone);
        Validate.notNull(scene);

        this.drone = drone;
        this.scene = scene;
    }

    public void start() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, this);
    }

    public void stop() {
        scene.removeEventHandler(KeyEvent.KEY_PRESSED, this);
        scene.removeEventHandler(KeyEvent.KEY_RELEASED, this);
    }

    @Override
    public void handle(KeyEvent event) {
        float amount = event.getEventType() == KeyEvent.KEY_PRESSED ? 1 : 0;

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
