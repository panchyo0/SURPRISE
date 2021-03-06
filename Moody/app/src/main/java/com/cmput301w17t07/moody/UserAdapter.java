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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * The UserAdapter class object of the Moody application. This class is used to properly display
 * the list of user after searching for user in SearchFilterOptionsActivity. It is used in the
 * SearchUserActivity class. <br>
 *
 * Implementation of part of this class was partially inspired by the Android developer's web
 * page on ListView Scrolling Smoothly: <br>
 * https://developer.android.com/training/improving-layouts/smooth-scrolling.html#AsyncTask <br>
 * Taken By: Qi Pang 2017/03/10 <br>
 */

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList;
    private String username;
    private String searchUsername;

    private Achievements achievements;


    /**
     * Constructor for the UserAdapter <br>
     * @param context       Context for the adapter <br>
     * @param userList      The list of users <br>
     */
    public UserAdapter(Context context, List<User> userList, String username, String searchUsername) {
        this.context = context;
        this.userList = userList;
        this.username = username;
        this.searchUsername = searchUsername;
    }

    /**
     * get the size of peoplelist <br>
     * @return size <br>
     */
    @Override
    public int getCount() {
        return userList.size();
    }

    /**
     * get the item in the position of peoplelist <br>
     * @param position <br>
     * @return int <br>
     */
    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    /**
     * get the position <br>
     * @param position <br>
     * @return Object <br>
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * set the people information in main activity <br>
     * @param position  <br>
     * @param convertView <br>
     * @param parent <br>
     * @return long <br>
     */
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view;
        final SearchViewHolder viewHolder;

        if (convertView == null) {
            viewHolder =new SearchViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.single_search_list,null);
            viewHolder.userName=(TextView) view.findViewById(R.id.singleSearchItemName);
            viewHolder.requestButton=(Button) view.findViewById(R.id.searchAdd);

            // determining whether to display send request button
            if(!FollowController.canRequestBeSent(username, searchUsername)){
                viewHolder.requestButton.setEnabled(false);
                viewHolder.requestButton.setBackgroundColor(context.getResources().getColor(R.color.blueTheme));
                viewHolder.requestButton.setText("");
            }


            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder=(SearchViewHolder) view.getTag();
        }


        viewHolder.userName.setText(userList.get(position).getUsername());
        viewHolder.userName.setTextSize(30);

        viewHolder.requestButton.setTag(position);
        viewHolder.requestButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click function for the adapter. Handles user response to sending a follow request
             * @param v
             */
            @Override
            public  void onClick(View v){
                //todo implement ability to show send request button or following text depending on if user is already following
                if(FollowController.sendPendingRequest(username, searchUsername, context)){
                    // if it returns true....

                    AchievementManager.initManager(context);
                    achievements = AchievementController.getAchievements();
                    achievements.followCount += 1;
                    AchievementController.checkForMoodAchievements(context);
                    AchievementController.saveAchievements();

                    // change button displayed
                    //todo need to implement check for what button to display on this screen
                    viewHolder.requestButton.setText("REQUEST SENT");
                    viewHolder.requestButton.setEnabled(false);
                    return;
                }
                else{
                    // display message to warn user that they are not connected to the internet
                    Toast.makeText(context, "Please check your internet connection",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        return view;
    }

    /**
     * SearchViewHolder class which stores the username and request button for each adapter entry
     */
    public class SearchViewHolder {

        public TextView userName;
        public Button requestButton;

    }

}