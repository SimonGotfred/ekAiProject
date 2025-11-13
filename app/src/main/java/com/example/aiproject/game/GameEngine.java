package com.example.aiproject.game;

import com.example.aiproject.MainActivity;
import com.example.aiproject.ai.Service;

import java.util.ArrayList;
import java.util.List;

public class GameEngine
{
    private final MainActivity ui;
    private final Service service;

    public GameEngine(MainActivity mainActivity, Service service)
    {
        this.ui = mainActivity;
        this.service = service;
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
