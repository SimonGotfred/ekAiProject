package com.example.aiproject.ai;

import lombok.Getter;

@Getter
public class Message
{
    final   String   prompt, api;
    private Response response;

    public Message(String prompt, String api)
    {
        this.prompt = prompt;
        this.api    = api;
    }

    public String getBody()
    {
        return  "{\"contents\": [{\"parts\": [{\"text\": \""+prompt+"\"}]}]}";
    }

    public byte[] getOutput() {return getBody().getBytes();}
    public String getResponseText() {return response==null ? "" : response.getText();}

    public void setResponse(Response response)
    {
        if (this.response != null) return;
        response.setMessage(this);
        this.response = response;
    }
}
