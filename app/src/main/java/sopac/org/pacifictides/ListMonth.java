package sopac.org.pacifictides;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListMonth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_month);
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

        SimpleDateFormat sdf = new SimpleDateFormat("MMM"); //Sat 18 Feb
        String filterMonth = sdf.format(new Date());

        TextView text = (TextView) findViewById(R.id.textMonth);
        text.setText(Global.station + ", " + filterMonth + " 2017");

        ArrayList<String> gridValues = new ArrayList<String>();
        gridValues.add("DAY");
        gridValues.add("TIME");
        gridValues.add("TYPE");
        gridValues.add("HEIGHT");

        //show daily grid
        GridView table = (GridView) findViewById(R.id.tableMonth);
        table.setAdapter(new TideTableAdapter(this, gridValues));

        String sql = "select * from tides2017 where area='" + Global.station + "' and short_date like '%" + filterMonth + "' order by id";
        SQLiteDatabase db = new TidesDatabase(getApplicationContext()).getReadableDatabase();

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String time = c.getString(6);
            String type = c.getString(9); //high, low
            String height = c.getString(10);
            String local_time = c.getString(8);

            gridValues.add(c.getString(5));
            gridValues.add(c.getString(6));
            gridValues.add(c.getString(9));
            gridValues.add(c.getString(10));

            c.moveToNext();
        }

        db.close();



    }

}
