package devilseye.android.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class StatsActivity extends Activity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper=new DBHelper(this);
        setContentView(R.layout.activity_stats);
        int mode=getIntent().getIntExtra("mode",0);
        String dateOne, dateTwo;
        dateOne=getIntent().getStringExtra("dateOne");
        dateTwo=getIntent().getStringExtra("dateTwo");
        LinkedHashMap<String,Long> statsMap;
        List<String> statsList = null;
        switch (mode){
            case 0:
                setTitle(getString(R.string.action_stats_frequent));
                statsMap=dbHelper.getPopularCategories(dateOne, dateTwo);
                statsList=new ArrayList<String>();
                for (String key:statsMap.keySet()){
                    statsList.add(key+": "+statsMap.get(key)+" "+getString(R.string.activities));
                }
                break;
            case 1:
                setTitle(getString(R.string.action_stats_longest));
                statsMap=dbHelper.getLargeCategories(dateOne, dateTwo);
                statsList=new ArrayList<String>();
                for (String key:statsMap.keySet()){
                    statsList.add(key+": "+statsMap.get(key)+" "+getString(R.string.mins));
                }
                break;
            case 2:
                setTitle("Categories");
                int[] ids=getIntent().getIntArrayExtra("ids");
                LinkedHashMap<Integer,Long> catsMap=dbHelper.getTimeOfCategories(ids,dateOne, dateTwo);
                statsList=new ArrayList<String>();
                for (int category_id:ids){
                    if (catsMap.get(category_id)!=null){
                        statsList.add(dbHelper.getCategory(category_id).get_name()+": "+catsMap.get(category_id)+" "+getString(R.string.mins));
                    } else {
                        statsList.add(dbHelper.getCategory(category_id).get_name()+": 0 "+getString(R.string.mins));
                    }
                }
        }
        if (statsList != null) {
            if (statsList.size()==0){
                statsList.add(getString(R.string.sorry));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, statsList);
            ListView stats = (ListView) findViewById(R.id.stats);
            stats.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
