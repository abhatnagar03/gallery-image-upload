package com.assignment.spark.galleryimagesupload.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.fragment.TilesFragment;
import com.assignment.spark.galleryimagesupload.interfaces.INavigate;
import com.assignment.spark.galleryimagesupload.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements INavigate {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, TilesFragment.newInstance()).commit();
    }

    @Override
    public void navigate(Uri uri) {
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(Constants.URI, uri);
        intent.putExtras(b);
        startActivity(intent);
    }
}
