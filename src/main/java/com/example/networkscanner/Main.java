/**
 * Это основной класс для приложения Network Port Scanner.
 * Он использует JavaFX для создания графического интерфейса.
 */
package com.example.networkscanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.util.Objects;

public class Main extends Application {

    /**
     * Этот метод используется для запуска JavaFX приложения.
     * Он загружает FXML, устанавливает сцену и показывает главное окно.
     *
     * @param primaryStage основная сцена для приложения
     * @throws Exception если возникнет ошибка при загрузке FXML или установке сцены
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));
        Scene scene = new Scene(root, 800, 500);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("logo.png"))));
        primaryStage.setTitle("Network Port Scanner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Этот метод является входной точкой для JavaFX приложения.
     */
    public static void main(String[] args) {
        launch(args);
    }
}