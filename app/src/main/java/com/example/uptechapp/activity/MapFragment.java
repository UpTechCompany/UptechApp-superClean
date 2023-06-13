package com.example.uptechapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.uptechapp.R;
import com.example.uptechapp.api.CompleteListener;
import com.example.uptechapp.dao.Database;
import com.example.uptechapp.dao.MapService;
import com.example.uptechapp.dao.MyViewModel;
import com.example.uptechapp.databinding.FragmentMapBinding;
import com.example.uptechapp.model.Emergency;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;

    private List<Emergency> myEmergencyList;
    private MapService mapService;
    GoogleMap mMap;

    private static LatLng latLngs = new LatLng(11, 22);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentMapBinding.inflate(getLayoutInflater());

        myEmergencyList = new ArrayList<Emergency>();
        latLngs = new LatLng(11,11);
        Database.loadEmergencies(new CompleteListener() {
            @Override
            public void OnSuccess() {
            }

            @Override
            public void OnFailure() {
            }
        });

        final Observer<List<Emergency>> myObserver = new Observer<List<Emergency>>() {
            @Override
            public void onChanged(List<Emergency> emergencies) {
                Log.d("NIKITA", "INOF");
                Log.d("NIKITA", String.valueOf(emergencies.size()));
                myEmergencyList.clear();
                myEmergencyList.addAll(emergencies);
            }
        };
        final Observer<LatLng> myObserver1 = new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                Log.d("LOC", latLngs.toString());
                latLngs = latLng;
            }
        };
        MyViewModel.getInstance().getEmergencyLiveData().observe(this, myObserver);
        MyViewModel.getInstance().getLatLng().observe(this, myObserver1);

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        mapService.setImage(uri);
                    }
                });

        mapService = new MapService(getContext(), getActivity(), mGetContent);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(mapService);

        if (latLngs != null){
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    zoom(latLngs, 18);
                }
            });
    }


    }

    public void zoom(LatLng latLng, float zoomLevel) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

//    public void setLatLng(LatLng latLng) {
//        this.latLng = latLng;
//    }
//
//    public void setZoomLevel(float zoomLevel) {
//        zoomLevel = zoomLevel;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
