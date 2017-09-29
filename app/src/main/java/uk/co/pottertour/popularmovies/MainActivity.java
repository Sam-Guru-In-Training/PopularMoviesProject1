package uk.co.pottertour.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import uk.co.pottertour.popularmovies.MoviesAdapter.MoviesAdapterOnClickHandler;
import uk.co.pottertour.popularmovies.utilities.MovieObject;
import uk.co.pottertour.popularmovies.utilities.MoviesDBJsonUtils;
import uk.co.pottertour.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();


    private String posterPath;

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private MovieObject[] mMoviesData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_posters);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);



        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        Log.e(TAG, "creating adapter");

        if (savedInstanceState != null && savedInstanceState.containsKey("moviesData")) {
            // if we have saved moviesData restore it
            ArrayList<MovieObject> tempMoviesData = savedInstanceState.getParcelableArrayList("moviesData");
            mMoviesData = (MovieObject[]) tempMoviesData.toArray();
            showMoviesDataView();
            Log.e("after rotation", "setting data on Adapter");
            // TODO IS THIS RIGHT?  DOES THE ADAPTER STILL EXIST AT THIS POINT?
            mMoviesAdapter.setMoviesData(mMoviesData);
        }

        else {
            // if we haven't check online and fetch from server
            if (isOnline()) {
                mMoviesAdapter = new MoviesAdapter(this);
                mRecyclerView.setAdapter(mMoviesAdapter);

                mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
                loadMoviesData("popular");
            } else {
                showErrorMessage();
            }
        }
    }

    /*
    * Checks if we have a network connection, returns false if don't
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadMoviesData(String sort_by) {
        showMoviesDataView();

        // sort method selectable via options menu
        Log.e(TAG, "creating Fetch Movies Task");
        new FetchMoviesTask().execute(sort_by);
    }

    /*
    * saves moviesData if we've got it back from network
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMoviesData != null) {
            outState.putParcelableArrayList("mMoviesAdapter", mMoviesAdapter.getItems());
        }
    }

    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     * TODO make it launch, use putParceable to send objects through
     * @param movieParticulars The weather for the day that was clicked
     */
    @Override
    public void onClick(MovieObject movieParticulars) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Log.e(TAG, "A view has been clicked");
        //Toast.makeText(context, "item clicked!", Toast.LENGTH_SHORT).show();

        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("MovieObject", movieParticulars);
        //intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieParticulars);
        startActivity(intentToStartDetailActivity);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieObject[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieObject[] doInBackground(String... params) {

            final String DIB = "doInBackground";
            // if no sorting method exit
            if (params.length == 0) {
                Log.e(DIB, "no params exiting!");
                return null;
            }
            Log.e(DIB, "Trying to get params[0]");
            String sort_movies_by = params[0];
            URL moviesRequestUrl = NetworkUtils.buildUrl(sort_movies_by);
            Log.e(DIB, "Trying to get JSON in doInBackground");
            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                MovieObject[] simpleJsonMovieData = MoviesDBJsonUtils
                        .getMovieObjectsFromJson(MainActivity.this, jsonMovieResponse);
                Log.e(TAG, "Got! JSON in doInBackground");
                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieObject[] moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                mMoviesData = moviesData;
                showMoviesDataView();
                Log.e("onPostExecute", "setting data on Adapter");
                mMoviesAdapter.setMoviesData(moviesData);
            } else {
                showErrorMessage();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.sortby, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (isOnline()) {
            if (id == R.id.popular) {
                mMoviesAdapter.setMoviesData(null);
                loadMoviesData("popular");
                return true;
            }
            if (id == R.id.top_rated) {
                mMoviesAdapter.setMoviesData(null);
                loadMoviesData("top_rated");
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
