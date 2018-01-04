package com.raamisharif.scarnesdice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int userOverallScore = 0;
    private int userTurnScore = 0;
    private int compOverallScore = 0;
    private int compTurnScore = 0;
    private int[] dice = {
            R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**  Called when the user clicks the roll button */
    public void roll(View view) {

        int numRolled = (int) (Math.random() * 6);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(dice[numRolled]);

        TextView label = (TextView) findViewById(R.id.textView);
        if (numRolled == 0) {
            userTurnScore = 0;
            label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore);
        } else {
            userTurnScore += numRolled + 1;
            label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore + " Your turn score: " + userTurnScore);
        }

    }

    /** Called when the user clicks the hold button */
    public void hold(View view) {
        userOverallScore += userTurnScore;
        userTurnScore = 0;
        TextView label = (TextView) findViewById(R.id.textView);
        label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore);
    }

    /** Called when the user clicks the reset button */
    public void reset(View view) {
        userTurnScore = userOverallScore = compOverallScore = compTurnScore = 0;
        TextView label = (TextView) findViewById(R.id.textView);
        label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore);
    }

}
