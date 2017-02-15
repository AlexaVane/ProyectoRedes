package ec.edu.epn.redes.proxy;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class EmailUtil {

    public static void sendEmail(Session session, String toEmail, String subject, String body, String from, String name,
                                 ArrayList<String> fileList, ArrayList<String> fileNames, boolean attachments) {
        try {
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from, name));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject(subject);
            if (attachments) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                Multipart multipart = new MimeMultipart();

                messageBodyPart.setContent(body, "text/html");
                multipart.addBodyPart(messageBodyPart);

                int i = 0;
                for (String aux : fileNames) {
                    messageBodyPart = new MimeBodyPart();
                    String file = fileList.get(i);
                    String fileName = aux;
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName);
                    multipart.addBodyPart(messageBodyPart);
                    i++;
                }

                msg.setContent(multipart);
            } else {
                msg.setContent(body, "text/html");
            }

            System.out.println("Message is ready");

            Transport.send(msg);
            System.out.println("Email Sent Successfully!!");

            if (attachments) {
                deleteTempFiles(fileList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTempFiles(ArrayList<String> fileList) {
        try {
            for (String curr : fileList) {
                Files.delete(Paths.get(curr));
            }
            System.out.println("\n\nSe eliminaron los archivos");
        } catch (NoSuchFileException x) {
            System.err.println(x);
        } catch (DirectoryNotEmptyException x) {
            System.err.println(x);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

}
