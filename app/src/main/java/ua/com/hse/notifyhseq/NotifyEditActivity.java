package ua.com.hse.notifyhseq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static ua.com.hse.notifyhseq.R.id.newNotifyAccidentType;
import static ua.com.hse.notifyhseq.R.id.newNotifyDepartment;
import static ua.com.hse.notifyhseq.R.id.newNotifyPlace;

public class NotifyEditActivity extends AppCompatActivity {

    String mNewNotifyDate;
    String mNewNotifyTime;
    String mNewNotifyCurrentDate;
    String mNewNotifyCurrentTime;
    String mNewNotifyPlace;
    String mNewNotifyDepartment;
    String mNewNotifyAccidentType;
    String mNewNotifyDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_edit);


//Spinner for place
        final Spinner spinnerPlace = (Spinner) findViewById(newNotifyPlace);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterPlace = ArrayAdapter.createFromResource(this,
                R.array.Place, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerPlace.setAdapter(adapterPlace);


        //Spinner for department
        final Spinner spinnerDepartment = (Spinner) findViewById(newNotifyDepartment);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterDepartment = ArrayAdapter.createFromResource(this,
                R.array.Department, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerDepartment.setAdapter(adapterDepartment);


        //Spinner for Accident type
        final Spinner spinnerAccidentType = (Spinner) findViewById(newNotifyAccidentType);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterAccidentType = ArrayAdapter.createFromResource(this,
                R.array.AccidentType, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterAccidentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerAccidentType.setAdapter(adapterAccidentType);


//Save button - listener
        final Button buttonSaveNotify = (Button) findViewById(R.id.saveNotifyButton);
        buttonSaveNotify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Save info about new Notify

//getting current date
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                mNewNotifyCurrentDate = df.format(c.getTime());


                mNewNotifyPlace = spinnerPlace.getSelectedItem().toString();
                mNewNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                mNewNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();


                // going back to MainActivity
                Intent activityChangeIntent = new Intent(NotifyEditActivity.this, MainActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                NotifyEditActivity.this.startActivity(activityChangeIntent);
            }
        });

//Send button - listener
        final Button buttonSendNotify = (Button) findViewById(R.id.sendNotifyButton);
        buttonSendNotify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Send info about new Notify


                // going back to MainActivity
                Intent activityChangeIntent = new Intent(NotifyEditActivity.this, MainActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                NotifyEditActivity.this.startActivity(activityChangeIntent);
            }
        });


    }


}
