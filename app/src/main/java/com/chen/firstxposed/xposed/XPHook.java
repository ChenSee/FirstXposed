package com.chen.firstxposed.xposed;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.chen.firstxposed.util.AutoCollectUtils;
import com.chen.firstxposed.util.Config2;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XPHook implements IXposedHookLoadPackage {
    private XC_LoadPackage.LoadPackageParam paramLoadPackageParams = null;

    static long bootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam paramLoadPackageParam) {
        String str = paramLoadPackageParam.packageName;
        paramLoadPackageParams = paramLoadPackageParam;
        if (str.equals("com.eg.android.AlipayGphone")) hookRpcCall();
    }

    public void hookRpcCall() {
        Config2.init();
        XposedBridge.log("ACEnergy2:开始检查能量");
        AutoCollectUtils.whiteList = Config2.bean.whiteList;
        ClassLoader loader = paramLoadPackageParams.classLoader;
        Class localClass1;
        Class localClass2;
        Class localClass3;
        do {
            do {
                localClass1 = XposedHelpers.findClass("com.alipay.mobile.nebulacore.ui.H5Activity", loader);
                if (localClass1 != null) {
                    XposedHelpers.findAndHookMethod(localClass1, "onResume", new XC_MethodHook() {
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
                            super.afterHookedMethod(paramAnonymousMethodHookParam);
                            AutoCollectUtils.h5Activity = (Activity) paramAnonymousMethodHookParam.thisObject;
                        }
                    });
                }
                localClass1 = XposedHelpers.findClass("com.alipay.mobile.nebulacore.ui.H5FragmentManager", loader);
                if ((localClass1 != null) && (XposedHelpers.findClass("com.alipay.mobile.nebulacore.ui.H5Fragment", loader) != null)) {
                    XposedHelpers.findAndHookMethod(localClass1, "pushFragment", XposedHelpers.findClass("com.alipay.mobile.nebulacore.ui.H5Fragment", loader), Boolean.TYPE, Bundle.class, Boolean.TYPE, Boolean.TYPE, new XC_MethodHook() {
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
                            super.afterHookedMethod(paramAnonymousMethodHookParam);
                            AutoCollectUtils.curH5Fragment = paramAnonymousMethodHookParam.args[0];
                        }
                    });
                }
                localClass1 = XposedHelpers.findClass("com.alipay.mobile.nebulabiz.rpc.H5RpcUtil", loader);
            } while (localClass1 == null);
            localClass2 = XposedHelpers.findClass("com.alipay.mobile.h5container.api.H5Page", loader);
            localClass3 = XposedHelpers.findClass("com.alibaba.fastjson.JSONObject", loader);
        } while ((localClass2 == null) || (localClass3 == null));

        XposedBridge.log("ACEnergy2:开始检查能量2");
        if (localClass1 != null && localClass2 != null && localClass3 != null)
            runsRpcCall(localClass1, localClass2, localClass3);
    }

    public void runsRpcCall(final Class localClass1, final Class localClass2, final Class localClass3) {
        ClassLoader loader = paramLoadPackageParams.classLoader;
        XposedHelpers.findAndHookMethod(localClass1, "rpcCall", String.class, String.class, String.class, Boolean.TYPE, localClass3, String.class, Boolean.TYPE, localClass2, Integer.TYPE, String.class, Boolean.TYPE, Integer.TYPE, new XC_MethodHook() {
            MethodHookParam params;

            public Class localClassM1 = null;
            public Class localClassM2 = null;
            public Class localClassM3 = null;
            void cron(MethodHookParam param) throws Throwable {
                if (localClassM1 == null) localClassM1 = localClass1;
                if (localClassM2 == null) localClassM2 = localClass2;
                if (localClassM3 == null) localClassM3 = localClass3;
                XposedBridge.log("ACEnergy2:监听能量");
                Object resp = param.getResult();
                if (resp != null && Config2.bean.steal) {
                    Method method = resp.getClass().getMethod("getResponse");
                    String response = (String) method.invoke(resp, new Object[]{});
                    if (AutoCollectUtils.isRankList(response)) {
                        XposedBridge.log("ACEnergy2:监听好友能量");
                        AutoCollectUtils.autoGetCanCollectUserIdList(loader, response);
                    }
                    // 第一次是自己的能量，比上面的获取用户信息还要早，所以这里需要记录当前自己的userid值
                    if (AutoCollectUtils.isUserDetail(response)) {
                        XposedBridge.log("ACEnergy2:监听自己能量");
                        AutoCollectUtils.autoGetCanCollectBubbleIdList(loader, response);
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("ACEnergy2:开启处理能量");
                params = param;
                cron(params);

                Config2.init();
                long timeStamp = System.currentTimeMillis();
                if (Config2.bean.openTime != bootTime() && Config2.bean.runTime < timeStamp) {
                    XposedBridge.log("ACEnergy2:开启自动监听");
                    Config2.bean.openTime = bootTime();
                    Config2.bean.runTime = timeStamp + 60 * 1000 * 2;
                    Config2.save();

                    handler.postDelayed(runnable, 1000);
                }
            }

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        long timeStamp = System.currentTimeMillis();
                        if (Config2.bean.runTime < timeStamp)
                            runsRpcCall(localClassM1, localClassM2, localClassM3);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    handler.postDelayed(this, 60000);
                }
            };

        });
    }

}




