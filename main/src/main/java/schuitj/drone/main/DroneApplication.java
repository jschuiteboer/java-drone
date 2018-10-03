package schuitj.drone.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.cx10.CX10Drone;
import schuitj.drone.lib.drone.cx10.CX10DroneImpl;
import schuitj.drone.main.io.GamePadHandler;
import schuitj.drone.main.io.KeyboardHandler;

@Slf4j
public class DroneApplication extends Application {

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

		cx10Drone = new CX10DroneImpl();

		// setup gamepad handler
		GamePadHandler gamepadHandler = new GamePadHandler();
		gamepadHandler.setDrone(cx10Drone);

		// setup keyboard handler
		KeyboardHandler keyboardHandler = new KeyboardHandler();
		keyboardHandler.setDrone(cx10Drone);

		// create a window for the keyboard handler to receive events from
		StackPane root = new StackPane();

		Scene scene = new Scene(root, 800, 600);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, keyboardHandler);
		scene.addEventHandler(KeyEvent.KEY_RELEASED, keyboardHandler);

		primaryStage.setTitle("Drone Application");
		primaryStage.setScene(scene);
		primaryStage.show();

		((CX10DroneImpl) cx10Drone).startCommandConnection();
		gamepadHandler.start();
		keyboardHandler.start();
	}

	@Override
	public void stop() throws Exception {
		((CX10DroneImpl) cx10Drone).close();
	}
}
