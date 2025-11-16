package com.example.aiproject.ai;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import lombok.Getter;

public interface Service
{
    // using a published FREE api-key (env-variables do not work with android)
    String apiKey   = "AIzaSyApwQZtXL4hv94gLGo4JP-2havj96HLcx0";
    String endPoint =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

//    Map<Message, Response> responses = new LinkedHashMap<>();
    List<Response> responses = new ArrayList<>();

            void onServiceResponse (Response response);
    default void onServiceException(Exception exception){throw new RuntimeException(exception);}

    default Thread prompt(String prompt)
    {
        Thread thread = new Thread()
        {
            @Override public void run()
            {
                URL url;
                HttpURLConnection connection = null;
                String body = "{\"contents\": [{\"parts\": [{\"text\": \""+prompt+"\"}]}]}"; // todo: proper bodybuilding

                try
                {
                    url = new URL(endPoint);
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
                    Response response = new Gson().fromJson(input, Response.class);
                    connection.disconnect();

                    responses.add(response);
                    onServiceResponse(response);
                }
                catch (Exception e)
                {
                    // close connection before letting 'client' spend time handling exception.
                    if (connection != null) connection.disconnect();
                    onServiceException(e);
                }
                finally
                {
                    if (connection != null) connection.disconnect(); // close connection finally - just in case
                }
            }
        };
        thread.start();
        return thread;
    }
}
