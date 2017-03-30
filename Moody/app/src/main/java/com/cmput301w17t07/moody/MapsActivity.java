/*
 * Copyright 2017 CMPUT301W17T07
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmput301w17t07.moody;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends BarMenuActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String username;
    private String filterFeeling;
    private Intent intent;
    private ArrayList<Mood> moodArrayList = new ArrayList<Mood>();

    private String selectedUser; // equals My Moods/Timeline Moods
    private Integer user; // user = 0 if My Moods else user = 1 if Timeline Moods
    ArrayList nameList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMenuBar(this);


        UserController userController = new UserController();
        username = userController.readUsername(MapsActivity.this).toString();

        intent = getIntent();

        selectedUser = intent.getStringExtra("selectedUser");
        if (selectedUser.equals("My Moods")) {
            user = 0;
        }
        else if (selectedUser.equals("Timeline Moods")) {
            user = 1;
        }
        else {
            user = 2;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        filterFeeling = intent.getStringExtra("feelingFilter");
        //Toast.makeText(MapsActivity.this, filterFeeling, Toast.LENGTH_SHORT).show();
        if (user == 0) {
            ElasticMoodController.GetFeelingFilterMoods getFeelingFilterMoods =
                    new ElasticMoodController.GetFeelingFilterMoods();
            getFeelingFilterMoods.execute(username, filterFeeling);

            try {
                moodArrayList = getFeelingFilterMoods.get();
                System.out.println("this is moodlist " + moodArrayList);

            } catch (Exception e) {
                Log.i("error", "failed to get the mood out of the async matched");
            }

            for (int i = 0; i < moodArrayList.size(); i++) {
                if (moodArrayList.get(i).getLongitude() == 0 && moodArrayList.get(i).getLatitude() == 0) {
                    break;
                } else {
                    double longitude;
                    double latitude;
                    longitude = moodArrayList.get(i).getLongitude();
                    latitude = moodArrayList.get(i).getLatitude();
                    LatLng tmp = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(tmp).title(filterFeeling).icon(BitmapDescriptorFactory.defaultMarker(setMarkerColor(filterFeeling))));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(tmp));
                }
            }

        } else if (user == 1) {
            // do timeline stuff ...
            UserController userController = new UserController();
            username = userController.readUsername(MapsActivity.this).toString();

            FollowController followController = new FollowController();
            FollowingList followingList = followController.getFollowingList(username);

            Toast.makeText(MapsActivity.this, "timeline", Toast.LENGTH_SHORT).show();

            nameList.addAll(followingList.getFollowingList());
            try {
                for (int i = 0; i < nameList.size(); i++) {

                    ElasticMoodController.GetFeelingFilterMoods getFeelingFilterMoods =
                            new ElasticMoodController.GetFeelingFilterMoods();
                    getFeelingFilterMoods.execute(nameList.get(i).toString(), filterFeeling);

                    try {
                        moodArrayList.addAll(getFeelingFilterMoods.get());

                    } catch (Exception e) {
                        System.out.println("this is fff" + e);
                    }
                    for (int j = 0; j < moodArrayList.size(); j++) {
                        if (moodArrayList.get(j).getLongitude() == 0 && moodArrayList.get(j).getLatitude() == 0) {
                            break;
                        } else {
                            double longitude;
                            double latitude;
                            longitude = moodArrayList.get(j).getLongitude();
                            latitude = moodArrayList.get(j).getLatitude();
                            Toast.makeText(MapsActivity.this, ""+longitude, Toast.LENGTH_SHORT).show();
                            LatLng tmp = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(tmp).title(nameList.get(i).toString()).snippet(filterFeeling).icon(BitmapDescriptorFactory.defaultMarker(setMarkerColor(filterFeeling))));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(tmp));
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("this is fff error" + e);
            }

        } else if (user == 2) {

            Toast.makeText(MapsActivity.this, "hello", Toast.LENGTH_SHORT).show();
//            UserController userController = new UserController();
//            username = userController.readUsername(MapsActivity.this).toString();
//
//            ElasticMoodController.FilterMapByLocation filterMapByLocation =
//                    new ElasticMoodController.FilterMapByLocation();
//            filterMapByLocation.execute(, filterFeeling);


        }


//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        LatLng uofa = new LatLng(-122.084, 37.422);
//        mMap.addMarker(new MarkerOptions().position(uofa).title("Marker in UofA"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(uofa));
    }


    public Float setMarkerColor(String feeling) {
        double d = 0.0;
        float markerColor = (float) d;
        switch (feeling) {
            case "anger":
                markerColor = BitmapDescriptorFactory.HUE_RED;
                break;
            case "confusion":
                markerColor = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case "disgust":
                markerColor = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case "fear":
                markerColor = BitmapDescriptorFactory.HUE_CYAN;
                break;
            case "happiness":
                markerColor = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case "sadness":
                markerColor = BitmapDescriptorFactory.HUE_AZURE;
                break;
            case "shame":
                markerColor = BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case "surprise":
                markerColor = BitmapDescriptorFactory.HUE_BLUE;
                break;
        }
        return markerColor;

    }
}