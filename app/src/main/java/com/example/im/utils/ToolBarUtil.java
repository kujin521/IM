package com.example.im.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.im.R;

import java.util.ArrayList;
import java.util.List;

public class ToolBarUtil {

    private List<TextView> mTextViews=new ArrayList<>();

    public void createToolBar(LinearLayout layout,String[] toobarstr,int[] icons){
        for (int i = 0; i < toobarstr.length; i++) {
            TextView tv= (TextView) View.inflate(layout.getContext(), R.layout.text_toolbar,null);
            tv.setText(toobarstr[i]);
            tv.setCompoundDrawablesWithIntrinsicBounds(0,icons[i],0,0);

            int width=0;
            int height= LinearLayout.LayoutParams.MATCH_PARENT;
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(width,height);
            params.weight=1;
            layout.addView(tv,params);

            mTextViews.add(tv);

            //设置点击事件
            int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**
                     * 不同模块之间传值，需要接口回调
                     * 用接口对象调用接口方法
                     */
                    onToolBarLinstener.onToolBarClick(finalI);

                }
            });
        }
    }

    public void change(int postion){
        //还原所有的颜色
        for (TextView mTextView : mTextViews) {
            mTextView.setSelected(false);
        }

        mTextViews.get(postion).setSelected(true);
    }


    //创建接口和接口方法
    public interface OnToolBarLinstener{
        void onToolBarClick(int postion);
    }
    //定义接口变量
    OnToolBarLinstener onToolBarLinstener;
    //暴露一个公共方法
    public void setOnToolBarLinstener(OnToolBarLinstener onToolBarLinstener) {
        this.onToolBarLinstener = onToolBarLinstener;
    }
}
