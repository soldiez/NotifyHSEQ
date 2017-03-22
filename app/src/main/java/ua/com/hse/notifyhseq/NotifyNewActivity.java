package ua.com.hse.notifyhseq;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ua.com.hse.notifyhseq.mail.Attachment;
import ua.com.hse.notifyhseq.mail.Mail;
import ua.com.hse.notifyhseq.mail.MailSender;
import ua.com.hse.notifyhseq.mail.Recipient;
import ua.com.hse.notifyhseq.pickers.DatePickerFragment;
import ua.com.hse.notifyhseq.pickers.TimePickerFragment;

import static ua.com.hse.notifyhseq.R.id.newNotifyAccidentType;
import static ua.com.hse.notifyhseq.R.id.newNotifyDepartment;
import static ua.com.hse.notifyhseq.R.id.newNotifyPlace;
import static ua.com.hse.notifyhseq.R.id.takePicture;

;

public class NotifyNewActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    final int TYPE_PHOTO = 1;
    final int REQUEST_CODE_PHOTO = 1;
    final String TAG = "myLogs";
    int uid = 1;
    String mNewNotifyDate;
    String mNewNotifyTime;
    String mNewNotifyCurrentTime;
    String mNewNotifyPlace;
    String mNewNotifyDepartment;
    String mNewNotifyAccidentType;
    String mNewNotifyDescription;
    String mPhotoPath = "";
    String mPhotoNameFile = "";
    int mNotifyStatus = 0;
    String mNamePerson = "Alex";
    String mEmailPerson = "soldiez@yandex.ru";
    String mPhonePerson = "0504223846";
    String mDepartmentPerson = "Deprt";

    EditText newNotifyEditTextDate, newNotifyEditTextTime, newNotifyEditTextDescription;
    // Для фото переменные
    File directory;

    String mNotifyFullText;
    // Firebase database
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    UploadTask uploadTask;

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_new);
//Проверка необходимых разрешений
        verifyStoragePermissions(this);
        //Создание директории (если ее нет)
        createDirectory();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("notifyHSEQ");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();



//** Получение текущей даты и времени из системы
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        mNewNotifyDate = day + "." + month + "." + year;
        mNewNotifyTime = hour + "." + minutes;

//Date picker
        newNotifyEditTextDate = (EditText) findViewById(R.id.newNotifyDate);
        final Button mPickDate = (Button) findViewById(R.id.newNotifyDateButton);
        mPickDate.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });


// Time picker
        newNotifyEditTextTime = (EditText) findViewById(R.id.newNotifyTime);
        final Button mPickTime = (Button) findViewById(R.id.newNotifyTimeButton);
        mPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });


//** Привязка к объектам в отображении


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

                //Save info to database new Notify

//** Получение текущей даты и времени из системы

                DateFormat df1 = new SimpleDateFormat("d MMM yyyy");
                DateFormat df2 = new SimpleDateFormat("HH:mm");
//                mNewNotifyCurrentDate = df1.format(Calendar.getInstance().getTime());
                mNewNotifyCurrentTime = df2.format(Calendar.getInstance().getTime());

//**Получение данных из заполненных полей
                mNewNotifyDate = newNotifyEditTextDate.getText().toString();
                mNewNotifyTime = newNotifyEditTextTime.getText().toString();
                mNewNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                mNewNotifyPlace = spinnerPlace.getSelectedItem().toString();
                mNewNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                mNewNotifyDescription = newNotifyEditTextDescription.getText().toString();

//send to Firebase database
                NotifyHSEQItem notifyHSEQItem = new NotifyHSEQItem(
                        uid, mNewNotifyCurrentTime, mNewNotifyDate, mNewNotifyTime, mNewNotifyAccidentType,
                        mNewNotifyPlace, mNewNotifyDepartment, mNewNotifyDescription, mPhotoPath, mPhotoNameFile,
                        mNotifyStatus, mNamePerson, mEmailPerson, mPhonePerson, mDepartmentPerson);
                myRef.push().setValue(notifyHSEQItem);

                //send file to cloud
                Uri file = Uri.fromFile(new File(mPhotoPath + "/" + mPhotoNameFile));


// Create the file metadata
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();

// Upload file and metadata to the path 'images/mountains.jpg'
                uploadTask = storageReference.child("images/" + file.getLastPathSegment()).putFile(file, metadata);

// Listen for state changes, errors, and completion of the upload.
                StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = uploadTask.addOnProgressListener
                        (new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                Toast.makeText(getApplicationContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show(); //TODO good view of uploading file
                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
//                        Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    }
                });


