package com.unnamedgreencompany.btwhahaa;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

    private String makeMessage() {
        double age = getAge(birthday, refDate);
        String dispFormat = "%s";
        switch(functionId) {
            case R.id.fun_square:
                age = Math.sqrt(age);
                dispFormat = "%s^2";
                break;
            case R.id.fun_cube:
                age = Math.pow(age, 1.0 / 3.0);
                dispFormat = "%s^3";
                break;
            case R.id.fun_sqrt:
                age *= age;
                dispFormat = "sqrt(%s)";
                break;
            case R.id.fun_cbrt:
                age = age * age * age;
                dispFormat = "%s^(1/3)";
                break;
            case R.id.fun_exp:
                age = Math.log(age);
                dispFormat = "e^%s";
                break;
            case R.id.fun_exp2:
                age = Math.log(age) / Math.log(2);
                dispFormat = "2^%s";
                break;
            case R.id.fun_exp12:
                age = Math.log(age) / Math.log(12);
                dispFormat = "12^%s";
                break;
            case R.id.fun_ln:
                age = Math.exp(age);
                dispFormat = "ln(%s)";
                break;
            case R.id.fun_log2:
                age = Math.pow(2, age);
                dispFormat = "log(%s)";
                break;
            case R.id.fun_recip:
                age = 12.0 / age;
                dispFormat = "12 / %s";
                break;
            case R.id.fun_tan:
                age = Math.atan(age);
                dispFormat = "tan(%s)";
                break;
            case R.id.fun_atan:
                age = Math.tan(age);
                dispFormat = "arctan(%s)";
                break;
            case R.id.fun_asin:
                age = Math.sin(age);
                dispFormat = "arcsin(%s)";
                break;
            case R.id.fun_acos:
                age = Math.cos(age);
                dispFormat = "arccos(%s)";
                break;
        }
        String decimalFormat = (age == Math.floor(age)) ? "%.0f" : "%.3f";
        return String.format(Locale.getDefault(), "I'm %s btw haHAA",
                String.format(Locale.getDefault(), dispFormat,
                        String.format(Locale.getDefault(), decimalFormat, age)));
    }

    public void displayResult() {
        if (validateInputs()) {
            messageDisplay.setTextColor(Color.BLACK);
            messageDisplay.setText(makeMessage());
        }
    }

    private double getAge(Calendar birthday, Calendar refDate) {
        int birthYear = birthday.get(Calendar.YEAR);
        int birthDayOfYear = birthday.get(Calendar.DAY_OF_YEAR);
        boolean isBornAfterLeapDay = birthYear % 4 == 0 && birthDayOfYear > 60;

        int refYear = refDate.get(Calendar.YEAR);
        int refDayOfYear = refDate.get(Calendar.DAY_OF_YEAR);
        boolean refDateAfterLeapDay = refYear % 4 == 0 && refDayOfYear > 60;
        boolean refDateBeginningOfPostLeapYear = refYear % 4 == 1 && refDayOfYear < 60;
        boolean refDateWithinLeapPeriod = refDateAfterLeapDay || refDateBeginningOfPostLeapYear;

        double numDaysInYear = 365;
        if (isBornAfterLeapDay && !refDateWithinLeapPeriod) {
            birthDayOfYear -= 1;
        }
        else if (refDateWithinLeapPeriod) {
            if (refDateAfterLeapDay && birthYear % 4 != 0 && birthDayOfYear > 60) {
                birthDayOfYear += 1;
            }
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

    public void sendText(View v) {
        if (!validateInputs()) {
            alert(getString(R.string.has_errors));
            return;
        }
        String message = makeMessage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); //Need to change the build to API 19
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            startActivity(sendIntent);
        }
        else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            sendIntent.putExtra("sms_body", message);
            startActivity(sendIntent);
        }
    }

    public void sendEmail(View v) {
        if (!validateInputs()) {
            alert(getString(R.string.has_errors));
            return;
        }
        String message = makeMessage();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Just used an app btw haHAA");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) == null) {
            alert(getString(R.string.email_need_setup));
        }
        else {
            startActivity(Intent.createChooser(intent, "sendEmail"));
        }
    }

    public void spamTwitch(View v) {
        if (!validateInputs()) {
            alert(getString(R.string.has_errors));
            return;
        }
        //TODO implement this part
        alert(getString(R.string.coming_soon));
    }

    @Override
    public void onDateSelection(int fieldId, int year, int month, int day) {
        EditText dateEditor = (EditText)findViewById(fieldId);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        dateEditor.setText(dateFormat.format(c.getTime()));
        displayResult();
    }

    private void showError(int messageId) {
        messageDisplay.setTextColor(Color.RED);
        messageDisplay.setText(getString(messageId));
    }

    private void alert(String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(message);
        b.setNeutralButton(getString(R.string.ok), (DialogInterface dialog, int id) -> dialog.cancel());
        AlertDialog dialog = b.create();
        dialog.show();
    }

    @Override
    public void onFunctionSelection(int fieldId) {
        displayResult();
    }
}
