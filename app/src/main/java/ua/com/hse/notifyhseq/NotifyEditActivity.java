package ua.com.hse.notifyhseq;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

import ua.com.hse.notifyhseq.pickers.DatePickerFragment;
import ua.com.hse.notifyhseq.pickers.TimePickerFragment;

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
    // for database
    DBAdapter adapter;
    NotifyOpenHelper notifyOpenHelper;


    //    NotifyOpenHelper openHelper;
    int rowId;
    Cursor c;
    Button deleteButton;
    String requestBody;

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
        adapter = new DBAdapter(this); //?


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
        ArrayAdapter<CharSequence> adapterPlace = ArrayAdapter.createFromResource(this,
                R.array.Place, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


// Apply the adapter to the spinner
        spinnerPlace.setAdapter(adapterPlace);

//Spinner for department
        final Spinner spinnerDepartment = (Spinner) findViewById(R.id.editNotifyDepartment);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterDepartment = ArrayAdapter.createFromResource(this,
                R.array.Department, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerDepartment.setAdapter(adapterDepartment);

//Spinner for Accident type
        final Spinner spinnerAccidentType = (Spinner) findViewById(R.id.editNotifyAccidentType);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterAccidentType = ArrayAdapter.createFromResource(this,
                AccidentType, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterAccidentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerAccidentType.setAdapter(adapterAccidentType);

        Bundle showData = getIntent().getExtras();
        rowId = showData.getInt("keyid");
        // Toast.makeText(getApplicationContext(), Integer.toString(rowId),
        // 500).show();
        adapter = new DBAdapter(this);

        c = adapter.queryAll(rowId);

        if (c.moveToFirst()) {
            do {
                mainNumber = Integer.parseInt(c.getString(0));
                sync = Integer.parseInt(c.getString(1));
                mEditNotifyCurrentDate = c.getString(2);
                mEditNotifyCurrentTime = c.getString(3);

                mEditNotifyDate = c.getString(4);
                editNotifyEditTextDate.setText(mEditNotifyDate);
                mEditNotifyTime = c.getString(5);
                editNotifyEditTextTime.setText(mEditNotifyTime);
                mEditNotifyAccidentType = c.getString(6);
                spinnerAccidentType.setSelection(getIndex(spinnerAccidentType, mEditNotifyAccidentType));
                mEditNotifyPlace = c.getString(7);
                spinnerPlace.setSelection(getIndex(spinnerPlace, mEditNotifyPlace));
                mEditNotifyDepartment = c.getString(8);
                spinnerDepartment.setSelection(getIndex(spinnerDepartment, mEditNotifyDepartment));
                mEditNotifyDescription = c.getString(9);
                editNotifyEditTextDescription.setText(mEditNotifyDescription);

                mNamePath = c.getString(10);
                mNameFile = c.getString(11);
                mNotifyStatus = Integer.parseInt(c.getString(12));
                mNamePerson = c.getString(13);
                mEmailPerson = c.getString(14);
                mPhonePerson = c.getString(15);
                mDepartmentPerson = c.getString(16);

            } while (c.moveToNext());
        }


// обработка картинки фото
        final ImageView buttonTakePhotoOne = (ImageView) findViewById(R.id.takePhotoOne);


        File imgFile = new File(mNamePath + "/" + mNameFile);

        if (imgFile.exists() && imgFile.isFile()) {
            Bitmap bitmapImage = BitmapFactory.decodeFile(mNamePath + "/" + mNameFile);
            int nh = (int) (bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
            buttonTakePhotoOne.setImageBitmap(scaled);

            buttonTakePhotoOne.setOnClickListener(new View.OnClickListener() {


                public void onClick(View v) {
                    //TODO show photo
//** делаем фото, сохраняем и вставляем в вид
                    Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                    startActivityForResult(intentPhoto, REQUEST_CODE_PHOTO);


                }
            });
        } else {

            buttonTakePhotoOne.setOnClickListener(new View.OnClickListener() {


                public void onClick(View v) {
                    //TODO show photo
//** делаем фото, сохраняем и вставляем в вид
                    Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                    startActivityForResult(intentPhoto, REQUEST_CODE_PHOTO);

                }
            });
        }

//загрузка данных из вызванной ячейки



//update button - listener
        final Button buttonUpdateNotify = (Button) findViewById(R.id.updateNotifyButton);
        buttonUpdateNotify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //update info to database Notify

//**Получение данных из заполненных полей
                mEditNotifyDate = editNotifyEditTextDate.getText().toString();
                mEditNotifyTime = editNotifyEditTextTime.getText().toString();
                mEditNotifyAccidentType = spinnerAccidentType.getSelectedItem().toString();
                mEditNotifyPlace = spinnerPlace.getSelectedItem().toString();
                mEditNotifyDepartment = spinnerDepartment.getSelectedItem().toString();
                mEditNotifyDescription = editNotifyEditTextDescription.getText().toString();
                sync = 0;


                updateToAppServer();//TODO must to save moore right save of data (only for changed fields

                finish();
                // going back to MainActivity
                Intent activityChangeIntent = new Intent(NotifyEditActivity.this, MainActivity.class);
                // currentContext.startActivity(activityChangeIntent);
                NotifyEditActivity.this.startActivity(activityChangeIntent);
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
        File file = null;
        mNameFileNew = "photo_" + System.currentTimeMillis() + ".jpg";
        mNamePathNew = directory.getPath();
        file = new File(mNamePathNew + "/" + mNameFileNew);

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

    private void updateToAppServer() {

        if (checkNetworkConnection()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mainNumber", mainNumber);
                jsonObject.put("sync", sync);
                jsonObject.put("dateRegistration", mEditNotifyCurrentDate);
                jsonObject.put("timeRegistration", mEditNotifyCurrentTime);
                jsonObject.put("dateHappened", mEditNotifyDate);
                jsonObject.put("timeHappened", mEditNotifyTime);
                jsonObject.put("type", mEditNotifyAccidentType);
                jsonObject.put("place", mEditNotifyPlace);
                jsonObject.put("department", mEditNotifyDepartment);
                jsonObject.put("description", mEditNotifyDescription);
                jsonObject.put("photoPath", mNamePathNew);
                jsonObject.put("photoName", mNameFileNew);
                jsonObject.put("status", mNotifyStatus);
                jsonObject.put("namePerson", mNamePerson);
                jsonObject.put("emailPerson", mEmailPerson);
                jsonObject.put("phonePerson", mPhonePerson);
                jsonObject.put("departmentPerson", mDepartmentPerson);
                Log.d(TAG + " URL:", jsonObject.toString());
                requestBody = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, NotifyOpenHelper.SERVER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG + " Response:", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG + " Error:", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return String.format("application/json; charset=utf-8");
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf(TAG + " Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };
            MySingleton.getInstance(this).addToRequestQue(stringRequest);
            updateToLocalStorage();
        }
    }


    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void updateToLocalStorage() {
        long val = adapter.updateDetail(rowId, mainNumber, sync, mEditNotifyCurrentDate, mEditNotifyCurrentTime,
                mEditNotifyDate, mEditNotifyTime, mEditNotifyAccidentType, mEditNotifyPlace,
                mEditNotifyDepartment, mEditNotifyDescription, mNamePathNew, mNameFileNew, mNotifyStatus,
                mNamePerson, mEmailPerson, mPhonePerson, mDepartmentPerson);
        // Toast.makeText(getApplicationContext(), Long.toString(val),
        // 300).show();
    }


}
