package com.example.aiproject.ai;

// structure generated using: https://json2csharp.com/code-converters/json-to-pojo

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Response {
    ArrayList<Candidate> candidates = new ArrayList<>();
    UsageMetadata UsageMetadataObject;
    private String modelVersion;
    private String responseId;

    // shortcut to what is actually wanted
    public String getText() {return candidates.get(0).content.parts.get(0).text;}
}

@Setter @Getter
class Candidate{
    public Content content;
    public String  finishReason;
    public double  avgLogprobs;
}

@Setter @Getter
class Content{
    public ArrayList<Part> parts;
    public String role;
}

@Setter @Getter
class Part{
    public String text;
}

@Setter @Getter
class UsageMetadata{
    public int promptTokenCount;
    public int candidatesTokenCount;
    public int totalTokenCount;
    public ArrayList<PromptTokensDetail>     promptTokensDetails;
    public ArrayList<CandidatesTokensDetail> candidatesTokensDetails;
}

@Setter @Getter
class PromptTokensDetail{
    public String modality;
    public int    tokenCount;
}

@Setter @Getter
class CandidatesTokensDetail{
    public String modality;
    public int    tokenCount;
}
