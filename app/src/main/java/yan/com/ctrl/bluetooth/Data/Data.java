package yan.com.ctrl.bluetooth.Data;

import android.app.Application;

/**
 * Created by yan on 2015/10/14.
 */
public class Data extends Application {
    private boolean isok;

    public boolean getisok(){
        return isok;
    }
    public void setIsok(boolean isok){
        this.isok = isok;
    }

    @Override
    public void onCreate() {
        isok = false;
        super.onCreate();
    }
}
