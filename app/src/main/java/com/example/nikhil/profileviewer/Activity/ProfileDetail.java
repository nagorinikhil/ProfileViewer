package com.example.nikhil.profileviewer.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikhil.profileviewer.POJO.User;
import com.example.nikhil.profileviewer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileDetail extends AppCompatActivity {

    ImageView imageViewProfileImage, imageViewEditHobby;
    TextView textViewName, textViewGender, textViewAge, textViewHobbies;
    EditText editTextHobbies;
    Button buttonRemoveProfile, buttonUpdateProfile;
    User user;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        buttonRemoveProfile = (Button) findViewById(R.id.buttonProfileDetailRemove);
        buttonUpdateProfile = (Button) findViewById(R.id.buttonProfileDetailUpdateProfile);
        textViewAge = (TextView) findViewById(R.id.textViewProfileDetailAge);
        textViewName = (TextView) findViewById(R.id.textViewProfileDetailName);
        textViewGender = (TextView) findViewById(R.id.textViewProfileDetailGender);
        textViewHobbies = (TextView) findViewById(R.id.textViewProfileDetailHobbies);
        imageViewEditHobby = (ImageView)findViewById(R.id.imageViewProfileDetailEditHobby);
        imageViewProfileImage = (ImageView)findViewById(R.id.imageViewProfileDetail);
        editTextHobbies = (EditText) findViewById(R.id.editTextProfileDetailHobbies);

        Bundle data = getIntent().getExtras();
        user = (User) data.getParcelable("User");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        getSupportActionBar().setTitle(user.getName());

        initUi();

        buttonRemoveProfile.setOnClickListener(buttonRemoveClickListener);
        buttonUpdateProfile.setOnClickListener(buttonUpdateClickListener);
        imageViewEditHobby.setOnClickListener(imageEditHobbyClickListener);
    }

    private void initUi() {
        textViewName.setText("Name: "+ user.getName());
        textViewGender.setText("Gender: "+ user.getGender());
        textViewHobbies.setText("Hobbies: "+ user.getHobbies());
        textViewAge.setText("Age: "+ String.valueOf(user.getAge()));
        if(user.getImage()!=null && !user.getImage().equals("")) {
            Picasso.get()
                    .load(user.getImage())
                    .into(imageViewProfileImage);
        }
    }

    View.OnClickListener buttonRemoveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            databaseReference.child(String.valueOf(user.getId())).removeValue();
            finish();
        }
    };

    View.OnClickListener buttonUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            databaseReference.child(String.valueOf(user.getId())).child("hobbies").setValue(editTextHobbies.getText().toString());
            editTextHobbies.setVisibility(View.INVISIBLE);
            textViewHobbies.setVisibility(View.VISIBLE);
            textViewHobbies.setText(editTextHobbies.getText().toString());
        }
    };

    View.OnClickListener imageEditHobbyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textViewHobbies.setVisibility(View.INVISIBLE);
            editTextHobbies.setVisibility(View.VISIBLE);
            editTextHobbies.setText(user.getHobbies());
        }
    };
}
