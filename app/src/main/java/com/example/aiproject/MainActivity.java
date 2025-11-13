package com.example.aiproject;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aiproject.ai.Response;
import com.example.aiproject.ai.Service;
import com.example.aiproject.game.GameEngine;
import com.example.aiproject.game.Option;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity
{
    private Service      service;
    private GameEngine   game;
    private TextView     textView;
    private LinearLayout optionView;
    private ArrayList<Response> responses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        optionView = findViewById(R.id.OptionCont);
        textView   = findViewById(R.id.TextWindow);
        textView.setTextSize(16);

        service = new Service(this);
        game    = new GameEngine(this);
    }

    public void onButton(View view) // initial start-button
    {
        responses.add(service.prompt("Write a short story about an octopus disguised as a school teacher"));
    }

    public void addButton(Option option)
    {
        // add button functionality
        option.setOnClickListener(v ->
        {
            game.submitOption(option);
            setText(game.getText());
            refreshButtons();
        });

        // add button to the view
        optionView.addView(option);
    }

    public void addMultipleBtn(List<Option> options)
    {
        for (Option option : options)
        {
            addButton(option);
        }
    }

    public void removeButton(Button btn)
    {
        optionView.removeView(btn);
    }

    public void removeAllBtn()
    {
        optionView.removeAllViews();
    }

    public void refreshButtons()
    {
        removeAllBtn();
        addMultipleBtn(game.listOptions());
    }

    public void setText(String str)
    {
        textView.setText(Html.fromHtml(str));
    }

    public String getText()
    {
        return (String)textView.getText();
    }

    public void addText(String str)
    {
        setText(getText() + "\n_______________________________\n\n" + str);
    }

    public void clearText()
    {
        textView.setText("");
    }
}