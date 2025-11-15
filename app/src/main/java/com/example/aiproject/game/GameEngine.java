package com.example.aiproject.game;

import com.example.aiproject.MainActivity;
import com.example.aiproject.R;
import com.example.aiproject.ai.Service;
import com.example.aiproject.game.character.Character;
import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.character.Player;
import com.example.aiproject.game.character.Stat;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.aiproject.game.character.Stat.*;

public class GameEngine
{
    private final MainActivity ui;
    private final Service service;

    private final StringBuilder text = new StringBuilder();

    private final List<Character> templates;
    private final List<Gear>      gearLib = new ArrayList<>();

    private List<String> turns = new ArrayList<>(); // todo

    private Player    player;
    private Character adversary;

    public GameEngine(MainActivity mainActivity, Service service)
    {
        this.ui = mainActivity;
        this.service = service;

        templates = List.of(new Gson().fromJson(new InputStreamReader(ui.getResources().openRawResource(R.raw.adversaries)), Character[].class));

        player    = new Player(templates.get(0));
        adversary = new Character(RandomSuite.oneOf(templates.subList(1,templates.size())));
    }

    public void start()
    {
        service.prompt("Describe the following scene in 4 sentences: " + "You (" + player.getDescription() + ") enter a dungeon and "
                               + "encounter a " + adversary.getName() + ", bent on fighting you.");
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

    public void submitOption(Option option) // todo return numerics
    {
        String result = option.gear.use(player,adversary);
        service.prompt("Describe the following scene in 2 sentences: " + result);
    }

    public String getText()
    {
        return ""; // todo
    }

    public String writeStats(){return writeStats(GOLD, VIGOR, DEFENCE, ATHLETICS, INTELLIGENCE, WILLPOWER);}

    public String writeStats(Stat... stats)
    {
        text.setLength(0);
        for (Stat stat : stats) {text.append("\t\t").append(player.writeStat(stat));}
        return text.toString();
    }
}
