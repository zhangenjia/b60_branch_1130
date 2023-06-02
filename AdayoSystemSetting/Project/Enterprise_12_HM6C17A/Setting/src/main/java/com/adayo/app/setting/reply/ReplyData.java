package com.adayo.app.setting.reply;

import android.content.ContentResolver;

public class ReplyData {
    private boolean replyButton;
    private String replySting;
    private ContentResolver contentResolver;

    private int condition = -1;

    public ReplyData(Boolean replyButton, String customReplyButton,ContentResolver contentResolver) {
        this.replyButton = replyButton;
       this. replySting = customReplyButton;
        if (replyButton == true) {
            condition = 1;
        } else if (replyButton == false && !"".equals(customReplyButton)) {
            condition = 2;
        } else if (replyButton == false && "".equals(customReplyButton)) {
            condition = 3;
        }
       this.contentResolver=contentResolver;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    public int getCondition() {
        return condition;
    }

    public boolean getReplyButton() {
        return replyButton;
    }

    public String getReplySting() {
        return replySting;
    }
}
