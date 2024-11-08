package t12311m0.shoes_store;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
    // Tạo Scene từ FXML với kích thước 600x400
    scene = new Scene(loadFXML("primary"), 600, 400);

    // Thiết lập Scene cho Stage
    stage.setScene(scene);

    // Không cho phép thay đổi kích thước cửa sổ
    stage.setResizable(false);

    // Hiển thị cửa sổ
    stage.show();
}


    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}