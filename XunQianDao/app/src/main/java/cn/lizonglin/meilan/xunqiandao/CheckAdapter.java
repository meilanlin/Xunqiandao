package cn.lizonglin.meilan.xunqiandao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lizl on 2017/5/12.
 */

public class CheckAdapter extends BaseAdapter {
    private List<CheckObject> list;
    private LayoutInflater inflater;

    public CheckAdapter(Context context, List<CheckObject> list) {
        inflater  = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void changeList(List<CheckObject> list) {
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.check_items, null);
            holder.stuid     = (TextView) convertView.findViewById(R.id.stuid);
            holder.classtime = (TextView) convertView.findViewById(R.id.classtime);
            holder.major     = (TextView) convertView.findViewById(R.id.major);
            holder.status    = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CheckObject listObject = list.get(position);
        holder.stuid.setText(listObject.getStuid());
        holder.classtime.setText(listObject.getClasstime());
        holder.major.setText(listObject.getMajor());
        holder.status.setText(listObject.getStatus());
        return convertView;
    }

    public class ViewHolder {
        ImageView icon;
        TextView stuid,classtime,major,status;
    }
}

