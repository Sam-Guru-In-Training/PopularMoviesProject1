/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.pottertour.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class MoviesDBJsonUtils {

    final static private String TAG = MoviesDBJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param moviesJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static MovieObject[] getMovieObjectsFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String MDB_LIST = "results";

        /* All temperatures are children of the "temp" object */
        final String OWM_TEMPERATURE = "temp";

        /* UNIQUE MOVIE id */
        final String MDB_ID = "id";
        final String MDB_USER_RATING = "vote_average";
        final String MDB_TITLE = "title";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";

        final String OWM_MESSAGE_CODE = "cod";

        /* MovieObject array to hold each Movie's details */
        MovieObject[] parsedMoviesData = null;

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        /* Is there an error? */
        if (moviesJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = moviesJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = moviesJson.getJSONArray(MDB_LIST);

        parsedMoviesData = new MovieObject[moviesArray.length()];


        for (int i = 0; i < moviesArray.length(); i++) {
            /* These are the values that will be collected */
            int movieID;
            double rating;
            String title;
            String posterPath;
            String overview;
            String releaseDate;

            /* Get the JSON object representing a particular movie */
            JSONObject movieDetails = moviesArray.getJSONObject(i);

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */

            movieID = movieDetails.getInt(MDB_ID); // TODO Check it will be OK as an int
            rating = movieDetails.getDouble(MDB_USER_RATING);
            title = movieDetails.getString(MDB_TITLE);
            posterPath = "http://image.tmdb.org/t/p/w185//" + movieDetails.getString(MDB_POSTER);
            overview = movieDetails.getString(MDB_OVERVIEW);
            releaseDate = movieDetails.getString(MDB_RELEASE_DATE).substring(0, 4);

            //Log.e(TAG, "got date as: " + releaseDate);

            parsedMoviesData[i] = new MovieObject(movieID, rating, title, posterPath, overview, releaseDate);
        }
        Log.e(TAG, "total parsed movies: " + parsedMoviesData.length);

        return parsedMoviesData;

    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}