package com.example.nikhil.profileviewer.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nikhil.profileviewer.Adapter.AdapterProfileList;
import com.example.nikhil.profileviewer.Interface.ProfileListClickInterface;
import com.example.nikhil.profileviewer.POJO.User;
import com.example.nikhil.profileviewer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements ProfileListClickInterface {

    FloatingActionButton floatingActionButtonAddProfile;
    RecyclerView recyclerViewProfileList;
    Spinner spinnerFilter, spinnerSort, spinnerAddProfileGender;
    ConstraintLayout constraintLayoutAddProfile, constraintLayoutProfileList;
    Button buttonCancel, buttonSave;
    ImageView imageViewAddProfileImage;
    EditText editTextAddProfileAge, editTextAddProfileHobbies, editTextAddProfileName;
    ProgressBar progressBar;

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    UploadTask uploadTask;
    AdapterProfileList adapterProfileList;

    int uniqueId;
    Uri filePath = null;
    ArrayList<User> userArrayList;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSpinners();
        userArrayList = new ArrayList<>();

        adapterProfileList = new AdapterProfileList(userArrayList, R.layout.list_profile_item, this);
        adapterProfileList.notifyDataSetChanged();
        recyclerViewProfileList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProfileList.setAdapter(adapterProfileList);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerViewProfileList.addItemDecoration(itemDecor);

        progressBar.setVisibility(View.VISIBLE);

        floatingActionButtonAddProfile.setOnClickListener(fabClickListener);
        buttonCancel.setOnClickListener(cancelButtonClickListener);
        buttonSave.setOnClickListener(saveButtonClickListener);
        imageViewAddProfileImage.setOnClickListener(profileImageClickListener);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseStorage = FirebaseStorage.getInstance();
    }

    private void initSpinners() {
        ArrayAdapter<CharSequence> adapterSpinnerFilter = ArrayAdapter.createFromResource(this, R.array.filter_array, android.R.layout.simple_spinner_item);
        adapterSpinnerFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapterSpinnerFilter);
        spinnerFilter.setOnItemSelectedListener(filterSpinnerSelectListener);

        ArrayAdapter<CharSequence> adapterSpinnerSort = ArrayAdapter.createFromResource(this, R.array.sort_array, android.R.layout.simple_spinner_item);
        adapterSpinnerSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapterSpinnerSort);
        spinnerSort.setOnItemSelectedListener(sortSpinnerSelectListener);

        ArrayAdapter<CharSequence> adapterSpinnerGender = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapterSpinnerSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddProfileGender.setAdapter(adapterSpinnerGender);
    }

    private void initViews(){
        floatingActionButtonAddProfile = (FloatingActionButton) findViewById(R.id.floatingActionButtonMainActivityAddProfile);
        recyclerViewProfileList = (RecyclerView) findViewById(R.id.recyclerViewMainActivityProfileList);
        spinnerFilter = (Spinner) findViewById(R.id.spinnerMainActivityFilter);
        spinnerSort = (Spinner) findViewById(R.id.spinnerMainActivitySort);
        spinnerAddProfileGender = (Spinner) findViewById(R.id.spinnerMainActivityAddProfileGender);
        constraintLayoutAddProfile = (ConstraintLayout) findViewById(R.id.constraintLayoutMainActivityAddProfile);
        constraintLayoutProfileList = (ConstraintLayout) findViewById(R.id.constraintLayoutMainActivityProfileList);
        buttonCancel = (Button) findViewById(R.id.buttonMainActivityCancel);
        buttonSave = (Button) findViewById(R.id.buttonMainActivitySaveProfile);
        imageViewAddProfileImage = (ImageView) findViewById(R.id.imageViewMainActivityAddProfile);
        editTextAddProfileAge = (EditText) findViewById(R.id.editTextMainActivityAddProfileAge);
        editTextAddProfileHobbies = (EditText) findViewById(R.id.editTextMainActivityAddProfileHobbies);
        editTextAddProfileName = (EditText) findViewById(R.id.editTextMainActivityAddProfileName);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMainActivity);
    }

    View.OnClickListener profileImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }
    };

    View.OnClickListener cancelButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintLayoutAddProfile.setVisibility(View.INVISIBLE);
            spinnerFilter.setEnabled(true);
            spinnerSort.setEnabled(true);
            recyclerViewProfileList.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener saveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isNetworkAvailable()){
                if(validate()){
                    String name = editTextAddProfileName.getText().toString();
                    int age = Integer.parseInt(editTextAddProfileAge.getText().toString());
                    String hobbies = editTextAddProfileHobbies.getText().toString();
                    String gender = spinnerAddProfileGender.getSelectedItem().toString();
                    id = 10000;
                    if(userArrayList.size()!=0){
                        id = userArrayList.get(userArrayList.size() -1 ).getId() + 1;
                    }

                    final User user = new User(id, name, gender, age, hobbies);

                    if(filePath != null){
                        storageReference = firebaseStorage.getReference("images/"+filePath.getLastPathSegment());
                        uploadTask = storageReference.putFile(filePath);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri profileImageUri = taskSnapshot.getDownloadUrl();
                                user.setImage(profileImageUri.toString());
                                Log.d("User:", user.toString());
                                databaseReference.child(String.valueOf(id)).setValue(user);
                                constraintLayoutAddProfile.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Profile Image could not save", Toast.LENGTH_SHORT).show();
                                user.setImage("");
                            }
                        });

                    } else {
                        user.setImage("");
                        databaseReference.child(String.valueOf(id)).setValue(user);
                        constraintLayoutAddProfile.setVisibility(View.INVISIBLE);
                    }
                    spinnerFilter.setEnabled(true);
                    spinnerSort.setEnabled(true);
                    recyclerViewProfileList.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(MainActivity.this, "Profile cannot be saved due to No Internet", Toast.LENGTH_LONG).show();
            }

        }
    };

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            constraintLayoutAddProfile.setVisibility(View.VISIBLE);
            spinnerFilter.setEnabled(false);
            spinnerSort.setEnabled(false);
            recyclerViewProfileList.setVisibility(View.GONE);
        }
    };

    AdapterView.OnItemSelectedListener filterSpinnerSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String genderFilter = (String) parent.getItemAtPosition(position);
            adapterProfileList.getFilter().filter(genderFilter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener sortSpinnerSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            applySort(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void applySort(int position){
        switch (position){
            case 0:
                sortAscId();
                break;
            case 1:
                sortAscAge();
                break;
            case 2:
                sortDscAge();
                break;
            case 3:
                sortAscName();
                break;
            case 4:
                sortDscName();
                break;
        }
        adapterProfileList.notifyDataSetChanged();
        adapterProfileList.getFilter().filter(spinnerFilter.getSelectedItem().toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.d("Path = ", filePath.toString());
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageViewAddProfileImage.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    private boolean validate(){
        boolean valid = true;

        if(TextUtils.isEmpty(editTextAddProfileName.getText().toString())){
            valid = false;
            editTextAddProfileName.setError("Required");
        }
        if(TextUtils.isEmpty(editTextAddProfileHobbies.getText().toString())){
            valid = false;
            editTextAddProfileHobbies.setError("Required");
        }
        if(TextUtils.isEmpty(editTextAddProfileAge.getText().toString())){
            valid = false;
            editTextAddProfileAge.setError("Required");
        }
        String gender = spinnerAddProfileGender.getSelectedItem().toString();
        if(gender.equals("Select Gender")){
            Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isNetworkAvailable()){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    userArrayList.add(user);
                }
                adapterProfileList.notifyDataSetChanged();
                applySort(spinnerSort.getSelectedItemPosition());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void openProfileDetail(User user) {
        Intent intent = new Intent(this, ProfileDetail.class);
        intent.putExtra("User",user);
        startActivity(intent);
    }

    private void sortAscName(){
        Collections.sort(userArrayList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getName().compareTo(u2.getName());
            }
        });
        adapterProfileList.notifyDataSetChanged();
    }

    private void sortDscName(){
        Collections.sort(userArrayList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u2.getName().compareTo(u1.getName());
            }
        });
        adapterProfileList.notifyDataSetChanged();
    }


    private void sortDscAge(){
        Collections.sort(userArrayList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                if(u1.getAge() < u2.getAge())
                    return 1;
                else if(u1.getAge() > u2.getAge())
                    return -1;
                else
                    return 0;
            }
        });
        adapterProfileList.notifyDataSetChanged();
    }

    private void sortAscAge(){
        Collections.sort(userArrayList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                if(u1.getAge() < u2.getAge())
                    return -1;
                else if(u1.getAge() > u2.getAge())
                    return 1;
                else
                    return 0;
            }
        });
        adapterProfileList.notifyDataSetChanged();
    }

    private void sortAscId(){
        Collections.sort(userArrayList, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                if(u1.getId() < u2.getId())
                    return -1;
                else if(u1.getId() > u2.getId())
                    return 1;
                else
                    return 0;
            }
        });
        adapterProfileList.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

