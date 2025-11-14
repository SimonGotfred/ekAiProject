package com.example.aiproject.game;

import com.example.aiproject.MainActivity;
import com.example.aiproject.R;
import com.example.aiproject.ai.Service;
import com.example.aiproject.game.character.CharacterBase;
import com.example.aiproject.game.character.Gear;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class GameEngine
{
    private final MainActivity ui;
    private final Service service;

    private final List<CharacterBase> templates;
    private final List<Gear> gearLib = new ArrayList<>();

    private CharacterBase player, adversary;

    public GameEngine(MainActivity mainActivity, Service service)
    {
        this.ui = mainActivity;
        this.service = service;
        templates = List.of(new Gson().fromJson(new InputStreamReader(ui.getResources().openRawResource(R.raw.adversaries)), CharacterBase[].class));
        player = templates.get(0); templates.remove(player);
        adversary = new CharacterBase(RandomSuite.oneOf(templates));
    }

    public List<Option> listOptions()
    {
        List<Option> options = new ArrayList<>();

        for (Gear gear : player.getGear())
        {
            options.add(new Option(ui, gear));
        }

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
