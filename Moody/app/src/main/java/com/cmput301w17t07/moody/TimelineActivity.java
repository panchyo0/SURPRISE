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


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 *  The Timeline Activity handles the user interface logic for when the user has first installed
 *  our application. It allows them to create a unique username after that.
 *
 *  After the user's first time using the application, it takes them to the timeline activity page
 *  which will display the moods of the users they follow. This functionality is being added in
 *  project part 5.
 */

public class TimelineActivity extends BarMenuActivity {
    ConnectivityManager manager;
    private EditText usernameText;
    Integer createUserFlag = null;

    String username;
    Boolean scrollFlag;

    ArrayList nameList=new ArrayList();
    private ArrayList<Mood> templist = new ArrayList<>();

    int indexOfScroll=0;
    int lastItem;
    private ListView oldUserList;
    private ArrayList<Mood> moodArrayList = new ArrayList<>();
    private MoodAdapter adapter;

    private ArrayList<Mood> sortedArrayList = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("isFirstIn", Activity.MODE_PRIVATE);
        boolean isFirstIn = sp.getBoolean("isFirstIn", true);
        if (isFirstIn) {
            sp.edit().putBoolean("isFirstIn", false).apply();
            setContentView(R.layout.activity_create_user);
            Toast.makeText(TimelineActivity.this, " Welcome to Moody! ", Toast.LENGTH_SHORT).show();
            Button registerButton = (Button) findViewById(R.id.register);

            usernameText = (EditText) findViewById(R.id.enterUsername);


            /**
             * On user's click of the register button
             */
            registerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!(checkNetworkState())) {
                        Toast.makeText(TimelineActivity.this, "Internet not available \n" +
                                "Please check internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        String username = usernameText.getText().toString();


                        //todo get image from user and createUser with image parameter
                        UserController userController = new UserController();
                        createUserFlag = userController.createUser(username);
                        if(createUserFlag.equals(1)){
                            Toast.makeText(TimelineActivity.this,
                                    "Username is already taken", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        else if(createUserFlag.equals(2)){
                            Toast.makeText(TimelineActivity.this,
                                    "Sorry, but the profile picture you selected is too large",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //todo use this internet checking method instead of one inside the activity
                        
                        userController.saveUsername(username.toLowerCase(), TimelineActivity.this);
                        // creating follow/following lists for new user
                        FollowController followController = new FollowController();
                        followController.createFollowLists(username.toLowerCase());


                    }


                    Toast.makeText(TimelineActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    firstime();
                }
            });
        }else {

            timelineActivity();
        }


    }

    //todo remove this from activity and implement in a separate class/controller/etc.
    //Internet checker temp, maybe need change later

    /**
     * checkNetworkState method. This is a temporary method in project part 4 that checks if a user
     * is online before attempting to register a username. A similar type of function will be moved
     * into an appropriate controller class for project part 5 to reflect better separation of
     * user interface logic and backend logic.
     *
     * Method currently returns a boolean indicating whether the user is connected to the internet.
     * @return
     */
    private boolean checkNetworkState() {
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return true;
    }


    private void firstime() {

        setContentView(R.layout.activity_timeline);
        setUpMenuBar(this);


    }

    /**
     * Current implementation of timelineActivity method. Just displays blank xml content screen
     * with menubar.
     */
    private void timelineActivity(){

        setContentView(R.layout.activity_timeline);
        setUpMenuBar(this);

        scrollFlag = true;


        UserController userController = new UserController();
        username = userController.readUsername(TimelineActivity.this).toString();

        FollowController followController = new FollowController();
        FollowingList followingList = followController.getFollowingList(username);
        System.out.println("this is fff"+followingList.getFollowingList()+"num="+followingList.countFollowing());
        oldUserList = (ListView) findViewById(R.id.list_view);

        nameList.addAll(followingList.getFollowingList());
        try {
            for (int i = 0; i <nameList.size(); i++) {
                moodArrayList.addAll(MoodController.getUserMoods(nameList.get(i).toString(),
                        String.valueOf(indexOfScroll), TimelineActivity.this));
            }
            System.out.println("this is fff moodlist "+moodArrayList.size());
            sortedArrayList = MoodController.sortMoods(moodArrayList);
            adapter = new MoodAdapter(this, R.layout.timeline_list, sortedArrayList);
            oldUserList.setAdapter(adapter);

        }catch (Exception e){
            System.out.println("this is a timeline error"+e);
        }


        oldUserList.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    // added a test to see if all moods have been loaded
                    if(scrollFlag) {
                        Toast.makeText(getApplicationContext(), "Starting load new moody", Toast.LENGTH_SHORT).show();
                        indexOfScroll = indexOfScroll + 6;
                        templist = MoodController.getUserMoods(username,
                                String.valueOf(indexOfScroll), TimelineActivity.this);
                        // determining if there any old moods to find
                        if (templist.size() == 0) {
                            scrollFlag = false;
                        }

                        moodArrayList.addAll(templist);
                        sortedArrayList = MoodController.sortMoods(moodArrayList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1 ;
            }
        });





        oldUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Mood viewMood = moodArrayList.get(position);
                Intent viewMoodIntent = new Intent(TimelineActivity.this, ViewMoodActivity.class);
                viewMoodIntent.putExtra("viewMood", viewMood);
                startActivity(viewMoodIntent);
                finish();

            }
        });



    }


}




