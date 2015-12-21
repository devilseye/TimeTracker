package devilseye.android.timetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import de.timroes.android.listview.EnhancedListView;
import devilseye.android.timetracker.adapter.PhotoAdapter;
import devilseye.android.timetracker.model.Photo;
import devilseye.android.timetracker.model.Record;

public class RecordActivity extends Activity {

    final int PICK_IMAGE=1;
    final int MAX_IMAGE_SIZE=1048576;
    DBHelper dbHelper;
    Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        int record_id = getIntent().getIntExtra("record_id", 0);
        dbHelper =new DBHelper(this);
        record=dbHelper.getRecord(record_id);
        setTitle(record.get_category().get_name());
        TextView idText = (TextView)findViewById(R.id.record_id);
        idText.append(" " + record_id);
        TextView description = (TextView)findViewById(R.id.description);
        description.setText(record.get_description());
        TextView dateStart = (TextView)findViewById(R.id.dateStart);
        dateStart.append(" "+Record.getStringDateTime(record.get_timeStart()));
        TextView dateEnd = (TextView)findViewById(R.id.dateEnd);
        dateEnd.append(" "+Record.getStringDateTime(record.get_timeEnd()));
        TextView duration = (TextView)findViewById(R.id.duration);
        duration.append(" "+record.get_timeMinutes()+" "+getString(R.string.mins));
        final EnhancedListView images=(EnhancedListView)findViewById(R.id.images);
        List<Photo> photos=dbHelper.getRecordPhotos(record_id);
        PhotoAdapter adapter=new PhotoAdapter(this,photos);
        images.setAdapter(adapter);
        images.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
                final PhotoAdapter listAdapter = (PhotoAdapter) listView.getAdapter();
                final Photo item = listAdapter.getItem(position);
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
                        return getString(R.string.deleted) + item.get_id();
                    }

                    @Override
                    public void discard() {
                        dbHelper.deletePhoto(item);
                    }
                };
            }
        });
        images.setRequireTouchBeforeDismiss(false);
        images.enableSwipeToDismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_photo) {
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_IMAGE);
            } catch (Exception ex) {
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.errorNoApp);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE) {
            if (data != null) {
                Uri savedUri = data.getData();
                if (savedUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),savedUri);
                        while (bitmap.getByteCount()>MAX_IMAGE_SIZE){
                            bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth()/2), (bitmap.getHeight()/2), true);
                        }
                        if (bitmap.getByteCount()<MAX_IMAGE_SIZE) {
                            Photo photo = new Photo(Photo.bitmapToBytes(bitmap), record);
                            dbHelper.addPhoto(photo);
                            updateList();
                        }
                    } catch (IOException e) {
                        Log.e("IO",e.getMessage());
                    }
                }
            }
        }
    }

    public void updateList(){
        List<Photo> photoList=dbHelper.getRecordPhotos(record.get_id());
        EnhancedListView listView = (EnhancedListView) findViewById(R.id.images);
        PhotoAdapter listAdapter = new PhotoAdapter(this,photoList);
        listView.setAdapter(listAdapter);
    }
}
