package com.example.aiproject;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private LinearLayout optionView;

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

        game       = new GameEngine(this);
        textView   = findViewById(R.id.TextWindow);
        optionView = findViewById(R.id.OptionCont);

        textView.setTextSize(16);
    }

    public void onButton(View view) // initial start-button
    {
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
        String strM = getText();
        setText(getText() + "\n_______________________________\n\n" + str);
    }

    public void clearText()
    {
        textView.setText("");
    }
}