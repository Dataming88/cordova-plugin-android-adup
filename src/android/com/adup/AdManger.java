package com.adup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.os.Bundle;


import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

public class AdManger extends CordovaPlugin {
    private static final String TAG = "AdUpManger";
    private Context _context;
    private Activity _activity;
    private FrameLayout _container;
    private String _rewardAdCodeId;
    private TTRewardVideoAd mTTRewardVideoAd;
    private TTAdNative.RewardVideoAdListener mRewardVideoListener; // 广告加载监听器
    private TTRewardVideoAd.RewardAdInteractionListener mRewardVideoAdInteractionListener; // 广告展示监听器
	
	public static TTAdManager get() {
        return TTAdSdk.getAdManager();
    }
    private static boolean sInit;
	private static void doInit(Context context,String appId) {
        if (!sInit) {
            //setp1.1：初始化SDK
            TTAdSdk.init(context, buildConfig(context,appId));
            //setp1.2：启动SDK
            TTAdSdk.start(new TTAdSdk.Callback() {
                @Override
                public void success() {
                    Log.i(TAG, "SDK初始化success: " + TTAdSdk.isSdkReady());
                }

                @Override
                public void fail(int code, String msg) {
                    Log.i(TAG, "fail:  code = " + code + " msg = " + msg);
                }
            });
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context,String appId) {

        return new TTAdConfig.Builder()

                .appId(appId)

                .debug(false)

                .useMediation(true)
                .customController(getTTCustomController()) //如果您需要设置隐私策略请参考该api
                .build();
    }

    //函数返回值表示隐私开关开启状态，未重写函数使用默认值
    private static TTCustomController getTTCustomController(){
        return new TTCustomController() {

            @Override
            public boolean isCanUseWifiState() {
                return super.isCanUseWifiState();
            }

            @Override
            public String getMacAddress() {
                return super.getMacAddress();
            }

            @Override
            public boolean isCanUseWriteExternal() {
                return super.isCanUseWriteExternal();
            }

            @Override
            public String getDevOaid() {
                return super.getDevOaid();
            }

            @Override
            public boolean isCanUseAndroidId() {
                return super.isCanUseAndroidId();
            }

            @Override
            public String getAndroidId() {
                return super.getAndroidId();
            }

            @Override
            public MediationPrivacyConfig getMediationPrivacyConfig() {
                return new MediationPrivacyConfig() {

                    @Override
                    public boolean isLimitPersonalAds() {
                        return super.isLimitPersonalAds();
                    }

                    @Override
                    public boolean isProgrammaticRecommend() {
                        return super.isProgrammaticRecommend();
                    }
                };
            }

            @Override
            public boolean isCanUsePermissionRecordAudio() {
                return super.isCanUsePermissionRecordAudio();
            }
        };
    }

    public void initAdup(String appId,String rewardAdCodeId) {
        AdManger.doInit(_context, appId);
		_rewardAdCodeId = rewardAdCodeId;
    }
	
	public void showRewardAd() {
        /** 1、创建AdSlot对象 */
        AdSlot adslot = new AdSlot.Builder()
                .setCodeId(_rewardAdCodeId)
                .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)  // ORIENTATION_VERTICAL:竖屏  ORIENTATION_LANDSCAPE: 横屏1
                .build();

        /** 2、创建TTAdNative对象 */
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(_context);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */
        adNativeLoader.loadRewardVideoAd(adslot, mRewardVideoListener);
    }
	
	 // 广告加载成功后，开始展示广告
    private void showRewardVideoAd() {
        if (mTTRewardVideoAd == null) {
            Log.i("UMAdDemo", "请先加载广告或等待广告加载完毕后再调用show方法");
            return;
        }
        /** 5、设置展示监听器，展示广告 */
        mTTRewardVideoAd.setRewardAdInteractionListener(mRewardVideoAdInteractionListener);
        mTTRewardVideoAd.showRewardVideoAd(_activity);
    }

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();
        _context = cordova.getContext();
        _activity = cordova.getActivity();
        addFullScreenContainer();
    }


    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, String.format("%s is called. Callback ID: %s.", action, callbackContext.getCallbackId()));
        if (action.equals("initAdup")) {
            this.initAdup(args.getString(0), args.getString(1));
            callbackContext.success();
            return true;
        } else if (action.equals("requestPermission")) {
            this.requestPermission();
            callbackContext.success();
            return true;
        }  else if (action.equals("showRewardAd")) {
            this.showRewardAd();
            callbackContext.success();
            return true;
        }
        return false;
    }

    private void requestPermission() {
        cordova.requestPermissions(this, 100, new String[]{
                Manifest.permission.READ_PHONE_STATE,
        });
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
                        }

                    } else {
                        // 权限被拒绝
                        if (Manifest.permission.READ_PHONE_STATE.equals(permissions[i])) {
                            Log.d(TAG, "onRequestPermissionsResult: READ_PHONE_STATE REFUSED");
                        }
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

    private void addFullScreenContainer() {
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _container = new FrameLayout(_activity);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
                _container.setLayoutParams(params);
                _container.bringToFront();
                _container.setVisibility(View.GONE);
                // 将广告容器添加到 WebView 之后
                ((ViewGroup) webView.getView().getParent()).addView(_container);
            }
        });
    }
	
	private void initListeners() {
        this.mRewardVideoListener = new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.i("UMAdDemo", "reward load fail: errCode: " + i + ", errMsg: " + s);
				triggerEvent("RewardAdLoadFailed", "reward load fail: errCode: " + i + ", errMsg: " + s);
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                Log.i("UMAdDemo", "reward load success");
                mTTRewardVideoAd = ttRewardVideoAd;
            }

            @Override
            public void onRewardVideoCached() {
                Log.i("UMAdDemo", "reward cached success");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                Log.i("UMAdDemo", "reward cached success 2");
                mTTRewardVideoAd = ttRewardVideoAd;
                showRewardVideoAd();
            }
        };
        this.mRewardVideoAdInteractionListener = new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                Log.i("UMAdDemo", "reward show");
				//建议在此回调中下发奖励
				triggerEvent("RewardAdSuccess", "rewardad show");
            }

            @Override
            public void onAdVideoBarClick() {
                Log.i("UMAdDemo", "reward click");
                triggerEvent("RewardAdBarClick", "rewardad click");
            }

            @Override
            public void onAdClose() {
                Log.i("UMAdDemo", "reward close");
				triggerEvent("RewardAdClose", "reward ad close");
            }

            @Override
            public void onVideoComplete() {
                Log.i("UMAdDemo", "reward onVideoComplete");
				triggerEvent("RewardAdComplete", "reward ad VideoComplete");
            }

            @Override
            public void onVideoError() {
                Log.i("UMAdDemo", "reward onVideoError");
				triggerEvent("RewardAdError", "reward onVideoError");
            }

            @Override
            public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
                Log.i("UMAdDemo", "reward onRewardVerify");
            }

            @Override
            public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                Log.i("UMAdDemo", "reward onRewardArrived");
            }

            @Override
            public void onSkippedVideo() {
                Log.i("UMAdDemo", "reward onSkippedVideo");
				triggerEvent("RewardAdSkipped", "reward ad Skipped");
            }
        };
    }
}
