package com.rizeup.FindQueue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rizeup.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapDialog extends AppCompatDialogFragment implements OnMapReadyCallback {

    private double lat, lng;
    private String name, imageUrl;


    public MapDialog(double lat, double lng, String name, String imageUrl) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_map_dialog, null);
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);

        builder.setView(view).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(mapFragment);
                ft.commit();
            }
        });

        TextView nameTV = view.findViewById(R.id.map_queue_name);
        nameTV.setText(this.name);
        CircleImageView civ = view.findViewById(R.id.map_queue_images);
        Glide.with(this).asBitmap().load(imageUrl).into(civ);
        Button btn = view.findViewById(R.id.map_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(mapFragment);
                ft.commit();
                dismiss();
            }
        });
        mapFragment.getMapAsync(this);
        return builder.create();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng queuePos = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(queuePos).title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(queuePos));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(queuePos, 17.0f));
    }
}
