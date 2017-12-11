package com.paulrlutz.passwordgenerator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.paulrlutz.passwordgenerator.Exceptions.WordsTooLongOrTooManyException;
import com.paulrlutz.passwordgenerator.Exceptions.WordsTooShortOrTooFewException;

import static com.paulrlutz.passwordgenerator.MainActivity.MAXIMUM_WORD_LENGTH_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.MINIMUM_WORD_LENGTH_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.NUMBERS_TO_USE_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.NUMBER_OF_NUMBERS_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.NUMBER_OF_SPECIAL_CHARACTERS_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.NUMBER_OF_WORDS_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.PASSWORD_LENGTH_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.SEPARATOR_KEY;
import static com.paulrlutz.passwordgenerator.MainActivity.SPECIAL_CHARACTERS_TO_USE_KEY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "paulrlutz.pass.main";

    EditText editTextGeneratedPassword;
    EditText editTextSeparator;
    EditText editTextPasswordLength;
    EditText editTextNumberOfWords;
    EditText editTextMaximumWordLength;
    EditText editTextMinimumWordLength;
    EditText editTextNumberOfNumbers;
    EditText editTextNumbersToUse;
    CheckBox checkBoxNumbersGrouped;
    CheckBox checkBoxNumbersSeparated;
    RadioButton radioButtonNumbersRandom;
    RadioButton radioButtonNumbersBeginning;
    RadioButton radioButtonNumbersEnd;
    EditText editTextNumberOfSpecialCharacters;
    EditText editTextSpecialCharactersToUse;
    CheckBox checkBoxSpecialCharactersGrouped;
    CheckBox checkBoxSpecialCharactersSeparated;
    RadioButton radioButtonSpecialCharactersRandom;
    RadioButton radioButtonSpecialCharactersBeginning;
    RadioButton radioButtonSpecialCharactersEnd;

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getApplicationContext().getSharedPreferences("prefs", 0);

        WordListManager wlm = new WordListManager();
        wlm.setup(this, prefs);

        instantiateViews();

        Button btnGeneratePassword = (Button) findViewById(R.id.btnGeneratePassword);
        btnGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextGeneratedPassword.setText(generatePassword());
            }
        });
    }

    private String generatePassword() {

        String separator = editTextSeparator.getText() + "";
        int passwordLength = Integer.parseInt(editTextPasswordLength.getText() + "");
        int numWords = Integer.parseInt(editTextNumberOfWords.getText() + "");
        int minWordLength = Integer.parseInt(editTextMinimumWordLength.getText() + "");
        int maxWordLength = Integer.parseInt(editTextMaximumWordLength.getText() + "");
        PasswordGenerator.Capitalization capsType = PasswordGenerator.Capitalization.NONE;
        int numNumbers = Integer.parseInt(editTextNumberOfNumbers.getText() + "");
        boolean numbersGrouped = checkBoxNumbersGrouped.isChecked();
        int numSpecialChars = Integer.parseInt(editTextNumberOfSpecialCharacters.getText() + "");
        boolean specialCharsGrouped = checkBoxSpecialCharactersGrouped.isChecked();

        PasswordGenerator.GroupLocation numbersLocation;
        if (radioButtonNumbersRandom.isChecked()) {
            numbersLocation = PasswordGenerator.GroupLocation.RANDOM;
        } else if (radioButtonNumbersBeginning.isChecked()) {
            numbersLocation = PasswordGenerator.GroupLocation.BEGINNING;
        } else {
            numbersLocation = PasswordGenerator.GroupLocation.END;
        }

        PasswordGenerator.GroupLocation specialCharsLocation;
        if (radioButtonSpecialCharactersRandom.isChecked()) {
            specialCharsLocation = PasswordGenerator.GroupLocation.RANDOM;
        } else if (radioButtonSpecialCharactersBeginning.isChecked()) {
            specialCharsLocation = PasswordGenerator.GroupLocation.BEGINNING;
        } else {
            specialCharsLocation = PasswordGenerator.GroupLocation.END;
        }

        char[] numbersArray = (editTextNumbersToUse.getText() + "").toCharArray();
        char[] specialCharsArray = (editTextSpecialCharactersToUse.getText() + "").toCharArray();

        try {
            String password = PasswordGenerator.generatePassword(prefs,
                separator, passwordLength, numWords, minWordLength, maxWordLength, capsType,
                numbersArray, numNumbers, numbersGrouped, numbersLocation,
                specialCharsArray, numSpecialChars, specialCharsGrouped, specialCharsLocation
            );
            return password;
        } catch (WordsTooLongOrTooManyException e) {
            e.printStackTrace();
            return "too long";
        } catch (WordsTooShortOrTooFewException e) {
            e.printStackTrace();
            return "too short";
        }
    }

    public final static String SEPARATOR_KEY = "separator_key";
    public final static String PASSWORD_LENGTH_KEY = "password_length_key";
    public final static String NUMBER_OF_WORDS_KEY = "number_of_words_key";
    public final static String MINIMUM_WORD_LENGTH_KEY = "minimum_word_length_key";
    public final static String MAXIMUM_WORD_LENGTH_KEY = "maximum_word_length_key";
    public final static String NUMBER_OF_NUMBERS_KEY = "number_of_numbers_key";
    public final static String NUMBERS_TO_USE_KEY = "numbers_to_use_key";
    public final static String NUMBERS_GROUPED_KEY = "numbers_grouped_key";
    public final static String NUMBERS_SEPARATED_KEY = "numbers_separated_key";
    public final static String NUMBERS_LOCATION_KEY = "numbers_location_key";
    public final static String NUMBER_OF_SPECIAL_CHARACTERS_KEY = "number_of_special_characters_key";
    public final static String SPECIAL_CHARACTERS_TO_USE_KEY = "special_characters_to_use_key";
    public final static String SPECIAL_CHARACTERS_GROUPED_KEY = "special_characters_grouped_key";
    public final static String SPECIAL_CHARACTERS_SEPARATED_KEY = "special_characters_separated_key";
    public final static String SPECIAL_CHARACTERS_LOCATION_KEY = "special_characters_location_key";

    private void instantiateViews() {
        editTextGeneratedPassword = (EditText) findViewById(R.id.editTextGeneratedPassword);

        editTextSeparator = (EditText) findViewById(R.id.editTextSeparator);
        editTextSeparator.setText(prefs.getString(SEPARATOR_KEY, "_"));
        editTextSeparator.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextSeparator, prefs));
        editTextPasswordLength = (EditText) findViewById(R.id.editTextPasswordLength);
        editTextPasswordLength.setText(prefs.getInt(PASSWORD_LENGTH_KEY, 18) + "");
        editTextPasswordLength.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextPasswordLength, prefs));

        editTextNumberOfWords = (EditText) findViewById(R.id.editTextNumberOfWords);
        editTextNumberOfWords.setText(prefs.getInt(NUMBER_OF_WORDS_KEY, 3) + "");
        editTextNumberOfWords.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextNumberOfWords, prefs));
        editTextMinimumWordLength = (EditText) findViewById(R.id.editTextMinimumWordLength);
        editTextMinimumWordLength.setText(prefs.getInt(MINIMUM_WORD_LENGTH_KEY, 3) + "");
        editTextMinimumWordLength.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextMinimumWordLength, prefs));
        editTextMaximumWordLength = (EditText) findViewById(R.id.editTextMaximumWordLength);
        editTextMaximumWordLength.setText(prefs.getInt(MAXIMUM_WORD_LENGTH_KEY, 12) + "");
        editTextMaximumWordLength.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextMaximumWordLength, prefs));

        editTextNumberOfNumbers = (EditText) findViewById(R.id.editTextNumberOfNumbers);
        editTextNumberOfNumbers.setText(prefs.getInt(NUMBER_OF_NUMBERS_KEY, 1) + "");
        editTextNumberOfNumbers.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextNumberOfNumbers, prefs));
        editTextNumbersToUse = (EditText) findViewById(R.id.editTextNumbersToUse);
        editTextNumbersToUse.setText(prefs.getString(NUMBERS_TO_USE_KEY, "157"));
        editTextNumbersToUse.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextNumbersToUse, prefs));
        checkBoxNumbersGrouped = (CheckBox) findViewById(R.id.checkBoxNumbersGrouped);
        checkBoxNumbersGrouped.setChecked(prefs.getBoolean(NUMBERS_GROUPED_KEY, false));
        checkBoxNumbersSeparated = (CheckBox) findViewById(R.id.checkBoxNumbersSeparated);
        checkBoxNumbersSeparated.setChecked(prefs.getBoolean(NUMBERS_SEPARATED_KEY, false));
        radioButtonNumbersRandom = (RadioButton) findViewById(R.id.radioButtonNumbersRandom);
        radioButtonNumbersBeginning = (RadioButton) findViewById(R.id.radioButtonNumbersBeginning);
        radioButtonNumbersEnd = (RadioButton) findViewById(R.id.radioButtonNumbersEnd);
        switch(prefs.getInt(NUMBERS_LOCATION_KEY, 0)) {
            case 0:
                radioButtonNumbersRandom.setChecked(true);
                radioButtonNumbersBeginning.setChecked(false);
                radioButtonNumbersEnd.setChecked(false);
                break;
            case 1:
                radioButtonNumbersRandom.setChecked(false);
                radioButtonNumbersBeginning.setChecked(true);
                radioButtonNumbersEnd.setChecked(false);
                break;
            case 2:
                radioButtonNumbersRandom.setChecked(false);
                radioButtonNumbersBeginning.setChecked(false);
                radioButtonNumbersEnd.setChecked(true);
                break;
            default:
                radioButtonNumbersRandom.setChecked(true);
                radioButtonNumbersBeginning.setChecked(false);
                radioButtonNumbersEnd.setChecked(false);
                break;
        }

        editTextNumberOfSpecialCharacters = (EditText) findViewById(R.id.editTextNumberOfSpecialCharacters);
        editTextNumberOfSpecialCharacters.setText(prefs.getInt(NUMBER_OF_SPECIAL_CHARACTERS_KEY, 1) + "");
        editTextNumberOfSpecialCharacters.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextNumberOfSpecialCharacters, prefs));
        editTextSpecialCharactersToUse = (EditText) findViewById(R.id.editTextSpecialCharactersToUse);
        editTextSpecialCharactersToUse.setText(prefs.getString(SPECIAL_CHARACTERS_TO_USE_KEY, "./!"));
        editTextSpecialCharactersToUse.addTextChangedListener(new TextWatcherSharedPrefs(R.id.editTextSpecialCharactersToUse, prefs));
        checkBoxSpecialCharactersGrouped = (CheckBox) findViewById(R.id.checkBoxSpecialCharactersGrouped);
        checkBoxSpecialCharactersGrouped.setChecked(prefs.getBoolean(SPECIAL_CHARACTERS_GROUPED_KEY, false));
        checkBoxSpecialCharactersSeparated = (CheckBox) findViewById(R.id.checkBoxSpecialCharactersSeparated);
        checkBoxSpecialCharactersSeparated.setChecked(prefs.getBoolean(SPECIAL_CHARACTERS_SEPARATED_KEY, false));
        radioButtonSpecialCharactersRandom = (RadioButton) findViewById(R.id.radioButtonSpecialCharactersRandom);
        radioButtonSpecialCharactersBeginning = (RadioButton) findViewById(R.id.radioButtonSpecialCharactersBeginning);
        radioButtonSpecialCharactersEnd = (RadioButton) findViewById(R.id.radioButtonSpecialCharactersEnd);
        switch(prefs.getInt(SPECIAL_CHARACTERS_LOCATION_KEY, 0)) {
            case 0:
                radioButtonSpecialCharactersRandom.setChecked(true);
                radioButtonSpecialCharactersBeginning.setChecked(false);
                radioButtonSpecialCharactersEnd.setChecked(false);
                break;
            case 1:
                radioButtonSpecialCharactersRandom.setChecked(false);
                radioButtonSpecialCharactersBeginning.setChecked(true);
                radioButtonSpecialCharactersEnd.setChecked(false);
                break;
            case 2:
                radioButtonSpecialCharactersRandom.setChecked(false);
                radioButtonSpecialCharactersBeginning.setChecked(false);
                radioButtonSpecialCharactersEnd.setChecked(true);
                break;
            default:
                radioButtonSpecialCharactersRandom.setChecked(true);
                radioButtonSpecialCharactersBeginning.setChecked(false);
                radioButtonSpecialCharactersEnd.setChecked(false);
                break;
        }

    }
}

