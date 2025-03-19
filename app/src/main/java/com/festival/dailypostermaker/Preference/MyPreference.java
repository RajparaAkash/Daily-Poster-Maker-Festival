package com.festival.dailypostermaker.Preference;

import com.festival.dailypostermaker.MyApplication;

public class MyPreference {

    public static String get_DeviceId() {
        return HelperPreference.getInstance().getString("deviceID", "");
    }

    public static void set_DeviceId(String str) {
        HelperPreference.getInstance().setString("deviceID", str);
    }

    public static String get_Token() {
        return HelperPreference.getInstance().getString("token", "");
    }

    public static void set_Token(String str) {
        HelperPreference.getInstance().setString("token", str);
    }

    public static String get_UserId() {
        return HelperPreference.getInstance().getString("userId", "");
    }

    public static void set_UserId(String str) {
        HelperPreference.getInstance().setString("userId", str);
    }

    public static String get_PrivacyPolicy() {
        return HelperPreference.getInstance().getString("privacyPolicy", "");
    }

    public static void set_PrivacyPolicy(String str) {
        HelperPreference.getInstance().setString("privacyPolicy", str);
    }

    public static String get_TermsCondition() {
        return HelperPreference.getInstance().getString("termsCondition", "");
    }

    public static void set_TermsCondition(String str) {
        HelperPreference.getInstance().setString("termsCondition", str);
    }

    public static String get_RefundCancellation() {
        return HelperPreference.getInstance().getString("refundCancellation", "");
    }

    public static void set_RefundCancellation(String str) {
        HelperPreference.getInstance().setString("refundCancellation", str);
    }

    public static Boolean get_IsProfileAdded() {
        return HelperPreference.getInstance().getBoolean("isProfileAdded", false);
    }

    public static void set_IsProfileAdded(Boolean aBoolean) {
        HelperPreference.getInstance().setBoolean("isProfileAdded", aBoolean);
    }

    public static Boolean get_IsBusinessAdded() {
        return HelperPreference.getInstance().getBoolean("isBusinessAdded", false);
    }

    public static void set_IsBusinessAdded(Boolean aBoolean) {
        HelperPreference.getInstance().setBoolean("isBusinessAdded", aBoolean);
    }

    public static Boolean get_IsPremium() {
        return HelperPreference.getInstance().getBoolean("isPremium", false);
    }

    public static void set_IsPremium(Boolean aBoolean) {
        HelperPreference.getInstance().setBoolean("isPremium", aBoolean);
    }

    public static Boolean get_IsProfileBusiness() {
        return HelperPreference.getInstance().getBoolean("isProfileBusiness", false);
    }

    public static void set_IsProfileBusiness(Boolean aBoolean) {
        HelperPreference.getInstance().setBoolean("isProfileBusiness", aBoolean);
    }


    public static String get_P_Name() {
        return HelperPreference.getInstance().getString("P_Name", MyApplication.pName);
    }

    public static void set_P_Name(String str) {
        HelperPreference.getInstance().setString("P_Name", str);
    }

    public static String get_P_MobileNo() {
        return HelperPreference.getInstance().getString("P_MobileNo", MyApplication.number);
    }

    public static void set_P_MobileNo(String str) {
        HelperPreference.getInstance().setString("P_MobileNo", str);
    }

    public static String get_P_About() {
        return HelperPreference.getInstance().getString("P_About", MyApplication.pAboutYou);
    }

    public static void set_P_About(String str) {
        HelperPreference.getInstance().setString("P_About", str);
    }

    public static String get_P_Address() {
        return HelperPreference.getInstance().getString("P_Address", MyApplication.pAddress);
    }

    public static void set_P_Address(String str) {
        HelperPreference.getInstance().setString("P_Address", str);
    }

    public static String get_P_Instagram() {
        return HelperPreference.getInstance().getString("P_Instagram", MyApplication.userName);
    }

    public static void set_P_Instagram(String str) {
        HelperPreference.getInstance().setString("P_Instagram", str);
    }

    public static String get_P_YouTube() {
        return HelperPreference.getInstance().getString("P_YouTube", "");
    }

