package app.retrofit_chucknorries;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.concurrent.TimeUnit;

import app.retrofit_chucknorries.Constants.Constants;
import app.retrofit_chucknorries.adapter.JokesAdapter;
import app.retrofit_chucknorries.model.JokesModel;
import app.retrofit_chucknorries.model.Value;
import app.retrofit_chucknorries.service.IJokes;
import retrofit.RestAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private RecyclerView mRecyclerView;

    private JokesAdapter mAdapter;
    private List<Value> mJokestList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_main);
      //  final OkHttpClient okHttpClient = new OkHttpClient();
       // okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
       // okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        pDialog = new ProgressDialog(this);

        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        mRecyclerView = (RecyclerView)findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        RestAdapter adapterJokes= new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
               // .setClient(new OkClient(okHttpClient))
                .build();

        IJokes apiJokes= adapterJokes.create(IJokes.class);
        apiJokes.getJokes()
                .cache()
                .timeout(5000, TimeUnit.MILLISECONDS)
                .retry(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<JokesModel>() {
                               @Override
                               public void call(JokesModel jokesModel) {
                                   mJokestList = jokesModel.getValue();
                                   displayGistList(mJokestList);

                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   Log.e(getClass().getName(), "ERROR: " + throwable.getMessage());
                                   throwable.printStackTrace();

                               }
                           }
                );
    /*    apiJokes.getJokes(new Callback<JokesModel>() {
            @Override
            public void success(JokesModel jokesModel, Response response) {
                mJokestList=jokesModel.getValue();
                mAdapter = new JokesAdapter(mJokestList, R.layout.card_row, getApplicationContext());
                mRecyclerView.setAdapter(mAdapter);

                hidePDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                hidePDialog();

            }
        });
*/
    }

    public void displayGistList(final List<Value> gists) {
        if (gists.size()>0 && mRecyclerView!=null) {
            mAdapter = new JokesAdapter(mJokestList, R.layout.card_row, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);
            hidePDialog();
        }
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
