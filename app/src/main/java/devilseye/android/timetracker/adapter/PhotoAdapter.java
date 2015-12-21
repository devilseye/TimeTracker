package devilseye.android.timetracker.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.List;

import devilseye.android.timetracker.R;
import devilseye.android.timetracker.model.Photo;

public class PhotoAdapter extends ArrayAdapter<Photo> {
    private final List<Photo> list;
    private final Activity context;

    public PhotoAdapter(Activity context, List<Photo> list) {
        super(context, R.layout.record_list, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.image_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        Bitmap bitmap=Photo.bytesToBitmap(list.get(position).get_image());
        holder.image.setImageBitmap(bitmap);
        return view;
    }
}
