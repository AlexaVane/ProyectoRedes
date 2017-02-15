package ec.edu.epn.redes.proxy;

import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MailVirusScan {

    public boolean scanVirus(ArrayList<String> fileList) {
        boolean status = false;
        ClamavClient client = new ClamavClient("localhost");

        try {
            for (String curr : fileList) {
                ScanResult result = client.scan(new FileInputStream(curr));
                if (result.getStatus().equals(ScanResult.Status.VIRUS_FOUND)) {
                    System.out.println("Se encontraron virus");
                    status = true;
                } else {
                    System.out.println("No se encontraron virus");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public String formatString(String content, String recipient, String subject, ArrayList<String> fileNames) {
        FileReader fr = null;
        StringBuilder contenido = new StringBuilder();

        try {
            File archivo = new File("formato1.txt");
            fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);

            contenido.append(br.readLine());
            contenido.append("\n\n");
            contenido.append(br.readLine());
            contenido.append("\n<UL>\n");
            for (String aux : fileNames) {
                contenido.append("<LI>" + aux + "</LI>\n");
            }
            contenido.append("</UL>\n\n\n\n");
            contenido.append("<H4> En el siguiente enlace puede enviar el correo sin los archivos infectados:</br>");
            contenido.append("<a href=" + "mailto:" + recipient + "?&subject=" + changeSpaces(subject) + "&body=" + changeSpaces(content)
                    + ">Volver a enviar el correo.</a></H4>");
            contenido.append(br.readLine());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                System.out.println(e2);
            }
        }
        return contenido.toString();
    }

    private String changeSpaces (String content) {
        ArrayList<String> myList = new ArrayList<>(Arrays.asList(content.split("\\W+")));
        StringBuilder sb = new StringBuilder();

        for (String aux : myList) {
            sb.append(aux);
            sb.append("%20");
        }

        return sb.toString();
    }
}
