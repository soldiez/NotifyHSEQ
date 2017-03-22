package ua.com.hse.notifyhseq;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import ua.com.hse.notifyhseq.pickers.DatePickerFragment;
import ua.com.hse.notifyhseq.pickers.TimePickerFragment;

import static ua.com.hse.notifyhseq.R.array.AccidentType;
import static ua.com.hse.notifyhseq.R.id.takePhotoOne;
import static ua.com.hse.notifyhseq.R.id.takePicture;

public class NotifyEditActivity extends AppCompatActivity {


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    final int TYPE_PHOTO = 1;
    final int REQUEST_CODE_PHOTO = 1;
    final String TAG = "myLogs";
    int uid;

    String mEditNotifyDate;
    String mEditNotifyTime;
    String mEditNotifyCurrentTime;
    String mEditNotifyPlace;
    String mEditNotifyDepartment;
    String mEditNotifyAccidentType;
    String mEditNotifyDescription;
    String mNotifyFullText;
    int mNotifyStatus = 0;
    String mNamePerson = "Alex";
    String mEmailPerson = "soldiez@yandex.ru";
    String mPhonePerson = "0504223846";
    String mDepartmentPerson = "Deprt";

    EditText editNotifyEditTextDate, editNotifyEditTextTime, editNotifyEditTextDescription;
    // Для фото переменные
    File directory;

    String mNameFile, mNamePath, mNameFileNew, mNamePathNew;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mNotifyDatabaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference, storageReferenceNew;
    UploadTask uploadTask;

    String key;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_edit);
        verifyStoragePermissions(this);
        //Создание директории (если ее нет)
        createDirectory();
        // actualise database


        Intent intent = getIntent();

        key = intent.getExtras().getString("key");
        Log.d("MyLOG     ", key);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNotifyDatabaseReference = mFirebaseDatabase.getReference().child("notifyHSEQ").child(key);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        storageReferenceNew = firebaseStorage.getReference();

        mNotifyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                NotifyHSEQItem notifyHSEQItem = dataSnapshot.getValue(NotifyHSEQItem.class);
//                Log.d("MyLOG      ", dataSnapshot.getValue(NotifyHSEQItem.class).toString());

                uid = notifyHSEQItem.getUid();
                mEditNotifyCurrentTime = notifyHSEQItem.getTimeRegistration();
                mEditNotifyDate = notifyHSEQItem.getDateHappened();
                mEditNotifyTime = notifyHSEQItem.getTimeHappened();
                mEditNotifyAccidentType = notifyHSEQItem.getType();
                mEditNotifyPlace = notifyHSEQItem.getPlace();
                mEditNotifyDepartment = notifyHSEQItem.getDepartment();
                mEditNotifyDescription = notifyHSEQItem.getDescription();
                mNamePath = notifyHSEQItem.getPhotoPath();
                mNameFile = notifyHSEQItem.getPhotoName();
                mNotifyStatus = notifyHSEQItem.getStatus();
                mNamePerson = notifyHSEQItem.getNamePerson();
                mEmailPerson = notifyHSEQItem.getEmailPerson();
                mPhonePerson = notifyHSEQItem.getPhonePerson();
                mDepartmentPerson = notifyHSEQItem.getDepartmentPerson();

                mNameFileNew = mNameFile;
                mNamePathNew = mNamePath;

//TODO button delete image

//Date picker
                editNotifyEditTextDate = (EditText) findViewById(R.id.newNotifyDate);
                final Button mPickDate = (Button) findViewById(R.id.editNotifyDateButton);
                mPickDate.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    }
                });

// Time picker
                editNotifyEditTextTime = (EditText) findViewById(R.id.newNotifyTime);
                final Button mPickTime = (Button) findViewById(R.id.editNotifyTimeButton);
                mPickTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment newFragment = new TimePickerFragment();
                        newFragment.show(getSupportFragmentManager(), "timePicker");
                    }
                });


//** Привязка к объектам в отображении
                editNotifyEditTextDescription = (EditText) findViewById(R.id.editNotifyCurentDescription);

