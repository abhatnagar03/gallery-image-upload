package com.assignment.spark.galleryimagesupload.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.adapter.BaseAdapter;
import com.assignment.spark.galleryimagesupload.interfaces.INavigate;
import com.assignment.spark.galleryimagesupload.layoutmanager.GridAutoFitLayoutManager;
import com.assignment.spark.galleryimagesupload.presenter.MainPresenterImpl;
import com.assignment.spark.galleryimagesupload.presenter.RecyclerItemClickListener;
import com.assignment.spark.galleryimagesupload.utils.CameraUtils;
import com.assignment.spark.galleryimagesupload.utils.Constants;
import com.assignment.spark.galleryimagesupload.view.MainGalleryView;

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

public abstract class BaseFragment extends Fragment implements MainGalleryView,
        RecyclerItemClickListener {

    private static final int REQUEST_CODE_CHECK_PERMISSIONS = 1001;
    private static final int REQUEST_CODE_GALLERY_PERMISSIONS = 1003;
    private static final int REQUEST_CODE_TAKE_PICTURE = 1002;
    private static final int PICK_IMAGE_REQUEST = 1004;

    static String[] permissions = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    static String[] galleryPermission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private MainPresenterImpl mainPresenter;
    private AlertDialog unlockDialog;
    private AlertDialog disconnectDialog;
    private AlertDialog noSpaceDialog;
    private AlertDialog errorDialog;
    private Uri uri;

    private RecyclerView.Adapter adapter;
    protected GridAutoFitLayoutManager layoutManager;
    private INavigate iNavigate;
    private String mCurrentPhotoPath;
    private boolean firstResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, rootView);

        mainPresenter = new MainPresenterImpl();
        mainPresenter.attachedView(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainPresenter.loadInitialData();
        firstResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(firstResume) {
            firstResume = false;
        } else {
            refreshGallery();
        }
    }

    public void refreshGallery() {
        File file = getEnvFilePath();
        ((BaseAdapter) adapter).addAll(Arrays.asList(file.listFiles()));
        recyclerView.setAdapter(adapter);
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).setRecyclerItemClickListener(this);
        }
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

    @OnClick(R.id.capture)
    public void onCaptureBtnCLick() {
        mainPresenter.onCaptureBtnClick();
    }

    @OnClick(R.id.gallery)
    public void onGallryBtnClick() {
        mainPresenter.onGalleryBtnClick();
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

    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_CODE_CHECK_PERMISSIONS) {
            if (anyPermissionRejected(grantResults)) {
                mainPresenter.permissionDenied();
            } else {
                mainPresenter.onCaptureBtnClick();
            }
        } else if (requestCode == REQUEST_CODE_GALLERY_PERMISSIONS) {
            if (anyPermissionRejected(grantResults)) {
                mainPresenter.galleryPermissionDenied();
            } else {
                mainPresenter.onGalleryBtnClick();
            }
        }
    }

    private boolean anyPermissionRejected(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED)
                return true;
        }

        return false;
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
    public void showUnlockGalleryPermissionsDialog() {
        if (unlockDialog == null) {
            unlockDialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.without_permissions))
                    .setMessage(getString(R.string.it_looks_you_locked_permissions_2_))
                    .create();
        }

        unlockDialog.show();
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
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
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
        iNavigate.openPreviewActivity(mCurrentPhotoPath);
    }

    @Override
    public boolean checkGalleryPermission() {
        for (String p : galleryPermission) {
            int result = ContextCompat.checkSelfPermission(getActivity(), p);

            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }

        return true;

    }

    @Override
    public void showGalleryPermissionDialog() {
        ActivityCompat.requestPermissions(getActivity(),
                galleryPermission,
                REQUEST_CODE_GALLERY_PERMISSIONS);
    }

    @Override
    public void openDeviceGallery(File file) {
        uri = FileProvider.getUriForFile(getContext(), Constants.FILE_AUTHORITY, file);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void showGallery() {
        recyclerView.setVisibility(View.VISIBLE);

        setupRecyclerViewProperties();

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
                mainPresenter.onPhotoClicked();
            } else {
                getContext().getContentResolver().delete(uri, null, null);
            }
        } else if(requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap mGalleryBitmap = null;
                try {
                    mGalleryBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != mGalleryBitmap) {
                    CameraUtils.saveEnhancedBitmap(mGalleryBitmap, mCurrentPhotoPath);
                    mGalleryBitmap.recycle();
                    mainPresenter.onPhotoClicked();
                }
            } else {
                getContext().getContentResolver().delete(uri, null, null);
            }
        }
    }

    @Override
    public void onItemClickListener(int position) {
        mainPresenter.onItemSelected(position);
    }

    @Override
    public void showDetails(int position) {
        iNavigate.openDisplayActivity(getEnvFilePath().listFiles()[position].getAbsolutePath());
    }

    protected abstract int getLayout();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected abstract RecyclerView.Adapter getAdapter(List<File> items);

    protected abstract void setupRecyclerViewProperties();
}
