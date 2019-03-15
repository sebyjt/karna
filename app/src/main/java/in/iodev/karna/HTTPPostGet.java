package in.iodev.karna;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by seby on 8/19/2018.
 */

public class HTTPPostGet
{
    public static String getJsonResponse(String myUrl,String StringData) throws IOException
    {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection Error!
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(StringData);
        writer.flush();
        writer.close();
        os.close();

        // 4. make POST request to the given URL
        conn.connect();
        Log.d("Responsefrom1st",conn.getResponseMessage().toString());

        int responseCode = conn.getResponseCode();
        Log.d("Response Code:", String.valueOf(responseCode));
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {

                sb.append(line);
                break;
            }
            in.close();

            Log.d("Response", sb.toString());
            return sb.toString();

        } else {
            return null;
        }
    }


}