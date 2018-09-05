package application;
	
import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.jna.platform.win32.User32;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ts.entity.License;
import ts.win32.KeyHook;


public class Main extends Application {
	
	public static License license = AppInfo.getLicense();
	
	public static Timer timer = new Timer();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Controller.stage = primaryStage;
			
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Scene.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Date now = new Date();
				if(license != null) {
					File file = new File(System.getProperty("user.dir")+"/License");
					if(!file.exists()) {
						User32.INSTANCE.UnhookWindowsHookEx(KeyHook.hhk);
						System.exit(0);
					}
					
					if(license.getExpire().compareTo(now) == 1 && AppInfo.sn().equals(Main.license.getSn())) {
						if(now.compareTo(license.getTime()) == 1) {
							license.setTime(now);
							AppInfo.setLicense(license);
						}
					}
				}
			}
		}, 0, 60000);
		launch(args);
	}
}