//                uploadTask = storageRef.putFile(file);
//
//// Register observers to listen for when the download is done or if it fails
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(getApplicationContext(), "Image uploaded to Server", Toast.LENGTH_SHORT ).show();
//                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
// //                       Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    }
//                });


                // going back to MainActivity
                Intent activityChangeIntent = new Intent(NotifyNewActivity.this, MainActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                NotifyNewActivity.this.startActivity(activityChangeIntent);
            }
        });

//Send button - listener
        final Button buttonSendNotify = (Button) findViewById(R.id.sendNotifyButton);
        buttonSendNotify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Send info about new Notify

                //** Получение текущей даты и времени из системы

                DateFormat df1 = new SimpleDateFormat("d MMM yyyy");
                DateFormat df2 = new SimpleDateFormat("HH:mm");
//                mNewNotifyCurrentDate = df1.format(Calendar.getInstance().getTime());
                mNewNotifyCurrentTime = df2.format(Calendar.getInstance().getTime());

//**Получение данных из заполненных полей
                mNewNotifyDate = newNotifyEditTextDate.getText().toString();
                mNewNotifyTime = newNotifyEditTextTime.getText().toString();
                mNewNotifyPlace = spinnerPlace.getSelectedItem().toString();
                mNewNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                mNewNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                mNewNotifyDescription = newNotifyEditTextDescription.getText().toString();

                mNotifyFullText = "Notify:";
                mNotifyFullText += "\nRegister: Date: " + mNewNotifyCurrentTime;
                mNotifyFullText += "\nDate: " + mNewNotifyDate;
                mNotifyFullText += "\nTime: " + mNewNotifyTime;
                mNotifyFullText += "\nPlace: " + mNewNotifyPlace;
                mNotifyFullText += "\nDepartment: " + mNewNotifyDepartment;
                mNotifyFullText += "\nAccident type: " + mNewNotifyAccidentType;
                mNotifyFullText += "\nShort information:\n: " + mNewNotifyDescription;


//** Посылаем письмо с информацией
                MailSender mailSender = new MailSender("soldiez111@gmail.com", "soldar111");

                Mail.MailBuilder builder = new Mail.MailBuilder();
                Mail mail;

                if (mPhotoNameFile.equals("")) {
                    mail = builder
                            .setSender("soldiez111@gmail.com")
                            .addRecipient(new Recipient("soldiez@yandex.ru"))
//                        .addRecipient(new Recipient(Recipient.TYPE.CC, recipientCC))
                            .setSubject("Notify: " + mNewNotifyCurrentTime)
                            .setText(mNotifyFullText)
//                        .setHtml("<h1 style=\"color:red;\">Hello</h1>");
                            .build();

                } else {
                    mail = builder
                            .setSender("soldiez111@gmail.com")
                            .addRecipient(new Recipient("soldiez@yandex.ru"))
//                        .addRecipient(new Recipient(Recipient.TYPE.CC, recipientCC))
                            .setSubject("Notify: " + mNewNotifyCurrentTime)
                            .setText(mNotifyFullText)
//                        .setHtml("<h1 style=\"color:red;\">Hello</h1>");
                            .addAttachment(new Attachment(mPhotoPath + "/" + mPhotoNameFile, mPhotoNameFile))
                            .build();
                }
                Log.d(TAG + " Send", mPhotoPath);
                Log.d(TAG + " Send", mPhotoNameFile);

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
                Intent activityChangeIntent = new Intent(NotifyNewActivity.this, MainActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                NotifyNewActivity.this.startActivity(activityChangeIntent);
            }
        });
// обработка картинки фото
        final ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);
        buttonTakePhotoOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //** делаем фото, сохраняем и вставляем в вид
                Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                startActivityForResult(intentPhoto, REQUEST_CODE_PHOTO);
                File file = new File(mPhotoPath + "/" + mPhotoNameFile);
//TODO insert image to imageview
                Glide.with(getApplicationContext() /* context */)

                        .load(file.toString())
                        .into(buttonTakePhotoOne);


                //               Bitmap bitmap = (Bitmap) file;
                //               buttonTakePhotoOne.setImageBitmap(Bitmap.file);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentPhoto) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (intentPhoto == null) {
                    Log.d(TAG, "Intent is null");

                } else {
                    Log.d(TAG, "Photo uri: " + intentPhoto.getData());
                    Bundle bndl = intentPhoto.getExtras();
                    if (bndl != null) {
                        Object obj = intentPhoto.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }

        }

    }

    private Uri generateFileUri(int type) {
        File file = null;
        mPhotoNameFile = "photo_" + System.currentTimeMillis() + ".jpg";
        mPhotoPath = directory.getPath();
        file = new File(mPhotoPath + "/" + mPhotoNameFile);

        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }

    //Date picker biblioteks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}