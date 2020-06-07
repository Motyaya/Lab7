import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;

public class Crawler {
    static final String HREF_TAG = "<a href=\"http";
    static LinkedList<URLDepthPair> ProsmotrSite = new LinkedList<URLDepthPair>();
    static LinkedList<URLDepthPair> NeProsmSite = new LinkedList<URLDepthPair>();

    public static void crawl(String startURL, int maxglubina)
            throws MalformedURLException {

        URLDepthPair urlPair = new URLDepthPair(startURL, 0);
        NeProsmSite.add(urlPair);
        int glubina;
        HashSet<String> seenURLs = new HashSet<String>();
        seenURLs.add(startURL);

        //Пока есть непросмотренные - продолжаем
        while (!NeProsmSite.isEmpty()) {
            URLDepthPair currPair = NeProsmSite.removeFirst();
            glubina = currPair.getglubina();
            // Подключение
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress(currPair.getHost(), 80), 3000);
                sock.setSoTimeout(3000);
                System.out.println("Connected to " + currPair.getURLString());
                PrintWriter out =
                        new PrintWriter(sock.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                // Отправка запроса на сайт
                out.println("GET " + currPair.getPath() + " HTTP/1.1");
                out.println("Host: " + currPair.getHost());
                out.println("Connection: close");
                out.println();
                out.flush();

                // Взятие ссылок со страниц
                String line;
                int lineLength;
                int shiftIdx;
                while ((line = in.readLine()) != null) {
                    // Check if the current line has a link
                    boolean foundFullLink = false;
                    int idx = line.indexOf(HREF_TAG);
                    if (idx > 0) {
                        // Extract the link
                        StringBuilder sb = new StringBuilder();
                        shiftIdx = idx + 9;
                        char c = line.charAt(shiftIdx);
                        lineLength = line.length();
                        while (c != '"' && shiftIdx < lineLength - 1) {
                            sb.append(c);
                            shiftIdx++;
                            c = line.charAt(shiftIdx);
                            if (c == '"') {
                                foundFullLink = true;
                            }
                        }
                        String newUrl = sb.toString();
                        if (foundFullLink && glubina < maxglubina &&
                                !seenURLs.contains(newUrl)) {
                            URLDepthPair newPair =
                                    new URLDepthPair(newUrl, glubina + 1);
                            NeProsmSite.add(newPair);
                            seenURLs.add(newUrl);
                        }
                    }
                }
                sock.close();
                ProsmotrSite.add(currPair);
            }
            catch (IOException e) {
            }
        }
        for (URLDepthPair pair : ProsmotrSite) {
            System.out.println(pair.toString());
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        if (args.length != 2) {
            System.out.println("usage: java Crawler <URL> <maximum_depth>");
            return;
        }
        String startURL = args[0];
        int maxglubina = Integer.parseInt(args[1]);
        crawl(startURL, maxglubina);
    }
}