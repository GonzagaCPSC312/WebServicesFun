package com.sprint.gina.webservicesfun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class FlickrAPI {
    // wrapper class for all of our Flickr
    // API related members

    static final String BASE_URL = "https://api.flickr.com/services/rest";
    // TODO: grab my API key from my Ed Discussion post to put in string below
    static final String API_KEY = "YOUR API KEY HERE"; // BAD PRACTICE!!!
    static final String TAG = "WebServicesFunTag";

    MainActivity mainActivity;

    public FlickrAPI(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void fetchInterestingPhotos() {
        // need a URL for the request
        String url = constructInterestingPhotoListURL();
//        Log.d(TAG, "fetchInterestingPhotos: " + url);

        // start the background task to fetch the photos
        // we have to use a background task!
        // Android will not let you do any network activity
        // on the main UI thread
        // define a subclass a AsyncTask
        // asynchronous means doesn't wait/block
        FetchInterestingPhotoListAsyncTask asyncTask = new FetchInterestingPhotoListAsyncTask();
        asyncTask.execute(url);
    }

    public String constructInterestingPhotoListURL() {
        String url = BASE_URL;
        url += "?method=flickr.interestingness.getList";
        url += "&api_key=" + API_KEY;
        url += "&format=json";
        url += "&nojsoncallback=1";
        url += "&extras=date_taken,url_h";

        return url;
    }

    class FetchInterestingPhotoListAsyncTask extends AsyncTask<String, Void, List<InterestingPhoto>> {
        // executes executes on the background thread
        // CANNOT update the UI thread
        // unless we are in the appropriate AsyncTask method
        // this is where we do 3 things
        // 1. open the url request
        // 2. download the JSON response
        // 3. parse the JSON response in to InterestingPhoto objects


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // executes on the main UI thread
            ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<InterestingPhoto> doInBackground(String... strings) {
            // String... is called var args, treat like an area
            String url = strings[0];
            List<InterestingPhoto> interestingPhotoList = new ArrayList<>();

            try {
                URL urlObject = new URL(url);
                HttpsURLConnection urlConnection = (HttpsURLConnection) urlObject.openConnection();

                // successfully opened url over HTTPS protocol
                // download the JSON response
                String jsonResult = "";
                // character by character we are going to build the json string from an input stream
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    jsonResult += (char) data;
                    data = reader.read();
                }
                Log.d(TAG, "doInBackground: " + jsonResult);

                // parse the JSON
                JSONObject jsonObject = new JSONObject(jsonResult);
                // grab the "root" photos jsonObject
                JSONObject photosObject = jsonObject.getJSONObject("photos"); // photos is the key
                JSONArray photoArray = photosObject.getJSONArray("photo"); // photo is the key
                for (int i = 0; i < photoArray.length(); i++) {
                    JSONObject singlePhotoObject = photoArray.getJSONObject(i);
                    // try to parse a single photo's info
                    InterestingPhoto interestingPhoto = parseInterestingPhoto(singlePhotoObject);
                    if (interestingPhoto != null) {
                        interestingPhotoList.add(interestingPhoto);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return interestingPhotoList;
        }

        private InterestingPhoto parseInterestingPhoto(JSONObject singlePhotoObject) {
            InterestingPhoto interestingPhoto = null;
            try {
                String id = singlePhotoObject.getString("id");
                String title = singlePhotoObject.getString("title");
                String dateTaken = singlePhotoObject.getString("datetaken");
                String photoURL = singlePhotoObject.getString("url_h");
                interestingPhoto = new InterestingPhoto(id, title, dateTaken, photoURL);
            }
            catch (JSONException e) {
                // print out the stack trace and/or try to request a different url...
                // do nothing
            }
            return interestingPhoto;
        }

        @Override
        protected void onPostExecute(List<InterestingPhoto> interestingPhotos) {
            super.onPostExecute(interestingPhotos);
            // executes on the main UI thread

            Log.d(TAG, "onPostExecute: " + interestingPhotos.size());
            mainActivity.receivedInterestingPhotos(interestingPhotos);
            ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    // GS: added
    public void fetchPhotoBitmap(String photoURL) {
        PhotoRequestAsyncTask asyncTask = new PhotoRequestAsyncTask();
        asyncTask.execute(photoURL);
    }

    // GS: added
    class PhotoRequestAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressBar progressBar = (ProgressBar) mainActivity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;

            try {
                URL url = new URL(strings[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection)
                        url.openConnection();

                InputStream in = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            ProgressBar progressBar = (ProgressBar) mainActivity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            mainActivity.receivedPhotoBitmap(bitmap);
        }
    }
}
