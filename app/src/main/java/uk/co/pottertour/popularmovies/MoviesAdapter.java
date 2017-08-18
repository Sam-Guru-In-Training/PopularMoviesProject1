package uk.co.pottertour.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.pottertour.popularmovies.utilities.MovieObject;

/**
 * Created by Sam on 13/08/2017.
 * Converts data into views for MainActivity to draw
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private MovieObject[] mMoviesData;
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final MoviesAdapterOnClickHandler mClickHandler;

    private Context mContext;

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(MovieObject movieDetails);
    }

    /**
     * Creates a MoviesAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {

        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMoviesImageView;
        //public final TextView mMoviesTextView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mMoviesImageView = (ImageView) view.findViewById(R.id.iv_poster_thumbnail);
            view.setOnClickListener(this);

        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieObject movieData = mMoviesData[adapterPosition];

            Log.e("MoviesAdapter", "A view has been clicked");
            mClickHandler.onClick(movieData);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        // TODO IS THIS BEST PRACTICE?  IS THERE A BETTER WAY TO GET CONTEXT FOR onBindViewHolder??
        mContext = context;

        return new MoviesAdapterViewHolder(view);

    }
    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param moviesAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        MovieObject movieDetails = mMoviesData[position];

        Picasso.with(mContext) // TODO IS GETTING CONTEXT THIS WAY BEST PRACTICE?
                .load(movieDetails.getPoster()).fit()
                .into(moviesAdapterViewHolder.mMoviesImageView);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) return 0;
        return mMoviesData.length;
    }

    /*
    * Returns an ArrayList of all MovieObjects for putting into a parceable onSaveInstanceState
     */
    public ArrayList<MovieObject> getItems() {
        ArrayList<MovieObject> mMoviesDataArrayList = new ArrayList<>(Arrays.asList(mMoviesData));
        return mMoviesDataArrayList;
    }

    /**
     * This method is used to set the movie details on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param moviesData The new movies data to be displayed.
     */
    public void setMoviesData(MovieObject[] moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
