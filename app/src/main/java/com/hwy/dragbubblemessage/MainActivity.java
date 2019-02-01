package com.hwy.dragbubblemessage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ListView;
import android.widget.Toast;

import com.hwy.adapter.list.SimpleListAdapter;
import com.hwy.adapter.list.ViewHolder;
import com.hwy.dragbubble.DragBubbleParams;
import com.hwy.dragbubble.DragBubbleView;
import com.hwy.dragbubble.OnStateListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    private List<String> mDatas;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        mContext = this;

        DragBubbleView.attach(findViewById(R.id.tv_message_count), this, new OnStateListener() {
            @Override
            public void onRollback(View targetView) {
                showToast("TextView 执行了回弹");
            }

            @Override
            public void onDismiss(View targetView) {
                showToast("TextView 执行了销毁");
            }
        });


        DragBubbleParams params = new DragBubbleParams();
        params.setColor(Color.parseColor("#FF6600"))
                .setInterpolator(new BounceInterpolator())
                .setDuration(300)
                .setDragMaxThreshold(400);

        DragBubbleView.attach(findViewById(R.id.iv_image_view), this, params, new OnStateListener() {
            @Override
            public void onRollback(View targetView) {
                showToast("ImageView  执行了回弹");
            }

            @Override
            public void onDismiss(View targetView) {
                showToast("ImageView  执行了销毁");
            }
        });

        init();
        mListView = findViewById(R.id.list_view);

        mListView.setAdapter(new SimpleListAdapter<String>(this, mDatas, R.layout.adapter_chat_record) {
            @Override
            public void convert(ViewHolder holder, final String data, int position) {
                holder.setText(R.id.tv_name, data);
                DragBubbleView.attach(holder.getView(R.id.tv_message_count), mContext, new OnStateListener() {
                    @Override
                    public void onRollback(View targetView) {
                        showToast(data + " 执行了回弹");
                    }

                    @Override
                    public void onDismiss(View targetView) {
                        showToast(data + " 执行了销毁");
                    }
                });
            }
        });


    }

    private void init() {
        mDatas = new ArrayList<>();

        int count = 20;

        for (int i = 0; i < count; i++) {
            mDatas.add("position = " + i);
        }
    }

    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}
