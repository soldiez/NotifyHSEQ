package ua.com.hse.notifyhseq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static ua.com.hse.notifyhseq.R.layout.activity_main;


public class MainActivity extends AppCompatActivity {

    //Data for MSQLite activities
    DBAdapter adapter_ob;
    NotifyOpenHelper helper_ob;
    SQLiteDatabase db_ob;
    ListView nameList;
    Cursor cursor;
    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //               readFromLocaldStorage(); //TODO не понятно что делать если появился сигнал
            }
        };

// code for MSQLite
        nameList = (ListView) findViewById(R.id.list);
//        registerBtn = (Button) findViewById(R.id.btn_register);
        adapter_ob = new DBAdapter(this);

        String[] from = {helper_ob.DATE_REGISTRATION, helper_ob.TIME_REGISTRATION, helper_ob.TYPE};
        int[] to = {R.id.dateItem, R.id.timeItem, R.id.typeItem}; //data for each row
        cursor = adapter_ob.queryName();
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.row_item, cursor, from, to);
        nameList.setAdapter(cursorAdapter);
        nameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Bundle passdata = new Bundle();
                Cursor listCursor = (Cursor) arg0.getItemAtPosition(arg2);
                int nameId = listCursor.getInt(listCursor
                        .getColumnIndex(helper_ob.KEY_ID));
                // Toast.makeText(getApplicationContext(),
                // Integer.toString(nameId), 500).show();
                passdata.putInt("keyid", nameId);
                Intent passIntent = new Intent(MainActivity.this,
                        NotifyEditActivity.class);
                passIntent.putExtras(passdata);
                startActivity(passIntent);
            }

        });


        //Запуск плавающей кнопки для введения извещения

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // OLD data from example code
                //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //          .setAction("Action", null).show();
                Intent myIntent = new Intent(MainActivity.this, NotifyNewActivity.class);
                //  myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        cursor.requery();

    }

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
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            //  myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(myIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(NotifyOpenHelper.UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
