package com.xiaositv.testjumptowechat;

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

    private final String SEARCH_ID = "com.tencent.mm:id/ij";

    private final String WECHAT_ID = "com.tencent.mm:id/d3t";

    private final String EDIT_TEXT_ID = "com.tencent.mm:id/ka";

    private String WECHAT_TEXT_ID = "com.tencent.mm:id/km";

    private String LAUNCHER_ACTIVITY_NAME = "com.tencent.mm.ui.LauncherUI";

    private String SEARCH_ACTIVITY_NAME = "com.tencent.mm.plugin.fts.ui.FTSMainUI";

    private String LIST_VIEW_NAME = "android.widget.ListView";

    /**
     * 微信备注组件id
     */
    private String USERNAME_ID = "com.tencent.mm:id/jw";


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.e(TAG, event.getEventType() + "");
        Log.e(TAG, event.getClassName() + "");

        if (Constant.flag == 0) {
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !LAUNCHER_ACTIVITY_NAME.equals(event.getClassName().toString()) && !SEARCH_ACTIVITY_NAME.equals(event.getClassName().toString())) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            return;
        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && LAUNCHER_ACTIVITY_NAME.equals(event.getClassName().toString())) {
            List<AccessibilityNodeInfo> list = event.getSource().findAccessibilityNodeInfosByViewId(USERNAME_ID);
            if (list.size() > 0) {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                return;
            }
        }

        List<AccessibilityNodeInfo> searchNode = event.getSource().findAccessibilityNodeInfosByViewId(SEARCH_ID);
        List<AccessibilityNodeInfo> wechatNode = event.getSource().findAccessibilityNodeInfosByViewId(WECHAT_ID);

        if (searchNode.size() == 2) {
            // 点击“搜索”按钮
            if (searchNode.get(0).getParent().isClickable()) {
                searchNode.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else if (searchNode.size() == 1) {
            // 如果在“我”页面，则进入“微信”页面
            for (AccessibilityNodeInfo info : wechatNode) {
                if (info.getText().toString().equals("微信") && !info.isChecked()) {

                    if (info.getParent().isClickable()) {
                        info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    break;
                }
            }
        }

        if (SEARCH_ACTIVITY_NAME.equals(event.getClassName().toString())) {

            List<AccessibilityNodeInfo> editTextNode = event.getSource().findAccessibilityNodeInfosByViewId(EDIT_TEXT_ID);

            if (editTextNode.size() > 0) {
                //                ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                //                ClipData clip = ClipData.newPlainText("text", "tianheng48");
                //                clipboard.setPrimaryClip(clip);
                //                //焦点（n是AccessibilityNodeInfo对象）
                //                editTextNode.get(0).performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //                ////粘贴进入内容
                //                editTextNode.get(0).performAction(AccessibilityNodeInfo.ACTION_PASTE);

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Constant.wechatId);
                editTextNode.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }

        } else if (LIST_VIEW_NAME.equals(event.getClassName())) {
            List<AccessibilityNodeInfo> textNodeList = event.getSource().findAccessibilityNodeInfosByText("微信号: " + Constant.wechatId);

            if (textNodeList.size() > 0) {
                textNodeList.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                Constant.flag = 0;
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
