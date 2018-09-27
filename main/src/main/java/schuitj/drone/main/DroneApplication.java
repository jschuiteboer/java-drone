package schuitj.drone.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class DroneApplication extends Application {

	private ConfigurableApplicationContext springContext;
	private Parent root;

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
		springContext = SpringApplication.run(DroneApplication.class);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sample.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		root = fxmlLoader.load();
	}
}
