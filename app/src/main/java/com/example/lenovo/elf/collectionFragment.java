package com.example.lenovo.elf;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lenovo.elf.util.Collection;
import com.example.lenovo.elf.util.SongListModel;

import java.util.ArrayList;
import java.util.List;

public class collectionFragment extends Fragment {
private List<SongListModel> modelList= modelList=new ArrayList<>();
private  Adapter adapter;
   private RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   // modelList=Collection.listModels;
        if (Collection.listModels.size()==0){
            Toast.makeText(getActivity(),"暂无收藏",Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i <10 ; i++) {
          if (i>=Collection.listModels.size()){
              break;
          }
            modelList.add(Collection.listModels.get(i));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.mycollectionlayout,container,false);
        adapter=new Adapter(modelList,getActivity());
        recyclerView=view.findViewById(R.id.rec);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {

            @Override
            public void onLoadMore() {
                if (adapter.getLoadState()!=adapter.LOADING){//判断，正在加载中的话再滑动就不再加载了
                    adapter.setLoadState(adapter.LOADING);//使footView出现
                    for (int i = modelList.size(); i <modelList.size()+10 ; i++) {
                        if (i-1>Collection.listModels.size()){
                            break;
                        }
                        modelList.add(Collection.listModels.get(i));
                    }

                }
                            adapter.setLoadState(adapter.LOADING_COMPLETE);
                             adapter.setLoadState(adapter.LOADING_END);

            }
        });

adapter.SetOnItemCickListener(new OnClickListener() {
    @Override
    public void onClick(int position) {
        SongListModel songListModel=modelList.get(position);


    }

    @Override
    public void onLongClick(int position) {

    }
});
        return  view;

    }


}
