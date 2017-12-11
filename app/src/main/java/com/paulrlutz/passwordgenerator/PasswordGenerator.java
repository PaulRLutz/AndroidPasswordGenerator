package com.paulrlutz.passwordgenerator;

import android.content.SharedPreferences;
import android.util.Log;

import com.paulrlutz.passwordgenerator.Exceptions.WordsTooLongOrTooManyException;
import com.paulrlutz.passwordgenerator.Exceptions.WordsTooShortOrTooFewException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PasswordGenerator {
    private static final String TAG = "paulrlutz.pass.gen";


    public enum GroupLocation {
        BEGINNING, END, RANDOM
    }

    public enum Capitalization {
        CAMEL, ALL, NONE
    }

    public static String generatePassword(SharedPreferences prefs,
            String separator, int passwordLength, int numWords, int minWordLength, int maxWordLength, Capitalization capsType,
            char[] numbersArray, int numNumbers, boolean numbersGrouped, GroupLocation numbersLocation,
            char[] specialCharsArray, int numSpecialChars, boolean specialCharsGrouped, GroupLocation specialCharsLocation
        ) throws WordsTooLongOrTooManyException, WordsTooShortOrTooFewException {

        WordListManager wlm = new WordListManager();

        int specialCharsCharNum = numSpecialChars;
        if (numSpecialChars == 0) {
            specialCharsCharNum = 0;
        } else if (specialCharsGrouped) {
            specialCharsCharNum += separator.length();
        } else {
            specialCharsCharNum += (numSpecialChars * separator.length());
        }

        int numbersCharNum = numNumbers;

        if (numNumbers == 0) {
            numbersCharNum = 0;
        } else if (numbersGrouped) {
            numbersCharNum += separator.length();
        } else {
            numbersCharNum += (numNumbers * separator.length());
        }

        int wordsSeparators = numWords * separator.length() + separator.length();
        Log.d(TAG, "Special chars num: " + specialCharsCharNum);
        Log.d(TAG, "Numbers chars num: " + numbersCharNum);
        Log.d(TAG, "Word separators: " + wordsSeparators);

        int wordsCharNum = passwordLength - (specialCharsCharNum + numbersCharNum + wordsSeparators - separator.length()*2);

        Log.d(TAG, "Char length: " + passwordLength);
        Log.d(TAG, "Number of words: " + numWords);
        Log.d(TAG, "Words char num: " + wordsCharNum);

        if (minWordLength * numWords > wordsCharNum) {
            throw new WordsTooShortOrTooFewException("");
        }
        if (maxWordLength * numWords < wordsCharNum) {
            throw new WordsTooLongOrTooManyException("");
        }

        int leftovers = wordsCharNum - (numWords * minWordLength);
        int[] wordLengths = new int[numWords];
        Arrays.fill(wordLengths, minWordLength);
        Log.d(TAG, "leftovers:" + leftovers);

        for (int i = 0; i < wordLengths.length; i++) {
            Log.d(TAG, "word: " + i + " length: " + wordLengths[i]);
        }

        int sentinel = 0;
        while(leftovers > 0) {
            //Log.d(TAG, "leftovers : " + leftovers);
            int wordDeltaMax = maxWordLength - minWordLength;
            //Log.d(TAG, "word delta max : " + wordDeltaMax);
            if (wordDeltaMax > leftovers) {
                wordDeltaMax = leftovers;
                //Log.d(TAG, "word delta max : " + wordDeltaMax);
            }
            int wordDelta = 0;
            if (wordDeltaMax >= 1) {
                wordDelta = Utilities.rand(0, wordDeltaMax);
            }
            Log.d(TAG, "word delta : " + wordDelta);
            wordLengths[sentinel] += wordDelta;
            leftovers -= wordDelta;
            sentinel++;
            if (sentinel > wordLengths.length-1) {
                sentinel = 0;
            }
        }

        for (int i = 0; i < wordLengths.length; i++) {
            Log.d(TAG, "word: " + i + " length: " + wordLengths[i]);
        }

        ArrayList<String> chunks = new ArrayList<>();
        for (int i = 0; i < wordLengths.length; i++ ) {
            int wordLength = wordLengths[i];
            String randomWord = wlm.getWordFromWordList(prefs, null, wordLength);
            Log.d(TAG, "Random word: '" + randomWord + "'");
            chunks.add(randomWord);
        }
        ArrayList<String> specialChars = new ArrayList<>();
        for (int i = 0; i < numSpecialChars; i++ ) {
            specialChars.add(specialCharsArray[Utilities.rand(0, specialCharsArray.length - 1)] + "");
        }
        ArrayList<String> numbers = new ArrayList<>();
        for (int i = 0; i < numNumbers; i++ ) {
            numbers.add(numbersArray[Utilities.rand(0, numbersArray.length - 1)] + "");
        }
        if (numbers.size() > 0) {
            chunks.addAll(numbers);
        }
        if (specialChars.size() > 0) {
            chunks.addAll(specialChars);
        }
        switch (specialCharsLocation) {
            case BEGINNING:
                break;
            case END:
                break;
            case RANDOM:
                break;
        }

        String pass = "";
        for (int i = 0; i < chunks.size(); i++) {
            if (i == chunks.size()-1) {
                pass += chunks.get(i);
            } else {
                pass += chunks.get(i) + separator;
            }
        }

        Log.d(TAG, "Password length: " + pass.length());
        Log.d(TAG, "Password: " + pass);
        Log.d(TAG, " ");
        Log.d(TAG, " ");
        Log.d(TAG, " ");

        return pass;
    }

}







