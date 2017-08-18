package uk.co.pottertour.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.co.pottertour.popularmovies.utilities.MovieObject;

public class DetailActivity extends AppCompatActivity {

    TextView mTitleTV;
    String mMovieTitle;

    ImageView mPosterImageView;
    ImageView mPosterThumbnailImageView;

    TextView mOverviewTV;
    String mOverview;

    TextView mVoteTV;
    float mVoteAverage;

    TextView mDateTV;
    String mReleaseDate;

    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /* a details screen:
        original title
        movie poster image thumbnail
        A plot synopsis (called overview in the api)
        user rating (called vote_average in the api)
        release date
        */

        mTitleTV = findViewById(R.id.tv_title);
        mPosterThumbnailImageView = findViewById(R.id.iv_poster_thumbnail);
        mOverviewTV = findViewById(R.id.tv_overview);
        mVoteTV = findViewById(R.id.tv_vote_average);
        mDateTV = findViewById(R.id.tv_release_date);

        Intent mIntent = getIntent();

        if (mIntent != null) {
            MovieObject movieDetails = getIntent().getParcelableExtra("MovieObject");
            Log.e(TAG, "inside, decoding parcelable");
            mMovieTitle = movieDetails.getTitle();
            mTitleTV.setText(mMovieTitle);

            String mPoster= movieDetails.getPoster();

            // TODO I'm getting the same pic from the server twice, is this best practice?
            // does picasso magically cache it to prevent re-requests?  How should I do it different?
            Picasso.with(this)
                    .load(movieDetails.getPoster()).fit()
                    .into(mPosterThumbnailImageView);

             mOverviewTV.setText(movieDetails.getOverview());
             mVoteTV.setText("average rating" + "\n" + String.valueOf(movieDetails.getRating()));
             mDateTV.setText(movieDetails.getReleaseDate());

        }
    }
}