//Spinner for place
                final Spinner spinnerPlace = (Spinner) findViewById(R.id.editNotifyPlace);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapterPlace = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.Place, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


// Apply the adapter to the spinner
                spinnerPlace.setAdapter(adapterPlace);

//Spinner for department
                final Spinner spinnerDepartment = (Spinner) findViewById(R.id.editNotifyDepartment);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapterDepartment = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.Department, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinnerDepartment.setAdapter(adapterDepartment);

//Spinner for Accident type
                final Spinner spinnerAccidentType = (Spinner) findViewById(R.id.editNotifyAccidentType);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapterAccidentType = ArrayAdapter.createFromResource(getBaseContext(),
                        AccidentType, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapterAccidentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinnerAccidentType.setAdapter(adapterAccidentType);


                editNotifyEditTextDate.setText(mEditNotifyDate);
                editNotifyEditTextTime.setText(mEditNotifyTime);
                spinnerAccidentType.setSelection(getIndex(spinnerAccidentType, mEditNotifyAccidentType));
                spinnerPlace.setSelection(getIndex(spinnerPlace, mEditNotifyPlace));
                spinnerDepartment.setSelection(getIndex(spinnerDepartment, mEditNotifyDepartment));
                editNotifyEditTextDescription.setText(mEditNotifyDescription);


// обработка картинки фото
                final ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);

                if (!mNameFile.equals("")) {
                    storageReference = firebaseStorage.getReference().child("images").child(mNameFile);

                    Glide.with(getApplicationContext() /* context */)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(buttonTakePhotoOne);
                }


//                File imgFile = new File(mNamePath + "/" + mNameFile);


                    buttonTakePhotoOne.setOnClickListener(new View.OnClickListener() {


                        public void onClick(View v) {

//** делаем фото, сохраняем и вставляем в вид
                            Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                            startActivityForResult(intentPhoto, REQUEST_CODE_PHOTO);
//TODO insert image to viewimage

                        }
                    });



//update button - listener
                final Button buttonUpdateNotify = (Button) findViewById(R.id.updateNotifyButton);
                buttonUpdateNotify.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

//**Получение данных из заполненных полей
                        mEditNotifyDate = editNotifyEditTextDate.getText().toString();
                        mEditNotifyTime = editNotifyEditTextTime.getText().toString();
                        mEditNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                        mEditNotifyPlace = spinnerPlace.getSelectedItem().toString();
                        mEditNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                        mEditNotifyDescription = editNotifyEditTextDescription.getText().toString();


                        //TODO update firebase function
                        NotifyHSEQItem notifyHSEQItem = new NotifyHSEQItem(
                                uid, mEditNotifyCurrentTime, mEditNotifyDate, mEditNotifyTime, mEditNotifyAccidentType,
                                mEditNotifyPlace, mEditNotifyDepartment, mEditNotifyDescription, mNamePathNew, mNameFileNew,
                                mNotifyStatus, mNamePerson, mEmailPerson, mPhonePerson, mDepartmentPerson);

                        mNotifyDatabaseReference.setValue(notifyHSEQItem);


//TODO upload image after cheking of changes
                        if (!mNameFileNew.equals(mNameFile)) {

                            Uri file = Uri.fromFile(new File(mNamePathNew + "/" + mNameFileNew));

                            //delete ol file
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                }
                            });
// Create the file metadata
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpeg")
                                    .build();

// Upload file and metadata
                            uploadTask = storageReferenceNew.child("images/" + file.getLastPathSegment()).putFile(file, metadata);

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

                        }

                        Intent activityChangeIntent = new Intent(NotifyEditActivity.this, MainActivity.class);
                        NotifyEditActivity.this.startActivity(activityChangeIntent);
                    }
                });

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    //get place for text in spinner
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
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
        File newFile = null;
        mNameFileNew = "photo_" + System.currentTimeMillis() + ".jpg";
        mNamePathNew = directory.getPath();
        newFile = new File(mNamePathNew + "/" + mNameFileNew);

        Log.d(TAG, "fileName = " + newFile);
        return Uri.fromFile(newFile);
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

//data from syncitem example program

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
