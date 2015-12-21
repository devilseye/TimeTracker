package devilseye.android.timetracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import devilseye.android.timetracker.model.Category;
import devilseye.android.timetracker.model.Record;

public class AddRecordActivity extends Activity {

    DBHelper dbHelper;
    List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        dbHelper =new DBHelper(this);
        categories = dbHelper.getAllCategories();
        String[] data=new String[categories.size()];
        for (int i=0;i<categories.size();i++){
            data[i]=categories.get(i).get_name();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner=(Spinner) findViewById(R.id.category);
        spinner.setAdapter(adapter);
        spinner.setPrompt(getString(R.string.choose_category));
        TimePicker timeStart=(TimePicker) findViewById(R.id.timeStart);
        timeStart.setIs24HourView(true);
        TimePicker timeEnd=(TimePicker) findViewById(R.id.timeEnd);
        timeEnd.setIs24HourView(true);
        DatePicker dateStart=(DatePicker) findViewById(R.id.dateStart);
        DatePicker dateEnd=(DatePicker) findViewById(R.id.dateEnd);
        if (timeStart.getCurrentHour()==23){
            dateEnd.updateDate(dateStart.getYear(),dateStart.getMonth(),dateStart.getDayOfMonth()+1);
        }
        timeEnd.setCurrentHour(timeStart.getCurrentHour()+1);
        timeEnd.setCurrentMinute(timeStart.getCurrentMinute());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_save_record){

            Spinner spinner=(Spinner) findViewById(R.id.category);
            TimePicker timeStart=(TimePicker) findViewById(R.id.timeStart);
            TimePicker timeEnd=(TimePicker) findViewById(R.id.timeEnd);
            DatePicker dateStart=(DatePicker) findViewById(R.id.dateStart);
            DatePicker dateEnd=(DatePicker) findViewById(R.id.dateEnd);
            EditText descriptionText=(EditText) findViewById(R.id.description);

            Record record=new Record();
            record.set_category(categories.get(spinner.getSelectedItemPosition()));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dateStartValue=new Date();
            Date dateEndValue=new Date();
            try {
                dateStartValue = format.parse(dateStart.getYear()+"-"
                        +(dateStart.getMonth()+1)+"-"
                        +dateStart.getDayOfMonth()
                        +" "
                        +timeStart.getCurrentHour()+":"
                        +timeStart.getCurrentMinute());
                dateEndValue = format.parse(dateEnd.getYear()+"-"
                        +(dateEnd.getMonth()+1)+"-"
                        +dateEnd.getDayOfMonth()
                        +" "
                        +timeEnd.getCurrentHour()+":"
                        +timeEnd.getCurrentMinute());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            record.set_timeStart(dateStartValue);
            record.set_timeEnd(dateEndValue);
            record.set_description(descriptionText.getText().toString());
            dbHelper.addRecord(record);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
