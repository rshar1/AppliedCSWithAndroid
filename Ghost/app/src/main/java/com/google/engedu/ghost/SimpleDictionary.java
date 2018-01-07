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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        if (prefix.length() == 0) return words.get((int)(Math.random() * words.size()));

        int index = binarySearch(prefix);

        if (index >= 0) return words.get(index);

        return null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;

        if (prefix.length() == 0) return words.get((int)(Math.random() * words.size()));

        int mid = binarySearch(prefix);

        if (mid < 0) return null;

        int lower = mid;
        int upper = mid + 1;
        ArrayList<String> odd = new ArrayList<>();
        ArrayList<String> even = new ArrayList<>();
        String current = "";

        while (lower >= 0 && (current = words.get(lower)).contains(prefix)) {
            if (current.length() % 2 == 0) even.add(current);
            else odd.add(current);
            lower--;
        }

        while (upper < words.size() && (current = words.get(upper)).contains(prefix)) {
            if (current.length() % 2 == 0) even.add(current);
            else odd.add(current);
            upper++;
        }

        if (odd.size() == 0 || (prefix.length() % 2 == 0 && even.size() != 0)) {
            return even.get((int)(Math.random() * even.size()));
        } else {
            return odd.get((int)(Math.random() * odd.size()));
        }

    }

    private int binarySearch(String prefix) {

        int min = 0;
        int max = words.size() - 1;

        while (min <= max) {

            int mid = min + (max - min) / 2;
            String midWord = words.get(mid);

            if (midWord.contains(prefix)) {
                return mid;
            } else if (midWord.compareTo(prefix) < 0) {
                min = mid + 1;
            } else {
                max = mid - 1;
            }

        }

        return -1;

    }

}
