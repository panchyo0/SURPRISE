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

import android.util.Log;

/**
 * Created by mike on 2017-03-25.
 */

public class FollowController {

    public static void createFollowLists(String username){
        // method is only invoked when user first registers his/her account

        // creating list class objects
        FollowerList followerList = new FollowerList(username);
        FollowingList followingList = new FollowingList(username);

        // Creating the elasticSearch controllers for adding the lists to the server
        ElasticSearchFollowController.AddFollowerList addFollowerList =
                new ElasticSearchFollowController.AddFollowerList();
        ElasticSearchFollowController.AddFollowingList addFollowingList =
                new ElasticSearchFollowController.AddFollowingList();

        // adding lists to server
        addFollowerList.execute(followerList);
        addFollowingList.execute(followingList);

    }

    public static boolean sendPendingRequest(String userSendingRequest, String userReceivingRequest){
        if(checkNetwork()){
            // if connection is present....

            // Get the FollowerList of the userReceiving the request from the server
            FollowerList followerList = null;

            ElasticSearchFollowController.GetFollowerList getFollowerList =
                    new ElasticSearchFollowController.GetFollowerList();
            getFollowerList.execute(userReceivingRequest);

            // trying to retrieve the follower list
            try {
                followerList = getFollowerList.get();;
            } catch (Exception E){
                Log.i("Error", "Was unable to retrieve follower list during sendPendingRequest()");
            }
            // adding the pending username to the followerlist
            followerList.addPending(userSendingRequest);

            //----------------------- NOW UPDATING THE SERVER --------------------------------------

            ElasticSearchFollowController.DeleteFollowerList deleteFollowerList =
                    new ElasticSearchFollowController.DeleteFollowerList();
            ElasticSearchFollowController.AddFollowerList addFollowerList =
                    new ElasticSearchFollowController.AddFollowerList();

            // deleting old followerlist
            deleteFollowerList.execute(userReceivingRequest);
            // adding updated one
            addFollowerList.execute(followerList);

            // DONE
            return true;
        }
        else{
            // todo notify user that they are not connected to the internet!
            return false;
        }
    }


    public static boolean acceptFollowRequest(String userAcceptingRequest, String userThatSentRequest){
        if(checkNetwork()){
            // if connection is present....

            //-------------------- UPDATING FOLLOWER LIST FOR ACCEPTING USER ----------------------
            // Get the FollowerList of the userAcceptingRequest the request from the server
            FollowerList followerList = null;

            ElasticSearchFollowController.GetFollowerList getFollowerList =
                    new ElasticSearchFollowController.GetFollowerList();
            getFollowerList.execute(userAcceptingRequest);

            // trying to retrieve the follower list
            try {
                followerList = getFollowerList.get();;
            } catch (Exception E){
                Log.i("Error", "Was unable to retrieve follower list during acceptFollowRequest()");
            }
            // adding follower to the accepting user's follower list
            followerList.addFollower(userAcceptingRequest);

            //----------------------- NOW UPDATING THE SERVER --------------------------------------

            ElasticSearchFollowController.DeleteFollowerList deleteFollowerList =
                    new ElasticSearchFollowController.DeleteFollowerList();
            ElasticSearchFollowController.AddFollowerList addFollowerList =
                    new ElasticSearchFollowController.AddFollowerList();

            // deleting old followerlist
            deleteFollowerList.execute(userAcceptingRequest);
            // adding updated one
            addFollowerList.execute(followerList);


            //--------------- UPDATING FOLLOWING LIST FOR USER WHO SENT REQUEST --------------------

            FollowingList followingList = null;

            ElasticSearchFollowController.GetFollowingList getFollowingList =
                    new ElasticSearchFollowController.GetFollowingList();
            getFollowingList.execute(userThatSentRequest);

            // trying to retrieve following list
            try {
                followingList = getFollowingList.get();
            } catch (Exception E){
                Log.i("Error", "Was unable to retrieve following list during acceptFollowRequest()");
            }

            followingList.addFollowing(userThatSentRequest);

            //----------------------- NOW UPDATING THE SERVER --------------------------------------

            ElasticSearchFollowController.DeleteFollowingList deleteFollowingList =
                    new ElasticSearchFollowController.DeleteFollowingList();
            ElasticSearchFollowController.AddFollowingList addFollowingList =
                    new ElasticSearchFollowController.AddFollowingList();

            // deleting old following list
            deleteFollowingList.execute(userThatSentRequest);
            // adding update one
            addFollowingList.execute(followingList);

            //------------------------------ DONE --------------------------------------------------
            return true;
        }
        else{
            // todo notify user that they are not connected to the internet!
            return false;
        }
    }

    private static boolean checkNetwork(){
        //todo implement a checkNetwork method/class to be called here
        return true;
    }

}