package com.example.aiproject.ai;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Service
{
    private final String apiKey = "AIzaSyApwQZtXL4hv94gLGo4JP-2havj96HLcx0";
    private final String spec = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final URL url = new URL(spec);

    public Service() throws MalformedURLException {}

    public String prompt(String prompt)
    {
        String body = "{\"contents\": [{\"parts\": [{\"text\": \""+prompt+"\"}]}]}";

        HttpURLConnection connection = null;
        try
        {
            connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("x-goog-api-key", apiKey);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream os = connection.getOutputStream();
            byte[] input = body.getBytes();
            os.write(input, 0, input.length);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null)
            {
                response.append(responseLine.trim());
            }

            JSONObject json = new JSONObject(response.toString());
            return json.getString("text");
        }
        catch (IOException | JSONException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }
}
