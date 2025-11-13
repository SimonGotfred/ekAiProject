package com.example.aiproject.ai;

import com.example.aiproject.MainActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import lombok.Getter;

public class Service
{
    // using a published FREE api-key (env-variables do not work with android)
    private static final String apiKey = "AIzaSyApwQZtXL4hv94gLGo4JP-2havj96HLcx0";
    private static final String spec =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private final   URL          url;
    private final   MainActivity main;
    @Getter private boolean      busy = false;

    public Service(MainActivity mainActivity)
    {
        try   {url = new URL(spec);}
        catch (MalformedURLException e) {throw new RuntimeException(e);}
        this.main = mainActivity;
    }

    public Response prompt(String prompt)
    {
        if (busy) return null;
        busy = true; // block new requests during main thread

        final Response[] response = new Response[1];
        Thread thread = new Thread(new Runnable()
        {
            @Override public void run()
            {
                String body = "{\"contents\": [{\"parts\": [{\"text\": \""+prompt+"\"}]}]}";
                HttpURLConnection connection = null;

                try
                {
                    connection = (HttpsURLConnection) url.openConnection();

                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setChunkedStreamingMode(0);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("x-goog-api-key", apiKey);

                    OutputStream outputStream = connection.getOutputStream();
                    byte[] output = body.getBytes();
                    outputStream.write(output, 0, output.length);

                    if (connection.getResponseCode() != 200) throw new ConnectException("Bad Status: " + connection.getResponseCode());

                    BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    response[0] = new Gson().fromJson(input, Response.class);

//                    StringBuilder response = new StringBuilder();
//                    String responseLine = null;
//                    while ((responseLine = br.readLine()) != null)
//                    {
//                        response.append(responseLine.trim());
//                    }
//                    JSONObject json = new JSONObject(response.toString());

                    main.setText(response[0].getText());
                }
                catch (Exception e)
                {
                    main.setText(e.getMessage());
                }
                finally
                {
                    if (connection != null) connection.disconnect();
                    busy = false; // open for new requests after threat finishes
                }
            }
        });

        thread.start();
        return response[0];
    }
}
