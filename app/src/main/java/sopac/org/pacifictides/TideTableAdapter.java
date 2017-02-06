package sopac.org.pacifictides;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sachin on 1/30/17.
 */

//Week, Month {
public class TideTableAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<String> values;

    // 1
    public TideTableAdapter(Context context, ArrayList<String> values) {
        this.mContext = context;
        this.values = values;
    }


    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String headers[] = {"DAY", "TIME", "TYPE", "  TYPE", "HEIGHT"};

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text = new TextView(mContext);
        //text.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);
        //text.setWidth(200);
        //text.setSingleLine(false);
        //text.setMaxLines(3);
        String val = values.get(position);
        text.setText(val);
        if (Arrays.asList(headers).contains(val)) {
            text.setTextColor(Color.BLUE);
            text.setTypeface(null, Typeface.BOLD);
            text.setPaintFlags(text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            //text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            //text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_camera, 0, 0, 0);
        } else {
            text.setTextScaleX(1f);
            text.setTextSize(13);
            text.setTypeface(Typeface.MONOSPACE);
        }

        if (val.equals("High")) {
            text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_upward_black_16dp, 0, 0, 0);
        }
        if (val.equals("Low")) {
            text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_downward_black_16dp, 0, 0, 0);
        }

        if (val.contains("TYPE")) {
            //text.setPaintFlags(0);
        }

        if (position % 2 == 1) {
            text.setBackgroundColor(Color.WHITE);
        } else {
            text.setBackgroundColor(Color.CYAN);
        }

        if (Arrays.asList(headers).contains(val)) {
            text.setBackgroundColor(Color.YELLOW);
        }
        return text;

    }
}
