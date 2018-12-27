package ui;

import DBConnectivity.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class NotifyController {

    @FXML
    private ImageView sendButton;

    @FXML
    private TextField emails;
    @FXML
    private TextArea message;

    private   String content;

    @FXML
    void closeWindow(MouseEvent event) {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        sendNotification();
        stage.close();
    }
    public void initialize() {


        sendButton.setImage(
                new Image("file:/C:/Users/USER/Desktop/send.png")
        );}

        @FXML
        void defaultMessage(String eventDesc){

            try {
                DBConnection dbc = new DBConnection();
                Connection connection = dbc.getConnection();

                String sql = "Select * from events WHERE eventDesc = '"+eventDesc+"'";
                Statement statement = connection.createStatement();
                ResultSet rs =  statement.executeQuery(sql);

                while(rs.next()) {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd @ hh:mm");

                    String dateString =dateFormat.format(rs.getTimestamp("eventDate")) + " " +rs.getString("eventTime");
                     content =  "Hiya Sunshine, \n You've been invited to join an event! \n\n Event: " + rs.getString("eventDesc") + "\n Location: " + rs.getString("eventLoc") + "\n Date: " + dateString + "\n\n Be there or be square!";
                     this.message.setText(content);
                    //"Event: " + rs.getString("eventDesc") + "\nLocation: " + rs.getString("eventLoc") + "\nDate :" + rs.getTimestamp("eventDate"));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    @FXML
    void sendNotification() {

        SendEmail.send(emails.getText(),"Event notification", message.getText());

    }

}
