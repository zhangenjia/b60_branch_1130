package com.adayo.app.setting.reply;

public abstract class BaseHandler {
    protected final static int AUTOMATIC_REPLY_BUTTON = 1;
    protected final static int CUSTOM_REPLY_BUTTON_NOT_NULL = 2;
    protected final static int CUSTOM_REPLY_BUTTON_NULL = 3;
    private BaseHandler mNextBaseHandler;
    private int conditions;

    public BaseHandler(int conditions) {
        this.conditions = conditions;
    }

    public void setNextBaseHandler(BaseHandler nextBaseHandler) {
        this.mNextBaseHandler = nextBaseHandler;
    }

    protected abstract void handleLeave(ReplyData leave);

    public final void submit(ReplyData leave) {
        if (this.mNextBaseHandler != null&& leave.getCondition()!= conditions) {
            this.mNextBaseHandler.submit(leave);
        } else if(leave.getCondition()==conditions){
            this.handleLeave(leave);
        }
    }
}

