package com.janek.maowithfriends.adapter;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.User;

import java.util.ArrayList;
import java.util.List;

public class PlayerSearchAdapter extends ArrayAdapter<User> {
    private Context context;
    private int resource;
    private LayoutInflater inflater;
    private List<User> allUsers, prefilteredUsers;


    public PlayerSearchAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        allUsers = new ArrayList<>();
        prefilteredUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }
        User player = getItem(position);
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(player.name());
        convertView.setTag(player);
        return convertView;
    }

    @Override
    public void add(@Nullable User object) {
        allUsers.add(object);
        prefilteredUsers.add(object);
        notifyDataSetChanged();
    }

    public void removeSelectedPlayer(User player) {
        prefilteredUsers.remove(player);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return playerFilter;
    }

    Filter playerFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((User) resultValue).name();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                List<User> suggestions = new ArrayList<>();
                for (User user : prefilteredUsers) {
                    if (user.name().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(user);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                addAll((List<User>) results.values);
            }
            notifyDataSetChanged();
        }
    };
}
