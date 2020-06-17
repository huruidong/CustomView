package com.example.customviewdemo.pinnerGroupTitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.example.customviewdemo.R;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * 支持分组的ItemDecoration, 暂且只支持LinearLayoutManager
 * Created by angcyo on 2017-01-15.
 */

public class PinnerItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;

    private GroupCallBack mGroupCallBack;

    final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    final Paint     bgPaint   = new Paint();
    final RectF     rectF     = new RectF();
    final Rect      rect      = new Rect();

    public PinnerItemDecoration(Context context, GroupCallBack groupCallBack) {
        mContext = context;
        mGroupCallBack = groupCallBack;

        textPaint.setTextSize(20);
        textPaint.setColor(Color.RED);
        bgPaint.setColor(Color.BLUE);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mGroupCallBack == null) {
            return;
        }

        String groupText;
        String preGroupText;
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View                      view            = parent.getChildAt(i);
            final RecyclerView.LayoutParams layoutParams    = (RecyclerView.LayoutParams) view.getLayoutParams();
            final int                       adapterPosition = layoutParams.getViewAdapterPosition();
            groupText = mGroupCallBack.getGroupText(adapterPosition);
            if (TextUtils.isEmpty(groupText)) {
                continue;
            }
            if (adapterPosition == 0) {
                //第一个位置, 肯定是有分组信息的
                onGroupDraw(c, view, adapterPosition);
            } else {
                //上一个分组信息
                preGroupText = mGroupCallBack.getGroupText(adapterPosition - 1);
                //当前的分组信息
                if (!TextUtils.equals(preGroupText, groupText)) {
                    //如果和上一个分组信息不相等
                    onGroupDraw(c, view, adapterPosition);
                }
            }
        }
    }


    /**
     * 绘制分组信息
     */
    void onGroupDraw(Canvas canvas, View view, int position) {

        rectF.set(view.getLeft(), view.getTop() - mGroupCallBack.getGroupHeight(), view.getRight(),
                  view.getTop());
        canvas.drawRoundRect(rectF, 0, 0, bgPaint);

        final String letter = mGroupCallBack.getGroupText(position);
        textPaint.getTextBounds(letter, 0, letter.length(), rect);

        canvas.drawText(letter, view.getLeft() + 40/*paddingstart*/,
                        view.getTop() - (mGroupCallBack.getGroupHeight() - rect.height()) / 2,
                        textPaint);
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mGroupCallBack == null || parent.getChildCount() <= 0) {
            return;
        }

        boolean isHorizontal = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL;

        final View                      view            = parent.getChildAt(0);
        final RecyclerView.LayoutParams layoutParams    = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int                       adapterPosition = layoutParams.getViewAdapterPosition();
        String                          groupText       =
                mGroupCallBack.getGroupText(adapterPosition);
        if (TextUtils.isEmpty(groupText)) {
            return;
        }
        if (adapterPosition == 0) {
            //第一个位置, 肯定是有分组信息的
            if ((isHorizontal ? view.getLeft() : view.getTop()) <= 0) {
                onGroupOverDraw(c, view, adapterPosition, 0);
            } else {
                onGroupOverDraw(c, view, adapterPosition,
                                mGroupCallBack.getGroupHeight() - (isHorizontal ? view.getLeft() : view.getTop()));
            }
        } else {
            if (parent.getLayoutManager().getItemCount() > adapterPosition + 1) {
                //下一个分组信息
                String nextGroupText =
                        mGroupCallBack.getGroupText(adapterPosition + 1);
                //当前的分组信息
                final View nextView = parent.getChildAt(1);
                if (!TextUtils.equals(nextGroupText, groupText)) {
                    if ((isHorizontal ? nextView.getLeft() : nextView.getTop()) <= 0) {
                        onGroupOverDraw(c, view, adapterPosition, 0);
                    } else {
                        onGroupOverDraw(c, view, adapterPosition, Math.max(0,
                                                                           2 * mGroupCallBack.getGroupHeight() - (isHorizontal ? nextView.getLeft() : nextView.getTop())));
                    }
                } else {
                    onGroupOverDraw(c, view, adapterPosition, 0);
                }
            } else {
                onGroupOverDraw(c, view, adapterPosition, 0);
            }
        }
    }

    /**
     * 绘制悬浮信息
     *
     * @param offset 需要偏移的距离
     */
    void onGroupOverDraw(Canvas canvas, View view, int position, int offset) {

        rectF.set(view.getLeft(), -offset, view.getRight(),
                  mGroupCallBack.getGroupHeight() - offset);

        canvas.drawRoundRect(rectF, 0, 0, bgPaint);

        final String letter = mGroupCallBack.getGroupText(position);
        textPaint.getTextBounds(letter, 0, letter.length(), rect);

        canvas.drawText(letter, view.getLeft() + 40/*paddingstart*/,
                        (mGroupCallBack.getGroupHeight() + rect.height()) / 2 - offset, textPaint);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();//布局管理器
        if (!(layoutManager instanceof LinearLayoutManager)) {
            throw new IllegalArgumentException("暂不支持 " + layoutManager.getClass().getSimpleName());
        }

        if (mGroupCallBack == null) {
            return;
        }

        final RecyclerView.LayoutParams layoutParams    = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int                       adapterPosition = layoutParams.getViewAdapterPosition();
        String                          groupText       =
                mGroupCallBack.getGroupText(adapterPosition);
        if (TextUtils.isEmpty(groupText)) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (adapterPosition == 0) {
            //第一个位置, 肯定是有分组信息的
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                outRect.set(mGroupCallBack.getGroupHeight(), 0, 0, 0);
            } else {
                outRect.set(0, mGroupCallBack.getGroupHeight(), 0, 0);
            }
        } else {
            //上一个分组信息
            String preGroupText = mGroupCallBack.getGroupText(adapterPosition - 1);
            //当前的分组信息
            if (!TextUtils.equals(preGroupText, groupText)) {
                //如果和上一个分组信息不相等
                if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    outRect.set(mGroupCallBack.getGroupHeight(), 0, 0, 0);
                } else {
                    outRect.set(0, mGroupCallBack.getGroupHeight(), 0, 0);
                }
            }
        }
    }

    public interface GroupCallBack {
        /**
         * 返回分组的高度
         */
        int getGroupHeight();

        /**
         * 返回分组的文本，即标题显示内容
         */
        String getGroupText(int position);
    }


    private final static int[]    li_SecPosValue = {
            1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590
    };
    private final static String[] lc_FirstLetter = {
            "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "w", "x", "y", "z"
    };

    /**
     * 取得给定汉字的首字母,即声母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    public static String getFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().length() == 0) {
            return "";
        }
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");

        if (chinese.length() > 1) // 判断是不是汉字
        {
            int li_SectorCode   = (int) chinese.charAt(0); // 汉字区码
            int li_PositionCode = (int) chinese.charAt(1); // 汉字位码
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {
                for (int i = 0; i < 23; i++) {
                    if (li_SecPosCode >= li_SecPosValue[i] && li_SecPosCode < li_SecPosValue[i + 1]) {
                        chinese = lc_FirstLetter[i];
                        break;
                    }
                }
            } else // 非汉字字符,如图形符号或ASCII码
            {
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }

        return getAlpha(chinese.toUpperCase(Locale.getDefault()));
    }

    public static String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    /**
     * 字符串编码转换
     *
     * @param str           要转换编码的字符串
     * @param charsetName   原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    public static String conversionStr(String str, String charsetName, String toCharsetName) {
        try {
            str = new String(str.getBytes(charsetName), toCharsetName);
        } catch (UnsupportedEncodingException ex) {
            System.out.println("字符串编码转换异常：" + ex.getMessage());
        }
        return str;
    }

}
