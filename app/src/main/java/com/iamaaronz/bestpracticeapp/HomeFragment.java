package com.iamaaronz.bestpracticeapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    public FloatingActionButton mFab;

    public Fragment setFab(@Nullable FloatingActionButton fab) {
        mFab = fab;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (mFab != null) {
            mFab.setBackgroundTintList(
                            ResourcesCompat.getColorStateList(getResources(), R.color.colorAccent, MyApplication.getContext().getTheme()));
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View fabView) {
                    NestedScrollView nestedScrollView = view.findViewById(R.id.scroll_view_home);
                    if (nestedScrollView != null) {
                        nestedScrollView.scrollTo(0, 0);
                    }
                }
            });
        }

        return view;
    }
}


