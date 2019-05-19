package com.example.lenovo.elf;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.elf.util.Loader;
import com.example.lenovo.elf.util.SongListModel;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 1;
    // 正在加载

    public int getLoadState() {//提供外部获得状态，便于在LoadMore方法里进行判断
        return loadState;
    }

    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    private List<SongListModel> dataList;
    private Context context;
    private OnClickListener mlistener;
    private Loader mloader;

    public Loader getMloader() {//给外部设置get方法，方便设置loader占位图
        return mloader;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public Adapter(List<SongListModel> songListModels, Context context) {
        this.dataList = songListModels;
        this.context = context;
        mloader = new Loader(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
LinearLayout linearLayout;
      ImageView imageViewBig;
        ImageView imageViewSmall;
        TextView textView1;
        TextView textView2;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
           linearLayout=itemView.findViewById(R.id.collect_line);
            imageViewBig = itemView.findViewById(R.id.coll_imag);
            imageViewSmall = itemView.findViewById(R.id.coll_small);
            textView1 = itemView.findViewById(R.id.text1);
            textView2 = itemView.findViewById(R.id.text22);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = itemView.findViewById(R.id.progress);
            tvLoading = itemView.findViewById(R.id.text3);
            llEnd = itemView.findViewById(R.id.linear);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.collectionitem_layout, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        } else if (i == TYPE_FOOTER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footlayout, viewGroup, false);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return footViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            if (mlistener != null) {
                viewHolder1.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mlistener.onClick(viewHolder.getAdapterPosition());
                    }
                });
                viewHolder1.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mlistener.onLongClick(viewHolder.getAdapterPosition());
                        return false;
                    }
                });
            }
            SongListModel data = dataList.get(viewHolder.getAdapterPosition());
            String picURL = data.getPicUrl();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mloader.bindBitmap(picURL, viewHolder1.imageViewBig);
                mloader.bindBitmap(picURL, viewHolder1.imageViewSmall);

            }
            viewHolder1.textView1.setText(data.getSongName());
            viewHolder1.textView2.setText(data.getSingerName());

        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.llEnd.setVisibility(View.INVISIBLE);
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    break;
                case LOADING_END: // 加载到底
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }


        }


    }

//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
//
//    }


    @Override
    public int getItemCount() {

        return dataList.size()+1;
    }

    void SetOnItemCickListener(OnClickListener listener) {
        this.mlistener = listener;

    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();

    }
}
