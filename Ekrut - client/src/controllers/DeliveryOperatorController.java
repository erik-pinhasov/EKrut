package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * DeliveryOperatorController is a controller class for Main Delivery Operator panel
 * for FXML file: DeliveryOperatorPanel.fxml
 * It has one method "moveToForm" that will move to Delivery Orders Form screen
 * that method connected to 'deliveryBtn' button
 * Methods "back" and "setUp" are not implemented (extends AbstractController)
 */
public class DeliveryOperatorController extends AbstractController{
	

    @FXML
    private Button deliveryBtn;
    
    @FXML
    private Label welcomeLbl;
    
	/**
	 * initialize method is a protected method that is called automatically when the FXML file is loaded.
	 * It sets the welcome label with "Welcome" string and user name.
	 */
    @FXML
    public void initialize() {
    	welcomeLbl.setText("Welcome "+myUser.getName());
    }
    
    /**
     * moveToForm is called when a mouse event occurs. It opens a the window "Delivery Orders".
     * @param event The MouseEvent that triggers the method.
     */
    @FXML
    public void moveToForm(MouseEvent event) {
		try {
			start("DeliveryOrdersForm", "Delivery Orders");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    

	@Override
	public void back(MouseEvent event) {
	}


	@Override
	public void setUp(Object... objects) {
		
	}
    
}
