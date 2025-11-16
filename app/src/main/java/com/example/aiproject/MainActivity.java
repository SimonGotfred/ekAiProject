package com.example.aiproject;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.aiproject.game.GameEngine;
import com.example.aiproject.game.Option;

import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity
{
    private GameEngine   game;
    private TextView     textView;
    private ScrollView   textScroll;
    private LinearLayout optionView;
    private Resources    resources;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); // todo: save game progress between instances?
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        optionView = findViewById(R.id.OptionCont);
        textScroll = findViewById(R.id.TextScroll);
        textView   = findViewById(R.id.TextWindow);
        resources  = getResources();

        game = new GameEngine(this, resources.openRawResource(R.raw.adversaries));
    }

    public void newText(String text)
    {
        setText(game.writeStats() + "<p>" + text + "</p>");
    }

    public void addText(String text)
    {
        setText(getText() + game.writeStats() + "<p>" + text + "</p>");
    }

    private void setText(String text)
    {
        runOnUiThread(() -> {textView.setText(Html.fromHtml(text));refreshButtons();});
    }

    public void clearText()
    {
        runOnUiThread(() -> textView.setText(""));
    }

    public String getText()
    {
        return Html.escapeHtml(textView.getText());
    }

    public void onButton(View view) // initial start-button
    {
        clearButtons();
        try {game.start();}
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void addButton(Option option)
    {
        // add button functionality
        option.setOnClickListener(v ->
        {
            clearButtons();
            game.submitOption(option);
        });

        // add button to the ui
        optionView.addView(option);
    }

    public void addMultipleBtn(List<Option> options)
    {
        for (Option option : options)
        {
            addButton(option);
        }
    }

    public void refreshButtons()
    {
        clearButtons();
        addMultipleBtn(game.listOptions());
    }

    public void removeButton(Button button)
    {
        optionView.removeView(button);
    }

    public void clearButtons()
    {
        optionView.removeAllViews();
    }
}