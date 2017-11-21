package com.example.noamnelke.senticam;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SelectPhotoFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public SelectPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.snap_photo_button).setOnClickListener(this);
        view.findViewById(R.id.select_from_gallery_button).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            if (view.getId() == R.id.snap_photo_button) {
                mListener.onSnapPhotoPressed();
            }
            if (view.getId() == R.id.select_from_gallery_button) {
                mListener.onSelectFromGalleryPressed();
            }
        }
    }

    public void setResult(String resultText, Bitmap image) {
        View view = getView();
        if (view != null) {
            TextView results = view.findViewById(R.id.results);
            ImageView detectedFace = view.findViewById(R.id.detected_face);

            results.setText(resultText);
            detectedFace.setImageBitmap(image);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSnapPhotoPressed();
        void onSelectFromGalleryPressed();
    }
}
