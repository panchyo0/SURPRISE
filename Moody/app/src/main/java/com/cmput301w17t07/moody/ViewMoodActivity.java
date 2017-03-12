package com.cmput301w17t07.moody;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewMoodActivity extends BarMenuActivity {
    private String username;
    public Mood viewMood;
    public Integer id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_mood);
        setUpMenuBar(this);
        // get the mood object that was selected
        Intent intent = getIntent();
        viewMood = (Mood) intent.getSerializableExtra("viewMood");

        // get username right
        UserController userController = new UserController();
        username = userController.readUsername(ViewMoodActivity.this).toString();
        // if the mood was from user profile allow edit/delete
        if (viewMood.getUsername().equals(username)) {
            displayAttributes();
        }
        // else we disable and don't show the edit/delete button
        else {
            Button edit = (Button) findViewById(R.id.button2);
            edit.setVisibility(Button.GONE);
            Button delete = (Button) findViewById(R.id.button3);
            delete.setVisibility(Button.GONE);
            displayAttributes();
        }
    }

    // display the attributes of the mood that was selected to view
    private void displayAttributes() {

        // NOTE MISSING IMAGE AND LOCATION STILL !!!!!!!!!!!
        TextView user = (TextView) findViewById(R.id.userUsernameTV);
        user.setText(viewMood.getUsername());

        TextView feeling = (TextView) findViewById(R.id.userFeelingTV);
        feeling.setText(viewMood.getMoodMessage());

        TextView date = (TextView) findViewById(R.id.userDateTV);
        date.setText(viewMood.getDate());

        TextView social = (TextView) findViewById(R.id.userSocialTV);
        social.setText(viewMood.getSocialSituation());

        ImageView emoji = (ImageView) findViewById(R.id.userFeelingEmoji);
        switch (viewMood.getFeeling()) {
            case "anger":
                emoji.setImageResource(R.drawable.angry);
                break;
            case "confusion":
                emoji.setImageResource(R.drawable.confused);
                break;
            case "disgust":
                emoji.setImageResource(R.drawable.disgust);
                break;
            case "fear":
                emoji.setImageResource(R.drawable.fear);
                break;
            case "happiness":
                emoji.setImageResource(R.drawable.happy);
                break;
            case "sadness":
                emoji.setImageResource(R.drawable.sad);
                break;
            case "shame":
                emoji.setImageResource(R.drawable.shame);
                break;
            case "surprise":
                emoji.setImageResource(R.drawable.surprise);
                break;
        }

    }

}
