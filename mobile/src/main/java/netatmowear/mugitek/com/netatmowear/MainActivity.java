package netatmowear.mugitek.com.netatmowear;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netatmowear.mugitek.com.netatmoapi.NetatmoResponseHandler;
import netatmowear.mugitek.com.netatmoapi.model.Measures;
import netatmowear.mugitek.com.netatmoapi.model.Module;
import netatmowear.mugitek.com.netatmoapi.model.Params;
import netatmowear.mugitek.com.netatmoapi.model.Station;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {
    final int REQUEST_CODE = 0;

    CustomAdapter mAdapter;
    List<Module> mListItems = new ArrayList<Module>();

    List<Station> mDevices;
    int mCompletedRequest;

    SampleHttpClient mHttpClient;
    ListView mListView;


    public static String TAG = "MainActivity: ";
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        final String M = "onCreate: ";
        Log.i(TAG, M);

        mListView = (ListView) findViewById(android.R.id.list);

        // HttpClient used for all requests in this activity.
        mHttpClient = new SampleHttpClient(this);

        if (mHttpClient.getAccessToken() != null) {
            // If the user is already logged in.
            initActionBar();
        } else {
            // Else, starts LoginActivity.
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                initActionBar();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    /**
     * Initializing the action bar with the stations' names using the parsed response returned by
     * NetatmoHttpClient.getDevicesList(NetatmoResponseHandler).
     */
    private void initActionBar() {
        mAdapter = new CustomAdapter(this, mListItems);
        mListView.setAdapter(mAdapter);

        final MainActivity activity = this;

        // NetatmoResponseHandler returns a parsed response (by overriding onGetDevicesListResponse).
        // You can also use JsonHttpResponseHandler and process the response as you wish.
        mHttpClient.getDevicesList(new NetatmoResponseHandler(mHttpClient,
                NetatmoResponseHandler.REQUEST_GET_DEVICES_LIST, null) {
            @Override
            public void onStart() {
                super.onStart();
                setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
            }

            @Override
            public void onGetDevicesListResponse(final List<Station> devices) {
                mDevices = devices;

                handler.post(new Runnable() {

                    @Override	public void run() {


                        List<String> stationsNames = new ArrayList<String>();
                        for (Station station : devices) {
                            stationsNames.add(station.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                                android.R.layout.simple_spinner_dropdown_item, stationsNames);

                        ActionBar actionBar = getSupportActionBar();
                        actionBar.setDisplayShowTitleEnabled(false);
                        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                        actionBar.setListNavigationCallbacks(adapter, activity);

                    }});
            }

            @Override
            public void onFinish() {

                super.onFinish();

                handler.post(new Runnable() {

                    @Override	public void run() {

                        setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
                    }});
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * "Disconnects" the user by clearing stored tokens. Then, starts the LoginActivity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                mHttpClient.clearTokens();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Calls getLastMeasures() for all modules associated with the selected station.
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);

        Station station = mDevices.get(itemPosition);
        final List<Module> modules = station.getModules();
        mCompletedRequest = modules.size();

        if (!mListItems.isEmpty()) {
            mListItems.clear();
            mAdapter.notifyDataSetChanged();
        }

        String[] types = new String[]{
                Params.TYPE_TEMPERATURE,
                Params.TYPE_MIN_TEMP,
                Params.TYPE_MAX_TEMP,
                Params.TYPE_DATE_MIN_TEMP,
                Params.TYPE_DATE_MAX_TEMP
        };

        Log.d(TAG, "calling HTTP");
        /* NetatmoResponseHandler returns a parsed response (by overriding onGetMeasuresResponse).
         * You can also use JsonHttpResponseHandler and process the response as you wish.
         *
         * The API changed a bit, and now the deviceList contains all the basic data you may need, no need to call
         * getMeasures (except if you need only module-specific data, like for a widget for example)
         * We are reloading it at every item selected only to show the update process, it's not really optimized.
         */
        mHttpClient.getDevicesList(
                new NetatmoResponseHandler(mHttpClient, NetatmoResponseHandler.REQUEST_GET_LAST_MEASURES, types) {
                    @Override
                    public void onGetMeasuresResponse( final HashMap<String, Measures> measures) {
                        for (final Module module : modules) {
                            if (measures.containsKey(module.getId())) {
                                module.setMeasures(measures.get(module.getId()));
                                mListItems.add(module);
                            }
                        }

                        handler.post(new Runnable() {
                            @Override   public void run() {
                                mAdapter.notifyDataSetChanged();
                                setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);

                            }});
                    }
                });
        return true;
    }
}
