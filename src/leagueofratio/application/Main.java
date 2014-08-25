/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package leagueofratio.application;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import leagueofratio.object.Game;

/**
 *
 * @author Miloune
 */
public class Main extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("League Of Ratio");
        this.primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("graphic/lol.png")));
        
        initGameDir();
        initRootLayout();

        showHomeOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("graphic/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the login overview inside the root layout.
     */
    public void showHomeOverview() {
        try {
            // Load login overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("graphic/Home.fxml"));
            AnchorPane homeOverview = (AnchorPane) loader.load();

            // Set login overview into the center of root layout.
            rootLayout.setCenter(homeOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initGameDir() {
        File gameDir = new File(Game.getPath());
        
        if(!gameDir.exists()) {
            gameDir.mkdirs();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
