/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static int WORD_LENGTH = 3;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private Stack<LetterTile> placedTiles = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                // Only add the words that are of the desired length
                if (!sizeToWords.containsKey(word.length())) {
                    sizeToWords.put(word.length(), new ArrayList<String>());
                }
                sizeToWords.get(word.length()).add(word);

            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                moveTileToView(v);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    moveTileToView(v);
                    return true;
            }
            return false;
        }
    }

    private void moveTileToView(View v) {
        LetterTile tile = (LetterTile) stackedLayout.peek();
        tile.moveToViewGroup((ViewGroup) v);
        if (stackedLayout.empty()) {
            TextView messageBox = (TextView) findViewById(R.id.message_box);
            // todo Get the current Words and check if they are in the dictionary

            LinearLayout word1View = (LinearLayout) findViewById(R.id.word1);
            LinearLayout word2View = (LinearLayout) findViewById(R.id.word2);

            StringBuilder userWord1 = new StringBuilder();
            StringBuilder userWord2 = new StringBuilder();

            for (int i = 0; i < word1View.getChildCount(); i++) {
                LetterTile tile1 = (LetterTile) word1View.getChildAt(i);
                userWord1.append(tile1.getText());
            }

            for (int i = 0; i < word2View.getChildCount(); i++) {
                LetterTile tile1 = (LetterTile) word2View.getChildAt(i);
                userWord2.append(tile1.getText());
            }


            if (isValidWord(userWord1.toString()) && isValidWord(userWord2.toString())) {
                gameOver(userWord1.toString(), userWord2.toString());
            } else {
                messageBox.setText("Invalid Text..." + word1 + " " + word2);
            }

        }
        placedTiles.push(tile);
    }

    public boolean onStartGame(View view) {
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        LinearLayout word1View = (LinearLayout) findViewById(R.id.word1);
        LinearLayout word2View = (LinearLayout) findViewById(R.id.word2);

        word1View.removeAllViews();
        word2View.removeAllViews();
        stackedLayout.clear();

        messageBox.setText("Game started");

        ArrayList<String> words = sizeToWords.get(WORD_LENGTH);

        // Makes the word length longer every time user presses start game
        if (sizeToWords.containsKey(WORD_LENGTH + 1) && sizeToWords.get(WORD_LENGTH + 1).size() > 2) {
            WORD_LENGTH++;
        }

        // Choose two random words
        word1 = words.get(random.nextInt(words.size()));
        word2 = words.get(random.nextInt(words.size()));

        // Start from the end of the word so that they can immediately be added to the stack
        // in reverse order

        int index1 = word1.length() - 1;
        int index2 = word2.length() - 1;

        while (index1 >= 0 || index2 >= 0) {
            int chosen = 0;
            if (index1 >= 0 && index2 >= 0) {
                chosen = random.nextInt(2);
            } else if (index2 >= 0) {
                chosen = 1;
            }

            if (chosen == 0) {
                stackedLayout.push(new LetterTile(stackedLayout.getContext(), word1.charAt(index1--)));
            } else if (chosen == 1) {
                stackedLayout.push(new LetterTile(stackedLayout.getContext(), word2.charAt(index2--)));
            }

        }

        return true;
    }

    private boolean isValidWord(String word) {
        int wordLength = word.length();
        ArrayList<String> words = sizeToWords.get(wordLength);
        if (words == null) return false;
        return words.contains(word);
    }

    private void gameOver(String word1, String word2) {
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Correct: " + word1 + " " + word2);
    }

    public boolean onUndo(View view) {
        if (placedTiles.size() == 0) return true;
        LetterTile tile = placedTiles.pop();
        tile.moveToViewGroup(stackedLayout);
        return true;
    }
}
