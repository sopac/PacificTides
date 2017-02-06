package sopac.org.pacifictides;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        //custom
        String country = "";
        String country_code = "";
        Date todayDate = new Date();
        String nextHighTide = "TODO";
        String nextLowTide = "TODO";

        Intent intent = getIntent();
        String station = Global.station;

        if (intent.getStringExtra("station") != null) {
            station = intent.getStringExtra("station");
            Global.station = station;
        } else {
            //expand drawer on first use (if no preset area defined)
            drawer.openDrawer(navigationView);
        }

        if (intent.getLongExtra("dateLong", 1L) > 1) {
            //String dateLongString = intent.getStringExtra("dateLong");
            //System.out.println("DATELONG = " + dateLongString);
            todayDate = new Date(intent.getLongExtra("dateLong", 1L));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM"); //Sun 1 Jan
        String todayShort = sdf.format(todayDate);

        String sql = "select * from tides2017 where area='" + station + "' and short_date='" + todayShort + "' order by id";
        //System.out.println(sql);
        SQLiteDatabase db = new TidesDatabase(getApplicationContext()).getReadableDatabase();
        int count = 0;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //2017-01-30T01:59:00+12:00

        ArrayList<String> gridValues = new ArrayList<String>();
        gridValues.add("TIME");
        gridValues.add("  TYPE");
        gridValues.add("HEIGHT");

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String time = c.getString(6);
            String type = c.getString(9); //high, low
            String height = c.getString(10);
            String local_time = c.getString(8);
            local_time = local_time.substring(0, local_time.indexOf("+")).trim();
            //System.out.println("DEBUG :" + local_time);
            //nextHighLowTide
            try {
                Date tmpDate = sdf.parse(local_time);
                if (type.equals("High") && tmpDate.after(todayDate)) {
                    if (nextHighTide.equals("TODO")) {
                        nextHighTide = String.valueOf(hoursDifference(tmpDate, new Date())); // + " approx hours from now";
                    }
                }
                if (type.equals("Low") && tmpDate.after(todayDate)) {
                    if (nextLowTide.equals("TODO")) {
                        nextLowTide = String.valueOf(hoursDifference(tmpDate, new Date())); // + " approx hours from now";
                    }
                }
            } catch (Exception e) {
                nextHighTide = "ERROR";
                nextLowTide = "ERROR";
                e.printStackTrace();
            }
            gridValues.add(time);
            gridValues.add(type);
            gridValues.add(height);
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


        //next, previous
        final Date currentDate = todayDate;
        Button buttonPrevious = (Button) findViewById(R.id.buttonPrevious);
        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("station", Global.station);
                Calendar c = Calendar.getInstance();
                c.setTime(currentDate);
                c.add(Calendar.DAY_OF_MONTH, 1);
                i.putExtra("dateLong", c.getTimeInMillis());
                startActivity(i);
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("station", Global.station);
                Calendar c = Calendar.getInstance();
                c.setTime(currentDate);
                c.add(Calendar.DAY_OF_MONTH, -1);
                i.putExtra("dateLong", c.getTimeInMillis());
                startActivity(i);
            }
        });

        //debug output
        TextView text = (TextView) findViewById(R.id.textView);
        /*
        text.setText(text.getText() + "\r\n" + station + ", " + country);
        text.setText(text.getText() + "\r\n" + todayDate.toString());
        text.setText(text.getText() + "\r\n" + todayShort);
        text.setText(text.getText() + "\r\n" + "Count: " + String.valueOf(count));
        text.setText(text.getText() + "\r\n" + "NextHighTide: " + nextHighTide);
        text.setText(text.getText() + "\r\n" + "NextLowTide: " + nextLowTide);
        */

        sdf = new SimpleDateFormat("h:mm a");
        String h = "<div align='center' style='width:100%'>";
        h = h + "<h3 align='right'>" + station + ", " + country + "</h3>";
        h = h + "<b><u>Today's High and Low Tides</u></b><br/><br/><hr/>";
        h = h + "On <b>" + todayShort + "</b> @ " + sdf.format(new Date()) + ", it's - <br/>";
        h = h + "Approx <b>" + nextHighTide + "</b> hours till next <b>HIGH</b> tide.<br/>";
        h = h + "Approx <b>" + nextLowTide + "</b> hours till next <b>LOW</b> tide.<br/>";
        h = h + "</div>";


        text.setText(Html.fromHtml(h));


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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("About");
            builder.setMessage("Completely offline. Developed by Geoscience Division, SPC. For further information contact ict4dev@spc.int.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        if (id == R.id.action_exit) {
            this.finish();
            System.exit(0);
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
            Intent i = new Intent(super.getApplicationContext(), MainActivity.class);
            i.putExtra("station", Global.station);
            i.putExtra("dateLong", new Date().getTime());
            startActivity(i);
        } else if (id == R.id.nav_week) {
            Intent i = new Intent(super.getApplicationContext(), ListWeek.class);
            i.putExtra("station", Global.station);
            startActivity(i);
        } else if (id == R.id.nav_month) {
            Intent i = new Intent(super.getApplicationContext(), ListMonth.class);
            i.putExtra("station", Global.station);
            startActivity(i);
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ict4dev@spc.int"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Pacific Tides App Feedback");
            //if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivity(Intent.createChooser(intent, "Email Feedback"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
            //}

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static int hoursDifference(Date date1, Date date2) {
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        return (int) (date1.getTime() - date2.getTime()) / MILLI_TO_HOUR;
    }
}
