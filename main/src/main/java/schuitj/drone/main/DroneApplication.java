package schuitj.drone.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import schuitj.drone.lib.drone.Drone;
import schuitj.drone.lib.drone.cx10.CX10DroneImpl;
import schuitj.drone.main.io.GamePadHandler;
import schuitj.drone.main.io.KeyboardHandler;
import java.io.IOException;

@Slf4j
public class DroneApplication extends Application {

    private Drone drone;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //springContext = SpringApplication.run(DroneApplication.class);

        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        //fxmlLoader.setControllerFactory(springContext::getBean);
        //root = fxmlLoader.load();

        // create a window for the keyboard handler to receive events from
        StackPane root = new StackPane();

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Drone Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        this.initDroneStuff(scene);
    }

    private void initDroneStuff(Scene scene) throws IOException {
        drone = new CX10DroneImpl();

        GamePadHandler gamepadHandler = new GamePadHandler(drone);
        KeyboardHandler keyboardHandler = new KeyboardHandler(drone, scene);

        //TODO: this..
        ((CX10DroneImpl) drone).startCommandConnection();

        gamepadHandler.start();
        keyboardHandler.start();
    }

    @Override
    public void stop() throws Exception {
        ((CX10DroneImpl) drone).close();
    }
}
