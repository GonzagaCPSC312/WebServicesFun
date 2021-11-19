package com.sprint.gina.webservicesfun;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.sprint.gina.webservicesfun.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    List<InterestingPhoto> interestingPhotoList;
    int currPhotoIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Fetching next photo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                nextPhoto();
            }
        });

        // Game plan for MainActivity
        // 1. in onCreate fetch a list of InterestingPhotos from the Flickr API
        FlickrAPI flickrAPI = new FlickrAPI(this);
        flickrAPI.fetchInterestingPhotos();
        // 2. on FAB click, flip through the photo list and download the current
        // InterestingPhoto's bitmap
    }

    public void receivedInterestingPhotos(List<InterestingPhoto> interestingPhotoList) {
        this.interestingPhotoList = interestingPhotoList;
        nextPhoto();
    }

    public void nextPhoto() {
        if (interestingPhotoList != null && interestingPhotoList.size() > 0) {
            currPhotoIndex++;
            currPhotoIndex %= interestingPhotoList.size();

            TextView titleTextView = findViewById(R.id.titleTextView);
            TextView dateTakenTextView = findViewById(R.id.dateTakenTextView);

            InterestingPhoto interestingPhoto = interestingPhotoList.get(currPhotoIndex);
            titleTextView.setText(interestingPhoto.getTitle());
            dateTakenTextView.setText(interestingPhoto.getDateTaken());

            // come back and add support to fetch the source image
            // using interestingPhoto.getPhotoURL()
            // GS: added
            FlickrAPI flickrAPI = new FlickrAPI(this);
            flickrAPI.fetchPhotoBitmap(interestingPhoto.getPhotoURL());
        }
    }

    // GS: added
    public void receivedPhotoBitmap(Bitmap bitmap) {
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
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