package devilseye.android.timetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import java.util.List;

import de.timroes.android.listview.EnhancedListView;
import devilseye.android.timetracker.adapter.CategoryAdapter;
import devilseye.android.timetracker.model.Category;

public class CategoryActivity extends Activity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        dbHelper=new DBHelper(this);
        List<Category> categories=dbHelper.getAllCategories();
        CategoryAdapter categoryAdapter=new CategoryAdapter(this,categories);
        EnhancedListView listView=(EnhancedListView)findViewById(R.id.categories);
        listView.setAdapter(categoryAdapter);
        listView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
                final CategoryAdapter listAdapter = (CategoryAdapter) listView.getAdapter();
                final Category item = listAdapter.getItem(position);
                listAdapter.remove(item);
                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        listAdapter.insert(item, position);
                        EnhancedListView listView = (EnhancedListView) findViewById(R.id.images);
                        listView.setAdapter(listAdapter);
                    }

                    @Override
                    public String getTitle() {
                        return getString(R.string.deleted) + item.get_name();
                    }

                    @Override
                    public void discard() {
                        dbHelper.deleteCategory(item);
                    }
                };
            }
        });
        listView.setRequireTouchBeforeDismiss(false);
        listView.enableSwipeToDismiss();
    }

    public void updateList(){
        List<Category> categories=dbHelper.getAllCategories();
        EnhancedListView listView = (EnhancedListView) findViewById(R.id.categories);
        CategoryAdapter listAdapter = new CategoryAdapter(this,categories);
        listView.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_category) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.enter_category));

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbHelper.addCategory(new Category(input.getText().toString()));
                    updateList();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
