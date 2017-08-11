package com.webviewtest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * 手机信息类
 * Created by feng on 2016/8/1.
 */
public class Util {

    /**
     * 获取手机型号
     */
    public static String getPhoneInfo() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        return imei;
    }

    /**
     * 获取Android ID
     *
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @return
     */
    public static String getWifiMAC(Context context) {
        String mac = "0";
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            mac = info.getMacAddress();
            Log.e("wifi_mac_o", mac);
            if (mac.contains(":")) {
                mac = mac.replace(":", "");
            } else if (mac.contains("-")) {
                mac = mac.replace("-", "");
            }
            Log.e("wifi_mac_x", mac);
        }
        return mac;
    }

    /**
     * 以太网下获取MAC
     *
     * @return
     */
    public static String getMACAddress() {
        String interfaceName = "eth0";
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                String macStr = "0";
                macStr = buf.toString();
                Log.e("eth0_mac_o", macStr);
                if (macStr.contains(":")) {
                    macStr = macStr.replace(":", "");
                } else if (macStr.contains("-")) {
                    macStr = macStr.replace("-", "");
                }
                Log.e("eth0_mac_x", macStr);
                return macStr;
//                return buf.toString().replace(":", "");
//                return "FCD5D9055AD3";
            }
        } catch (Exception ex) {

        }
        return "";

    }

    /**
     * 获取版本号
     *
     * @param context
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    /**
     * 获取版本号CODE
     *
     * @param context
     * @return 当前应用的版本号
     */
    public static String getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = String.valueOf(info.versionCode);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    /**
     * 获取系统内应用信息
     *
     * @param context
     * @return
     */

    /**
     * 判断app是否可以正常打开
     *
     * @param context
     * @param packagename
     * @return
     */
    private static boolean appIsOpen(Context context, String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return false;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        if (resolveinfoList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取CPU信息
     *
     * @return
     */
    public static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            //读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    //查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        //提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1,
                                str.length());
                        //去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    //文件结尾
                    break;
                }
            }
        } catch (Exception ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }


//    public static String getDeviceID(Context context){
//        String CPUID =  context.SystemProperties.get(
//                "ro.hardware.cpuid", "0");
//        return CPUID;
//    }

    public static String printDeviceInf(String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append("PRODUCT ").append(android.os.Build.PRODUCT).append("\n");
        sb.append("BOARD ").append(android.os.Build.BOARD).append("\n");
        sb.append("BOOTLOADER ").append(android.os.Build.BOOTLOADER).append("\n");
        sb.append("BRAND ").append(android.os.Build.BRAND).append("\n");
        sb.append("CPU_ABI ").append(android.os.Build.CPU_ABI).append("\n");
        sb.append("CPU_ABI2 ").append(android.os.Build.CPU_ABI2).append("\n");
        sb.append("DEVICE ").append(android.os.Build.DEVICE).append("\n");
        sb.append("DISPLAY ").append(android.os.Build.DISPLAY).append("\n");
        sb.append("FINGERPRINT ").append(android.os.Build.FINGERPRINT).append("\n");
        sb.append("HARDWARE ").append(android.os.Build.HARDWARE).append("\n");
        sb.append("HOST ").append(android.os.Build.HOST).append("\n");
        sb.append("ID ").append(android.os.Build.ID).append("\n");
        sb.append("MANUFACTURER ").append(android.os.Build.MANUFACTURER).append("\n");
        sb.append("MODEL ").append(android.os.Build.MODEL).append("\n");
        sb.append("PRODUCT ").append(android.os.Build.PRODUCT).append("\n");
        sb.append("RADIO ").append(android.os.Build.RADIO).append("\n");
        sb.append("SERIAL ").append(android.os.Build.SERIAL).append("\n");
        sb.append("TAGS ").append(android.os.Build.TAGS).append("\n");
        sb.append("TIME ").append(android.os.Build.TIME).append("\n");
        sb.append("TYPE ").append(android.os.Build.TYPE).append("\n");
        sb.append("USER ").append(android.os.Build.USER).append("\n");
        Log.e(tag, "开始//////////////");

        return sb.toString();
    }

    /**
     * 补全台号到3位数
     *
     * @param num
     * @return
     */
    public static String changeNum(String num) {
        String numStr = "";
        if (num.length() == 2) {
            numStr = "0" + num;
        } else if (num.length() == 1) {
            numStr = "00" + num;
        } else {
            numStr = num;
        }
        return numStr;
    }

    /**
     * 补全评分到小数点后1位
     *
     * @param rate
     * @return
     */
    public static String changeRate(String rate) {
        String rateStr = "";
        if (rate.trim().length() == 1) {
            rateStr = rate + ".0";
        } else {
            rateStr = rate;
        }
        return rateStr;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getModel() {
        String model = "";
        model = android.os.Build.MODEL;
        return model;
    }


//    /**
//     * 获取设备SN
//     *
//     * @return
//     */
//    public static String getSN() {
//        String sn = "";
////        try {
////            Class<?> classType = Class.forName("android.os.SystemProperties");
////            Method[] method = classType.getDeclaredMethods();
////            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
////            sn = (String) getMethod.invoke(classType, new Object[]{"ro.serialno"});
////            Log.e("SN", sn + "///");
////        } catch (Exception e) {
////            Log.e("TAG", e.getMessage(), e);
////        }
//
//        String []propertys = {"ro.boot.serialno", "ro.serialno"};
//        for (String key : propertys){
////          String v = android.os.SystemProperties.get(key);
//            String v = getAndroidOsSystemProperties(key);
//            Log.e("1234567", "get " + key + " : " + v);
//        }
//        return sn;
//    }
//
//    public static String getAndroidOsSystemProperties(String key) {
//        Method systemProperties_get = null;
//        String ret;
//        try {
//            systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
//            if ((ret = (String) systemProperties_get.invoke(null, key)) != null)
//                return ret;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        return "";
//    }

    /**
     * 获取设备MAC
     *
     * @return
     */
    public static String getDeviceMAC() {
        String mac = "";
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method[] method = classType.getDeclaredMethods();
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            mac = (String) getMethod.invoke(classType, new Object[]{"ubootenv.var.ethaddr"});
            Log.e("MAC", mac);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
        }
        return mac;
    }


    /**
     * 通过Intent安装APK文件
     *
     * @param context
     * @param apkFile apk路径
     */
    public static void appInstall(Context context, String apkFile) {
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkFile),
                "application/vnd.android.package-archive");
        context.startActivity(i);
    }
}
