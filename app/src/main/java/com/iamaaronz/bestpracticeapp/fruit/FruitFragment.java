package com.iamaaronz.bestpracticeapp.fruit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamaaronz.bestpracticeapp.R;

import java.util.ArrayList;
import java.util.List;

public class FruitFragment extends Fragment {

    List<Fruit> mFruitList = new ArrayList<>();

    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fruits, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_fruits);
        RecyclerView.Adapter<FruitAdapter.ViewHolder> adapter = new FruitAdapter(mFruitList);
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = null;

        return view;
    }
}

class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    List<Fruit> mFruits;

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        ImageView imageView;

        TextView textView;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view_fruit);
            imageView = view.findViewById(R.id.image_view_fruit);
            textView = view.findViewById(R.id.text_view_fruit);
        }
    }

    public FruitAdapter(List<Fruit> fruits) {
        mFruits = fruits;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fruit fruit = mFruits.get(position);
        holder.imageView.setImageResource(fruit.id);
        holder.textView.setText(fruit.desc);
    }

    @Override
    public int getItemCount() {
        return mFruits.size();
    }
}

class Fruit {

    int id;

    String desc;

    Fruit(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}