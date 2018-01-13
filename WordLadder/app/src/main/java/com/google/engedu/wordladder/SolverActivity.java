package com.google.engedu.wordladder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.engedu.worldladder.R;

import java.util.ArrayList;


/**
 * Created by rshar1 on 1/12/18.
 */

public class SolverActivity extends AppCompatActivity {

    private String[] wordList;
    private TextView mStartTextView;
    private TextView mEndTextView;
    private LinearLayout layout;
    private ArrayList<EditText> userEditTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_solver);

        mStartTextView = (TextView) findViewById(R.id.startTextView);
        mEndTextView = (TextView) findViewById(R.id.endTextView);
        layout = (LinearLayout) findViewById(R.id.solver_layout);

        Log.d("Solver Activity", "onCreate: checkpoint");

        Intent intent = getIntent();
        wordList = intent.getStringArrayExtra(WordSelectionActivity.EXTRA_MESSAGE);

        mStartTextView.setText(wordList[0]);
        mEndTextView.setText(wordList[wordList.length - 1]);

        userEditTexts = new ArrayList<>();

        for (int i = 1; i < wordList.length - 1; i++) {
            EditText editText = new EditText(this);
            userEditTexts.add(editText);
            layout.addView(editText, i);
        }

    }

    public void onSolve(View view) {

        for (int i = 1; i < wordList.length - 1; i++) {
            userEditTexts.get(i - 1).setText(wordList[i]);
        }

    }
}
