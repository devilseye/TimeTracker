package devilseye.android.timetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import devilseye.android.timetracker.adapter.CheckboxAdapter;
import devilseye.android.timetracker.model.Category;

public class ChooseCategoriesActivity extends Activity {

    DBHelper dbHelper;
    CheckboxAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper=new DBHelper(this);
        setContentView(R.layout.activity_choose_categories);
        List<Category> categories=dbHelper.getAllCategories();
        adapter=new CheckboxAdapter(this,categories);
        ListView listView=(ListView)findViewById(R.id.categories);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_categories, menu);
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
            List<Category> categories=adapter.getList();
            ArrayList<Integer> chosenCategories=new ArrayList<Integer>();
            for (Category category:categories){
                if (category.isSelected())
                {
                    chosenCategories.add(category.get_id());
                }
            }
            Intent statsActivity = new Intent(getBaseContext(),
                    StatsActivity.class);
            String dateOne=getIntent().getStringExtra("dateOne");
            String dateTwo=getIntent().getStringExtra("dateTwo");
            statsActivity.putExtra("mode", 2);
            statsActivity.putExtra("dateOne", dateOne);
            statsActivity.putExtra("dateTwo", dateTwo);
            int[] ids=new int[chosenCategories.size()];
            for (int i=0;i<chosenCategories.size();i++){
                ids[i]=chosenCategories.get(i);
            }
            statsActivity.putExtra("ids",ids);
            startActivity(statsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
