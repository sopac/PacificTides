package sopac.org.pacifictides;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //expand drawer on first use (if no preset area defined)
        drawer.openDrawer(navigationView);

        Intent intent = getIntent();
        String station = Global.station;

        if (intent.getStringExtra("station") != null) {
            station = intent.getStringExtra("station");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM"); //Sun 1 Jan
        Date todayDate = new Date();
        String todayShort = sdf.format(todayDate);


        String sql = "select * from tides2017 where area='" + station + "' and short_date='" + todayShort + "';";
        System.out.println(sql);
        SQLiteDatabase db = new TidesDatabase(getApplicationContext()).getReadableDatabase();
        int count = 0;
        sdf = new SimpleDateFormat("EEE d MMM"); //2017-01-30T01:59:00+12:00


        String country = "";
        String country_code = "";
        ArrayList<String> gridValues = new ArrayList<String>();
        gridValues.add("TIME");
        gridValues.add("  TYPE");
        gridValues.add("HEIGHT");

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            gridValues.add(c.getString(6));
            gridValues.add(c.getString(9));
            gridValues.add(c.getString(10));
            country = c.getString(2);
            country_code = c.getString(1);
            c.moveToNext();
            count++;
        }
        c.close();
        db.close();

        ImageView flag = (ImageView) findViewById(R.id.flag);
        try {
            Drawable d = Drawable.createFromStream(getAssets().open("flags/" + country_code + ".jpg"), null);
            flag.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.view.ViewGroup.LayoutParams layoutParams = flag.getLayoutParams();
        layoutParams.width = 300;
        layoutParams.height = 140;
        flag.setLayoutParams(layoutParams);


        //show daily grid
        GridView table = (GridView) findViewById(R.id.table);
        table.setAdapter(new TideTableAdapter(this, gridValues));

        //debug output
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText(text.getText() + "\r\n" + station + ", " + country);
        text.setText(text.getText() + "\r\n" + todayDate.toString());
        text.setText(text.getText() + "\r\n" + todayShort);
        text.setText(text.getText() + "\r\n" + "Count: " + String.valueOf(count));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_country) {
            Intent i = new Intent(super.getApplicationContext(), ListCountry.class);
            //i.putExtra("type", "standard");
            startActivity(i);
        } else if (id == R.id.nav_station) {
            Intent i = new Intent(super.getApplicationContext(), ListStation.class);
            i.putExtra("country", "none");
            startActivity(i);
        } else if (id == R.id.nav_today) {

        } else if (id == R.id.nav_week) {

        } else if (id == R.id.nav_month) {

        } else if (id == R.id.nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
