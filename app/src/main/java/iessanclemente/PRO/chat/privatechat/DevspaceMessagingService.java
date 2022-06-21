package iessanclemente.PRO.chat.privatechat;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import iessanclemente.PRO.R;

public class DevspaceMessagingService extends AsyncTask< Void, Void, Void> {

    private final String EMAIL = "acc.devspace.suport@gmail.com";
    private final String PASSWORD = "Pru3845_PR0y3Ct0";
    private Context context;

    private Session session;
    private String email, subject, message;

    public DevspaceMessagingService(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.imap.auth.mechanisms", "XOAUTH2");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                PasswordAuthentication auth = new PasswordAuthentication(EMAIL, PASSWORD);
                return auth;
            }
        });

        session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(EMAIL));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            Transport.send(mimeMessage);

            Toast advice = Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.email_sent), Toast.LENGTH_LONG);
            advice.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 180);
            advice.show();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
