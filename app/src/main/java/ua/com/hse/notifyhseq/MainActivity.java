package ua.com.hse.notifyhseq;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.com.hse.notifyhseq.R.layout.activity_main;


public class MainActivity extends AppCompatActivity {

    public static String mUserName, mUserEmail;
    public static ArrayList<String> arrayDepartments, arrayPlaces;
    static boolean calledAlready = false;
    //Data for activities
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter mAdapter;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mNotifyDatabaseReference, mDepartmentDatabaseReference, mPlaceDatabaseReference;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String ANONYMOUS = "anonymous";
    int RC_SIGN_IN = 1;
//TODO widget for initial start (photo, type, other)

//TODO analytic part on main screen

//TODO preferences and data in it

//TODO ALL notify/myNotify/myResponsibleNotify tabs

//TODO work with status Notify (notification etc.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase components
        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }


        mUserName = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mNotifyDatabaseReference = mFirebaseDatabase.getReference().child("notifyHSEQ");
        mDepartmentDatabaseReference = mFirebaseDatabase.getReference().child("departments");
        mPlaceDatabaseReference = mFirebaseDatabase.getReference().child("places");

        recyclerView = (RecyclerView) findViewById(R.id.main_scroll_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mDepartmentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String stringDepartments = snapshot.getValue(String.class);
                arrayDepartments = new ArrayList<>(Arrays.asList(stringDepartments.split(",")));
                //                    Log.d("MySTRING", arrayDepartments.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            // onCancelled...
        });

        mPlaceDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String stringPlaces = snapshot.getValue(String.class);
                arrayPlaces = new ArrayList<>(Arrays.asList(stringPlaces.split(",")));
                //                    Log.d("MySTRING", arrayDepartments.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            // onCancelled...
        });

        //Запуск плавающей кнопки для введения извещения

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, NotifyNewActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signed in
//                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to NotifyHSEQ.", Toast.LENGTH_SHORT).show();
                    OnSignedInInitialize(user.getDisplayName(), user.getEmail());
                } else {
                    //user is signed out
                    onSignedOutCleanUp();
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                    );

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(providers)
                                    .build(),
                            RC_SIGN_IN
                    );
                }
            }
        };

    }

    private void onSignedOutCleanUp() {
        mUserName = ANONYMOUS;
//        mAdapter.cleanup();
    }

    private void OnSignedInInitialize(String userName, String userEmail) {
        mUserName = userName;
        mUserEmail = userEmail;
        attachDatabaseReadListener();
    }

    void attachDatabaseReadListener() {
        mAdapter = new FirebaseRecyclerAdapter<NotifyHSEQItem, NotifyHSEQAdapter>(NotifyHSEQItem.class, R.layout.row_item,
                NotifyHSEQAdapter.class, mNotifyDatabaseReference) {

            @Override
            protected void populateViewHolder(NotifyHSEQAdapter viewHolder, final NotifyHSEQItem notifyHSEQItem, final int position) {

                viewHolder.setmDateField(notifyHSEQItem.getTimeRegistration());
                viewHolder.setmTimeField(notifyHSEQItem.getTimeRegistration());
                //         viewHolder.setmTypeField(notifyHSEQItem.getType());
                viewHolder.setmTypeImage(notifyHSEQItem.getType());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String key = mAdapter.getRef(position).getKey();
                        Log.d("MyLOG     start    ", key);
                        Intent intent = new Intent(view.getContext(), NotifyShowActivity.class);
                        intent.putExtra("key", key);
                        view.getContext().startActivity(intent);

                    }
                });
            }

        };

        recyclerView.setAdapter(mAdapter);
    }

    void detachDatabaseReadListener() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);


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

        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                //  myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            return true;

            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
//        mAdapter.cleanup();

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }

}
