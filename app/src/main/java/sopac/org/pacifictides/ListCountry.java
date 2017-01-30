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
import android.widget.Toast;

import java.util.ArrayList;

public class ListCountry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_country);
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


        ListView lv = (ListView) findViewById(R.id.listCountry);
        SQLiteDatabase db = new TidesDatabase(getApplicationContext()).getReadableDatabase();
        final ArrayList<String> l = new ArrayList<String>();
        int count = 0;

        Cursor c = db.rawQuery("select distinct(country) from tides2017 order by country", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            l.add(c.getString(0));
            c.moveToNext();
            count++;
        }
        c.close();
        db.close();

        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, l);
        lv.setAdapter(adapterList);

        //event
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String country = l.get(position);
                Toast.makeText(ListCountry.this, country, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), ListStation.class);
                i.putExtra("country", country);
                startActivity(i);
            }
        });




    }

}
