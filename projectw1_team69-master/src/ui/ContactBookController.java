package ui;
import java.sql.*;

import DBConnectivity.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ContactBookController {



    @FXML
    private ImageView closeButton;

    @FXML
    private TextField eventDesc;

    @FXML
    private TextField eventLoc;

    @FXML
    private DatePicker eventDate;

    @FXML
    private TextField eventHr;

    @FXML
    private TextField eventMin;

    @FXML
    private Label resultsField;

    private String event;



    public void initialize() {

        closeButton.setImage(
                new Image("file:/C:/Users/USER/Desktop/check.png")
        );
}

    @FXML
    void editEvent(String eventDesc){
        this.event = eventDesc;
        try {
            DBConnection dbc = new DBConnection();
            Connection connection = dbc.getConnection();

            String sql = "Select * from events WHERE eventDesc = '"+eventDesc+"'";
            Statement statement = connection.createStatement();
            ResultSet rs =  statement.executeQuery(sql);

            while(rs.next()) {

                this.eventDesc.setText(eventDesc);
                eventLoc.setText(rs.getString("eventLoc"));

                DateFormat hourFormat = new SimpleDateFormat("hh");
                DateFormat minuteFormat = new SimpleDateFormat("mm");

                String hh =hourFormat.format(rs.getTimestamp("eventDate"));
                String mm =minuteFormat.format(rs.getTimestamp("eventDate"));

                Date ts =rs.getDate("eventDate");

                Instant instant = Instant.ofEpochMilli(ts.getTime());
                LocalDate res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();

                this.eventDate.setValue(res);
                this.eventHr.setText(hh);
                this.eventMin.setText(mm);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }




    }

    @FXML
    void closeWindow(MouseEvent event){
        String date = eventDate.getValue() + " " + eventHr.getText()+":"+eventMin.getText()+":00";
        try{
        DBConnection dbc = new DBConnection();
        Connection connection = dbc.getConnection();
        String sql = "UPDATE events SET eventDesc = '"+eventDesc.getText()+"', eventLoc = '"+eventLoc.getText()+"',eventDate ='"+date+"' WHERE eventDesc = '"+this.event+"'";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);


    } catch (Exception ex) {
        ex.printStackTrace();
    }

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
