package com.raamisharif.scarnesdice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        int numRolled = rollAndDisplay();

        TextView label = (TextView) findViewById(R.id.textView);
        if (numRolled == 1) {
            userTurnScore = 0;
            label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore);
            computerTurn();
        } else {
            userTurnScore += numRolled;
            label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore + " Your turn score: " + userTurnScore);
        }


    }

    /** Called when the user clicks the hold button */
    public void hold(View view) {
        userOverallScore += userTurnScore;
        userTurnScore = 0;
        TextView label = (TextView) findViewById(R.id.textView);
        label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore);
        checkGameOver();
        computerTurn();
    }

    /** Called when the user clicks the reset button */
    public void reset(View view) {
        userTurnScore = userOverallScore = compOverallScore = compTurnScore = 0;
        TextView label = (TextView) findViewById(R.id.textView);
        label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore);
        Button btnRoll = (Button) findViewById(R.id.button);
        Button btnHold = (Button) findViewById(R.id.button2);
        btnHold.setEnabled(true);
        btnRoll.setEnabled(true);
    }


    public void computerTurn() {
        Button btnRoll = (Button) findViewById(R.id.button);
        Button btnHold = (Button) findViewById(R.id.button2);
        TextView label = (TextView) findViewById(R.id.textView);

        btnRoll.setEnabled(false);
        btnHold.setEnabled(false);

        int numRolled = 0;
        while (compTurnScore < 20 && compTurnScore + compOverallScore < 100 && (numRolled = rollAndDisplay()) != 1) {
            compTurnScore += numRolled;
            label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore + " Computer turn score: " + compTurnScore);
            // todo wait
        }

        if (numRolled != 1) {
            compOverallScore += compTurnScore;
            label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore + " Computer holds");
        } else {
            label.setText("Your Score: " + userOverallScore + " Computer Score: " + compOverallScore + " Computer rolled 1");
        }

        compTurnScore = 0;
        btnHold.setEnabled(true);
        btnRoll.setEnabled(true);
        checkGameOver();
    }

    private int rollAndDisplay() {
        int numRolled = (int) (Math.random() * 6);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(dice[numRolled]);
        return numRolled + 1;
    }

    private void checkGameOver() {
        if (userOverallScore < 100 && compOverallScore < 100) return;

        Button btnRoll = (Button) findViewById(R.id.button);
        Button btnHold = (Button) findViewById(R.id.button2);
        TextView label = (TextView) findViewById(R.id.textView);

        if (userOverallScore >= 100) {
            label.setText("You win!");
        }
        else if (compOverallScore >= 100) {
            label.setText("Computer wins!");
        }

        btnHold.setEnabled(false);
        btnRoll.setEnabled(false);
    }

}
