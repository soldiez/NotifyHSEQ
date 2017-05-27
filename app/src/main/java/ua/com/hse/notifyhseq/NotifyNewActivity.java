package ua.com.hse.notifyhseq;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import ua.com.hse.notifyhseq.mail.Attachment;
import ua.com.hse.notifyhseq.mail.Mail;
import ua.com.hse.notifyhseq.mail.MailSender;
import ua.com.hse.notifyhseq.mail.Recipient;
import ua.com.hse.notifyhseq.pickers.DatePickerFragment;
import ua.com.hse.notifyhseq.pickers.TimePickerFragment;

import static ua.com.hse.notifyhseq.R.id.newNotifyAccidentType;
import static ua.com.hse.notifyhseq.R.id.newNotifyDepartment;
import static ua.com.hse.notifyhseq.R.id.newNotifyPlace;



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
    Long mNewNotifyCurrentTime;
    Long mNewNotifyTime;
    String mNewNotifyPlace;
    String mNewNotifyDepartment;
    String mNewNotifyAccidentType;
    String mNewNotifyDescription;
    String mPhotoPath = "";
    String mPhotoNameFile = "";
    String mPhotoNameFileCamera = "";
    int mNotifyStatus = 0;
    String mNamePerson; //= MainActivity.mUserName;
    String mEmailPerson; //= MainActivity.mUserEmail;
    String mPhonePerson; // = "0504223846";
    String mDepartmentPerson; // = "Deprt";
    ArrayList<String> arrayDepartment;
    ArrayList<String> arrayPlace;
    EditText newNotifyEditTextDate, newNotifyEditTextTime;
    EditText newNotifyEditTextDescription;
    // Для фото переменные
    File directory;

    SharedPreferences basePreference;
    int int_condition = 0;

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

        String stringDepartments = MainActivity.getPreferences("arrayDepartments", this);
        String stringPlaces = MainActivity.getPreferences("arrayPlaces", this);
        mNamePerson = MainActivity.getPreferences("m_name_person", this);
        mEmailPerson = MainActivity.getPreferences("m_email_person", this);
        mPhonePerson = MainActivity.getPreferences("m_phone_person", this);
        mDepartmentPerson = MainActivity.getPreferences("m_department_person", this);
        // Log.d("MyTAG    ", stringDepartments);

        //Проверка необходимых разрешений
        verifyStoragePermissions(this);

        //Создание директории (если ее нет)
        createDirectory();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("notifyHSEQ");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();



//** Получение текущей даты и времени из системы
        long c = System.currentTimeMillis();

        newNotifyEditTextDate = (EditText) findViewById(R.id.newNotifyDate);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MM, yyyy", Locale.US);
        String mNewNotifyDateVisible = sdfDate.format(c);
        newNotifyEditTextDate.setText(mNewNotifyDateVisible);

        newNotifyEditTextTime = (EditText) findViewById(R.id.newNotifyTime);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.US);
        String mNewNotifyTimeVisible = sdfTime.format(c);
        newNotifyEditTextTime.setText(mNewNotifyTimeVisible);

        mNewNotifyTime = c;

        final ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);

//Date picker
//        final EditText mPickDate = (EditText) findViewById(R.id.newNotifyDate);
        newNotifyEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

// Time picker
//        final EditText mPickTime = (EditText) findViewById(R.id.newNotifyTime);
        newNotifyEditTextTime.setOnClickListener(new View.OnClickListener() {
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
        arrayPlace = new ArrayList<>(Arrays.asList(stringPlaces.split(",")));
        ArrayAdapter<String> adapterPlace = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arrayPlace);
// Specify the layout to use when the list of choices appears
        adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerPlace.setAdapter(adapterPlace);

//Spinner for department
        final Spinner spinnerDepartment = (Spinner) findViewById(newNotifyDepartment);
        // Create an ArrayAdapter using the string array and a default spinner layout
        arrayDepartment = new ArrayList<>(Arrays.asList(stringDepartments.split(",")));
        ArrayAdapter<String> adapterDepartment = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arrayDepartment);
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
                mNewNotifyCurrentTime = System.currentTimeMillis();


