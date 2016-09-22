package com.example.wuht.learnpath;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.btn_start)
    Button mBtnStart;
    @InjectView(R.id.btn_end)
    Button mBtnEnd;
    @InjectView(R.id.searchview)
    SearchView mSearchview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.btn_start, R.id.btn_end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                mSearchview.setState(SearchView.SearchState.START);
                break;
            case R.id.btn_end:
                mSearchview.setState(SearchView.SearchState.END);
                break;
        }
    }
}
