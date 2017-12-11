package com.paulrlutz.passwordgenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


// https://github.com/first20hours/google-10000-english
public class WordListManager {
    private static final String TAG = "paulrlutz.pass.wlm";

    private static final String WORDS_LIST_FOLDER = "PaulRLutz_PasswordManager" + File.separator + "word_lists";
    private static final String WORDS_LIST_PARTS_FOLDER = "PaulRLutz_PasswordManager" + File.separator + "word_lists_parts";
    private static final String DEFAULT_WORD_LIST = "google_20k.txt";

    public boolean setup(Context context, SharedPreferences prefs) {
        setupFolders();

        File defaultWordList = new File(Environment.getExternalStorageDirectory(), DEFAULT_WORD_LIST);
        if (!defaultWordList.exists()) {
            copyFileFromAssets(context, DEFAULT_WORD_LIST, getWordListsFolder());
        }

        File wordListsFolder = getWordListsFolder();
        File[] wordLists = wordListsFolder.listFiles();
        for (File wordList : wordLists) {
            Log.d(TAG, "Processing word list file: " + wordList.getAbsolutePath());
            processWordList(wordList, prefs);
        }

        return true;
    }

    private void processWordList(File wordList, SharedPreferences prefs) {
        String md5Sum = MD5.calculateMD5(wordList);
        String oldMd5Sum = prefs.getString(wordList.getName() + "_md5sum", null);
        if (oldMd5Sum == null || !oldMd5Sum.equals(md5Sum)) {
            Log.d(TAG, "Word list new or updated: " + wordList.getName());
            cleanupWordListParts(wordList);
            HashMap<Integer, ArrayList<String>> sortedWordList = new HashMap();
            // TODO close everything properly
            try(BufferedReader br = new BufferedReader(new FileReader(wordList))) { // https://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
                for(String line; (line = br.readLine()) != null; ) {
                    Log.d(TAG, line + " : " + line.length());
                    if (!sortedWordList.containsKey(line.length())) {
                        sortedWordList.put(line.length(), new ArrayList<String>());
                    }
                    sortedWordList.get(line.length()).add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            File wordListsPartsFolder = getWordListsPartsFolder();
            for (Integer key : sortedWordList.keySet()) {
                File wordListPartFile = new File (wordListsPartsFolder, getWordListPartFilename(wordList.getName(), key));
                writeArrayListToFile(sortedWordList.get(key), wordListPartFile);
                prefs.edit().putInt(wordListPartFile.getName() + "_length", sortedWordList.get(key).size()).apply();
            }
        }
    }

    private void writeArrayListToFile(ArrayList<String> arrayList, File file) {
        Log.d(TAG, "Trying to write to file: " + file.getAbsolutePath());
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(file));
            for (String text : arrayList) {
                out.println(text);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing array list to file: " + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void cleanupWordListParts(File wordList) {
        for (File file : getWordListsPartsFolder().listFiles()) {
            if (file.getName().startsWith(wordList.getName())) {
                file.delete();
            }
        }
    }

    // https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard
    private void copyFileFromAssets(Context context, String assetsFileName, File dest) {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(assetsFileName);
            out = new FileOutputStream(new File(dest, assetsFileName));
            copyFile(in, out);
        } catch(IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + assetsFileName, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private File getWordListsFolder() {
        File wordsListsFolder = new File(Environment.getExternalStorageDirectory(), WORDS_LIST_FOLDER);
        return wordsListsFolder;
    }

    private File getWordListsPartsFolder() {
        File wordsListsPartsFolder = new File(Environment.getExternalStorageDirectory(), WORDS_LIST_PARTS_FOLDER);
        return wordsListsPartsFolder;
    }

    private void setupFolders() {
        File wordListsFolder = getWordListsFolder();
        if (!wordListsFolder.exists()) {
            wordListsFolder.mkdirs();
        }
        File wordListsPartsFolder = getWordListsPartsFolder();
        if (!wordListsPartsFolder.exists()) {
            wordListsPartsFolder.mkdirs();
        }
    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public String getWordFromWordList(SharedPreferences prefs, String wordList, int wordLength) {
        if (wordList == null) {
            wordList = DEFAULT_WORD_LIST;
        }
        int numberOfLines = prefs.getInt(getWordListPartFilename(wordList, wordLength) + "_length", -1); // TODO error check
        Log.d(TAG, "number of lines in file: " + numberOfLines);
        int randomWordIndex = Utilities.rand(1, numberOfLines);
        Log.d(TAG, "random word index: " + randomWordIndex);

        File wordListPartFile = new File(getWordListsPartsFolder(), getWordListPartFilename(wordList, wordLength));
        try(BufferedReader br = new BufferedReader(new FileReader(wordListPartFile))) { // https://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
            int lineNumber = 1;
            for(String line; (line = br.readLine()) != null; ) {
                if (lineNumber == randomWordIndex) {
                    Log.d(TAG, "does " + lineNumber + " == " + randomWordIndex);
                    return line;
                }
                lineNumber ++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NOPE"; // TODO fix
    }

    private String getWordListPartFilename(String wordList, Integer length) {
        return wordList + "_" + length;
    }

    private static ArrayList<ArrayList<String>> getFakeWords() {
        String[] letters = {"A", "B", "C", "D", "E"};
        ArrayList<ArrayList<String>> fakeWords = new ArrayList<>();
        fakeWords.add(new ArrayList<String>());
        fakeWords.add(new ArrayList<String>());
        fakeWords.add(new ArrayList<String>());
        for (int i = 3; i < 15; i++) {
            ArrayList<String> words = new ArrayList<>();
            for (String letter : letters) {
                String word = i + "";
                for (int j = 0; j < i - 2; j++) {
                    word += " ";
                }
                word += letter;
                words.add(word);
            }
            fakeWords.add(words);
        }
        return fakeWords;
    }
}
