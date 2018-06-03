package com.assignment.spark.galleryimagesupload.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.fragment.TilesFragment;
import com.assignment.spark.galleryimagesupload.interfaces.INavigate;
import com.assignment.spark.galleryimagesupload.utils.Constants;

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
    public void openPreviewActivity(String uri) {
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
        Bundle b = new Bundle();
        b.putString(Constants.URI, uri);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof TilesFragment) {
            ((TilesFragment) fragment).handlePermissionResult(requestCode, grantResults);
        }
    }
}
