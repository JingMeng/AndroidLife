package com.camnter.hook.loadedapk.classloader.host;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * @author CaMnter
 */

@SuppressWarnings("DanglingJavadoc")
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getLayoutId() != 0) this.setContentView(this.getLayoutId());
        this.initViews(savedInstanceState);
        this.initData();
        this.initListeners();
    }


    /**
     * Fill in layout id
     *
     * @return layout id
     */
    protected abstract int getLayoutId();

    /**
     * Initialize the view in the layout
     *
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void initViews(Bundle savedInstanceState);

    /**
     * Initialize the View of the listener
     */
    protected abstract void initListeners();

    /**
     * Initialize the Activity data
     */
    protected abstract void initData();


    /**
     * Find the view by id
     *
     * @param id id
     * @param <V> V
     * @return V
     */
    @SuppressWarnings("unchecked")
    protected <V extends View> V findView(int id) {
        return (V) this.findViewById(id);
    }


    /**
     * @param intent The intent to start.
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }


    /**
     * @param intent The intent to start.
     * @param options Additional options for how the Activity should be started.
     */
    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
    }


    /**
     * @param intent The intent to start.
     * @param requestCode If >= 0, this code will be returned in
     * onActivityResult() when the activity exits.
     * @param options Additional options for how the Activity should be started.
     * See {@link Context#startActivity(Intent, Bundle)
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }


    /**
     * @param intent intent
     * @param requestCode requestCode
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }


    /**
     * Call this when your activity is done and should be closed.  The
     * ActivityResult is propagated back to whoever launched you via
     * onActivityResult().
     */
    @Override
    public void finish() {
        super.finish();
    }


    /*********
     * Toast *
     *********/

    public void showToast(String msg) {
        this.showToast(msg, Toast.LENGTH_SHORT);
    }


    public void showToast(String msg, int duration) {
        if (msg == null) return;
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            ToastUtils.show(this, msg, duration);
        } else {
            ToastUtils.show(this, msg, ToastUtils.LENGTH_SHORT);
        }
    }


    public void showToast(int resId) {
        this.showToast(resId, Toast.LENGTH_SHORT);
    }


    public void showToast(int resId, int duration) {
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            ToastUtils.show(this, resId, duration);
        } else {
            ToastUtils.show(this, resId, ToastUtils.LENGTH_SHORT);
        }
    }

    /*********
     * Toast *
     *********/

}
