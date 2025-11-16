package com.example.aiproject.game;

import com.example.aiproject.MainActivity;
//import com.example.aiproject.R;

import com.example.aiproject.ai.Response;
import com.example.aiproject.ai.Service;

import com.example.aiproject.game.character.Character;
import com.example.aiproject.game.character.Gear;
import com.example.aiproject.game.character.Player;
import com.example.aiproject.game.character.Stat;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.aiproject.game.character.Stat.*;

public class GameEngine implements Service
{
    private final MainActivity ui;

    private final StringBuilder text = new StringBuilder();
    private final String promptInstructions =
            "Dramatically describe how the following scene play out in 2-4 sentences using present tense, "
          + "always refer to the player as 'you', and do NOT mention specific numbers or stats:\n";

    private final List<Character> templates = new ArrayList<>();
    private final List<Gear>      gearLib = new ArrayList<>();

    private final Gear            proceed = new Gear("Proceed");

    private Player    player;
    private Character adversary;

    public GameEngine(MainActivity mainActivity, InputStream resources)
    {
        this.ui = mainActivity;
        this.loadAdversaries(resources);
    }

    public void loadAdversaries(InputStream stream)
    {
        templates.addAll(List.of(new Gson().fromJson(new InputStreamReader(stream), Character[].class)));
    }

    public void start()
    {
        player    = new Player(templates.get(0));
        adversary = new Character(RandomSuite.oneOf(templates.subList(1,templates.size())));
        prompt(promptInstructions + player.getName() + " enters a dungeon and encounters a "
                                  + adversary.getName() + ", bent on fighting them.");
    }

    public List<Option> listOptions()
    {
        List<Option> options = new ArrayList<>();
        if (adversary.isDead()) options.add(new Option(ui, proceed));
        else for (Gear gear : player.getActions()) {options.add(new Option(ui, gear));}
        return options;
    }

    public void submitOption(Option option) // todo return numerics
    {
        try
        {
            text.setLength(0);
            if (option.gear.equals(proceed)) resolveTravel(option.gear);
            else resolveCombat(option.gear);
            prompt(promptInstructions + text);
        }
        catch (Exception e) {ui.newText(e.getMessage());}
    }

    public void resolveCombat(Gear gear)
    {
        text.append(gear.use(player, adversary));
        if (adversary.isAlive()) text.append("\nMeanwhile ").append(adversary.act(player));
    }

    public void resolveTravel(Gear gear)
    {

    }

    public String getText()
    {
        return ""; // todo
    }

    public String writeStats(){return writeStats(GOLD, VIGOR, DEFENCE, ATHLETICS, INTELLIGENCE, WILLPOWER);}

    public String writeStats(Stat... stats)
    {
        text.setLength(0);
        text.append("<p>\t\t\t");
        for (Stat stat : stats) {text.append(player.writeStat(stat)).append("\t\t");}
        text.append("\n_________________________________________</p>");
        return text.toString();
    }

    @Override
    public void onServiceResponse(Response response)
    {
        ui.newText(response.getText());
    }
}
