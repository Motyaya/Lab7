import java.net.MalformedURLException;
import java.net.URL;

public class URLDepthPair {
    URL url;
    int glubina;
    public static final String URL_PREFIX = "http://";

    public URLDepthPair(String url, int glubina) throws MalformedURLException {
        this.url = new URL(url);
        this.glubina = glubina;
    }

    public String toString(){
        String out = url + "\t" + glubina;
        return out;
    }

    public String getHost() {
        return url.getHost();
    }

    public String getPath() {
        return url.getPath();
    }

    public int getglubina() {
        return glubina;
    }

    public String getURLString() {
        return url.toString();
    }

}