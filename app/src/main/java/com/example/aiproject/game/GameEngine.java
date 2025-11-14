package com.example.aiproject.game;

import com.example.aiproject.MainActivity;
import com.example.aiproject.R;
import com.example.aiproject.ai.Service;
import com.example.aiproject.game.character.Character;
import com.example.aiproject.game.character.Gear;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameEngine
{
    private final MainActivity ui;
    private final Service service;

    private final List<Character> templates;
    private final List<Gear>      gearLib = new ArrayList<>();

    private Character player, adversary;

    public GameEngine(MainActivity mainActivity, Service service)
    {
        this.ui = mainActivity;
        this.service = service;
        templates = List.of(new Gson().fromJson(new InputStreamReader(ui.getResources().openRawResource(R.raw.adversaries)), Character[].class));
        player = templates.get(0); templates.remove(player);
        adversary = new Character(RandomSuite.oneOf(templates));
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
