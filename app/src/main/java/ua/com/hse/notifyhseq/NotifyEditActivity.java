package ua.com.hse.notifyhseq;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import ua.com.hse.notifyhseq.pickers.DatePickerFragment;
import ua.com.hse.notifyhseq.pickers.TimePickerFragment;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.NONE;
import static ua.com.hse.notifyhseq.R.array.AccidentType;

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

    Long mEditNotifyCurrentTime;
    Long mEditNotifyTime;
    String mEditNotifyPlace;
    String mEditNotifyDepartment;
    String mEditNotifyAccidentType;
    String mEditNotifyDescription;
    String mPhotoNameFileCamera = "";
    int mNotifyStatus = 0;
    String mNamePerson;
    String mEmailPerson;
    String mPhonePerson;
    String mDepartmentPerson;

    EditText editNotifyEditTextDescription;

    File directory;

    String mNameFile, mNamePath, mNameFileNew, mNamePathNew;

    ArrayList<String> arrayDepartment; //= MainActivity.arrayDepartments;
    ArrayList<String> arrayPlace; // = MainActivity.arrayPlaces;

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

        final String stringDepartments = MainActivity.getPreferences("arrayDepartments", this);
        final String stringPlaces = MainActivity.getPreferences("arrayPlaces", this);

        mNamePerson = MainActivity.getPreferences("m_name_person", this);
        mEmailPerson = MainActivity.getPreferences("m_email_person", this);
        mPhonePerson = MainActivity.getPreferences("m_phone_person", this);
        mDepartmentPerson = MainActivity.getPreferences("m_department_person", this);

//TODO сейчас не меняет данные юзера после редактирования - проверить сделать

        //Создание директории (если ее нет)
        createDirectory();

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

                uid = notifyHSEQItem.getUid();
                mEditNotifyCurrentTime = notifyHSEQItem.getTimeRegistration();
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

//Date picker
                final EditText mPickDate = (EditText) findViewById(R.id.newNotifyDate);
                mPickDate.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    }
                });

// Time picker
                final EditText mPickTime = (EditText) findViewById(R.id.newNotifyTime);
                mPickTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment newFragment = new TimePickerFragment();
                        newFragment.show(getSupportFragmentManager(), "timePicker");
                    }
                });
// Delete button
                Button mDeleteImage = (Button) findViewById(R.id.deleteImageButton);
                mDeleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDeleteDialog();
                    }
                });


//** Привязка к объектам в отображении
                editNotifyEditTextDescription = (EditText) findViewById(R.id.editNotifyCurentDescription);

//Spinner for place
                final Spinner spinnerPlace = (Spinner) findViewById(R.id.editNotifyPlace);
                arrayPlace = new ArrayList<>(Arrays.asList(stringPlaces.split(",")));
                ArrayAdapter<String> adapterPlace = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_spinner_item, arrayPlace);
                adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPlace.setAdapter(adapterPlace);

//Spinner for department
                final Spinner spinnerDepartment = (Spinner) findViewById(R.id.editNotifyDepartment);
                arrayDepartment = new ArrayList<>(Arrays.asList(stringDepartments.split(",")));
                ArrayAdapter<String> adapterDepartment = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_spinner_item, arrayDepartment);
                adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDepartment.setAdapter(adapterDepartment);

//Spinner for Accident type
                final Spinner spinnerAccidentType = (Spinner) findViewById(R.id.editNotifyAccidentType);
                ArrayAdapter<CharSequence> adapterAccidentType = ArrayAdapter.createFromResource(getBaseContext(),
                        AccidentType, android.R.layout.simple_spinner_item);
                adapterAccidentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAccidentType.setAdapter(adapterAccidentType);


                SimpleDateFormat sdfDate = new SimpleDateFormat("dd MM, yyyy", Locale.US);
                Date resultDate = new Date(mEditNotifyTime);
                mPickDate.setText(sdfDate.format(resultDate));

                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.US);
                Date resultTime = new Date(mEditNotifyTime);
                mPickTime.setText(sdfTime.format(resultTime));

                spinnerAccidentType.setSelection(getIndex(spinnerAccidentType, mEditNotifyAccidentType));
                spinnerPlace.setSelection(getIndex(spinnerPlace, mEditNotifyPlace));
                spinnerDepartment.setSelection(getIndex(spinnerDepartment, mEditNotifyDepartment));
                editNotifyEditTextDescription.setText(mEditNotifyDescription);

