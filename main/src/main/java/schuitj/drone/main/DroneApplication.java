package schuitj.drone.main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.cx10.CX10Drone;
import schuitj.drone.lib.drone.cx10.CX10DroneImpl;
import java.io.IOException;

@Slf4j
public class DroneApplication extends Application implements EventHandler<KeyEvent> {

	private CX10Drone cx10Drone;

	// create a window that shows a message that it's waiting for a connection
	// once connected
	// 	open the camera window
	// 	control using either the gamepad or the keyboard

	// extra:
	//  smoothing when using the keyboard
	//  adjust trim with directional pad and buttons
	//  adjust sensitivity

	@Override
	public void start(Stage primaryStage) throws Exception {
		//springContext = SpringApplication.run(DroneApplication.class);

		//FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sample.fxml"));
		//fxmlLoader.setControllerFactory(springContext::getBean);
		//root = fxmlLoader.load();

		StackPane root = new StackPane();

		Scene scene = new Scene(root, 800, 600);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, this);
		scene.addEventHandler(KeyEvent.KEY_RELEASED, this);

		primaryStage.setTitle("Drone Application");
		primaryStage.setScene(scene);
		primaryStage.show();

		cx10Drone = new CX10DroneImpl();

		new Thread(() -> {
			try {
				((CX10DroneImpl) cx10Drone).startCommandConnection();
			} catch (IOException ex) {
				throw new RuntimeException("unable to start drone connection", ex);
			}
		}, "drone connection starter").start();
	}

	@Override
	public void handle(KeyEvent event) {
		int amount = event.getEventType() == KeyEvent.KEY_PRESSED ? 127 : 0;

		switch(event.getCode()) {
			case W:
				cx10Drone.setThrottle(amount);
				break;
			case S:
				cx10Drone.setThrottle(-amount);
				break;
			case A:
				cx10Drone.setYaw(-amount);
				break;
			case D:
				cx10Drone.setYaw(amount);
				break;

			case UP:
				cx10Drone.setPitch(amount);
				break;
			case DOWN:
				cx10Drone.setPitch(-amount);
				break;
			case LEFT:
				cx10Drone.setRoll(-amount);
				break;
			case RIGHT:
				cx10Drone.setRoll(amount);
				break;

			case PAGE_UP:
				cx10Drone.takeOff();
				break;
			case PAGE_DOWN:
				cx10Drone.land();
				break;
		}
	}
}
