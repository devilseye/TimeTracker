package devilseye.android.timetracker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import devilseye.android.timetracker.R;
import devilseye.android.timetracker.model.Category;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private final List<Category> list;
    private final Activity context;

    public CategoryAdapter(Activity context, List<Category> list) {
        super(context, R.layout.record_list, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView category;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.category_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.category = (TextView) view.findViewById(R.id.category);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.category.setText(list.get(position).get_name());
        return view;
    }
}
