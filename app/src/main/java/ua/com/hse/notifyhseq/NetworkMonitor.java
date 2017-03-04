package ua.com.hse.notifyhseq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NetworkMonitor extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (checkNetworkConnection(context)) {
            final NotifyOpenHelper notifyOpenHelperHelper = new NotifyOpenHelper(context);
            final SQLiteDatabase database = notifyOpenHelperHelper.getWritableDatabase();

            Cursor cursor = NotifyOpenHelper.readFromLocalDatabase(database);

            while (cursor.moveToNext()) {
                int sync_status = cursor.getInt(cursor.getColumnIndex(NotifyOpenHelper.SYNC));
                if (sync_status == NotifyOpenHelper.SYNC_STATUS_FAILED) {
                    final String Name = cursor.getString(cursor.getColumnIndex(NotifyOpenHelper.DATE_REGISTRATION));
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, NotifyOpenHelper.SERVER_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String Response = jsonObject.getString("response");
                                        if (Response.equals("OK")) {
                                            notifyOpenHelperHelper.updateLocalDatabase(Name, NotifyOpenHelper.SYNC_STATUS_OK, database);
                                            context.sendBroadcast(new Intent(NotifyOpenHelper.UI_UPDATE_BROADCAST));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", Name);
                            return params;
                        }
                    };
                    MySingleton.getInstance(context).addToRequestQue(stringRequest);
                }
            }
            notifyOpenHelperHelper.close();
        }

    }

    public boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


}