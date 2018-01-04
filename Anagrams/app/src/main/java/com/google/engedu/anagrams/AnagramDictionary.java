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

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();


    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            String sortedWord = sortLetters(word);
            if (lettersToWord.containsKey(sortedWord)) {
                lettersToWord.get(sortedWord).add(word);
            } else {
                lettersToWord.put(sortedWord, new ArrayList<String>());
                lettersToWord.get(sortedWord).add(word);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        for (String word: wordList) {
            if (word.length() == targetWord.length() && sortLetters(word).equals(sortLetters(targetWord)))
                result.add(word);
        }
        return result;
    }

    private String sortLetters(String str) {
        char[] arr = str.toCharArray();
        Arrays.sort(arr);
        return new String(arr);
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();

        for (char c = 'a'; c <= 'z'; c++) {
            String sorted = sortLetters(word + c);

            if (lettersToWord.containsKey(sorted)) {
                for (String validAnagram : lettersToWord.get(sorted)) {

                    if (isGoodWord(validAnagram, word))
                        result.add(validAnagram);
                }
            }
        }

        return result;
    }

    public String pickGoodStarterWord() {

        int startPoint = random.nextInt(wordList.size());

        while (getAnagramsWithOneMoreLetter(wordList.get(startPoint)).size() < MIN_NUM_ANAGRAMS) {
            startPoint = (startPoint + 1) % wordList.size();
        }

        return wordList.get(startPoint);
    }
}
