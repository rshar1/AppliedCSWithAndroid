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

package com.google.engedu.wordladder;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

public class PathDictionary {
    private static final int MAX_WORD_LENGTH = 4;
    private static HashSet<String> words = new HashSet<>();
    private static HashMap<String, HashSet<String>> wordsGraph = new HashMap<>();
    private static final int MAX_PATH_LENGTH = 4;

    public PathDictionary(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        HashMap<String, ArrayList<String>> pathGraph = new HashMap<>();
        Log.i("Word ladder", "Loading dict");
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        Log.i("Word ladder", "Loading dict");
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() > MAX_WORD_LENGTH) {
                continue;
            }
            words.add(word);

            /*
            With this implementation, the word is stored the number of letters that it has times.
            Using a node, may be better
             */


            for (int i = 0; i < word.length(); i++) {
                String node = word.substring(0, i) + "_" + word.substring(i + 1);

                if (!pathGraph.containsKey(node)) {
                    pathGraph.put(node, new ArrayList<String>());
                }
                pathGraph.get(node).add(word);
            }

        }

        Log.i("Word Ladder", "Dict loaded");
        Log.i("Word Ladder", "Size of graph " + pathGraph.size());


        // Go through the list of nodes, adding each one to the graph
        /*
        Makes the edges between each node
         */

        for (ArrayList<String> connections: pathGraph.values()) {

            for (String word: connections) {

                if (!wordsGraph.containsKey(word)) {
                    wordsGraph.put(word, new HashSet<String>());
                }

                for (String neighbor: connections) {
                    if (neighbor != word) {
                        wordsGraph.get(word).add(neighbor);
                    }
                }

            }

        }

    }

    public boolean isWord(String word) {
        return words.contains(word.toLowerCase());
    }

    private ArrayList<String> neighbours(String word) {

        ArrayList<String> result = new ArrayList<>();

        if (wordsGraph.containsKey(word)) {
            result.addAll(wordsGraph.get(word));
        }

        return result;
    }

    public String[] findPath(String start, String end) {

        ArrayDeque<ArrayList<String>> explored = new ArrayDeque<>();

        ArrayList<String> firstPath = new ArrayList<>();
        firstPath.add(start);

        explored.push(firstPath);

        while (!explored.isEmpty()) {
            ArrayList<String> current = explored.pop();
            String lastInPath = current.get(current.size() - 1);
            if (lastInPath.equals(end)) {
                return current.toArray(new String[0]);
            } else if (current.size() < MAX_PATH_LENGTH){
                for (String neighbour: neighbours(lastInPath)) {
                    ArrayList<String> next = new ArrayList<String> (current);
                    next.add(neighbour);
                }
            }

        }

        return null;
    }
}
