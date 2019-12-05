package com.heaven7.core.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * the permission helper to request a list of permissions.
 * <p>if any permission request failed. the permission list of request is end and call callback right now.
 * Otherwise util the all permission request success.</p>
 * Created by heaven7 on 2016/7/25.
 */
public class PermissionHelper {

    private final Activity mActivity;

    private ICallback mCallback;
    private PermissionParam[] mParams;

    /**
     * the check index for verify loop
     */
    private int mCheckingIndex;

    /**
     * the permission callback
     */
    public interface ICallback {

        /**
         * called when request permissions done or failed.
         *
         * @param requestPermission the permission to request
         * @param requestCode       the request code
         * @param success           if success to request the permission group.
         */
        void onRequestPermissionResult(String requestPermission, int requestCode, boolean success);

        /**
         * callback on permission had been refused before.
         *
         * @param requestCode       the request code
         * @param requestPermission the request permission
         * @param task              the task to run when you want to request permission again. this task often be called on user click confirm.
         * @return true if handled success. when return true the permission chain will be broken until you run the task.
         * @since 1.1.5
         */
        boolean handlePermissionHadRefused(String requestPermission, int requestCode, Runnable task);
    }

    public PermissionHelper(Activity activity) {
        if (activity == null) {
            throw new NullPointerException();
        }
        this.mActivity = activity;
    }

    /**
     * begin request the permission group
     *
     * @param params   the permission params to request ,but must in order
     * @param callback the callback
     */
    public void startRequestPermission(PermissionParam[] params, ICallback callback) {
        if (params == null || callback == null) {
            throw new NullPointerException();
        }
        this.mParams = params;
        this.mCallback = callback;
        this.mCheckingIndex = 0;
        requestPermissionImpl();
    }

    /**
     * begin request the permission group
     *
     * @param permission  the permissionto request and must in order
     * @param requestCode the request codes , but must in order and  match the requestPermissions
     * @param callback    the callback
     */
    public void startRequestPermission(String permission, int requestCode, ICallback callback) {
        startRequestPermission(new String[]{permission}, new int[]{requestCode}, callback);
    }

    /**
     * begin request the permission group
     *
     * @param requestPermissions the permissions to request and must in order
     * @param requestCodes       the request codes , but must in order and  match the requestPermissions
     * @param callback           the callback
     */
    public void startRequestPermission(String[] requestPermissions, int[] requestCodes, ICallback callback) {
        if (requestPermissions == null || requestCodes == null) {
            throw new NullPointerException();
        }
        if (requestPermissions.length != requestCodes.length) {
            throw new IllegalArgumentException("caused by requestPermissions.length != requestCodes.length");
        }
        int size = requestPermissions.length;
        PermissionParam[] params = new PermissionParam[size];
        for (int i = 0; i < size; i++) {
            params[i] = new PermissionParam(requestPermissions[i], requestCodes[i]);
        }
        startRequestPermission(params, callback);
    }

    /**
     * do begin request permission.
     */
    protected void requestPermissionImpl() {
        final Activity activity = this.mActivity;
        final PermissionParam permissionParam = mParams[mCheckingIndex];

        if (ContextCompat.checkSelfPermission(activity, permissionParam.requestPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionParam.requestPermission}, permissionParam.requestCode);
        } else {
            if (Build.VERSION.SDK_INT < 23 || permissionParam.verifier == null) {
                checkNext(permissionParam, true);
            } else {
                //check third app intercept
                checkNext(permissionParam, permissionParam.verifier.verify(permissionParam.params));
            }
        }
    }

    /**
     * check and do next with permission
     *
     * @param permissionParam the permission param
     * @param success         the result of check the current permission
     */
    private void checkNext(PermissionParam permissionParam, boolean success) {
        if (!success) {
            mCallback.onRequestPermissionResult(permissionParam.requestPermission, permissionParam.requestCode, false);
            reset();
            return;
        }
        //check request end
        if (mParams.length - 1 > mCheckingIndex) {
            //request next
            mCheckingIndex += 1;
            requestPermissionImpl();
        } else {
            //request end
            mCallback.onRequestPermissionResult(permissionParam.requestPermission, permissionParam.requestCode, true);
            reset();
        }
    }

    public void reset() {
        mCheckingIndex = 0;
        mParams = null;
        mCallback = null;
    }

    /**
     * for activity call.
     *
     * @param requestCode  the request code
     * @param permissions  the permissions
     * @param grantResults the grant result
     */
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, int[] grantResults) {
        if (mParams != null) {
            final PermissionParam permissionParam = mParams[mCheckingIndex];
            if (permissionParam.requestCode == requestCode) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //have refuse then
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissionParam.requestPermission)
                            && mCallback.handlePermissionHadRefused(permissionParam.requestPermission, requestCode, new Runnable() {
                        @Override
                        public void run() {
                            ActivityCompat.requestPermissions(mActivity, permissions, requestCode);
                        }
                    })) {
                        return;
                    }
                }
                checkNext(permissionParam, grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    /**
     * the permission param
     */
    public static class PermissionParam {
        final String requestPermission;
        final int requestCode;
        /**
         * the exception verifier , when we want to check permission by ourselves.
         * this is caused by some third app intercept the permission.
         */
        final ExceptionVerifier verifier;
        /**
         * the params to execute verification
         */
        final Object params;

        /**
         * @param requestPermission the permission to request
         * @param requestCode       the request code
         */
        public PermissionParam(String requestPermission, int requestCode) {
            this(requestPermission, requestCode, null);
        }

        /**
         * create a permission param by the param
         *
         * @param requestPermission the permission
         * @param requestCode       the request code
         * @param verifier          the exception verifier
         * @param params            the extra params to execute verification
         * @param <Prams>           the extra params to execute verification
         * @param <Result>          the result of execute verification
         */
        public <Prams, Result> PermissionParam(String requestPermission, int requestCode, ExceptionVerifier<Prams, Result> verifier, Prams... params) {
            if (requestPermission == null) {
                throw new IllegalArgumentException("requestPermission can't be null");
            }
            this.requestPermission = requestPermission;
            this.requestCode = requestCode;
            this.verifier = verifier;
            this.params = params;
        }
    }

/**
 *  boolean success = false;
 try {
 doDemoAudio();
 success = true;
 } catch (Exception e) {
 mCallback.onRequestAudioFailed(mRequestedCamera);
 mRequestedCamera = false;
 }finally {
 if(success) {
 mAudioChecked = true;
 doWithRequestAudioSuccess();
 }
 }
 */
}
