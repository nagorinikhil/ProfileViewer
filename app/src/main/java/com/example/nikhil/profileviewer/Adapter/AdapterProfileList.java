package com.example.nikhil.profileviewer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikhil.profileviewer.Interface.ProfileListClickInterface;
import com.example.nikhil.profileviewer.POJO.User;
import com.example.nikhil.profileviewer.R;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProfileList extends RecyclerView.Adapter<AdapterProfileList.ViewHolder> implements Filterable{

    ArrayList<User> userArrayList;
    ArrayList<User> userArrayFilterList;
    //Context context;
    //DataSnapshot dataSnapshot;
    int resource;
    GenderFilter genderFilter;
    ProfileListClickInterface listClickInterface;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProfile;
        TextView textViewName, textViewAge, textViewGender, textViewId, textViewHobbies;
        ConstraintLayout constraintLayoutProfileList;

        public ViewHolder(View view) {
            super(view);
            textViewAge = (TextView) view.findViewById(R.id.textViewProfileListAge);
            textViewName = (TextView) view.findViewById(R.id.textViewProfileListName);
            textViewGender = (TextView) view.findViewById(R.id.textViewProfileListGender);
            textViewId = (TextView) view.findViewById(R.id.textViewProfileListId);
            textViewHobbies = (TextView) view.findViewById(R.id.textViewProfileListHobbies);
            imageViewProfile = (ImageView) view.findViewById(R.id.imageViewProfileListImage);
            constraintLayoutProfileList = (ConstraintLayout) view.findViewById(R.id.constraintLayoutProfileListItem );

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked(getAdapterPosition());
                }
            });
        }

    }

    public AdapterProfileList(ArrayList<User> userArrayList, int resource, ProfileListClickInterface listClickInterface) {
        this.userArrayList = userArrayList;
        //this.context = context;
        this.resource = resource;
        this.userArrayFilterList = userArrayList;
        this.listClickInterface = listClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.textViewName.setText("Name: "+ user.getName());
        holder.textViewAge.setText("Age: "+ String.valueOf(user.getAge()));
        holder.textViewGender.setText("Gender: "+ user.getAge());
        holder.textViewHobbies.setText("Hobbies: "+ user.getHobbies());
        holder.textViewId.setText("Id: "+ String.valueOf(user.getId()));
        if(user.getImage()!=null && !user.getImage().equals("")){
            Picasso.get()
                    .load(user.getImage())
                    .into(holder.imageViewProfile);
        }
        if(user.getGender().equals("Male")){
            holder.constraintLayoutProfileList.setBackgroundColor(Color.parseColor("#4b87ff"));
        } else {
            holder.constraintLayoutProfileList.setBackgroundColor(Color.parseColor("#FFC0CB"));
        }

    }

    private void itemClicked(int pos){
        listClickInterface.openProfileDetail(userArrayList.get(pos));
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(genderFilter == null)
            genderFilter = new GenderFilter();
        return genderFilter;
    }

    private class GenderFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String gender = constraint.toString();
            FilterResults filterResults = new FilterResults();
            ArrayList<User> filterList = new ArrayList<>();

            if(!gender.equals("All")){

                for(int i=0; i<userArrayFilterList.size(); i++){
                    if(userArrayFilterList.get(i).getGender().equals(gender)){
                        filterList.add(userArrayFilterList.get(i));
                    }
                }

                filterResults.count = filterList.size();
                filterResults.values = filterList;

            } else {
                filterResults.count = userArrayFilterList.size();
                filterResults.values = userArrayFilterList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userArrayList = (ArrayList<User>)results.values;
            notifyDataSetChanged();
        }
    }
}
