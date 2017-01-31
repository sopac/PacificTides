package sopac.org.pacifictides;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListStation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_station);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, Global.info, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Intent intent = getIntent();
        String country = intent.getStringExtra("country");

        ListView lv = (ListView) findViewById(R.id.listStation);
        SQLiteDatabase db = new TidesDatabase(getApplicationContext()).getReadableDatabase();
        final ArrayList<String> larea = new ArrayList<String>();
        final ArrayList<String> lcountry = new ArrayList<String>();

        int count = 0;

        String sql = "select distinct(area), country, country_code from tides2017 order by area;";
        if (!country.equals("none")) {
            sql = "select distinct(area), country, country_code from tides2017 where country='" + country + "' order by area;";
        }
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            larea.add(c.getString(0));
            lcountry.add(c.getString(1) + ", " + c.getString(2));
            c.moveToNext();
            count++;
        }
        c.close();
        db.close();

        //ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, l);
        //lv.setAdapter(adapterList);

        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i = 0; i < count; i++){
            Map<String, String> map = new HashMap<String, String>(2);
            map.put("station", larea.get(i));
            map.put("country", lcountry.get(i));
            data.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"station", "country"},
                new int[]{android.R.id.text1,  android.R.id.text2});

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String station = data.get(position).get("station");
                Toast.makeText(ListStation.this, station, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("station", station);
                startActivity(i);
            }
        });




    }

}
