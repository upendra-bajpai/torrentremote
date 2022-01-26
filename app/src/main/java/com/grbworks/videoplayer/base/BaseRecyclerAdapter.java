package com.grbworks.videoplayer.base;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*public class BaseRecyclerAdapter {
}*/
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<T> items;

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent);

    public abstract void onBindData(RecyclerView.ViewHolder holder, T val);

    public BaseRecyclerAdapter(Context context, ArrayList<T> items){
        this.context = context;
        this.items = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = setViewHolder(parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindData(holder,items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems( ArrayList<T> savedCardItemz){
        items = savedCardItemz;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public T getItem(int position){
        return items.get(position);
    }


}


/**
 * how to use this generic adapter
 */
/*adapter = new BaseRecyclerAdapter<DataModel>(context,modelList) {
@Override
public RecyclerView.ViewHolder setViewHolder(ViewGroup parent) {
final View view =           LayoutInflater.from(context).inflate(R.layout.item_view_holder, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(context, view);
        return viewHolder;
        }

@Override
public void onBindData(RecyclerView.ViewHolder holder1, DataModel val) {
        DataModel userModel = val;

        ItemViewHolder holder = (ItemViewHolder)holder1;
        holder.name.setText(userModel.getName());
        holder.fatherName.setText(userModel.getFatherName());
        }
        };
        mRecyclerView.setAdapter(adapter);*/
