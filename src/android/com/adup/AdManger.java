package com.adup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashAdListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

public class AdManger extends CordovaPlugin {
    private static final String TAG = "AdupAd";
    private ATSplashAd _splashAd;
    private ATRewardVideoAd _rewardAd;
    private Context _context;
    private Activity _activity;
    private FrameLayout _container;
    private String _splashAdPlacementId;
    private String _rewardAdPlacementId;

    public void initTaku(String appId, String appKey, String splashAdPlacementId, String rewardAdPlacementId) {
        ATSDK.init(_context, appId, appKey);
        ATSDK.start();
        _splashAdPlacementId = splashAdPlacementId;
        _rewardAdPlacementId = rewardAdPlacementId;
        _splashAd = new ATSplashAd(_context, _splashAdPlacementId, new ATSplashExListenerImpl());
        _splashAd.loadAd();
        _rewardAd = new ATRewardVideoAd(_context, _rewardAdPlacementId);
    }

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();
        // your init code here
        _context = cordova.getContext();
        _activity = cordova.getActivity();
        //initTaku("a62b013be01931", "c3d0d2a9a9d451b07e62b509659f7c97","b62b0272f8762f");
        addFullScreenContainer();
    }


    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, String.format("%s is called. Callback ID: %s.", action, callbackContext.getCallbackId()));
        if (action.equals("initTaku")) {
            this.initTaku(args.getString(0), args.getString(1), args.getString(2), args.getString(3));
            callbackContext.success();
            return true;
        } else if (action.equals("requestPermission")) {
            this.requestPermission();
            callbackContext.success();
            return true;
        } else if (action.equals("loadSplashAd")) {
            this.loadSplashAd();
            callbackContext.success();
            return true;
        } else if (action.equals("showSplashAd")) {
            this.showSplashAd();
            callbackContext.success();
            return true;
        } else if (action.equals("loadRewardAd")) {
            this.loadRewardAd();
            callbackContext.success();
            return true;
        } else if (action.equals("showRewardAd")) {
            this.showRewardAd();
            callbackContext.success();
            return true;
        } else if (action.equals("isSplashAdReady")) {
            callbackContext.success(this.isSplashAdReady());
            return true;
        }


        return false;
    }

    private void requestPermission() {
        //cordova.requestPermission(this,100,Manifest.per++ission.READ_PHONE_STATE);
        cordova.requestPermissions(this, 100, new String[]{
                Manifest.permission.READ_PHONE_STATE,
                // Manifest.permission.READ_EXTERNAL_STORAGE,
                // Manifest.permission.ACCESS_NETWORK_STATE
        });
        //requestPermissions(100);
        //requestPermissions(100);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        Log.d(TAG, "RequestCode:" + requestCode);
        switch (requestCode) {
            case 100:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        // 权限授予了
                        if (Manifest.permission.READ_PHONE_STATE.equals(permissions[i])) {
                            Log.d(TAG, "onRequestPermissionsResult: READ_PHONE_STATE GRANTED");
                            // 执行与 READ_PHONE_STATE 相关的操作
                        }
//                        else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i])) {
//                            Log.d(TAG, "onRequestPermissionsResult: READ_EXTERNAL_STORAGE GRANTED");
//                            // 执行与 READ_EXTERNAL_STORAGE 相关的操作
//                        }
                    } else {
                        // 权限被拒绝
                        if (Manifest.permission.READ_PHONE_STATE.equals(permissions[i])) {
                            Log.d(TAG, "onRequestPermissionsResult: READ_PHONE_STATE REFUSED");
                            // 处理拒绝 READ_PHONE_STATE 的情况
                        }
//                        else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i])) {
//                            // 处理拒绝 READ_EXTERNAL_STORAGE 的情况
//                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void triggerEvent(String eventName, String eventData) {
        String js = String.format("javascript:cordova.fireDocumentEvent('%s', {detail:'%s'});", eventName, eventData);
        webView.getEngine().evaluateJavascript(js, null);
    }

    private void setContainerVisiable() {
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _container.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addFullScreenContainer() {
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _container = new FrameLayout(_activity);

                // 设置 LayoutParams 为全屏
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
                _container.setLayoutParams(params);
                _container.bringToFront();
                _container.setVisibility(View.GONE);
                // 将广告容器添加到 WebView 之后
                ((ViewGroup) webView.getView().getParent()).addView(_container);
                // 添加容器到根视图中
//        rootView.addView(container);
//        debugViewHierarchy(rootView);
            }
        });
    }

    public void loadSplashAd() {
        _splashAd.loadAd();
    }

    public void loadRewardAd() {
        _rewardAd.setAdListener(new ATRewardVideoListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
            }

            @Override
            public void onRewardedVideoAdFailed(AdError adError) {
                //注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                //AdError，请参考 https://docs.takuad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                Log.e(TAG, "onRewardedVideoAdFailed:" + adError.getFullErrorInfo());
                triggerEvent("RewardAdLoadFailed", adError.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo adInfo) {
                //ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                //请参考 https://docs.takuad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info

                //建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                _rewardAd.load();
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {
                //AdError，请参考 https://docs.takuad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                Log.e(TAG, "onRewardedVideoAdPlayFailed:" + adError.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
            }

            @Override
            public void onReward(ATAdInfo atAdInfo) {
                triggerEvent("RewardAdSuccess", "");
                //建议在此回调中下发奖励，一般在onRewardedVideoAdClosed之前回调
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {
            }
        });
        _rewardAd.load();
    }

    public void showSplashAd() {
        ATSplashAd.entryAdScenario(_splashAdPlacementId, "splash_ad_show_1");
        setContainerVisiable();
        _splashAd.show(_activity, _container);
    }

    public void showRewardAd() {
        if (_rewardAd.isAdReady()) {
            _rewardAd.show(_activity);
        } else {
            _rewardAd.load();
        }
    }

    public int isSplashAdReady() {
        if (_splashAd.isAdReady())
            return 1;
        else
            return 0;
    }

    class ATSplashExListenerImpl implements ATSplashAdListener {

        @Override
        public void onAdLoaded(boolean b) {
            Log.d(TAG, "onAdLoaded: " + b);
            showSplashAd();
        }

        @Override
        public void onAdLoadTimeout() {
            Log.d(TAG, "onAdLoadTimeout: ");
        }

        @Override
        public void onNoAdError(AdError adError) {
            Log.d(TAG, "onNoAdError: " + adError.getFullErrorInfo());
            triggerEvent("SplashAdLoadFailed", adError.getFullErrorInfo());
        }

        @Override
        public void onAdShow(ATAdInfo atAdInfo) {
        }

        @Override
        public void onAdClick(ATAdInfo atAdInfo) {

        }

        @Override
        public void onAdDismiss(ATAdInfo atAdInfo, ATSplashAdExtraInfo atSplashAdExtraInfo) {
            if (_container != null) {
                _container.removeAllViews();
                _container.setVisibility(View.GONE);
            }
        }
    }
}
