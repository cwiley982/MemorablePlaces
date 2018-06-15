package com.caitlynwiley.memorableplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView placesListView;
    List<String> addresses = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesListView = findViewById(R.id.placesListView);
        addresses.add("Add a new place...");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, addresses);
        placesListView.setAdapter(adapter);

        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //open the map activity centered on user's location so they can add a new location
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("FOCUS", "USER");
                    startActivity(intent);
                    Bundle b = new Bundle();
                } else {
                    //open map activity centered on the location the user clicked on
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("FOCUS", ((Integer) position).toString());
                    //maybe also put lat/long and address/name so MapsActivity can get it from the intent
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        addresses = data.getStringArrayListExtra("PLACES");

        //TODO: now update the list view if it doesn't automatically?
    }
}
