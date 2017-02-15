package ec.edu.epn.redes.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

public class MailFilter {

    private ArrayList<String> contenido;

    private int hashCode(String palabra) {
        int h;
        char val[] = palabra.toCharArray();

        h = (int) Character.toLowerCase(val[0])/3;
        h = h - 32;

        return h;
    }

    public Hashtable<Integer, ArrayList<String>> readFile() {
        FileReader fr = null;
        Hashtable<Integer, ArrayList<String>> hashtable = new Hashtable<>();

        try {
            File archivo = new File("palabras.txt");
            fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);

            for (int i = 0; i < 9; i++) {
                hashtable.put(i, new ArrayList<>());
            }

            String aux;
            int h;

            while ((aux = br.readLine()) != null) {
                h = hashCode(aux);

                hashtable.get(h).add(aux);
            }

            for (int i = 0; i < 9; i++) {
                Collections.sort(hashtable.get(i));
            }
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

        return hashtable;
    }

    public boolean searchList(String contenido, Hashtable<Integer, ArrayList<String>> wordDict) {
        ArrayList<String> myList = new ArrayList<>(Arrays.asList(contenido.split("[^a-zA-Z']+")));
        this.contenido = myList;
        int h, index;

        for (String aux : myList) {
            h = hashCode(aux);
            index = Collections.binarySearch(wordDict.get(h), aux);

            if (index > -1) {
                return true;
            }
        }

        return false;
    }

    public String formatString(String content, String recipient, String subject, Hashtable<Integer, ArrayList<String>> wordDict) {
        FileReader fr = null;
        StringBuilder contenido = new StringBuilder();
        ArrayList<String> temp = new ArrayList<>();
        int h, index;

        for (String aux : this.contenido) {
            h = hashCode(aux);
            index = Collections.binarySearch(wordDict.get(h), aux);

            if (index > -1) {
                temp.add(aux);
            }
        }

        try {
            File archivo = new File("formato.txt");
            fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);

            contenido.append(br.readLine());
            contenido.append("\n\n");
            contenido.append(br.readLine());
            contenido.append("\n<UL>\n");
            for (String aux : temp) {
                contenido.append("<LI>" + aux + "</LI>\n");
            }
            contenido.append("</UL>\n\n\n\n");
            contenido.append("<H4> En el siguiente enlace puede enviar el correo cambiando las palabras anteriores:</br>");
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