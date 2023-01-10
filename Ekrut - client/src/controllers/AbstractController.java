package controllers;

import java.io.IOException;
import Util.Msg;
import Util.Tasks;
import Entities.User;
import client.ClientBackEnd;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Super abstract class for all controllers
 * All the controllers inherit from this class and the method start
 */
public abstract class AbstractController {
	public static Stage prStage;
	public static Object monitor = new Object();
	public static Msg msg;
	public static User myUser;

	public void start(String fxml, String title) throws IOException {
		FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/" + fxml + ".fxml"));
		Parent root = load.load();
		Scene scene = new Scene(root);

		prStage.setTitle("Ekrut" + " " + title);
		prStage.setScene(scene);
		prStage.setResizable(false);
		if (fxml != "ConnectionConfig")
			prStage.setOnCloseRequest(event -> {
				ClientBackEnd.getInstance().quit();
			});
		ClientBackEnd.setAbstractController(load.getController());
		prStage.show();
	}

	public void Wait() { // Nave
		try {
			synchronized (monitor) {
				monitor.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(Msg msg) { //Nave
		try {
			ClientBackEnd.getInstance().handleMessageFromClientUI(msg);
			Wait();
		} catch (Exception e) {
			e.printStackTrace();
		} //Send task to server
	}

	public static void Notify() { //Nave
		synchronized (monitor) {
			monitor.notify();
		}
	}
	
	public void logout() throws IOException {
		String logoutQuery = "UPDATE users SET isLogged = 0 WHERE id = " + myUser.getId();
		msg = new Msg(Tasks.Logout, logoutQuery);
		sendMsg(msg);
		myUser = null;
		start("LoginForm", "Login");
	}
	
	public void popupAlert(String msg) { //ERIK
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initOwner(prStage);
		alert.setTitle("info");
		alert.setContentText(msg);
		alert.showAndWait();
	}
	
	public void waitForAlert(String msg) { //erik
		new Thread(new Runnable() {
		    @Override public void run() {
		        Platform.runLater(new Runnable() {
		            @Override public void run() {
		            	popupAlert(msg);
		            }
		        });
		    }
		}).start();
	}
	
	public abstract void back(MouseEvent event);

}
