package com.example.licenseview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CaptureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CaptureFragment extends Fragment {
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private Context safeContext;
    private Executor executor = Executors.newSingleThreadExecutor();
    ImageView captureImage;
    PreviewView mPreviewView;


    public static CaptureFragment newInstance(String param1, String param2) {
        CaptureFragment fragment = new CaptureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        safeContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_capture, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState ){
        super.onViewCreated(view,savedInstanceState);
        mPreviewView = view.findViewById(R.id.viewFinder);
        captureImage = view.findViewById(R.id.cameraCaptureImageButton);
        if(checkPermission()){
            startCamera();
        } else{
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);

            } catch (InterruptedException | ExecutionException e) {
            }
        }, ContextCompat.getMainExecutor(safeContext));
    }


    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        ImageCapture.Builder builder = new ImageCapture.Builder();

        ImageCapture.Builder builder1 = builder.setTargetRotation(Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getRotation());
        final ImageCapture imageCapture = builder.build();
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);

        captureImage.setOnClickListener(v -> imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(ImageProxy image) {
                Log.i("Capture Fragment", "Went Inside onCaptureSuccess");
                Bitmap imageConvert = imageProxyToBitmap(image);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageConvert.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ImageApprovalFragment imageApprovalFragment = ImageApprovalFragment.newInstance(byteArray, "");

                int findThisFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, imageApprovalFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
            }
        }));
    }

    private boolean checkPermission() {
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(safeContext, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if(checkPermission()){ startCamera(); }
    }

    private Bitmap imageProxyToBitmap(ImageProxy image)
    {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

//    public String getBatchDirectoryName() {
//
//        String app_folder_path = "";
//        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
//        File dir = new File(app_folder_path);
//        if (!dir.exists() && !dir.mkdirs()) {
//
//        }
//        return app_folder_path;
//    }


//SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
//File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");

//    private Preview preview;
//    private ImageCapture imageCapture;
//    private ImageAnalysis imageAnalysis;
//    private Camera camera;
//    private int REQUEST_CODE_PERMISSIONS = 1001;
//    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
//    private File outputFile;
//    private ExecutorService cameraExecutor;

//    ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();