//**Получение данных из заполненных полей

                String toParse = newNotifyEditTextDate.getText().toString() + " " + newNotifyEditTextTime.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("dd MM, yyyy HH:mm", Locale.US);

                Date date = null;
                try {
                    date = formatter.parse(toParse);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date != null) {
                    mNewNotifyTime = date.getTime();
                }

                mNewNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                mNewNotifyPlace = spinnerPlace.getSelectedItem().toString();
                mNewNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                mNewNotifyDescription = newNotifyEditTextDescription.getText().toString();

//send to Firebase database
                NotifyHSEQItem notifyHSEQItem = new NotifyHSEQItem(
                        uid, mNewNotifyCurrentTime, mNewNotifyTime, mNewNotifyAccidentType,
                        mNewNotifyPlace, mNewNotifyDepartment, mNewNotifyDescription, mPhotoPath, mPhotoNameFile,
                        mNotifyStatus, mNamePerson, mEmailPerson, mPhonePerson, mDepartmentPerson);
                myRef.push().setValue(notifyHSEQItem);

                if (!mPhotoNameFile.equals("")) {
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
//                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                                Toast.makeText(getApplicationContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Upload photo is done", Toast.LENGTH_SHORT).show();
//                        Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                        }
                    });
                }
                finish();
            }
        });

//Send button - listener
        final Button buttonSendNotify = (Button) findViewById(R.id.sendNotifyButton);
        buttonSendNotify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Send info about new Notify

                //Получение текущей даты и времени из системы
                mNewNotifyCurrentTime = System.currentTimeMillis();

                //Получение данных из заполненных полей
                String toParse = newNotifyEditTextDate.getText().toString() + " " + newNotifyEditTextTime.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("dd MM, yyyy HH:mm", Locale.US);
                Date date = null; // You will need try/catch around this
                try {
                    date = formatter.parse(toParse);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date != null) {
                    mNewNotifyTime = date.getTime();
                    mNewNotifyPlace = spinnerPlace.getSelectedItem().toString();
                    mNewNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                    mNewNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                    mNewNotifyDescription = newNotifyEditTextDescription.getText().toString();

                    mNotifyFullText = "Notify:";
                    mNotifyFullText += "\nRegister: Date: " + mNewNotifyCurrentTime;
                    mNotifyFullText += "\nTime: " + mNewNotifyTime;
                    mNotifyFullText += "\nPlace: " + mNewNotifyPlace;
                    mNotifyFullText += "\nDepartment: " + mNewNotifyDepartment;
                    mNotifyFullText += "\nAccident type: " + mNewNotifyAccidentType;
                    mNotifyFullText += "\nShort information:\n: " + mNewNotifyDescription;


// Посылаем письмо с информацией
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

                    finish();
                }
            }
        });
// обработка картинки фото

        buttonTakePhotoOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //** делаем фото, сохраняем
                Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                startActivityForResult(intentPhoto, REQUEST_CODE_PHOTO);
            }
        });

        // check intent to photo request
        String resultIntent = getIntent().getAction();
        // Log.d("MyLOG", resultIntent.toString());
        if (resultIntent != null && resultIntent.equals("photoButton")) {
            buttonTakePhotoOne.performClick();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentPhoto) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (intentPhoto == null) {
                    Log.d(TAG, "Intent is null");

                    ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);
                    String filePath = new ImageCompression(getBaseContext()).compressImage(mPhotoPath + "/" + mPhotoNameFileCamera);
                    mPhotoNameFile = filePath.substring(filePath.lastIndexOf("/") + 1);
                    Log.d(TAG, "New file name:" + mPhotoNameFile);
                    Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
                    buttonTakePhotoOne.setImageBitmap(myBitmap);
                    File fileCamera = new File(mPhotoPath + "/" + mPhotoNameFileCamera);
                    fileCamera.delete();
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
        File fileCamera = null;
        mPhotoNameFileCamera = "photo_" + System.currentTimeMillis() + ".jpg";
        mPhotoPath = directory.getPath();
        fileCamera = new File(mPhotoPath + "/" + mPhotoNameFileCamera);
        Log.d(TAG, "fileName = " + fileCamera);
        return Uri.fromFile(fileCamera);
    }

    private void createDirectory() {
        directory = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getBaseContext().getApplicationContext().getPackageName()
                + "/Files");
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