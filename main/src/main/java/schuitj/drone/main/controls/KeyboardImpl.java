package schuitj.drone.main.controls;

import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.DroneCommander;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.KEY_PRESSED;
import static java.awt.event.KeyEvent.KEY_RELEASED;
import static java.awt.event.KeyEvent.VK_SPACE;

@Slf4j
public class KeyboardImpl implements Keyboard {

    private final DroneCommander droneCommander;

    public KeyboardImpl(DroneCommander droneCommander) {
        this.droneCommander = droneCommander;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        log.info("KeyboardImpl.dispatchKeyEvent: {}", e);

        if(e.getID() == KEY_PRESSED) {
            this.onKeyEvent(e, true);
        } else if(e.getID() == KEY_RELEASED) {
            this.onKeyEvent(e, false);
        }
        return false;
    }

    private void onKeyEvent(KeyEvent e, boolean isDown) {

        switch(e.getKeyCode()) {
            case VK_SPACE:
                if(isDown) {
                    //droneCommander.takeOff();
                } else {
                    //droneCommander.land();
                }
        }
    }
}
