package com.lanxingren.testjumptowechat;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by he_jhua on 2019/1/17.
 */
public class WeChatService extends AccessibilityService {
    private final String TAG = "WeChatService_TAG";

    /**
     * 微信主页面的“搜索”按钮id
     */
    private final String SEARCH_ID = "com.tencent.mm:id/ij";

    /**
     * 微信主页面bottom的“微信”按钮id
     */
    private final String WECHAT_ID = "com.tencent.mm:id/d3t";

    /**
     * 微信搜索页面的输入框id
     */
    private final String EDIT_TEXT_ID = "com.tencent.mm:id/ka";

    /**
     * 微信主页面ViePage的id
     */
    private final String VIEW_PAGE_ID = "com.tencent.mm:id/bko";

    /**
     * 微信主页面活动id
     */
    private String LAUNCHER_ACTIVITY_NAME = "com.tencent.mm.ui.LauncherUI";

    /**
     * 微信搜索页面活动id
     */
    private String SEARCH_ACTIVITY_NAME = "com.tencent.mm.plugin.fts.ui.FTSMainUI";

    /**
     * 微信备注组件id
     */
    private String USERNAME_ID = "com.tencent.mm:id/jw";

    private String LIST_VIEW_NAME = "android.widget.ListView";
    private String WECHAT_TEXT_ID = "com.tencent.mm:id/km";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.e(TAG, event.getEventType() + "");
        Log.e(TAG, event.getClassName() + "");

        // 只有从app进入微信才进行监听
        if (Constant.flag == 0) {
            return;
        }

        // 页面改变时需要延迟一段时间进行布局加载
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !LAUNCHER_ACTIVITY_NAME.equals(event.getClassName().toString()) && !SEARCH_ACTIVITY_NAME.equals(event.getClassName().toString())) {
            // 如果当前页面不是微信主页面也不是微信搜索页面，就模拟点击返回键
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            return;
        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && LAUNCHER_ACTIVITY_NAME.equals(event.getClassName().toString())) {
            List<AccessibilityNodeInfo> list = event.getSource().findAccessibilityNodeInfosByViewId(USERNAME_ID);
            if (list.size() > 0) {
                // 如果是微信主页面，但是是微信聊天页面，则模拟点击返回键
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                return;
            }
        }

        List<AccessibilityNodeInfo> searchNode = event.getSource().findAccessibilityNodeInfosByViewId(SEARCH_ID);
        List<AccessibilityNodeInfo> wechatNode = event.getSource().findAccessibilityNodeInfosByViewId(WECHAT_ID);
        List<AccessibilityNodeInfo> viewPageNode = event.getSource().findAccessibilityNodeInfosByViewId(VIEW_PAGE_ID);

        // 由于搜索控件在多个页面都有，所以还得判断是否在主页面
        if (searchNode.size() > 1 && viewPageNode.size() > 0) {
            // 点击“搜索”按钮
            if (searchNode.get(0).getParent().isClickable()) {
                searchNode.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
        } else if (searchNode.size() == 1) {
            // 如果在“我”页面，则进入“微信”页面
            for (AccessibilityNodeInfo info : wechatNode) {
                if (info.getText().toString().equals("微信") && !info.isChecked()) {

                    if (info.getParent().isClickable()) {
                        info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                    break;
                }
            }
        }

        // 当前页面是搜索页面
        if (SEARCH_ACTIVITY_NAME.equals(event.getClassName().toString())) {

            List<AccessibilityNodeInfo> editTextNode = event.getSource().findAccessibilityNodeInfosByViewId(EDIT_TEXT_ID);

            if (editTextNode.size() > 0) {

                /*ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", "tianheng48");
                clipboard.setPrimaryClip(clip);
                //焦点（n是AccessibilityNodeInfo对象）
                editTextNode.get(0).performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                ////粘贴进入内容
                editTextNode.get(0).performAction(AccessibilityNodeInfo.ACTION_PASTE);*/

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 输入框内输入查询的微信号
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Constant.wechatId);
                editTextNode.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }

        } else if (LIST_VIEW_NAME.equals(event.getClassName().toString())) {
            // 如果监听到了ListView的内容改变，则找到查询到的人，并点击进入
            List<AccessibilityNodeInfo> textNodeList = event.getSource().findAccessibilityNodeInfosByText("微信号: " + Constant.wechatId);
            if (textNodeList.size() > 0) {
                textNodeList.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                // 模拟点击之后将暂存值置空，类似于取消监听
                Constant.flag = 0;
                Constant.wechatId = null;
            }
        }

    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "connected");
    }
}
