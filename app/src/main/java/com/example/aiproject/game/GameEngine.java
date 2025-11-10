package com.example.aiproject.game;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class GameEngine
{
    private final Context context;

    public GameEngine(Context context)
    {
        this.context = context;
    }

    public List<Option> listOptions()
    {
        List<Option> options = new ArrayList<>();

        // todo

        return options;
    }

    public void submitOption(Option option)
    {
        // todo
    }

    public String getText()
    {
        return ""; // todo
    }
}