    public static void set_P_YouTube(String str) {
        HelperPreference.getInstance().setString("P_YouTube", str);
    }

    public static String get_P_Facebook() {
        return HelperPreference.getInstance().getString("P_Facebook", "");
    }

    public static void set_P_Facebook(String str) {
        HelperPreference.getInstance().setString("P_Facebook", str);
    }

    public static String get_P_Twitter() {
        return HelperPreference.getInstance().getString("P_Twitter", "");
    }

    public static void set_P_Twitter(String str) {
        HelperPreference.getInstance().setString("P_Twitter", str);
    }

    public static String get_P_Email() {
        return HelperPreference.getInstance().getString("P_Email", "");
    }

    public static void set_P_Email(String str) {
        HelperPreference.getInstance().setString("P_Email", str);
    }

    public static String get_P_Website() {
        return HelperPreference.getInstance().getString("P_Website", "");
    }

    public static void set_P_Website(String str) {
        HelperPreference.getInstance().setString("P_Website", str);
    }

    public static String get_P_ProfilePath() {
        return HelperPreference.getInstance().getString("P_ProfilePath", "");
    }

    public static void set_P_ProfilePath(String str) {
        HelperPreference.getInstance().setString("P_ProfilePath", str);
    }


    public static int get_B_BusinessId() {
        return HelperPreference.getInstance().getInt("B_BusinessId", 0);
    }

    public static void set_B_BusinessId(int id) {
        HelperPreference.getInstance().setInt("B_BusinessId", id);
    }

    public static String get_B_Name() {
        return HelperPreference.getInstance().getString("B_Name", MyApplication.bName);
    }

    public static void set_B_Name(String str) {
        HelperPreference.getInstance().setString("B_Name", str);
    }

    public static String get_B_Designation() {
        return HelperPreference.getInstance().getString("B_Designation", MyApplication.bAbout);
    }

    public static void set_B_Designation(String str) {
        HelperPreference.getInstance().setString("B_Designation", str);
    }

    public static String get_B_Address() {
        return HelperPreference.getInstance().getString("B_Address", MyApplication.bAddress);
    }

    public static void set_B_Address(String str) {
        HelperPreference.getInstance().setString("B_Address", str);
    }

    public static String get_B_Whatsapp() {
        return HelperPreference.getInstance().getString("B_Whatsapp", MyApplication.number);
    }

    public static void set_B_Whatsapp(String str) {
        HelperPreference.getInstance().setString("B_Whatsapp", str);
    }

    public static String get_B_Instagram() {
        return HelperPreference.getInstance().getString("B_Instagram", MyApplication.userName);
    }

    public static void set_B_Instagram(String str) {
        HelperPreference.getInstance().setString("B_Instagram", str);
    }

    public static String get_B_YouTube() {
        return HelperPreference.getInstance().getString("B_YouTube", "");
    }

    public static void set_B_YouTube(String str) {
        HelperPreference.getInstance().setString("B_YouTube", str);
    }

    public static String get_B_Facebook() {
        return HelperPreference.getInstance().getString("B_Facebook", "");
    }

    public static void set_B_Facebook(String str) {
        HelperPreference.getInstance().setString("B_Facebook", str);
    }

    public static String get_B_Twitter() {
        return HelperPreference.getInstance().getString("B_Twitter", "");
    }

    public static void set_B_Twitter(String str) {
        HelperPreference.getInstance().setString("B_Twitter", str);
    }

    public static String get_B_Email() {
        return HelperPreference.getInstance().getString("B_Email", "");
    }

    public static void set_B_Email(String str) {
        HelperPreference.getInstance().setString("B_Email", str);
    }

    public static String get_B_Website() {
        return HelperPreference.getInstance().getString("B_Website", "");
    }

    public static void set_B_Website(String str) {
        HelperPreference.getInstance().setString("B_Website", str);
    }

    public static String get_B_ProfilePath() {
        return HelperPreference.getInstance().getString("B_ProfilePath", "");
    }

    public static void set_B_ProfilePath(String str) {
        HelperPreference.getInstance().setString("B_ProfilePath", str);
    }

