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

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {

        if (s.equals("")) {
            isWord = true;
            return;
        }

        String nextChar = s.substring(0, 1);

        if (!children.containsKey(nextChar)) {
            children.put(nextChar, new TrieNode());
        }

        children.get(nextChar).add(s.substring(1));

        return;
    }

    public boolean isWord(String s) {

        if (s.length() == 0) return isWord;

        String nextChar = s.substring(0 , 1);

        if (!children.containsKey(nextChar)) return false;
        return children.get(nextChar).isWord(s.substring(1));
    }

    public String getAnyWordStartingWith(String s) {
        ArrayList<String> words = new ArrayList<>();
        TrieNode root = this;
        StringBuilder prefix = new StringBuilder();

        while (prefix.length() < s.length()) {

            String current = s.substring(prefix.length(), prefix.length() + 1);
            if (!root.children.containsKey(current)) {
                return null;
            }
            else {
                root = root.children.get(current);
                prefix.append(current);
            }

        }

        // Fill the words list with words that start with this prefix
        traversal(prefix.toString(), root, words);

        // Choose a random word from the list of words
        return words.get((int) (Math.random() * words.size()));

    }

    private void traversal(String prefix, TrieNode node, ArrayList<String> words) {

        if (node.isWord && node.children.size() == 0) {
            words.add(prefix);
        } else {
            if (node.isWord) {
                words.add(prefix);
            }
            for (String key: node.children.keySet()) {
                traversal(prefix + key, node.children.get(key), words);
            }
        }

    }

    public String getGoodWordStartingWith(String s) {

        ArrayList<String> words = new ArrayList<>();
        TrieNode root = this;
        StringBuilder prefix = new StringBuilder();

        while (prefix.length() < s.length()) {

            String current = s.substring(prefix.length(), prefix.length() + 1);
            if (!root.children.containsKey(current)) {
                return null;
            }
            else {
                root = root.children.get(current);
                prefix.append(current);
            }

        }

        ArrayList<String> notWords = new ArrayList<>();
        for (String key: root.children.keySet()) {
            if (!root.children.get(key).isWord) {
                notWords.add(key);
            }
        }

        if (!notWords.isEmpty()) {
            String myKey = notWords.get((int)(Math.random() * notWords.size()));
            traversal(prefix + myKey, root.children.get(myKey), words);
        } else {
            traversal(prefix.toString(), root, words);
        }

        // Choose a random word from the list of words
        return words.get((int) (Math.random() * words.size()));

    }
}
