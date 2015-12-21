package devilseye.android.timetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import de.timroes.android.listview.EnhancedListView;

import java.util.List;

import devilseye.android.timetracker.adapter.RecordAdapter;
import devilseye.android.timetracker.model.Record;

public class MainActivity extends Activity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper =new DBHelper(this);

        final EnhancedListView listView = (EnhancedListView) findViewById(R.id.itemList);
        listView.setOnItemClickListener(new EnhancedListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final RecordAdapter recordAdapter = (RecordAdapter) listView.getAdapter();
                final Record record = recordAdapter.getItem(position);
                Intent recordActivity = new Intent(getBaseContext(),
                        RecordActivity.class);
                recordActivity.putExtra("record_id", record.get_id());
                startActivity(recordActivity);
            }
        });
                            listView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
                                @Override
                                public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
                                    final RecordAdapter recordAdapter = (RecordAdapter) listView.getAdapter();
                                    final Record item = recordAdapter.getItem(position);
                                    recordAdapter.remove(item);
                                    return new EnhancedListView.Undoable() {
                                        @Override
                                        public void undo() {
                                            recordAdapter.insert(item, position);
                                            EnhancedListView listView = (EnhancedListView) findViewById(R.id.itemList);
                                            listView.setAdapter(recordAdapter);
                                        }

                                        @Override
                                        public String getTitle() {
                                            return getString(R.string.deleted) + item.get_category().get_name();
                                        }

                                        @Override
                                        public void discard() {
                                            dbHelper.deleteRecord(item);
                                        }
                                    };
                                }
                            });
        listView.setRequireTouchBeforeDismiss(false);
        listView.enableSwipeToDismiss();
        updateList();
    }

    @Override
     public void onResume(){
        super.onResume();
        updateList();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        updateList();
    }

    private void updateList(){
        List<Record> recordList=dbHelper.getAllRecords();
        EnhancedListView listView = (EnhancedListView) findViewById(R.id.itemList);
        ArrayAdapter<Record> listAdapter = new RecordAdapter(this,recordList);
        listView.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add:
                Intent addRecordActivity = new Intent(getBaseContext(),
                        AddRecordActivity.class);
                startActivity(addRecordActivity);
                return true;
            case R.id.action_categories:
                Intent categoryActivity = new Intent(getBaseContext(),
                        CategoryActivity.class);
                startActivity(categoryActivity);
                return true;
            case R.id.action_stats_frequent:
                Intent frequentActivity = new Intent(getBaseContext(),
                        DatePickersActivity.class);
                frequentActivity.putExtra("mode",0);
                startActivity(frequentActivity);
                return true;
            case R.id.action_stats_longest:
                Intent longestActivity = new Intent(getBaseContext(),
                        DatePickersActivity.class);
                longestActivity.putExtra("mode",1);
                startActivity(longestActivity);
                return true;
            case R.id.action_stats_categories:
                Intent catsActivity = new Intent(getBaseContext(),
                        DatePickersActivity.class);
                catsActivity.putExtra("mode",2);
                startActivity(catsActivity);
                return true;
            case R.id.action_category_diagram:
                Intent diagramActivity = new Intent(getBaseContext(),
                        DatePickersActivity.class);
                diagramActivity.putExtra("mode",4);
                startActivity(diagramActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
