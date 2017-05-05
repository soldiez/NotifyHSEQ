package ua.com.hse.notifyhseq;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotifyShowActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mNotifyDatabaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    int uid;

    Long mEditNotifyCurrentTime;
    Long mEditNotifyTime;
    String mEditNotifyPlace;
    String mEditNotifyDepartment;
    String mEditNotifyAccidentType;
    String mEditNotifyDescription;
    int mNotifyStatus;
    String mNamePerson;
    String mEmailPerson;
    String mPhonePerson;
    String mDepartmentPerson;

    // Для фото переменные
    String mNameFile, mNamePath;
    // for database
    String key;


    Button deleteButton;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_show);

        Intent intent = getIntent();
        key = intent.getExtras().getString("key");
        Log.d("MyLOG     ", key);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNotifyDatabaseReference = mFirebaseDatabase.getReference().child("notifyHSEQ").child(key);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        mNotifyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final NotifyHSEQItem notifyHSEQItem = dataSnapshot.getValue(NotifyHSEQItem.class);


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

                // do your stuff here with value

                TextView notifyDateTimeRegistered = (TextView) findViewById(R.id.NotifyDateTimeRegistered);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy   HH:mm", Locale.US);
                Date resultdate = new Date(mEditNotifyCurrentTime);
                notifyDateTimeRegistered.setText(sdf.format(resultdate));

                TextView notifyDateTimeHappened = (TextView) findViewById(R.id.NotifyDateTimeHappened);
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM, yyyy   HH:mm", Locale.US);
                Date resultdate1 = new Date(mEditNotifyTime);
                notifyDateTimeHappened.setText(sdf1.format(resultdate1));

                TextView notifyAccidentType = (TextView) findViewById(R.id.NotifyAccidentType);
                notifyAccidentType.setText(mEditNotifyAccidentType);

                TextView notifyPlace = (TextView) findViewById(R.id.NotifyPlace);
                notifyPlace.setText(mEditNotifyPlace);

                TextView notifyDepartment = (TextView) findViewById(R.id.NotifyDepartment);
                notifyDepartment.setText(mEditNotifyDepartment);

                TextView notifyDescription = (TextView) findViewById(R.id.NotifyDescription);
                notifyDescription.setText(mEditNotifyDescription);


                final ImageView takePicture = (ImageView) findViewById(R.id.takePicture);
// обработка картинки фото

                if (!mNameFile.equals("")) {
                    storageReference = firebaseStorage.getReference().child("images").child(mNameFile);
//TODO проверку наличия файла на сайте - выдает ошибку и показывает белый imageView
//download image to cache and show in imageview
                    Glide.with(getApplicationContext() /* context */)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(takePicture);
                    takePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
// button show image in full screen (in other activity)
                if (!mNameFile.equals("")) {
                    takePicture.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            Intent intent = new Intent(NotifyShowActivity.this, PhotoShow.class);
                            intent.putExtra("path", mNamePath);
                            intent.putExtra("nameFile", mNameFile);
                            startActivity(intent);
                        }
                    });
                }
//update button - listener
                final Button buttonUpdateNotify = (Button) findViewById(R.id.editNotifyButton);
                buttonUpdateNotify.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent(NotifyShowActivity.this, NotifyEditActivity.class);
                        intent.putExtra("key", key);
                        startActivity(intent);
                        finish();
                    }
                });

// delete button
                deleteButton = (Button) findViewById(R.id.deleteNotifyButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        openDeleteDialog();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

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

//    public boolean checkNetworkConnection() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        return (networkInfo != null && networkInfo.isConnected());
//    }


    private void openDeleteDialog() {

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setTitle("Delete notify: Are you sure?");

        quitDialog.setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mNotifyDatabaseReference.removeValue();

// Delete the file
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Toast.makeText(getApplicationContext(), "Delete notify is done", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                    }
                });
                finish();
            }
        });

        quitDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

//    public void showImage() {
//        Dialog builder = new Dialog(this);
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        builder.getWindow().setBackgroundDrawable(
//                new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                //nothing;
//            }
//        });
//
//        ImageView imageView = new ImageView(this);
//        Glide.with(getApplicationContext() /* context */)
//                .using(new FirebaseImageLoader())
//                .load(firebaseStorage.getReference().child("images").child(mNameFile))
//                .into();
//     //   imageView.setImageURI(imageUri);
//        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        builder.show();
//    }



}
