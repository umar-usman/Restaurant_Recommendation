package com.example.hp.navigation_drawer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class PlaceDetails extends Fragment {

    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_place_details,container,false);

        setupListView();

        return v;
    }

    private void setupListView() {
        String[] listItems = new String[]{
                ""
        };

        ArrayList Items = getArguments().getStringArrayList("PlaceNames");

        if (!(Items == null)) {
            ArrayAdapter<String> listItemAdapter =
                    new ArrayAdapter<String>(getActivity()
                            , android.R.layout.simple_list_item_1
                            , Items);
            ListView lv = v.findViewById(R.id.list_view_id);
            lv.setAdapter(listItemAdapter);
        }
    }

}
