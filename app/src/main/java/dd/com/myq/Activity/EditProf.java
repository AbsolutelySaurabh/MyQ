package dd.com.myq.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import dd.com.myq.App.Config;
import dd.com.myq.Fragment.AccountFragment;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

import static dd.com.myq.Util.SessionManager.KEY_ABOUTME;
import static dd.com.myq.Util.SessionManager.KEY_DOB;
import static dd.com.myq.Util.SessionManager.KEY_EMAIL;
import static dd.com.myq.Util.SessionManager.KEY_UID;
import static dd.com.myq.Util.SessionManager.KEY_USERNAME;

public class EditProf extends AppCompatActivity {

    Button b1, b2;
    private EditText e1, e2, e3, e4;
    String profilepicture, uid, a, b, d, e, first, c, s, sex;
    RadioGroup rg;
    private ProgressDialog progress;
    private Button save_profile;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prof);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addListenerRadioButton();

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        progress = new ProgressDialog(this);
        progress.setMessage("Updating Profile...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);


        e1 = (EditText) findViewById(R.id.editText);
        e2 = (EditText) findViewById(R.id.editText2);
        e3 = (EditText) findViewById(R.id.editText3);
        e4 = (EditText) findViewById(R.id.editText7);

        e2.setKeyListener(null);///so that email cannot be updated


        Object name = user.get(KEY_USERNAME);
        e1.setText(name.toString());

        Object email = user.get(KEY_EMAIL);
        e2.setText(email.toString());


        e4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    fn();
                }
                else {

                }
            }
        });

        final Object aboutme = user.get(KEY_ABOUTME);
        e3.setText(aboutme.toString());
        final Object dob = user.get(KEY_DOB);


        // StringTokenizer tokens = new StringTokenizer(dob.toString(), "T");
        // first = tokens.nextToken();

        String[] parts = dob.toString().split("T"); // escape .
        String part1 = parts[0];
        e4.setText(part1);


        final Object userid = user.get(KEY_UID);
        uid = userid.toString();
        profilepicture = null;


//        Object gender = user.get(KEY_GENDER);
//        String sex = gender.toString();
//        if (sex.equalsIgnoreCase("Male")) {
//            s = "Male";
//            rg.check(R.id.radioButton);
//        } else {
//            s = "Female";
//            rg.check(R.id.radioButton2);
//        }

        save_profile = (Button) findViewById(R.id.save_profile);
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = e1.getText().toString();
                b = e2.getText().toString();
                c = s;
                d = e3.getText().toString();
                e = e4.getText().toString();

                login(uid, b, a, e, c, d);
            }
        });
    }

    public void login(final String userid, String email, String username, String dob, String gender, String aboutme) {

        if (TextUtils.isEmpty(a)) {
            e1.setError("This field cannot be empty!!");
            return;
        } else if (TextUtils.isEmpty(d)) {
            e3.setError("This field cannot be empty!!");
            return;
        } else {
            progress.show();

            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams requestParams = new RequestParams();
            requestParams.put("userid", userid);
            requestParams.put("emailaddress", email);
            requestParams.put("aboutme", aboutme);
            requestParams.put("username", username);
            requestParams.put("gender", gender);
            requestParams.put("dob", dob);


            client.post(this, Config.UpdateDetailAPIUrl, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    sessionManager.UpdateLoginSession(uid, a, b, d, e, c);

                    progress.hide();
                    Log.e("Response Login: ", response.toString());
                    Toast.makeText(EditProf.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditProf.this, AccountFragment.class);
                    startActivity(intent);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    progress.hide();
                    Toast.makeText(EditProf.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    progress.hide();
                    Toast.makeText(EditProf.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseString) {
                    super.onFailure(statusCode, headers, throwable, responseString);
                    progress.hide();
                    Toast.makeText(EditProf.this, "Error Occurred", Toast.LENGTH_SHORT).show();

                }

            });
        }
    }

    //////////////////////////////

    public void fn()
    {

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                e4.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.show();

    }
    ////////////////////////


    private void addListenerRadioButton() {

        rg = (RadioGroup) findViewById(R.id.radioGroup);
        b1 = (Button) findViewById(R.id.radioButton);
        b2 = (Button) findViewById(R.id.radioButton2);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioButton:
                        s=b1.getText().toString();
                        break;
                    case R.id.radioButton2:
                        s=b2.getText().toString();
                        break;
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}