//TODO проверку наличия фото в базе (где в файле пока не понятно) - выдает ошибку при отсутствии файла на сайте, и показывает белый imageView
// обработка картинки фото
                final ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);

                if (!mNameFile.equals("")) {
                    storageReference = firebaseStorage.getReference().child("images").child(mNameFile);

                    // Got the download URL for 'users/me/profile.png'

                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .diskCacheStrategy(NONE)
                            .skipMemoryCache(true)
                            .into(buttonTakePhotoOne);
                    buttonTakePhotoOne.setScaleType(ImageView.ScaleType.CENTER_CROP);

                }

                    buttonTakePhotoOne.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
//** делаем фото, сохраняем
                            Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                            startActivityForResult(intentPhoto, REQUEST_CODE_PHOTO);
                        }
                    });

//update button - listener
                final Button buttonUpdateNotify = (Button) findViewById(R.id.updateNotifyButton);
                buttonUpdateNotify.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

//**Получение данных из заполненных полей
                        String toParse = mPickDate.getText().toString() + " " + mPickTime.getText().toString();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd MM, yyyy HH:mm", Locale.US);
                        Date date = null;
                        try {
                            date = formatter.parse(toParse);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            mEditNotifyTime = date.getTime();
                            mEditNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                            mEditNotifyPlace = spinnerPlace.getSelectedItem().toString();
                            mEditNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                            mEditNotifyDescription = editNotifyEditTextDescription.getText().toString();


                            NotifyHSEQItem notifyHSEQItem = new NotifyHSEQItem(
                                    uid, mEditNotifyCurrentTime, mEditNotifyTime, mEditNotifyAccidentType,
                                    mEditNotifyPlace, mEditNotifyDepartment, mEditNotifyDescription, mNamePathNew, mNameFileNew,
                                    mNotifyStatus, mNamePerson, mEmailPerson, mPhonePerson, mDepartmentPerson);

                            mNotifyDatabaseReference.setValue(notifyHSEQItem);


                            if (!mNameFileNew.equals(mNameFile) && !mNameFileNew.equals("")) {

                                //delete of file
                                deleteImage();
                                Uri file = Uri.fromFile(new File(mNamePathNew + "/" + mNameFileNew));


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
                                                //                                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                //                                            Toast.makeText(getApplicationContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                        //                                       System.out.println("Upload is paused");
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
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void deleteImage() {
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                mNameFileNew = "";
                Log.d(TAG, "Image is deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "Image did not delete");
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

                    ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);
                    String filePath = new ImageCompression(getBaseContext()).compressImage(mNamePathNew + "/" + mPhotoNameFileCamera);
                    mNameFileNew = filePath.substring(filePath.lastIndexOf("/") + 1);
                    Log.d(TAG, "New file name:" + mNameFileNew);
                    Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
                    buttonTakePhotoOne.setImageBitmap(myBitmap);
                    File fileCamera = new File(mNamePathNew + "/" + mPhotoNameFileCamera);
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
        File newFileCamera = null;
        mPhotoNameFileCamera = "photo_" + System.currentTimeMillis() + ".jpg";
        mNamePathNew = directory.getPath();
        newFileCamera = new File(mNamePathNew + "/" + mPhotoNameFileCamera);
        Log.d(TAG, "fileNameCamera = " + mPhotoNameFileCamera);
        return Uri.fromFile(newFileCamera);
    }

    private void createDirectory() {
        directory = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getBaseContext().getApplicationContext().getPackageName()
                + "/Files");
        if (!directory.exists())
            directory.mkdirs();
    }

    private void openDeleteDialog() {

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Delete photo: Are you sure?");

        quitDialog.setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImage();
                final ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);
                buttonTakePhotoOne.setImageResource(R.drawable.ic_photo_camera_black_24dp);
            }
        });

        quitDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        quitDialog.show();
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