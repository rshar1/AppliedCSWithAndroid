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

package com.google.engedu.palindromes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private HashMap<Range, PalindromeGroup> findings = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onFindPalindromes(View view) {
        findings.clear();
        EditText editText = (EditText) findViewById(R.id.editText);
        TextView textView = (TextView) findViewById(R.id.textView);
        String text = editText.getText().toString();
        text = text.replaceAll(" ", "");
        text = text.replaceAll("'", "");
        char[] textAsChars = text.toCharArray();
        if (isPalindrome(textAsChars, 0, text.length())) {
          textView.setText(text + " is already a palindrome!");
        } else {
            PalindromeGroup palindromes = breakIntoPalindromes(text.toCharArray(), 0, text.length());
            textView.setText(palindromes.toString());
        }
        return true;
    }

    private boolean isPalindrome(char[] text, int start, int end) {

        for (int i = 0; i < (end - start) / 2; i++) {
            if (text[start + i] != text[end - i - 1]) return false;
        }
        return true;
    }

    private PalindromeGroup breakIntoPalindromes(char[] text, int start, int end) {

        //return breakIntoPalindromesGreedy(text, start, end);

        //return breakIntoPalindromesRecursive(text, start, end);

        return breakIntoPalindromesDynamic(text, start, end);
    }

    private PalindromeGroup breakIntoPalindromesGreedy(char[] text, int start, int end) {

        PalindromeGroup bestGroup = null;

        while (start != end) {
            int i = end;
            while (i > start && !isPalindrome(text, start, i)) {
                i--;
            }
            if (bestGroup == null) {
                bestGroup = new PalindromeGroup(text, start, i);
            } else {
                bestGroup.append(new PalindromeGroup(text, start, i));
            }

            start = i;

        }
        return bestGroup;
    }

    private PalindromeGroup breakIntoPalindromesRecursive(char[] text, int start, int end) {
        PalindromeGroup bestGroup = null;

        if (end - start == 1) {
            return new PalindromeGroup(text, start, end);
        }

        // Recursive solution
        for (int i = start + 1; i <= end; i++){

            if (isPalindrome(text, start, i)) {
                PalindromeGroup group = new PalindromeGroup(text, start, i);
                group.append(breakIntoPalindromes(text, i, end));
                if (bestGroup == null || bestGroup.length() > group.length())
                    bestGroup = group;
            }

        }
        return bestGroup;

    }

    private PalindromeGroup breakIntoPalindromesDynamic(char[] text, int start, int end) {
        PalindromeGroup bestGroup = null;

        if (end - start == 1) {
            return new PalindromeGroup(text, start, end);
        }

        Range<Integer> range = new Range<>(start, end);
        if (findings.containsKey(range)) {
            return findings.get(range);
        }

        // Recursive solution
        for (int i = start + 1; i <= end; i++){

            if (isPalindrome(text, start, i)) {
                PalindromeGroup group = new PalindromeGroup(text, start, i);
                group.append(breakIntoPalindromes(text, i, end));
                if (bestGroup == null || bestGroup.length() > group.length())
                    bestGroup = group;
            }

        }

        findings.put(range, bestGroup);
        return bestGroup;

    }
}
