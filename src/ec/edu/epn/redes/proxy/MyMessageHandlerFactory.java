package ec.edu.epn.redes.proxy;

import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.*;
import java.util.Properties;
import java.util.StringTokenizer;

public class MyMessageHandlerFactory implements MessageHandlerFactory {

    public MessageHandler create(MessageContext ctx) {
        return new Handler(ctx);
    }

    class Handler implements MessageHandler {
        MessageContext ctx;
        String from, subject, content, name, emailTo;

        public Handler(MessageContext ctx) {
            this.ctx = ctx;
        }

        public void from(String from) throws RejectException {
            this.from = from;
        }

        public void recipient(String recipient) throws RejectException {
        }

        public void data(InputStream data) throws IOException {
            mimeMessage(data);

            sendEmail();
        }

        public void done() {
            System.out.println("Finished");
        }

        private void mimeMessage(InputStream is) {
            Session s = Session.getDefaultInstance(new Properties());
            try {
                MimeMessage message = new MimeMessage(s, is);
                Address[] in = message.getFrom();
                for (Address address : in) {
                    this.name = address.toString();
                }

                Address[] to = message.getRecipients(Message.RecipientType.TO);
                for (Address address : to) {
                    this.emailTo = address.toString();
                }

                System.out.println("Subject: " + message.getSubject());
                this.subject = message.getSubject();

                Part messagePart = message;
                this.content = getText(messagePart);
            } catch (MessagingException e) {
                System.out.println("Error in mimeMessage: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error in mimeMessage: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private String getText(Part p) throws MessagingException, IOException {
            if (p.isMimeType("text/*")) {
                String s = (String) p.getContent();
                return s;
            }

            if (p.isMimeType("multipart/alternative")) {
                Multipart mp = (Multipart) p.getContent();
                String text = null;
                for (int i = 0; i < mp.getCount(); i++) {
                    Part bp = mp.getBodyPart(i);
                    if (bp.isMimeType("text/plain")) {
                        if (text == null)
                            text = getText(bp);
                        continue;
                    } else if (bp.isMimeType("text/html")) {
                        String s = getText(bp);
                        if (s != null)
                            return s;
                    } else {
                        return getText(bp);
                    }
                }
                return text;
            } else if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String s = getText(mp.getBodyPart(i));
                    if (s != null)
                        return s;
                }
            }

            return null;
        }

        public void sendEmail() {
            System.out.println("SimpleEmail Start");

            Properties props = System.getProperties();

            String userName = this.from;

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, inputPassword());
                }
            };

            String host;
            String port = "587";
            StringTokenizer tokens = new StringTokenizer(userName, "@");
            tokens.nextToken();
            host = tokens.nextToken().toString();
            StringTokenizer tokens1 = new StringTokenizer(host, ".");
            host = tokens1.nextToken().toString();
            if (host.equals("gmail")) {
                host = "smtp.gmail.com";
            } else if (host.equals("live") || host.equals("hotmail") || host.equals("outlook")) {
                host = "smtp-mail.outlook.com";
            } else if (host.equals("yahoo")) {
                host = "smtp.mail.yahoo.com";
                port = "465";
            }

            props.put("mail.transport.protocol", "smtps");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, auth);
            session.setDebug(true);

            String contenido = this.content;
            String recipient = this.emailTo;
            String subject = this.subject;

            MailFilter mf = new MailFilter();
            if (mf.searchList(contenido, SMTPGateway.getWordDict())) {
                recipient = this.from;
                subject = "Email Filtering System";
                contenido = mf.formatString(contenido, this.emailTo, this.subject, SMTPGateway.getWordDict());
            }

            EmailUtil.sendEmail(session, recipient, subject, contenido, this.from, this.name);
        }

        JFrame frmOpt;

        private String inputPassword() {
            if (frmOpt == null) {
                frmOpt = new JFrame();
            }
            frmOpt.setVisible(true);
            frmOpt.setLocationRelativeTo(null);
            frmOpt.setState(JFrame.ICONIFIED);
            frmOpt.setState(JFrame.NORMAL);
            JPasswordField pf = new JPasswordField();
            JOptionPane.showConfirmDialog(frmOpt, pf, "Password", JOptionPane.PLAIN_MESSAGE);
            frmOpt.dispose();
            return new String(pf.getPassword());
        }

    }

}