    public static int get_P_SelectedSocialMedia() {
        return HelperPreference.getInstance().getInt("P_SelectedSocialMedia", 1);
    }

    public static void set_P_SelectedSocialMedia(int pos) {
        HelperPreference.getInstance().setInt("P_SelectedSocialMedia", pos);
    }

    public static int get_B_SelectedSocialMedia() {
        return HelperPreference.getInstance().getInt("B_SelectedSocialMedia", 1);
    }

    public static void set_B_SelectedSocialMedia(int pos) {
        HelperPreference.getInstance().setInt("B_SelectedSocialMedia", pos);
    }

    public static Integer get_Ad() {
        return HelperPreference.getInstance().getInt("ad", 1);
    }

    public static void set_Ad(Integer value) {
        HelperPreference.getInstance().setInt("ad", value);
    }

    public static String get_AdPlace() {
        return HelperPreference.getInstance().getString("adPlace", "Single");
    }

    public static void set_AdPlace(String str) {
        HelperPreference.getInstance().setString("adPlace", str);
    }


    public static String get_AdPriority() {
        return HelperPreference.getInstance().getString("adPriority", "AdMob");
    }

    public static void set_AdPriority(String str) {
        HelperPreference.getInstance().setString("adPriority", str);
    }

    public static String get_AmBanner() {
        return HelperPreference.getInstance().getString("amBanner", "");
    }

    public static void set_AmBanner(String str) {
        HelperPreference.getInstance().setString("amBanner", str);
    }

    public static String get_AmInterstitial() {
        return HelperPreference.getInstance().getString("amInterstitial", "");
    }

    public static void set_AmInterstitial(String str) {
        HelperPreference.getInstance().setString("amInterstitial", str);
    }

    public static String get_AmRewarded() {
        return HelperPreference.getInstance().getString("amRewarded", "");
    }

    public static void set_AmRewarded(String str) {
        HelperPreference.getInstance().setString("amRewarded", str);
    }

    public static String get_AmNative() {
        return HelperPreference.getInstance().getString("amNative", "");
    }

    public static void set_AmNative(String str) {
        HelperPreference.getInstance().setString("amNative", str);
    }

    public static String get_AmAppOpen() {
        return HelperPreference.getInstance().getString("amAppOpen", "");
    }

    public static void set_AmAppOpen(String str) {
        HelperPreference.getInstance().setString("amAppOpen", str);
    }

    public static String get_AdxBanner() {
        return HelperPreference.getInstance().getString("adxBanner", "");
    }

    public static void set_AdxBanner(String str) {
        HelperPreference.getInstance().setString("adxBanner", str);
    }

    public static String get_AdxInterstitial() {
        return HelperPreference.getInstance().getString("adxInterstitial", "");
    }

    public static void set_AdxInterstitial(String str) {
        HelperPreference.getInstance().setString("adxInterstitial", str);
    }

    public static String get_AdxRewarded() {
        return HelperPreference.getInstance().getString("adxRewarded", "");
    }

    public static void set_AdxRewarded(String str) {
        HelperPreference.getInstance().setString("adxRewarded", str);
    }

    public static String get_AdxNative() {
        return HelperPreference.getInstance().getString("adxNative", "");
    }

    public static void set_AdxNative(String str) {
        HelperPreference.getInstance().setString("adxNative", str);
    }

    public static String get_AdxAppOpen() {
        return HelperPreference.getInstance().getString("adxAppOpen", "");
    }

    public static void set_AdxAppOpen(String str) {
        HelperPreference.getInstance().setString("adxAppOpen", str);
    }

    public static String get_LanguageCode() {
        return HelperPreference.getInstance().getString("languageCode", "en");
    }

    public static void set_LanguageCode(String str) {
        HelperPreference.getInstance().setString("languageCode", str);
    }

    public static Boolean isFirstTimeUser() {
        return HelperPreference.getInstance().getBoolean("firstTimeUser", true);
    }

    public static void set_FirstTimeUser(Boolean bool) {
        HelperPreference.getInstance().setBoolean("firstTimeUser", bool);
    }
}
