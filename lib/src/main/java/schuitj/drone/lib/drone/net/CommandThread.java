package schuitj.drone.lib.drone.net;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class CommandThread extends Thread {
    private CommandConnection commandConnection;
    private final int sleepTimeMs;

    @Setter
    private Command nextCommand;

    public CommandThread(CommandConnection commandConnection, int sleepTimeMs) {
        this.setName("command thread");
        this.setDaemon(true);

        this.commandConnection = commandConnection;
        this.sleepTimeMs = sleepTimeMs;
    }

    @Override
    public void run() {
        while(!this.isInterrupted()) {
            try {
                if(nextCommand != null) {
                    log.info("sending command {}", nextCommand);
                    commandConnection.sendCommand(nextCommand);
                    nextCommand = null;
                }
                Thread.sleep(this.sleepTimeMs);
            } catch (InterruptedException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
