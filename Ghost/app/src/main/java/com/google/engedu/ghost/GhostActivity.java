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

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            dictionary = new SimpleDictionary(assetManager.open("words.txt"));
        } catch (Exception exc) {
            // todo exception thrown
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn again
        TextView text = (TextView) findViewById(R.id.ghostText);
        String fragment = (String) text.getText();

        if (fragment.length() >= 4 && dictionary.isWord(fragment)) {
            label.setText("Computer wins - Thats a word");
            return;
        } else {

            String possibleWord = dictionary.getGoodWordStartingWith(fragment);
            if (possibleWord == null) {
                label.setText("Computer wins - Not a prefix");
                return;
            }
            Log.i("GhostActivity-Computer", possibleWord);
            text.setText(possibleWord.substring(0, fragment.length() + 1));
        }


        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i("GhostActivity", "Keycode: " + keyCode);


        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {

            if (!userTurn) return true;

            TextView text = (TextView) findViewById(R.id.ghostText);
            TextView status = (TextView) findViewById(R.id.gameStatus);

            String currentFragment = (String) text.getText() + ((char) (keyCode + ('a' - KeyEvent.KEYCODE_A)));
            text.setText(currentFragment);

            if (dictionary.isWord(currentFragment)) {
                status.setText("Game Over - " + currentFragment + " is a word");
            } else {
                userTurn = false;
                computerTurn();
            }

            return true;

        } else {
            return super.onKeyUp(keyCode, event);
        }

    }

    public void challenge(View view) {

        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView status = (TextView) findViewById(R.id.gameStatus);
        String fragment = (String) text.getText();

        if (fragment.length() >= 4 && dictionary.isWord(fragment)) {
            status.setText("User Wins!");
        }
        else if (dictionary.getAnyWordStartingWith(fragment) != null) {
            status.setText("Computer wins - Possible Word: " + dictionary.getAnyWordStartingWith(fragment));
        } else {
            status.setText("User wins...No words can be formed");
        }

    }
}
