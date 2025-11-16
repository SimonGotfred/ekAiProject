package com.example.aiproject.game;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.aiproject.game.character.Gear;

public class Option extends androidx.appcompat.widget.AppCompatButton
{
    final Gear gear;

    public Option(Context context, Gear gear)
    {
        super(context);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.setTextSize(12);

        // make button slimmer
        float pad = context.getResources().getDisplayMetrics().density;
        int padP = (int)(0*pad);
        this.setMinHeight((int)(1*pad));
        this.setPadding(padP,padP,padP,padP);

        this.gear = gear;
        this.setText(this.gear.toString());
    }
}
