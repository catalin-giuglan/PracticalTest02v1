package com.example.practicaltest02v1;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Important: Asigura-te ca aceasta clasa este in com.example.practicaltest02v1
// Daca ai creat-o in alt pachet, mut-o sau schimba linia de 'package' de sus.

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); // Asigura-te ca ai acest layout generat

        // Obtinem fragmentul de harta din XML si cerem notificarea cand harta e gata
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Aceasta metoda este apelata cand harta este gata de utilizare.
     * Aici putem adauga markere sau muta camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 1. Definim coordonatele pentru Ghelmegioaia, Romania (Cerinta 4)
        // Latitudine: 44.6258, Longitudine: 22.8753
        LatLng ghelmegioaia = new LatLng(44.6258, 22.8753);

        // 2. Adaugam un marker pe harta
        mMap.addMarker(new MarkerOptions()
                .position(ghelmegioaia)
                .title("Marker in Ghelmegioaia"));

        // 3. Mutam camera si dam zoom (15f este un nivel bun de zoom pt sate/strazi)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ghelmegioaia, 15f));
    }
}