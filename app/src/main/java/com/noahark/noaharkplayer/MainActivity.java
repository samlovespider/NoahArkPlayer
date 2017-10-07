package com.noahark.noaharkplayer;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] songs = {"Song A", "Song B", "Song C", "Song D", "Song E", "Song F"};
        ListAdapter musicListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);
        ListView lvMusicList = (ListView) findViewById(R.id.lvMusicList);
        lvMusicList.setAdapter(musicListAdapter);

        lvMusicList.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String song = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(MainActivity.this, song, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

}