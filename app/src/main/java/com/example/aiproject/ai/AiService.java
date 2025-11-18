package com.example.aiproject.ai;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.example.aiproject.ai.ApiKey.*;

public interface AiService
{
    // url instanced as array to let interface's *final* value be "instanced" after interface initialization.
    // this is because exceptions, eg. caused by URL instantiation, cannot be caught during interface initialization.
    URL[] url = new URL[]{null};

    // list of API correspondence, logged for... *prosperity*
    LinkedHashMap<Message, Response> responses = new LinkedHashMap<>();

    void onServiceResponse (Response response);
    void onServiceException(Throwable e);

    default Thread prompt(String prompt)
    {
        if (url[0]==null) // null-check faster than instancing new URL each prompt
        {
            try {url[0] = new URL(GEMINI.api);}
            catch (MalformedURLException e)
            {throw new RuntimeException(e);}
        }

        Thread thread = new Thread()
        {
            @Override public void run()
            {
                HttpURLConnection connection = null;
                Message message = new Message(prompt, GEMINI.api); // todo: proper bodybuilding
                byte[] output = message.getOutput();

                try
                {
                    connection = (HttpsURLConnection) url[0].openConnection();

                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setChunkedStreamingMode(0);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("x-goog-api-key", GEMINI.apiKey);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(output, 0, output.length);

                    if (connection.getResponseCode() != 200) throw new ConnectException("Bad Status: " + connection.getResponseCode());

                    BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Response response = new Gson().fromJson(input, Response.class);
                    connection.disconnect();

                    message.setResponse(response); // associate message and response through each other
                    responses.put(message,response);
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
