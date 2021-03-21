package com.example.licenseview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageApprovalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageApprovalFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private byte[] mParam1;
    private String mParam2;
    private Context safeContext;
    ImageView capImage;

    public static ImageApprovalFragment newInstance(byte[] param1, String param2) {
        ImageApprovalFragment fragment = new ImageApprovalFragment();
        Bundle args = new Bundle();
        args.putByteArray(ARG_PARAM1, param1);
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
            mParam1 = getArguments().getByteArray(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_approval, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);
        capImage = view.findViewById(R.id.imageCapture);
        Bitmap bmp = BitmapFactory.decodeByteArray(mParam1, 0, mParam1.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        capImage.setImageBitmap(resizedBitmap);
        //capImage.setRotation(90);

    }

}