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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

/*
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
*/

}

/*
public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HashMap<String, String> item;
        for (int i = 0; i < SongAndAuthor.length; i++) {
            item = new HashMap<String, String>();
            item.put("line1", SongAndAuthor[i][0]);
            item.put("line2", SongAndAuthor[i][1]);
            list.add(item);
        }
        sa = new SimpleAdapter(this, list,
                R.layout.activity_main,
                new String[]{"line1", "line2"},
                new int[]{R.id.tvSongTitle, R.id.tvAlbumTitle});
        ((ListView) findViewById(R.id.lvMusicList)).setAdapter(sa);
    }

    private String[][] SongAndAuthor =
            {
                    {"Alabama", "Montgomery"},
                    {"Alaska", "Juneau"},
                    {"Arizona", "Phoenix"},
                    {"Arkansas", "Little Rock"},
                    {"California","Sacramento"},
                    {"Colorado","Denver"},
                    {"Connecticut","Hartford"},
                    {"Delaware","Dover"},
                    {"Florida","Tallahassee"},
                    {"Georgia","Atlanta"},
                    {"Hawaii","Honolulu"},
                    {"Idaho","Boise"},
                    {"Illinois","Springfield"},
                    {"Indiana","Indianapolis"},
                    {"Iowa","Des Moines"},
                    {"Kansas","Topeka"},
                    {"Kentucky","Frankfort"},
                    {"Louisiana","Baton Rouge"},
                    {"Maine","Augusta"}
            };

    }*/
