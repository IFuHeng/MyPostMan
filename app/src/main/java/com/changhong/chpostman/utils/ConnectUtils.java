package com.changhong.chpostman.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;

import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConnectUtils {


    public static CharSequence getConnectedInfo(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        return ssb;
    }

    public static CharSequence getWifiInfo(@NotNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectInfo = wifiManager.getConnectionInfo();

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        final float[] temp_hsv = new float[]{1, 1, 1};

        ssb.append(TextTools.string2CharSequence("Wifi SSID"
                , new RelativeSizeSpan(1.2f)
                , new TextAppearanceSpan(context, android.R.style.TextAppearance_Large)))
                .append(' ');

        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append(':').append(' ')
                .append(TextTools.string2CharSequence(clearSSID(connectInfo.getSSID()), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n').append('\n');
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("MAC").append(':').append(' ')
                .append(TextTools.string2CharSequence(WifiMacUtils.getMac(context), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n');
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("BSSID").append(':').append(' ')
                .append(TextTools.string2CharSequence(connectInfo.getBSSID(), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n');
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("LinkSpeed").append(':').append(' ')
                .append(TextTools.string2CharSequence(connectInfo.getLinkSpeed()+ WifiInfo.LINK_SPEED_UNITS, new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n');
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
            ssb.append("frequency").append(':').append(' ')
                    .append(TextTools.string2CharSequence(connectInfo.getFrequency() + WifiInfo.FREQUENCY_UNITS, new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                    .append('\n');
        }
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("HiddenSSID").append(':').append(' ')
                .append(TextTools.string2CharSequence(String.valueOf(connectInfo.getHiddenSSID()), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n').append('\n');

        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("IP").append(':').append(' ')
                .append(TextTools.string2CharSequence(turnInteger2Ip(dhcpInfo.ipAddress), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n');
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("Netmask").append(':').append(' ')
                .append(TextTools.string2CharSequence(turnInteger2Ip(dhcpInfo.netmask), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n');
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("Gateway").append(':').append(' ')
                .append(TextTools.string2CharSequence(turnInteger2Ip(dhcpInfo.gateway), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n');
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("DNS1").append(':').append(' ')
                .append(TextTools.string2CharSequence(turnInteger2Ip(dhcpInfo.dns1), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))))
                .append('\n');
        temp_hsv[0] = Math.round(Math.random() * (360 - 180) + 180);
        ssb.append("DNS2").append(':').append(' ')
                .append(TextTools.string2CharSequence(turnInteger2Ip(dhcpInfo.dns2), new ForegroundColorSpan(Color.HSVToColor(temp_hsv))));

        return ssb;
    }


    public static boolean isWifiNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)
            return true;
        else
            return false;
    }

    public static boolean isMobileNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            return true;
        else
            return false;
    }

    public static int turnArr2Ip(String[] IP) {
        int ip = 0;
        for (int i = 0; i < IP.length; i++) {
            int temp = Integer.parseInt(IP[i]);
            ip = (temp << (i * 8)) | ip;
        }
        return ip;
    }

    public static int turnIpv4Str2Ip(String IP) {
        int ip = 0;
        String[] tempArr = IP.split(".");
        for (int i = 0; i < tempArr.length; i++) {
            int temp = Integer.parseInt(tempArr[i]);
            ip = (temp << (i * 8)) | ip;
        }
        return ip;
    }

    public static String turnInteger2Ip(int ip) {
        int[] temp = new int[4];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = ip >>> (8 * i);
            temp[i] &= 0xff;
        }
        return String.format("%d.%d.%d.%d", temp[0], temp[1], temp[2], temp[3]);
    }

    /**
     * 获取SSID
     *
     * @return WIFI 的SSID
     */
    public static String getWifiSSID(Context context, WifiManager wifiManager) {
        String ssid = "unknown id";

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {

            assert wifiManager != null;
            WifiInfo info = wifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                ssid = info.getSSID();
            } else {
                ssid = info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {

            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    ssid = networkInfo.getExtraInfo().replace("\"", "");
                }
            }
        }

        if (ssid == null || ssid.contains("unknown")) {
            int networkId = wifiManager.getConnectionInfo().getNetworkId();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , 9527);
                return null;
            }
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            if (configuredNetworks == null)
                return ssid;
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (wifiConfiguration.networkId == networkId) {
                    ssid = clearSSID(wifiConfiguration.SSID);
                    break;
                }
            }
        }

        return ssid;
    }

    public static String clearSSID(String ssid) {
        if (TextUtils.isEmpty(ssid) || ssid.length() < 2)
            return ssid;
        if (ssid.charAt(0) == '\"' && ssid.charAt(ssid.length() - 1) == '\"')
            return ssid.substring(1, ssid.length() - 1);

        return ssid;
    }

}
