package devilseye.android.timetracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.Calendar;
import java.util.LinkedHashMap;

public class DatePickersActivity extends Activity {

    DatePicker dateStart;
    DatePicker dateEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pickers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_date_pickers, menu);
        dateStart=(DatePicker)findViewById(R.id.dateStartPicker);
        dateEnd=(DatePicker)findViewById(R.id.dateEndPicker);
        dateStart.setCalendarViewShown(false);
        dateEnd.setCalendarViewShown(false);
        int month=dateEnd.getMonth();
        if (month==0){
            dateStart.updateDate(dateEnd.getYear()-1,11, dateEnd.getDayOfMonth());
        } else {
            dateStart.updateDate(dateEnd.getYear(),month-1, dateStart.getDayOfMonth());
        }
        Button resetButton=(Button)findViewById(R.id.updateButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                dateEnd.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                int month = dateEnd.getMonth();
                if (month == 0) {
                    dateStart.updateDate(dateEnd.getYear() - 1, 11, dateEnd.getDayOfMonth());
                } else {
                    dateStart.updateDate(dateEnd.getYear(), month - 1, dateStart.getDayOfMonth());
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_data) {
            int mode=getIntent().getIntExtra("mode",0);
            String dateOne = dateStart.getYear() + "-" + (dateStart.getMonth() + 1) + "-" + dateStart.getDayOfMonth() + " 00:00:00";
            String dateTwo = dateEnd.getYear() + "-" + (dateEnd.getMonth() + 1) + "-" + dateEnd.getDayOfMonth() + " 23:59:59";

            if (mode!=2 && mode!=4) {
                Intent statsActivity = new Intent(getBaseContext(),
                        StatsActivity.class);
                statsActivity.putExtra("mode", mode);
                statsActivity.putExtra("dateOne", dateOne);
                statsActivity.putExtra("dateTwo", dateTwo);
                startActivity(statsActivity);
            } else {
                if (mode==4){
                    openChart(dateOne, dateTwo);
                    finish();
                } else {
                    Intent chooseCatsActivity = new Intent(getBaseContext(),
                            ChooseCategoriesActivity.class);
                    chooseCatsActivity.putExtra("mode", mode);
                    chooseCatsActivity.putExtra("dateOne", dateOne);
                    chooseCatsActivity.putExtra("dateTwo", dateTwo);
                    startActivity(chooseCatsActivity);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void openChart(String dateOne, String dateTwo){

        DBHelper dbHelper=new DBHelper(this);

        LinkedHashMap<Integer,Long> hashMap=dbHelper.getTimeOfCategories(dateOne, dateTwo);
        String[] categories = new String[hashMap.size()];
        long[] time = new long[hashMap.size()];
        int identifier=0;
        for (int key:hashMap.keySet()){
            categories[identifier]=dbHelper.getCategory(key).get_name();
            time[identifier]=hashMap.get(key);
            identifier++;
        }

        int[] colors = { Color.BLUE, Color.DKGRAY, Color.MAGENTA, Color.GREEN,Color.GRAY, Color.CYAN, Color.RED,
                Color.YELLOW, Color.BLACK };

        CategorySeries categorySeries = new CategorySeries(getString(R.string.diagram_title));
        for(int i=0 ;i < time.length;i++){
            categorySeries.add(categories[i], time[i]);
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer  = new DefaultRenderer();
        int k=0;
        for (long aTime : time) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            if (k >= colors.length) {
                k = 0;
            }
            seriesRenderer.setColor(colors[k]);
            k++;
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        if (hashMap!=null && hashMap.size()!=0) {
            defaultRenderer.setChartTitle(getString(R.string.diagram_desc));
        } else {
            defaultRenderer.setChartTitle(getString(R.string.sorry));
        }
        defaultRenderer.setChartTitleTextSize(40);
        defaultRenderer.setLabelsColor(Color.BLACK);
        defaultRenderer.setZoomEnabled(false);
        defaultRenderer.setClickEnabled(false);

        Intent intent = ChartFactory.getPieChartIntent(getBaseContext(), categorySeries, defaultRenderer, getString(R.string.diagram_title));

        startActivity(intent);
    }
}
