package devilseye.android.timetracker.adapter;

import android.app.Activity;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import devilseye.android.timetracker.R;
import devilseye.android.timetracker.model.Record;

public class RecordAdapter extends ArrayAdapter<Record> {
    private final List<Record> list;
    private final Activity context;

    public RecordAdapter(Activity context, List<Record> list) {
        super(context, R.layout.record_list, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView category;
        protected TextView time;
        protected TextView minutes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.record_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.category = (TextView) view.findViewById(R.id.category);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.minutes = (TextView)view.findViewById(R.id.minutes);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.category.setText(list.get(position).get_category().get_name());
        String timeStart=Record.getStringDateTime(list.get(position).get_timeStart());
        String timeEnd=Record.getStringDateTime(list.get(position).get_timeEnd());
        holder.time.setText(timeStart+" / "+timeEnd);
        holder.minutes.setText(list.get(position).get_timeMinutes() + " " + context.getString(R.string.mins));
        return view;
    }
}
