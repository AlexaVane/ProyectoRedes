package ec.edu.epn.redes.proxy;

import org.subethamail.smtp.server.SMTPServer;

import java.util.ArrayList;
import java.util.Hashtable;

public class SMTPGateway {

    private static int PORT = 25000;
    private static Hashtable<Integer, ArrayList<String>> wordDict;

    public static Hashtable<Integer, ArrayList<String>> getWordDict() {
        return wordDict;
    }

    public static void main(String[] args) {
        MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory() ;
        SMTPServer smtpServer = new SMTPServer(myFactory);
        System.out.println("SMTP server listening on port: " + PORT);
        smtpServer.setPort(PORT);
        smtpServer.start();

        MailFilter mf = new MailFilter();
        wordDict = mf.readFile();
    }

}
