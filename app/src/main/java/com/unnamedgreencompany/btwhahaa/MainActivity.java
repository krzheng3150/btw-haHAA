package com.unnamedgreencompany.btwhahaa;

import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.OnFragmentInteractionListener, RadioGrid.OnOptionSelectListener {

    private java.text.DateFormat dateFormat;

    private Calendar birthday;
    private Calendar refDate;
    private Integer functionId;

    private TextView messageDisplay;
    private EditText birthdayEditor;
    private EditText refDateEditor;
    private RadioGrid functions;

    private static final long ONE_YEAR = 31536000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat = DateFormat.getDateFormat(this);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        refDate = Calendar.getInstance();
        refDate.setTime(c.getTime());
        c.add(Calendar.YEAR, -12);
        birthday = Calendar.getInstance();
        birthday.setTime(c.getTime());

        messageDisplay = (TextView)findViewById(R.id.message);
        showError(R.string.missing_function_error);
        birthdayEditor = (EditText)findViewById(R.id.birthday_editor);
        birthdayEditor.setText(dateFormat.format(birthday.getTime()));
        birthdayEditor.setOnFocusChangeListener(focusListener);
        refDateEditor = (EditText)findViewById(R.id.ref_date_editor);
        refDateEditor.setText(dateFormat.format(refDate.getTime()));
        refDateEditor.setOnFocusChangeListener(focusListener);

        functions = (RadioGrid)findViewById(R.id.functions);
    }

    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus){
                validateInputs();
            }
        }
    };

    public void showDatePickerDialog(View v) {
        String fieldName = v.getTag().toString();
        int fieldId = getResources().getIdentifier(fieldName, "id", getPackageName());
        EditText dateEditor = (EditText)findViewById(fieldId);
        String value = dateEditor.getText().toString();
        DialogFragment newFragment = DatePickerFragment.newInstance(fieldId, value);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private boolean validateInputs() {
        try {
            Date birthdayInput = dateFormat.parse(birthdayEditor.getText().toString());
            Date refDateInput = dateFormat.parse(refDateEditor.getText().toString());
            if (refDateInput.before(birthdayInput)) {
                showError(R.string.negative_age_error);
                return false;
            }
            if (refDateInput.getTime() - birthdayInput.getTime() < ONE_YEAR) {
                showError(R.string.young_age_error);
                return false;
            }
            birthday = Calendar.getInstance();
            birthday.setTime(birthdayInput);
            refDate = Calendar.getInstance();
            refDate.setTime(refDateInput);
        }
        catch (Exception e) {
            showError(R.string.date_parse_error);
            return false;
        }
        functionId = functions.getCheckedRadioButtonId();
        if (functionId == -1) {
            showError(R.string.missing_function_error);
            return false;
        }
        return true;
    }

    public void calculate() {
        if (validateInputs()) {
            double age = getAge(birthday, refDate);
            messageDisplay.setTextColor(Color.BLACK);
            messageDisplay.setText(String.format(Locale.getDefault(), "I'm %f btw haHAA", age));
        }
    }

    private double getAge(Calendar birthday, Calendar refDate) {
        int birthYear = birthday.get(Calendar.YEAR);
        int birthDayOfYear = birthday.get(Calendar.DAY_OF_YEAR);
        boolean isBornAfterLeapDay = birthYear % 4 == 0 && birthDayOfYear > 60;

        int refYear = refDate.get(Calendar.YEAR);
        int refDayOfYear = refDate.get(Calendar.DAY_OF_YEAR);
        boolean refDateWithinLeapPeriod = (refYear % 4 == 1 && refDayOfYear < 60) || (refYear % 4 == 0 && refDayOfYear > 60);

        double numDaysInYear = 365;

        if (isBornAfterLeapDay && !refDateWithinLeapPeriod) {
            birthDayOfYear -= 1;
        }
        else if (refDateWithinLeapPeriod) {
            numDaysInYear = 366;
        }

        boolean isBirthdayNoLaterThanRefDate = birthDayOfYear <= refDayOfYear;
        double yearDiff = refYear - birthYear;
        double dateDiff = isBirthdayNoLaterThanRefDate ?
                refDayOfYear - birthDayOfYear : 366 - (birthDayOfYear - refDayOfYear);
        if (!isBirthdayNoLaterThanRefDate) {
            yearDiff -= 1;
        }
        return yearDiff + (dateDiff / numDaysInYear);
    }

    @Override
    public void onDateSelection(int fieldId, int year, int month, int day) {
        EditText dateEditor = (EditText)findViewById(fieldId);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        dateEditor.setText(dateFormat.format(c.getTime()));
        calculate();
    }

    private void showError(int messageId) {
        messageDisplay.setTextColor(Color.RED);
        messageDisplay.setText(getString(messageId));
    }

    @Override
    public void onFunctionSelection(int fieldId) {
        calculate();
    }
}
