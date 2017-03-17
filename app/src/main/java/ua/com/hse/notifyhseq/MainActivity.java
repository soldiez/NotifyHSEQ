package ua.com.hse.notifyhseq;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static ua.com.hse.notifyhseq.R.layout.activity_main;


public class MainActivity extends AppCompatActivity {

    //Data for activities
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter mAdapter;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mNotifyDatabaseReference;
    ChildEventListener mChildEventListener;
    private List<NotifyHSEQItem> notifyHSEQList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNotifyDatabaseReference = mFirebaseDatabase.getReference().child("notifyHSEQ");

        recyclerView = (RecyclerView) findViewById(R.id.main_scroll_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new FirebaseRecyclerAdapter<NotifyHSEQItem, NotifyHSEQAdapter>(NotifyHSEQItem.class, R.layout.row_item,
                NotifyHSEQAdapter.class, mNotifyDatabaseReference) {

            @Override
            protected void populateViewHolder(NotifyHSEQAdapter viewHolder, NotifyHSEQItem model, int position) {
                viewHolder.setmDateField(model.getDateHappened());
                viewHolder.setmTimeField(model.getTimeRegistration());
                viewHolder.setmTypeField(model.getType());
            }
        };
        recyclerView.setAdapter(mAdapter);



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

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


}
