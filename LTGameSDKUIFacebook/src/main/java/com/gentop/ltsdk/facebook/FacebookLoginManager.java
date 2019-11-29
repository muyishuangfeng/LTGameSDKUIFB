package com.gentop.ltsdk.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.FacebookSdkNotInitializedException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gentop.ltsdk.common.impl.OnLoginSuccessListener;
import com.gentop.ltsdk.common.manager.LoginResultManager;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;


public class FacebookLoginManager {

    private static CallbackManager mFaceBookCallBack;


    /**
     * 初始化
     */
    public static void initFaceBook(final Context context, String mFacebookID, final String baseUrl,
                                    final String LTAppID, final String LTAppKey, final String adID,
                                    final String packageID, boolean isLoginOut,
                                    final OnLoginSuccessListener mListener) {
        FacebookSdk.setApplicationId(mFacebookID);
        FacebookSdk.sdkInitialize(context);
        if (isLoginOut) {
            LoginManager.getInstance().logOut();
        }
        try {
            mFaceBookCallBack = CallbackManager.Factory.create();
            LoginManager.getInstance()
                    .logInWithReadPermissions((Activity) context,
                            Arrays.asList("public_profile"));
            LoginManager.getInstance().registerCallback(mFaceBookCallBack,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            if (loginResult != null) {
                                Map<String, Object> map = new WeakHashMap<>();
                                if (!TextUtils.isEmpty(adID)) {
                                    map.put("access_token", loginResult.getAccessToken().getToken());
                                    map.put("adid", "");
                                    map.put("gps_adid", adID);
                                    map.put("platform_id", packageID);
                                }
                                LoginResultManager.facebookLogin(context, baseUrl, LTAppID, LTAppKey,
                                        map, mListener);
                            }

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    });

        } catch (FacebookSdkNotInitializedException ex) {
            ex.printStackTrace();
        }

    }


    /**
     * 设置登录结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFaceBookCallBack != null) {
            mFaceBookCallBack.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 退出登录
     */
    public static void loginOut() {
        LoginManager.getInstance().logOut();
    }


    /**
     * 绑定FB
     */
    public static void bindFB(final Context context, String mFacebookID, boolean isLoginOut, final OnFBCallBack mCallBack) {
        FacebookSdk.setApplicationId(mFacebookID);
        FacebookSdk.sdkInitialize(context);
        if (isLoginOut) {
            LoginManager.getInstance().logOut();
        }
        try {
            mFaceBookCallBack = CallbackManager.Factory.create();
            LoginManager.getInstance()
                    .logInWithReadPermissions((Activity) context,
                            Arrays.asList("public_profile"));
            LoginManager.getInstance().registerCallback(mFaceBookCallBack,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            if (loginResult != null) {
                                mCallBack.onSuccess(loginResult.getAccessToken().getToken());
                            } else {
                                mCallBack.onFailed("get Facebook token is failed");
                            }

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {
                            mCallBack.onFailed(error.getMessage());
                        }
                    });

        } catch (FacebookSdkNotInitializedException ex) {
            ex.printStackTrace();
        }

    }

}
