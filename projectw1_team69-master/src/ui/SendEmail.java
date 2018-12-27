package ui;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**REFERENCE: https://www.tutorialspoint.com/javamail_api/javamail_api_sending_simple_email.htm**/

public class SendEmail {
        public static final String USER_NAME = "projectw1.team69";
        public static final String PASSWORD = "Projectw1team69!";


    // private static void sendFromGMail(String from, String pass, String[] to, String subject, String body)
    public static void send(String to, String subject, String message){
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", USER_NAME);
        props.put("mail.smtp.password", PASSWORD);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER_NAME, PASSWORD);
                    }
                });
        MimeMessage mimeMessage = new MimeMessage(session);
        try {

            mimeMessage.setFrom(new InternetAddress(USER_NAME));
            mimeMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            Transport.send(mimeMessage);

        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

        }

}
