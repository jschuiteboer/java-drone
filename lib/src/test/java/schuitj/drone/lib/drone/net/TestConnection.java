package schuitj.drone.lib.drone.net;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import schuitj.drone.lib.drone.cx10.CX10CommanderImpl;
import java.io.IOException;

@Slf4j
public class TestConnection {
	@Test
	public void test() throws IOException {
        try(CX10CommanderImpl drone = new CX10CommanderImpl()) {
            try {
                log.debug("taking off");
                drone.takeOff();

                log.debug("waiting...");
                Thread.sleep(2000);

                log.debug("landing");
                drone.land();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
	}
}
