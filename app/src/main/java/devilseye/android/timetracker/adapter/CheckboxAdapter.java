package devilseye.android.timetracker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

import devilseye.android.timetracker.R;
import devilseye.android.timetracker.model.Category;

public class CheckboxAdapter extends ArrayAdapter<Category> {
    private final List<Category> list;
    private final Activity context;

    public CheckboxAdapter(Activity context, List<Category> list) {
        super(context, R.layout.record_list, list);
        this.context = context;
        this.list = list;
    }

    public List<Category> getList() {
        return list;
    }

    static class ViewHolder {
        protected CheckBox category;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.check_category, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.category = (CheckBox) view.findViewById(R.id.category);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.category.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v ;
                for (Category category:list) {
                    if (category.get_id()==Integer.parseInt(cb.getTag().toString())){
                        category.setSelected(cb.isChecked());
                        break;
                    }
                }
            }
        });
        holder.category.setText(list.get(position).get_name());
        holder.category.setSelected(list.get(position).isSelected());
        holder.category.setTag(list.get(position).get_id());
        return view;
    }
}