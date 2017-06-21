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

public class MainActivity extends AppCompatActivity implements DatePickerFragment.OnFragmentInteractionListener {

    private java.text.DateFormat dateFormat;

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

        final Date refDate = c.getTime();
        c.add(Calendar.YEAR, -12);
        final Date birthday = c.getTime();

        birthdayEditor = (EditText)findViewById(R.id.birthday_editor);
        birthdayEditor.setText(dateFormat.format(new Date(birthday.getTime())));
        refDateEditor = (EditText)findViewById(R.id.ref_date_editor);
        refDateEditor.setText(dateFormat.format(new Date(refDate.getTime())));

        functions = (RadioGrid)findViewById(R.id.functions);
    }

    public void showDatePickerDialog(View v) {
        String fieldName = v.getTag().toString();
        int fieldId = getResources().getIdentifier(fieldName, "id", getPackageName());
        EditText dateEditor = (EditText)findViewById(fieldId);
        String value = dateEditor.getText().toString();
        DialogFragment newFragment = DatePickerFragment.newInstance(fieldId, value);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void submitInputs(View v) {
        Calendar birthday, refDate;
        try {
            Date birthdayInput = dateFormat.parse(birthdayEditor.getText().toString());
            Date refDateInput = dateFormat.parse(refDateEditor.getText().toString());
            if (refDateInput.before(birthdayInput)) {
                showError(R.string.negative_age_error);
                return;
            }
            if (refDateInput.getTime() - birthdayInput.getTime() < ONE_YEAR) {
                showError(R.string.young_age_error);
                return;
            }
            birthday = Calendar.getInstance();
            birthday.setTime(birthdayInput);
            refDate = Calendar.getInstance();
            refDate.setTime(refDateInput);
        }
        catch (Exception e) {
            showError(R.string.date_parse_error);
            return;
        }
        if (functions.getCheckedRadioButtonId() == -1) {
            showError(R.string.missing_function_error);
            //return;
        }
    }

    @Override
    public void onDateSelection(int fieldId, int year, int month, int day) {
        EditText dateEditor = (EditText)findViewById(fieldId);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        dateEditor.setText(dateFormat.format(c.getTime()));
    }

    private void showError(int messageId) {
        Toast toast = Toast.makeText(getApplicationContext(), messageId, Toast.LENGTH_LONG);
        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
        toastMessage.setBackgroundColor(Color.RED);
        toast.show();
    }
}
