package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

class Values {
    Context context;
    int COLOR_RED;
    int COLOR_BLUE;
    int COLOR_YELLOW;
    int COLOR_GREY;
    int COLOR_GREY_DK;
    int COLOR_GREY_MD;
    int COLOR_GREY_BG;
    int DP_2;
    int DP_3;
    int DP_5;
    int DP_10;
    int DP_12;
    int DP_13;
    int DP_15;
    int DP_20;

    Values(Context context) {
        this.context = context;

        COLOR_RED = Color.parseColor("#FF6644");
        COLOR_BLUE = Color.parseColor("#3366CC");
        COLOR_YELLOW = Color.parseColor("#E2A56F");
        COLOR_GREY = Color.parseColor("#969696");
        COLOR_GREY_MD = Color.parseColor("#838383");
        COLOR_GREY_DK = Color.parseColor("#757575");
        COLOR_GREY_BG = Color.parseColor("#999999");
        DP_2 = dpToPx(2);
        DP_3 = dpToPx(3);
        DP_5 = dpToPx(5);
        DP_10 = dpToPx(10);
        DP_12 = dpToPx(12);
        DP_13 = dpToPx(13);
        DP_15 = dpToPx(15);
        DP_20 = dpToPx(20);
    }


    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
}
