package cashkaro.com.listwhodoesnotcall;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yasar on 4/8/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Row> {

    private static final String TAG = "RecyclerViewAdapter";


    public RecyclerViewAdapter(Context context, List<Contact> list) {
        this.list = list;
        this.context = context;
        this.onCheckBoxClick = (OnCheckBoxClick) context;
    }

    public RecyclerViewAdapter(Fragment context, List<Contact> list) {
        this.list = list;
        this.context = context.getActivity();
        this.onCheckBoxClick = (OnCheckBoxClick) context;
    }

    private List<Contact> list;
    private Context context;
    private OnCheckBoxClick onCheckBoxClick;


    public void updateList(List<Contact> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();

    }

    @Override
    public Row onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row, parent, false);
        return new Row(view);
    }

    @Override
    public void onBindViewHolder(final Row holder, final int position) {
        final Contact visitorInfo = list.get(position);

        holder.name.setText(visitorInfo.getPhoneNumber());
        holder.number.setText(visitorInfo.getName());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e(TAG, "onCheckedChanged: " + b);
                if (b) {
                    onCheckBoxClick.OnItemClickAdd(visitorInfo);
                } else {
                    onCheckBoxClick.OnItemClickRemove(visitorInfo);
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Row extends RecyclerView.ViewHolder {

        private TextView name, number;
        private CheckBox checkBox;

        public Row(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            checkBox = (CheckBox) itemView.findViewById(R.id.isinorout);
        }
    }

    interface OnCheckBoxClick {
        void OnItemClickAdd(Contact contact);

        void OnItemClickRemove(Contact contact);

    }

}