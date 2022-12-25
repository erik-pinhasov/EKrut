package controllers;

import java.util.ResourceBundle;

import javax.print.DocFlavor.URL;

import client.ClientApp;
import client.ClientApp2;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MarketingManagerController extends AbstractController {

    @FXML
    private Button btnInitiate;

    @FXML
    private Button btnLogout;
    
    public void InitiateSaleButton(ActionEvent event) throws Exception {
    	try {
    		start("SaleInitiateForm", "Sale Initiate Form");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    //Not implemented
	@Override
	public void back() {}
}


