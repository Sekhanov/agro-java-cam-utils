package ru.skhanov.ipcam;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxAppMain extends Application {

	public static void main(String[] args) {		
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent rooPane = FXMLLoader.load(getClass().getResource("/camView.fxml"));
		primaryStage.setTitle("Driver photo");
		primaryStage.setScene(new Scene(rooPane));
		primaryStage.centerOnScreen();
		primaryStage.show();
		
	}

}
