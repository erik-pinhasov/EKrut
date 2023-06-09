package controllers;

import java.time.LocalDate;

import Util.Msg;
import Util.Tasks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * A controller class for a JavaFX application that allows marketing managers to initiate sales.
 */
public class SaleInitiateFormController extends AbstractController {

    @FXML
    private Button btnInitiate;

    @FXML
    private ComboBox<StringBuilder> lstSaleTemplate;
    
    @FXML
    private ComboBox<StringBuilder> lstRegion;
    
    @FXML
    private DatePicker StartingDate;
    
    @FXML
    private DatePicker EndingDate;
    
    @FXML
    private ComboBox<String> lstStartingHours;

    @FXML
    private ComboBox<String> lstEndingHours;

    @FXML
    private TextField txtSaleName;
    
    @FXML
    private Label lblErrDate;
    
    @FXML
    private Label lblErrTime;
    
    @FXML
    private Label lblErrName;
    
    @FXML
    private Label lblErrTemplate;
    
    @FXML
    private Label lblErrRegion;
    
    /**
     * Initializes the form by setting up the combo boxes for sale templates and regions,
     * and configuring the date picker fields to only allow selecting dates that are not in the past.
     */
    @FXML
    public void initialize() {
    	
    	//Initializing templates combo-box
    	String templatesQuery = "SELECT templateName FROM sale_template";
    	msg = new Msg(Tasks.Select, templatesQuery);
    	sendMsg(msg);
    	ObservableList<StringBuilder> saleTemplatesList = FXCollections.observableArrayList(msg.getArr(StringBuilder.class));
    	lstSaleTemplate.setItems(saleTemplatesList);
    	
    	//Initializing regions combo-box
    	String regionsQuery = "SELECT name FROM regions";
    	msg = new Msg(Tasks.Select, regionsQuery);
    	sendMsg(msg);
    	ObservableList<StringBuilder> regionsList = FXCollections.observableArrayList(msg.getArr(StringBuilder.class));
    	lstRegion.setItems(regionsList);
    	
    	//Disabling past dates for StartingDate
    	StartingDate.setDayCellFactory(picker -> new DateCell() {
    		public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
    		}
    	});

    	
    	//Disabling past dates or dates before StartingDate(if picked already) for EndingDate
    	EndingDate.setDayCellFactory(picker -> new DateCell() {
    		public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if(StartingDate.getValue() == null) {
                	LocalDate today = LocalDate.now();
                	setDisable(empty || date.compareTo(today) < 0 );
                }
                else
                	setDisable(empty || date.compareTo(StartingDate.getValue()) < 0 );
    		}
    	});
    	
    	//Initializing hours combo-boxes
    	ObservableList<String> StartingHoursList = FXCollections.observableArrayList("00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00",
    			"08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00");
    	lstStartingHours.setItems(StartingHoursList);
    	
    	ObservableList<String> EndingHoursList = FXCollections.observableArrayList("00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00",
    			"08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00");
    	lstEndingHours.setItems(EndingHoursList);
    	
    }
    
    /**
     * Validates the starting and ending dates and times entered by the user.
     * If the dates and times are invalid, displays an error message to the user.
     * 
     * @param event the event that triggered the method call
     */
    public void checkDates(ActionEvent event) {
        // Get selected dates
        LocalDate endingDate = EndingDate.getValue();
        LocalDate startingDate = StartingDate.getValue();
        
        // Check dates for validity
        if (endingDate != null && startingDate != null) {
            if (endingDate.isBefore(startingDate)) {
                // If ending date is before starting date, display error message
                lblErrDate.setText("Please fix the dates");
                // Clear error message for times, if present
                if (!(lblErrTime.getText().equals("Please choose times")))
                    lblErrTime.setText("");
            } else {
                // If dates are valid, clear error message and check times
                lblErrDate.setText("");
                checkTimes(event);
            }
        }
    }
    
    /**
     * Checks the validity of the times selected in the starting and ending hours combo-boxes.
     *
     * @param event the event that triggered the method call
     */
    public void checkTimes(ActionEvent event) {
    	// Get selected dates and times
    	LocalDate endingDate = EndingDate.getValue();
    	LocalDate startingDate = StartingDate.getValue();
    	String startingHour = lstStartingHours.getSelectionModel().getSelectedItem();
    	String endingHour = lstEndingHours.getSelectionModel().getSelectedItem();
    	int start,end;
    	
    	// Check times for validity
    	if(startingHour != null && endingHour != null) {
	    	start = Integer.parseInt(startingHour.substring(0, 2));
	    	end = Integer.parseInt(endingHour.substring(0, 2));
	    	if(endingDate != null && startingDate != null) {
		    	if(endingDate.isEqual(startingDate) && end <= start)
		    		// If ending time is before starting time, display error message
		    		lblErrTime.setText("Please fix the times");
		    	else
		    		// If times are valid, clear error message
		    		lblErrTime.setText("");
	    	}
	    }
    }
    
    /**
     * Checks if the sale name is valid. If the sale name is empty, an error message is displayed. If the
     * sale name already exists in the `sale_initiate` table, an error message is displayed. Otherwise,
     * the error message is cleared.
     *
     * @return true if the sale name is valid, false otherwise
     */
    public boolean checkSaleName() {
    	if(txtSaleName.getText().equals("")) {
    		lblErrName.setText("Please enter sale name");
    		return false;
    	}
    	// Checking if the sale name is already exists in database
    	String query = "SELECT saleName FROM sale_initiate WHERE saleName = '" + txtSaleName.getText() + "'";
    	msg = new Msg(Tasks.Select, query);
    	sendMsg(msg);
    	if(msg.getBool()) {
    		lblErrName.setText("This sale name already exists");
    		return false;
    	}
    	// otherwise, the error message is cleared
    	lblErrName.setText("");
    	return true;
    }
    
    /**
     * Check if a template has been selected.
     *
     * @return true if a template has been selected, false otherwise
     */
    public boolean checkTemplate() {
    	if(lstSaleTemplate.getSelectionModel().getSelectedItem() == null) {
    		lblErrTemplate.setText("Please choose template");
    		return false;
    	}
    	lblErrTemplate.setText("");
    	return true;
    }
    
    /**
     * Check if a region has been selected.
     *
     * @return true if a region has been selected, false otherwise
     */
    public boolean checkRegion() {
    	if(lstRegion.getSelectionModel().getSelectedItem() == null) {
    		lblErrRegion.setText("Please choose region");
    		return false;
    	}
    	lblErrRegion.setText("");
    	return true;
    }
    
    /**
     * Method to initiate a sale.
     * 
     * @param event the event that triggered the method call
     */
    public void InitiateSaleButton(ActionEvent event) {
    	
    	// Checking if there are no errors in the fields
    	boolean flag = true;
    	if(!checkSaleName())
    		flag = false;
    	if(!checkRegion())
    		flag = false;
    	if(!checkTemplate()) 
    		flag = false;
    	
    	//Checking if dates' fields are empty and display a message
    	if(StartingDate.getValue() == null || EndingDate.getValue() == null) {
    		lblErrDate.setText("Please choose dates");
    		flag = false;
    	}
    	
    	//Checking if times' fields are empty and display a message
    	if(lstStartingHours.getSelectionModel().getSelectedItem() == null || lstEndingHours.getSelectionModel().getSelectedItem() == null) {
    		lblErrTime.setText("Please choose times");
    		flag = false;
    	}
    	else if(!(lblErrTime.getText().equals("Please fix the times")))
    				lblErrTime.setText("");
    	
    	//Checking if there are errors with the dates or times
    	if(!(lblErrDate.getText().equals("")) || !(lblErrTime.getText().equals("")))
    		flag = false;
    	
    	//Initiate the sale if no errors displayed
    	if(flag) {
	    		//Getting templateId
		    	String query = "SELECT * FROM sale_template WHERE templateName = '" + lstSaleTemplate.getValue() + "'";
		    	msg = new Msg(Tasks.Select, query);
		    	sendMsg(msg);
		    	int templateId = msg.getObj(0);
		    	
		    	//Getting rid
		    	query = "SELECT * FROM regions WHERE name = '" + lstRegion.getValue() + "'";
		    	msg = new Msg(Tasks.Select, query);
		    	sendMsg(msg);
		    	int rid = msg.getObj(0);
		    	
		    	//Insert sale to database
		    	query = "INSERT into sale_initiate (templateId, saleName, rid, startDate, endDate, startHour, endHour, active) "
		    			+ "VALUES (" + templateId + " , '" + txtSaleName.getText() + "' , " + rid + " , '" + StartingDate.getValue() + 
		    			"' , '" + EndingDate.getValue() + "' , '" + lstStartingHours.getValue() + "' , '" + lstEndingHours.getValue() + "', 0)";
		    	msg = new Msg(Tasks.Insert, query);
		    	sendMsg(msg);
		    	
		    	//Display a confirmation pop-up message and resetting the fields
				Alert alert = new Alert(Alert.AlertType.NONE, "Sale initiate succeeded !", ButtonType.FINISH);
		        alert.setTitle("Success");
		        alert.showAndWait();
		        resetFields();
	    }
    	else {
    		//Display an error pop-up message
    		Alert alert = new Alert(Alert.AlertType.ERROR, "Sale initiate failed !",ButtonType.OK);
	        alert.setTitle("Failed");
	        alert.setHeaderText("Error");
	        alert.showAndWait();
    	}
    }
    
    /**
     * Resets the fields on the GUI to their default state.
     */
    public void resetFields() {
        lstSaleTemplate.setValue(null);
        lstRegion.setValue(null);
        StartingDate.setValue(null);
        EndingDate.setValue(null);
        lstStartingHours.setValue(null);
        lstEndingHours.setValue(null);
        lblErrTemplate.setText("");
        lblErrRegion.setText("");
        txtSaleName.setText("");
    }
    
    
    /**
     * Handles the mouse event of the back button.
     * @param event the mouse event that triggered this method
     */
    @Override
    public void back(MouseEvent event) {
    	try {
			start("MarketingManagerPanel","Marketing Manager Panel");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public void setUp(Object... objects) {
		//Not implemented
	}
}
















