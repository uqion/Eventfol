/**
 * An application utilising the MVC design pattern to manage events. Events are stored on a local database server (MariaDB, XAMPP), UI controls give basic
 * INSERT, UPDATE, and DELETE functionalities provided by java.sql library. Additional feature included sending email notifications to
 * invite/remind people for events, functionality provided by javax.mail library.
 *
 * Additional notes:
 * -Event objects and containers were wholly replaced by the usage of a database; manipulation of data, including saving/loading
 * proved cumbersome and at one point parallel updating of lists and maps was involved. Exceptions continued to be enforced within context of SQL e.g. UNIQUE constraints,
 * primary keys.
 * -Although there were hopes for a chatbot, the AIML library proved difficult, mostly due to scant reference material available, instead all the functionality planned for chatbot was implemented
 * directly

 ***************************************************************************
 TODO: ENFORCE EXCEPTIONS
 TODO: DISABLE RADIO BUTTONS ACCORDINGLY
 TODO: HR, MM DISLAY FOR EDITVIEW

 **/
package ui;
import java.sql.*;
import DBConnectivity.DBConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Controller {

    @FXML
    ResultSet result;
    @FXML
    private ImageView notifyButton;
    @FXML
    private ImageView editButton;
    @FXML
    private ImageView deleteButton;
    @FXML
    private ObservableList<String> scheduleContents;
    @FXML
    private TextField locField;
    @FXML
    private ListView<String> schedule;
    @FXML
    private Label selectedDetails;
    @FXML
    private TextField eventDesc;
    @FXML
    private TextField eventMin;
    @FXML
    private Label resultsField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField eventHr;
    @FXML
    private RadioButton amBool;
    @FXML
    private RadioButton pmBool;
    private boolean anteMeridiem = false;
    /**Preps scene**/
        public void initialize() {
            getIcons();
            populateListView();
            displayUpcoming();

    }
    /**Helper method, called in initialize()**/
    private void getIcons(){
        notifyButton.setImage(
                new Image("file:/C:/Users/USER/Desktop/notify.png")
        );
        editButton.setImage(
                new Image("file:/C:/Users/USER/Desktop/edit.png")
        );
        deleteButton.setImage(
                new Image("file:/C:/Users/USER/Desktop/delete.png")
        );
    }
    /**Displays upcoming event; retrieves first row of table in database, ordered by date**/
    private void displayUpcoming(){
        try {
            DBConnection dbc = new DBConnection();
            Connection connection = dbc.getConnection();

            String sql = "Select * from events order by eventDate asc limit 1";
            Statement statement = connection.createStatement();
            ResultSet rs =  statement.executeQuery(sql);

            while(rs.next()) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd @ hh:mm");
                String dateString =dateFormat.format(rs.getTimestamp("eventDate"));
                selectedDetails.setText("Event: " + rs.getString("eventDesc") + "\nLocation: " + rs.getString("eventLoc") + "\nDate :" + dateString + rs.getString("eventTime"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
    /**Method triggered by notify button; creates a popup (new stage) for user to compose and send email notification**/
    @FXML
    void notify(MouseEvent me) throws IOException{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NotifyView.fxml"));
        Parent notifyParent = loader.load();
        Scene contactBookScene = new Scene(notifyParent);
        NotifyController controller = loader.getController();
        controller.defaultMessage(schedule.getSelectionModel().getSelectedItem());
        Stage window = new Stage();
        window.setScene(contactBookScene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Eventfol");
        window.showAndWait();

    }
    /**Method triggered by edit button; creates a popup (new stage) for user to edit selected event from listview*/
    @FXML
    void edit(MouseEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ContactBookView.fxml"));
        Parent contactViewParent = loader.load();
        Scene contactBookScene = new Scene(contactViewParent);
        ContactBookController controller = loader.getController();
        controller.editEvent(schedule.getSelectionModel().getSelectedItem());

        Stage window = new Stage();
        window.setScene(contactBookScene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Eventfol");
        window.showAndWait();

        updateView();

    }

    /**Populates listview with contents of events table in database**/
    private void populateListView() {
        try {
            DBConnection dbc = new DBConnection();
            String query = "select * from events order by eventDate asc";
            PreparedStatement preState = dbc.getConnection().prepareStatement(query);
            result = preState.executeQuery(query);

            while (result.next()) {
                String current = result.getString("eventDesc");
                scheduleContents = FXCollections.observableArrayList(current);
                schedule.getItems().addAll(scheduleContents);
            }
            preState.close();
            result.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**Clears and repopulates relevant controls to reflect changes e.g. after edit or delete*/
    public void updateView(){
        schedule.getItems().clear();
        populateListView();
        displayUpcoming();
    }
    /**Creates entry for databse, new event*/
    @FXML
    void createEvent(ActionEvent event) {
        String date = getDate(event) + " " + getEventHr(event)+":"+getEventMin(event)+":00";
        String meridiem = getAM(event) ? "AM" : "PM";

        try{
            Timestamp timestamp = java.sql.Timestamp.valueOf(date);
            DBConnection dbc = new DBConnection();
            Connection connection = dbc.getConnection();
            String sql = "INSERT INTO Events (eventDesc,eventLoc,eventDate,eventTime) VALUES('"+getEventDesc(event)+"','"+getLoc(event)+"','"+date+"','"+meridiem+"')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            updateView();

        }catch(Exception exception){
            exception.printStackTrace();
        }
        resultsField.setText("Event created!");
        amBool.setDisable(false);
        amBool.setDisable(false);

    }
    /**Removes event in listview and databse*/
    @FXML
    public void deleteSelected() throws IOException{

        String selectedEvent = schedule.getSelectionModel().getSelectedItem();
        try {
            DBConnection dbc = new DBConnection();
            Connection connection = dbc.getConnection();
            String sql = "DELETE FROM events WHERE eventDesc='"+selectedEvent+"'";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

            updateView();

            resultsField.setText("Event deleted!");


        } catch (Exception ex) {
            ex.printStackTrace();
        }


            }

    /**Display event details selected from list**/
    @FXML
   void displaySelected(MouseEvent event) {
        String selectedEvent = schedule.getSelectionModel().getSelectedItem();
        try {
            DBConnection dbc = new DBConnection();
            Connection connection = dbc.getConnection();
            String sql = "SELECT * FROM events WHERE eventDesc='"+selectedEvent+"'";
            Statement statement = connection.createStatement();
            ResultSet rs =  statement.executeQuery(sql);

            while(rs.next()) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd @ hh:mm");

                String dateString =dateFormat.format(rs.getTimestamp("eventDate"));
                    resultsField.setText("Event: " + rs.getString("eventDesc") + "\nLocation: " + rs.getString("eventLoc") + "\nDate :" + dateString+ " " +rs.getString("eventTime"));

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            }

    }


    @FXML
    void clearDisplay(MouseEvent event){
        resultsField.setText("");
    }

    @FXML
    void clearResults(MouseEvent event){
        resultsField.setText("");
    }


    @FXML
    boolean getAM(ActionEvent event) {
        return amBool.isSelected();
    }

    @FXML
    LocalDate getDate(ActionEvent event) {
        return datePicker.getValue();
    }

    @FXML
    String getEventDesc(ActionEvent event) {
        return eventDesc.getText();
    }
    @FXML
    String getLoc(ActionEvent event) {
        return locField.getText();
    }

    @FXML
    String getEventHr(ActionEvent event) {
        return eventHr.getText();
    }

    @FXML
    String getEventMin(ActionEvent event) {
        return eventMin.getText();
    }

    @FXML
    boolean getPM(ActionEvent event) {
        amBool.setDisable(!pmBool.isArmed());

        return true;
    }


}