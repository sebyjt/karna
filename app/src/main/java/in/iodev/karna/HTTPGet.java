package in.iodev.karna;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by seby on 8/19/2018.
 */

public class HTTPGet {
    public static String getJsonResponse(String myUrl) throws IOException
    {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        URL url = new URL(myUrl);
        connection = (HttpURLConnection)
                url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }
        return buffer.toString();
    }
}
