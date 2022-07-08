package com.changhong.chpostman.preference;

public interface KeyConfig {

    String KEY_LOCAL = "local";
    String KEY_COOKIE_SSID = "cookie ssid";
    String KEY_SSID = "ssid";
    String KEY_PASSWORD = "password";

    String KEY_ROUTER_PASSWORD = "router password";

    String KEY_DEVICE_TYPE = "device type";
    String KEY_GUIDE_STATE = "guide state";

    String KEY_STATUS_INFO = "state info";

    String KEY_MESH_NETWORK_SHOW = "mesh_network_show";
    String KEY_DEVICE_MAC = "device mac";

    String KEY_5G = "5G";
    String KEY_24G = "2.4G";
    String KEY_WIFI_ADVANCE = "wifi advance";

    String KEY_IP = "IP";

    String KEY_GATEWAY = "GATEWAY";
    String KEY_CONNECT_TYPE = "ConnectType";

    String KEY_DEVICE_INFO = "device info";
    String KEY_DEVICE_BINDED = "device binded";

    /**
     * 用于判断组网中是否是主子路由
     */
    String KEY_DEVICE_QLINK = "Qlink";

    String KEY_STA_INFO = "sta info";

    String KEY_IS_ADD_CHILD = "is_add_child";
    String KEY_USER_CONFIG_IN_CLOUD = "user_config_in_cloud";

    /**
     * 外部应用所用到的字段
     */
    String KEY_INFO_FROM_APP = "info_from_app";

    String ACTION_UNBIND_DEVICE = "com.changhong.smarthome.action.ACTION_UNBIND_DEVICE";
    String ACTION_BIND_DEVICE = "com.changhong.smarthome.action.ACTION_BIND_DEVICE";

    String KEY_FIRMWARE_VERSION = "firmwareVersion";

    /**
     * used in WifiAdvanceItemFragment
     */
    String KEY_CHANNEL_CHOICE = "channel choice";
    String KEY_BANDWIDTH_CHOICES = "bandwidth choice";
    String KEY_ADVANCE_WIFI_VALUE = "advance_item_value";
}
