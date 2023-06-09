package controllers;

import Util.Msg;
import Util.Tasks;
import client.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * CustomerPanelController is the main controller for costumer panel.
 * It extends AbstractController and overrides methods of it.
 */
public class CustomerPanelController extends AbstractController {

	@FXML
	private Button btnLogout;

	@FXML
	private Button btnMakeOrder;

	@FXML
	private Button btnPickup;

	@FXML
	private Button btnApproveDelivery;

	@FXML
	private Label welcomeLbl;

	@FXML
	private Label errLbl;

	/**
	 * Initializes the panel based on the user that logged in, whether he's an approved/not approved or a registered/not registered customer.
	 */
	@FXML
	public void initialize() {
		//Editing welcome label
		String welcome = welcomeLbl.getText() + " ";
		welcome = myUser.getName() == null ? welcome + myUser.getUsername() : welcome + myUser.getName();
		welcomeLbl.setText(welcome);

		//Get the customer status and check if he is subscriber
		String customerQuery = "SELECT status, subscriber FROM customer WHERE id = " + myUser.getId();
		msg = new Msg(Tasks.Select, customerQuery);
		sendMsg(msg);

		if (myUser.getRole().equals("new_user") || !msg.getBool()) {
			//If a new user / not a customer
			errLbl.setText("You need to register as a customer to continue.");
		}

		else {
			//Customer
			if (msg.getObj(0).equals("Not Approved")) {
				//Customer not approved
				errLbl.setText("Your account has been rejected.");
			}
			else if (msg.getObj(0).equals("Pending"))
				errLbl.setText("Your account has not been approved yet");
			else {
				//Customer approved
				if (Config.getConfig().equals("OL")) {
					//OL configuration

					if ((int) (msg.getObj(1)) == 0) {

						//Regular customer
						errLbl.setText("You need to be a subscriber to login here.");
					} else {
						//Subscriber
						btnMakeOrder.setVisible(true);
						btnApproveDelivery.setVisible(true);
						errLbl.setDisable(true);
					}
				} else {
					//EK configuration
					btnMakeOrder.setVisible(true);
					//Subscriber customer

					btnPickup.setVisible((int) (msg.getObj(1)) == 1);
					errLbl.setDisable(true);
				}
			}
		}
	}

    /**
     * Handles the mouse event of the back button.
     * @param event the mouse event that triggered this method
     */
	@Override
	public void back(MouseEvent event) {
		// not implemented
	}

	/**
	* The MakeOrder method is used to open the order form or order screen, depending on the system configuration.
	* If the configuration is OL, it opens the "OrderMethodForm" window.
	* If the configuration is EK, it skips the "OrderMethodForm" window and opens the "OrderScreen" window.
    * @param event the mouse event that triggered this method
	*/
	@FXML
	public void MakeOrder(ActionEvent event) {
		if (Config.getConfig().equals("OL")) {
			try {
				start("OrderMethodForm", "Order Method Form");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				start("OrderScreen", "Order Screen");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* The PickupOrder method is used to open the pickup order form.
	* @param event the mouse event that triggered this method
	*/
	@FXML
	public void PickupOrder(ActionEvent event) {
		try {
			start("PickupForm", "Pickup Order Form");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* The ApproveDelivery method is used to open the customer deliveries form.
	* @param event the mouse event that triggered this method
	*/
	@FXML
	public void ApproveDelivery(ActionEvent event) {
		try {
			start("CustomerDeliveriesForm", "Customer Deliveries Form");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setUp(Object... objects) {
		// Not implemented
	}

}