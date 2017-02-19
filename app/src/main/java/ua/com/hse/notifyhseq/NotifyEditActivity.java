package ua.com.hse.notifyhseq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

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
    String mNotifyFullText;
    EditText newNotifyEditTextDate, newNotifyEditTextTime, newNotifyEditTextDescription;

    // Для фото переменные




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_edit);

//** Получение текущей даты и времени из системы
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        mNewNotifyDate = day + "." + month + "." + year;
        mNewNotifyTime = hour + "." + minutes;
//** Привязка к объектам в отображении
        newNotifyEditTextDate = (EditText) findViewById(R.id.newNotifyDate);
        newNotifyEditTextTime = (EditText) findViewById(R.id.newNotifyTime);
        newNotifyEditTextDescription = (EditText) findViewById(R.id.newNotifyCurentDescription);

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

                //** Получение текущей даты и времени из системы
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minutes = c.get(Calendar.MINUTE);
                mNewNotifyCurrentDate = day + "." + month + "." + year;
                mNewNotifyCurrentTime = hour + "." + minutes;

//**Получение данных из заполненных полей
                mNewNotifyDate = newNotifyEditTextDate.getText().toString();
                mNewNotifyTime = newNotifyEditTextTime.getText().toString();
                mNewNotifyPlace = spinnerPlace.getSelectedItem().toString();
                mNewNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                mNewNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                mNewNotifyDescription = newNotifyEditTextDescription.getText().toString();

                mNotifyFullText = "Notify:";
                mNotifyFullText += "\nDate and Time of record: " + mNewNotifyCurrentDate + " " + mNewNotifyCurrentTime;
                mNotifyFullText += "\nDate: " + mNewNotifyDate;
                mNotifyFullText += "\nTime: " + mNewNotifyTime;
                mNotifyFullText += "\nPlace: " + mNewNotifyPlace;
                mNotifyFullText += "\nDepartment: " + mNewNotifyDepartment;
                mNotifyFullText += "\nAccident type: " + mNewNotifyAccidentType;
                mNotifyFullText += "\nShort information:\n: " + mNewNotifyDescription;


//** Посылаем письмо с информацией
                MailSender mailSender = new MailSender("soldiez111@gmail.com", "soldar111");

                Mail.MailBuilder builder = new Mail.MailBuilder();
                Mail mail = builder
                        .setSender("soldiez111@gmail.com")
                        .addRecipient(new Recipient("soldiez@yandex.ru"))
//                        .addRecipient(new Recipient(Recipient.TYPE.CC, recipientCC))
                        .setSubject("Notify: " + mNewNotifyCurrentTime + " " + mNewNotifyCurrentTime)
                        .setText(mNotifyFullText)
//                        .setHtml("<h1 style=\"color:red;\">Hello</h1>")
//                        .addAttachment(new Attachment(mCurrentPhotoPath, "pic.jpg"))
                        .build();

                MailSender.OnMailSentListener onMailSentListener = new MailSender.OnMailSentListener() {

                    @Override
                    public void onSuccess() {
                        // mail sent!
                    }

                    @Override
                    public void onError(Exception error) {
                        // something bad happened :(
                    }
                };

                mailSender.sendMail(mail, onMailSentListener);


                // going back to MainActivity
                Intent activityChangeIntent = new Intent(NotifyEditActivity.this, MainActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                NotifyEditActivity.this.startActivity(activityChangeIntent);
            }
        });
// обработка картинки фото
        final ImageButton buttonTakePhotoOne = (ImageButton) findViewById(R.id.takePhotoOne);
        buttonTakePhotoOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //** делаем фото, сохраняем и вставляем в вид


            }
        });



    }


}