class TextWatcherSharedPrefs implements TextWatcher {

    int viewId = -1;
    SharedPreferences prefs;

    public TextWatcherSharedPrefs(int mViewId, SharedPreferences mPrefs) {
        viewId = mViewId;
        prefs = mPrefs;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String prefs_key = null;
        boolean number = false;
        switch (viewId) {
            case R.id.editTextSeparator:
                prefs_key = SEPARATOR_KEY;
                break;
            case R.id.editTextPasswordLength:
                number = true;
                prefs_key = PASSWORD_LENGTH_KEY;
                break;
            case R.id.editTextNumberOfWords:
                number = true;
                prefs_key = NUMBER_OF_WORDS_KEY;
                break;
            case R.id.editTextMinimumWordLength:
                number = true;
                prefs_key = MINIMUM_WORD_LENGTH_KEY;
                break;
            case R.id.editTextMaximumWordLength:
                number = true;
                prefs_key = MAXIMUM_WORD_LENGTH_KEY;
                break;
            case R.id.editTextNumberOfNumbers:
                number = true;
                prefs_key = NUMBER_OF_NUMBERS_KEY;
                break;
            case R.id.editTextNumbersToUse:
                prefs_key = NUMBERS_TO_USE_KEY;
                break;
             case R.id.editTextNumberOfSpecialCharacters:
                number = true;
                prefs_key = NUMBER_OF_SPECIAL_CHARACTERS_KEY;
                break;
            case R.id.editTextSpecialCharactersToUse:
                prefs_key = SPECIAL_CHARACTERS_TO_USE_KEY;
                break;
            default:
                break;
        }
        if (number) {
            int num = 0;
            try {
                num = Integer.parseInt(s.toString());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            prefs.edit().putInt(prefs_key, num).apply();
        } else {
            prefs.edit().putString(prefs_key, s.toString()).apply();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
