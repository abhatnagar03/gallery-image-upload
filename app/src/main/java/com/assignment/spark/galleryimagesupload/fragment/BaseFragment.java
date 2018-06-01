package com.assignment.spark.galleryimagesupload.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.adapter.BaseAdapter;
import com.assignment.spark.galleryimagesupload.interfaces.INavigate;
import com.assignment.spark.galleryimagesupload.presenter.ItemPresenter;
import com.assignment.spark.galleryimagesupload.presenter.RecyclerItemClickListener;
import com.assignment.spark.galleryimagesupload.utils.Constants;
import com.assignment.spark.galleryimagesupload.view.ItemView;
import com.assignment.spark.galleryimagesupload.widget.ItemOffsetDecoration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public abstract class BaseFragment extends Fragment implements ItemView,
        RecyclerItemClickListener {

    private static final int REQUEST_CODE_CHECK_PERMISSIONS = 1001;
    private static final int REQUEST_CODE_TAKE_PICTURE = 1002;

    static String[] permissions = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private ItemPresenter itemPresenter;
    private AlertDialog unlockDialog;
    private AlertDialog disconnectDialog;
    private AlertDialog noSpaceDialog;
    private AlertDialog errorDialog;
    private Uri uri;

    private RecyclerView.Adapter adapter;
    private GridLayoutManager layoutManager;
    private INavigate iNavigate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, rootView);

        itemPresenter = new ItemPresenter();
        itemPresenter.attachedView(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showGallery();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iNavigate = (INavigate) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ItemInteractionCallback and ISetTitle");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iNavigate = null;
    }

    private void setupRecyclerView() {
        if (getLayoutManager() != null) {
            layoutManager = (GridLayoutManager) getLayoutManager();
            recyclerView.setLayoutManager(layoutManager);
        }

        recyclerView.addItemDecoration(new ItemOffsetDecoration(recyclerView.getContext(), R.dimen.item_decoration));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void openDeviceCamera(File file) {
        uri = FileProvider.getUriForFile(getContext(), Constants.FILE_AUTHORITY, file);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
        }
    }

    @Override
    public boolean checkPermission() {
        for (String p : permissions) {
            int result = ContextCompat.checkSelfPermission(getActivity(), p);

            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }

        return true;
    }

    @Override
    public void showPermissionDialog() {
        ActivityCompat.requestPermissions(getActivity(),
                permissions,
                REQUEST_CODE_CHECK_PERMISSIONS);
    }

    @Override
    public File getEnvFilePath() {
        File file = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CHECK_PERMISSIONS) {
            if (!allPermissionsGranted(grantResults)) {
                itemPresenter.permissionDenied();
            } else {
                itemPresenter.onCaptureBtnClick();
            }
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }

        return true;
    }

    @Override
    public boolean isStorageMounted() {
        String externalStorageState = Environment.getExternalStorageState();

        return externalStorageState.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public boolean shouldShowDialog() {
        for (String p : permissions) {
            boolean b = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), p);

            if (!b) return false;
        }

        return true;
    }

    @Override
    public void showUnlockPermissionsDialog() {
        if (unlockDialog == null) {
            unlockDialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.without_permissions))
                    .setMessage(getString(R.string.it_looks_you_locked_permissions_))
                    .create();
        }

        unlockDialog.show();
    }

    @Override
    public String[] getPermissions() {
        return permissions;
    }

    @Override
    public void showDisconnectFromPCDialog() {
        if (disconnectDialog == null) {
            disconnectDialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.media_unavailable))
                    .setMessage(getString(R.string.external_storage_unavailable_pls_))
                    .create();
        }

        disconnectDialog.show();
    }

    @Override
    public void showNoSpaceDialog() {
        if (noSpaceDialog == null) {
            noSpaceDialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.no_few_space_on_disk))
                    .setMessage(getString(R.string.your_have_no_space_available_pls_))
                    .create();
        }

        noSpaceDialog.show();
    }

    @Override
    public int availableDisk() {
        File envFilePath = getEnvFilePath();
        long freeSpace = envFilePath.getFreeSpace();
        int mb = Math.round(freeSpace / 1048576);
        return mb;
    }

    @Override
    public File newFile() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void showErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.file_system_error))
                    .setMessage(getString(R.string.something_went_wrong_during_file_))
                    .create();
        }

        errorDialog.show();
    }

    @Override
    public void navigateToPreviewActivity() {
        iNavigate.navigate(uri);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showDetails(int position) {

    }

    @Override
    public void resetRefreshingLayout() {

    }

    @OnClick(R.id.capture)
    public void onCaptureBtnCLick() {
        itemPresenter.onCaptureBtnClick();
    }

    public void showGallery() {
        recyclerView.setVisibility(View.VISIBLE);

        setupRecyclerView();

        File file = getEnvFilePath();
        adapter = getAdapter(Arrays.asList(file.listFiles()));
        recyclerView.setAdapter(adapter);
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).setRecyclerItemClickListener(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                itemPresenter.onPhotoClicked();
            } else {
                new File(uri.getPath()).delete();
            }
        }
    }

    protected abstract int getLayout();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected abstract RecyclerView.Adapter getAdapter(List<File> items);

    @Override
    public void onItemClickListener(int position) {

    }
}
