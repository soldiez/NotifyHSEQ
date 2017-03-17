package ua.com.hse.notifyhseq;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class NotifyShowActivity extends AppCompatActivity {


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    int mainNumber;
    int sync;
    String mEditNotifyDate;
    String mEditNotifyTime;
    String mEditNotifyCurrentDate;
    String mEditNotifyCurrentTime;
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
    //   DBAdapter adapter;

    //NotifyOpenHelper openHelper;
    int rowId;
    Cursor c;
    Button editButton, deleteButton;
    String requestBody;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_show);
        // actualise database
        //       adapter = new DBAdapter(this);

        Bundle showData = getIntent().getExtras();
        rowId = showData.getInt("keyid");
        // Toast.makeText(getApplicationContext(), Integer.toString(rowId),
        // 500).show();
//        adapter = new DBAdapter(this);

//        c = adapter.queryAll(rowId);

        if (c.moveToFirst()) {
            do {
                mainNumber = Integer.parseInt(c.getString(0));
                sync = Integer.parseInt(c.getString(1));
                mEditNotifyCurrentDate = c.getString(2);
                mEditNotifyCurrentTime = c.getString(3);

                mEditNotifyDate = c.getString(4);

                mEditNotifyTime = c.getString(5);

                mEditNotifyAccidentType = c.getString(6);
                mEditNotifyPlace = c.getString(7);
                mEditNotifyDepartment = c.getString(8);

                mEditNotifyDescription = c.getString(9);
                mNamePath = c.getString(10);
                mNameFile = c.getString(11);
                mNotifyStatus = Integer.parseInt(c.getString(12));
                mNamePerson = c.getString(13);
                mEmailPerson = c.getString(14);
                mPhonePerson = c.getString(15);
                mDepartmentPerson = c.getString(16);

            } while (c.moveToNext());
        }
        TextView notifyDateTimeRegistered = (TextView) findViewById(R.id.NotifyDateTimeRegistered);
        notifyDateTimeRegistered.setText(mEditNotifyCurrentDate + " " + mEditNotifyCurrentTime);

        TextView notifyDateTimeHappened = (TextView) findViewById(R.id.NotifyDateTimeHappened);
        notifyDateTimeHappened.setText(mEditNotifyDate + " " + mEditNotifyTime);

        TextView notifyAccidentType = (TextView) findViewById(R.id.NotifyAccidentType);
        notifyAccidentType.setText(mEditNotifyAccidentType);

        TextView notifyPlace = (TextView) findViewById(R.id.NotifyPlace);
        notifyPlace.setText(mEditNotifyPlace);

        TextView notifyDepartment = (TextView) findViewById(R.id.NotifyDepartment);
        notifyDepartment.setText(mEditNotifyDepartment);

        TextView notifyDescription = (TextView) findViewById(R.id.NotifyDescription);
        notifyDescription.setText(mEditNotifyDescription);


// обработка картинки фото
        final ImageView takePicture = (ImageView) findViewById(R.id.takePicture);
        File imgFile = new File(mNamePath + "/" + mNameFile);

        if (imgFile.exists() && imgFile.isFile()) {
            Bitmap bitmapImage = BitmapFactory.decodeFile(mNamePath + "/" + mNameFile);
            int nh = (int) (bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
            takePicture.setImageBitmap(scaled);

            takePicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO show photo

                }
            });
        } else {

        }


//update button - listener
        final Button buttonUpdateNotify = (Button) findViewById(R.id.editNotifyButton);
        buttonUpdateNotify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Bundle passdata = new Bundle();
                int nameId = rowId;
                passdata.putInt("keyid", nameId);
                Intent passIntent = new Intent(NotifyShowActivity.this,
                        NotifyEditActivity.class);
                passIntent.putExtras(passdata);
                startActivity(passIntent);


            }
        });

// delete button
        deleteButton = (Button) findViewById(R.id.deleteNotifyButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Are you sure window?

//                adapter.deleteOneRecord(rowId);

//delete from app server


                finish();
                // going back to MainActivity
                Intent activityChangeIntent = new Intent(NotifyShowActivity.this, MainActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                NotifyShowActivity.this.startActivity(activityChangeIntent);
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

//

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

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


}
