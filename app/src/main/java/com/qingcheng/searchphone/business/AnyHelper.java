package com.qingcheng.searchphone.business;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


public class AnyHelper {

    private static int firstVisibleItem = 0, selectBankItem = 0;
    private static HashMap<String, String> hMap = new HashMap<String, String>();
    private static final String TAG = "AnyHelper";
    private static final int app_android = 3;
    //公用的sharedPreferences名字
    private static final String GENERAL = "general";

    /**
     * 将dp转为px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取assets文件下的文件中的内容
     * @param fileName
     * @param context
     * @return
     */
    public static String getFromAssetFile(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getSreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕宽度
     */
    private int getSreenWidth(Context context) {
        WindowManager a = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d1 = a.getDefaultDisplay(); // 获取屏幕宽、高用
        return d1.getWidth();
    }

    /**
     * 获取屏幕高度
     */
    public static int getSreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getSreenHeight(Context context) {
        WindowManager a = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d1 = a.getDefaultDisplay(); // 获取屏幕宽、高用
        return d1.getHeight();
    }


    /**
     * 银行卡等四位加空格/手机号
     *
     * @param mEditText
     */
    public static TextWatcher numAddSpace(final EditText mEditText, final boolean isPhone) {
        TextWatcher TW = new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;

            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (isChanged) {
                        location = mEditText.getSelectionEnd();
                        int index = 0;
                        while (index < buffer.length()) {
                            if (buffer.charAt(index) == ' ') {
                                buffer.deleteCharAt(index);
                            } else {
                                index++;
                            }
                        }

                        index = 0;
                        int konggeNumberC = 0;
                        while (index < buffer.length()) {
                            if (isPhone) {
                                if ((index == 3 || index == 8)) {
                                    buffer.insert(index, ' ');
                                    konggeNumberC++;
                                } else if (index > 12) {
                                    buffer.deleteCharAt(index);
                                }
                            } else {
                                if ((index == 4 || index == 9 || index == 14 || index == 19)) {
                                    buffer.insert(index, ' ');
                                    konggeNumberC++;
                                }
                            }
                            index++;
                        }

                        if (konggeNumberC > konggeNumberB) {
                            location += (konggeNumberC - konggeNumberB);
                        }

                        tempChar = new char[buffer.length()];
                        buffer.getChars(0, buffer.length(), tempChar, 0);
                        String str = buffer.toString();
                        if (location > str.length()) {
                            location = str.length();
                        } else if (location < 0) {
                            location = 0;
                        }

                        mEditText.setText(str);
                        Editable etable = mEditText.getText();
                        Selection.setSelection(etable, location);
                        isChanged = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mEditText.addTextChangedListener(TW);
        return TW;
    }

    /**
     * 身份证格式化，添加空格
     *
     * @param mEditText
     */
    public static TextWatcher numAddSpaceIdentifyNum(final EditText mEditText) {
        TextWatcher TW = new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;

            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {


                    if (isChanged) {
                        location = mEditText.getSelectionEnd();
                        int index = 0;
                        while (index < buffer.length()) {
                            if (buffer.charAt(index) == ' ') {
                                buffer.deleteCharAt(index);
                            } else {
                                index++;
                            }
                        }

                        index = 0;
                        int konggeNumberC = 0;
                        while (index < buffer.length()) {

                            if ((index == 6 || index == 15)) {
                                buffer.insert(index, ' ');
                                konggeNumberC++;
                            } else if (index > 19) {
                                buffer.deleteCharAt(index);
                            }
                            index++;
                        }

                        if (konggeNumberC > konggeNumberB) {
                            location += (konggeNumberC - konggeNumberB);
                        }

                        tempChar = new char[buffer.length()];
                        buffer.getChars(0, buffer.length(), tempChar, 0);
                        String str = buffer.toString();
                        if (location > str.length()) {
                            location = str.length();
                        } else if (location < 0) {
                            location = 0;
                        }

                        mEditText.setText(str);
                        Editable etable = mEditText.getText();
                        Selection.setSelection(etable, location);
                        isChanged = false;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mEditText.addTextChangedListener(TW);
        return TW;
    }

    /**
     * 键盘根据目前的状态显示或隐藏
     */
    public void toggleSoftInput(final EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * Toast提示
     *
     * @param context
     * @param expresion
     */
    public static void showToast(Context context, String expresion) {
        if (expresion.contains("证件号码和证件类型在数据中心库中对应的客户编号不存在") || expresion.contains("系统内部异常") || expresion.contains("查询失败"))
            return;
        Toast.makeText(context, expresion, Toast.LENGTH_LONG).show();

    }


    /**
     * 产生16为随机字符创
     */
    public static String RandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    /**
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }

    // 通用参数的获取

    /**
     * App唯一凭证
     *
     * @return
     */
    public static String getAppkey() {
        return "J7zqYf";
    }

    /**
     * App唯一凭证密钥
     *
     * @return
     */
    public static String getAppsecret() {
        return "2ABFny";
    }

    /**
     * 获取版本号
     */
    public static String getAppVersion(Activity activity) {
        String version = "";
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo pInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            ApplicationInfo appInfo = pInfo.applicationInfo;
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return version;

        // return "1.0.0";
    }

    public static String getAppVerdionCode(Context activity) {
        int version = 0;
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo pInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            ApplicationInfo appInfo = pInfo.applicationInfo;
            version = pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version + "";
    }

    /**
     * 获取分发渠道
     */
    public static String getMarket() {// =========================这个不是要传整型吗
        return "g9";
    }

    /**
     * 获取appChannel
     */
    public static int getChannel() {
        return app_android;
    }

    /**
     * 获取接口秘钥 //5C0AD6BDE8B64C50281245A6B50E3E84
     */
    public static String getKey() {
        return "5C0AD6BDE8B64C50281245A6B50E3E84";
    }


    /**
     * 获取android系统imei号
     *
     * @param contex
     * @return
     */
    public static String getIMEI(Context contex) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) contex.getSystemService(contex.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }



    /**
     * @param
     * @return
     * @throws
     * @Description: 格式化银行卡密码
     */
    public static String formatbankCardNum(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("([\\d]{4})(?=\\d)", "$1");
    }


    /**
     * 内部类 限制小数点后只能输入两位
     *
     * @author fatlyz
     */
    public class TextWatchers implements TextWatcher {
        private boolean b = false;
        private boolean hasDoc = false;
        private EditText edit;
        private int docLocation = -1;
        int location = 0;
        String str = "";

        public TextWatchers(EditText edit) {
            this.edit = edit;
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            String temp = arg0.toString();
            int posDot = temp.indexOf(".");
            if (posDot == 0) {
                edit.setText("0.");
                Selection.setSelection((Spannable) (edit.getText()), edit.getText().length());
                return;
            } else if (posDot < 0) {
                return;
            }
            if (temp.length() - posDot - 1 > 2) {
                arg0.delete(posDot + 3, posDot + 4);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

    }


    /**
     * 判断字符串中是否含有中文 Java判断一个字符串是否有中文是利用Unicode编码来判断， 因为中文的编码区间为：0x4e00--0x9fbb
     */
    public static boolean isChineseCharacter(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if ((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证交易密码
     */
    public static String checkTradePassword(String tradePassWord) {
        String rule1 = "^[a-zA-Z0-9_-]{6,8}$"; // 密码由6-8位数字，大小写英文字母，下划线组成，不能有空格
        String rule2 = ("([\\d]|[a-zA-Z]|-|_)\\1{2,}"); // 不允许有3位或3位以上连续相同字符
        String rule3 = ("\\d{6,8}"); // 密码全是数字时不允许有3位或3位以上连续数字
        String result; // 存放每项规则校验证结果
        StringBuffer sb = new StringBuffer(); // 校验结果
        Pattern pattern;
        Matcher matcher;

        // rule1校验
        pattern = Pattern.compile(rule1);
        matcher = pattern.matcher(tradePassWord);
        if (!matcher.matches()) {
            result = "密码由6-8位数字，大小写英文字母，下划线组成，不能有空格";
            sb.append(result).append("|");
        }

        String checkResult = sb.toString();
        if (checkResult.lastIndexOf("|") > 0) {
            checkResult = checkResult.substring(0, checkResult.lastIndexOf("|"));
        }

        return checkResult;
    }


    /**
     * 方法描述    验证交易密码
     *
     * @param tradePassWord 描述 要校验的密码
     * @param rulenum 描述  要实用的校验规则  ：（现在只用四种规则，且这四种规则不能叠加）
     * @return 方法返回参数说明
     */

    public static String checkTradePassword(String tradePassWord, int rulenum) {
        String rule1 = "^[a-zA-Z0-9_-]{6,8}$"; // 密码由6-8位数字，大小写英文字母，下划线组成，不能有空格
        String rule2 = ("([\\d]|[a-zA-Z]|-|_)\\1{2,}"); // 不允许有3位或3位以上连续相同字符
        String rule3 = ("\\d{6,8}"); // 密码全是数字时不允许有3位或3位以上连续数字
        String rule4 = "^[0-9]{6,6}$"; // 仅可设置6位数字密码。String rule1 = "^[a-zA-Z0-9]{6,8}$"
        String rule5 = "^[a-zA-Z0-9]{6,8}$"; // 密码由6-8位数字，大小写英文字母，
        String result = ""; // 存放每项规则校验证结果
        StringBuffer sb = new StringBuffer(); // 校验结果
        Pattern pattern;
        Matcher matcher;

        // rule1校验
        switch (rulenum) {
            case 1:
                pattern = Pattern.compile(rule1);
                result = "密码由6-8位数字，大小写英文字母，下划线组成，不能有空格";
                break;
            case 2:
                pattern = Pattern.compile(rule2);
                result = "不允许有3位或3位以上连续相同字符";
                break;
            case 3:
                pattern = Pattern.compile(rule3);
                result = "密码全是数字时不允许有3位或3位以上连续数字";
                break;
            case 4:
                pattern = Pattern.compile(rule4);
                result = "仅可设置6位数字密码";
                break;
            case 5:
                pattern = Pattern.compile(rule5);
                result = "密码由6-8位数字，大小写英文字母，";
                break;
            default:
                pattern = Pattern.compile(rule4);
                result = "仅可设置6位数字密码";
                break;
        }
        matcher = pattern.matcher(tradePassWord);
        if (!matcher.matches()) {
            sb.append(result).append("|");
        }

        String checkResult = sb.toString();
        if (checkResult.lastIndexOf("|") > 0) {
            checkResult = checkResult.substring(0, checkResult.lastIndexOf("|"));
        }

        return checkResult;
    }

    /**
     * 验证登录密码
     */
    public static String checkLoginPassword(String logonPassword) {
        String rule1 = "^\\S{6,10}$"; // 密码必须是长度大于6位、小于10位的数字、字母或符号（不能包含空格）
        String rule2 = "^([0-9a-zA-Z])\\1+$"; // 密码不能是完全相同的数字或字母
        String result; // 存放每项规则校验证结果
        StringBuffer sb = new StringBuffer(); // 校验结果
        Pattern pattern;
        Matcher matcher;

        // 不可为空
        if ((logonPassword == null) || ("".equals(logonPassword))) {
            result = "登陆密码不可为空";
            sb.append(result).append("|");
        }

        // rule1校验
        pattern = Pattern.compile(rule1);
        matcher = pattern.matcher(logonPassword);
        if (!matcher.matches()) {
            result = "密码必须是长度大于6位、小于10位的数字、字母或符号（不能包含空格）";
            sb.append(result).append("|");
        }

        // rule2校验
        pattern = Pattern.compile(rule2);
        matcher = pattern.matcher(logonPassword);
        if (matcher.matches()) {
            result = "密码不能是完全相同的数字或字母";
            sb.append(result).append("|");
        }

        // 判断密码是否连号
        if ("123456789".indexOf(logonPassword) > -1 || "987654321".indexOf(logonPassword) > -1) {
            result = "密码不能是连号的数字";
            sb.append(result);

        }
        String checkResult = sb.toString();
        if (checkResult.lastIndexOf("|") > 0) {
            checkResult = checkResult.substring(0, checkResult.lastIndexOf("|"));
        }
        return checkResult;
    }


    /**
     * 隐藏系统键盘
     */
    public static boolean hideSystemKeyBoard(Activity acy) {
        try {
            if (acy != null) {
                InputMethodManager imm = (InputMethodManager) acy.getSystemService(acy.getApplicationContext().INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(acy.getCurrentFocus().getWindowToken(), 0);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 关闭软键盘
    public static void closeKeyboard(final EditText mEditText) {
        try {
            InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(mEditText.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        } catch (Exception e) {
        }

    }

    /**
     * 显示键盘
     */
    public static void showSystemKeyBoard(Activity activity) {
        try {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } catch (Exception e) {
        }

    }

    /**
     * 友盟，测试设备信息
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 截取银行卡号的后四位
     */
    public static String spitStr(String num, int spitNum) {
        if (num == null) return "";
        if (spitNum > num.length()) return "";
        String newStr = num.substring(num.length() - spitNum, num.length());
        return newStr;
    }

    /**
     * @param num     银行卡号
     * @return
     * @throws
     * @Description: 显示银行卡后四位，如果没有银行卡则不显示
     */
    public static String displayBankCodeLast4(String num) {
        if (isNoNull(num)) {
            num = num.replace(" ", "");
        }
        String result = spitStr(num, 4);
        return isNoNull(result) ? "(尾号" + result + ")" : "";
    }

    /**
     * @param num     银行卡号
     * @param spitNum 保留多少位
     * @return
     * @throws
     * @Description: 显示银行卡后四位，如果没有银行卡则不显示
     */
    public static String displayBankCodeLast4ForDaCheng(String num, int spitNum) {
        if (isNoNull(num)) {
            num = num.replace(" ", "");
        }
        String result = spitStr(num, spitNum);
        return isNoNull(result) ? " 尾号" + result : "";
    }

    /**
     * URL检查
     *
     * @param pInput 要检查的字符串
     * @return boolean 返回检查结果
     */
    public static boolean isUrl(String pInput) {
        if (pInput == null) {
            return false;
        }// http://funds.hexun.com/2014-04-01/163539971.html
        String regEx = "^((https|http|ftp|rtsp|mms)?://)" + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|" + "([0-9a-z_!~*'()-]+\\.)*" + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." + "[a-z]{2,6})" + "(:[0-9]{1,4})?" + "((/?)|" + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 编码转换
     *
     * @param str        数据源
     * @param oldCharset 元数据的编码如GBK
     * @param newCharset 目标编码如UTF-8
     * @return string 编码结果
     */
    public static String changeCharset(String str, String oldCharset, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            // 用旧的字符编码解码字符串。解码可能会出现异常。
            String string0 = new String(str.getBytes("GBK"), "ISO-8859-1");
            String string1 = new String(string0.getBytes("ISO-8859-1"), "UTF-8");

            // 用新的字符编码生成字符串
            return string1;
        }
        return null;
    }

    //  获取当前时间前timeLong天的值
    public static String getDateByDay(int day) {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        date.add(Calendar.DATE, -day);
        return format.format(date.getTime());
    }

    public static String getDateByDay2(int day) {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        date.add(Calendar.DATE, -day);
        return format.format(date.getTime());
    }

    // 为字符串添加逗号 如2454545--》second_circle,454,545
    public static String addCommaToStr(String str) {
        if (str == null || str.length() == 0) return "";
        int strLength = str.length();
        int commaNum = (strLength / 3);
        if (commaNum == 0) return str;
        StringBuffer sb = new StringBuffer();
        sb.append(str.substring(0, strLength - 3 * (commaNum)));
        for (int i = 0; i < commaNum; i++) {
            sb.append("," + str.substring(strLength - 3 * (commaNum - i), strLength - 3 * (commaNum - i - 1)));
        }

        return sb.toString();
    }

    // 为日期添加横杆 如20140312 --》2014-03-12
    public static String addLneTodate(String str) {
        if (str == null || str.length() == 0) return "";
        int strLength = str.length();
        if (strLength != 8) {
            return str;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(str.substring(0, 4) + "-");
        sb.append(str.substring(4, 6) + "-");
        sb.append(str.substring(6, 8));
        return sb.toString();
    }

    // 为金额加逗号 金额格式为xxxx.89
    public static String addCommaToMoney(String str) {
        try {
            if (str == null || str.length() == 0) return "";
            boolean state = false;//判断是否为有负号
            if (str.charAt(0) == '-') {
                state = true;
                str = str.substring(1, str.length());
            }

            int strLength = str.length();
            int popIndex = 0;
            popIndex = str.indexOf(".");
            if (popIndex == -1) popIndex = strLength;
            String strStart = str.substring(0, popIndex);
            int strStartLength = strStart.length();
            int commaNum = ((strStart.length()) / 3);
            int yushu = ((strStart.length()) % 3);
            StringBuffer sb = new StringBuffer();
            sb.append(str.substring(0, strStartLength - 3 * (commaNum)));
            for (int i = 0; i < commaNum; i++) {
                sb.append("," + strStart.substring(strStartLength - 3 * (commaNum - i), strStartLength - 3 * (commaNum - i - 1)));
            }

            sb.append(str.substring(popIndex, strLength));
            if (state) sb = sb.insert(0, "-");
            if (yushu == 0) {
                int index = sb.indexOf(",");
                //String newStr = sb.substring(first_circle, sb.length());
                String newStr = sb.replace(index, index + 1, "").toString();

                return newStr;
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }

    }


    /**
     * session_id不存在提示框
     *
     * acco_add_bank 绑卡 acco_modify_trade_pwd 修改交易密码接口 acco_modify_login_pwd
     * 修改登录密码接口 acco_user_info 个人信息接口 wallet_channel 渠道查询接口 wallet_recharge
     * 充值接口 wallet_trade 交易查询接口 nwallet_personal 个人概况接口 nwallet_profit
     * nwallet_channel nwallet_redeem 取现接口
     */
    /**
     * 已配接口
     */
    // 判断是否是数据
    public static boolean isNumeric(String str) {
        boolean isNumber = Character.isDigit(str.charAt(0));
        return isNumber;
    }

    // 关掉软键盘
    public static void clooseSoftKeyBord(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
        }

    }

    // 强制关掉软键盘
    public static void clooseSoftKeyBordForce(Activity activity) { //没用
        try {

            int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            activity.getWindow().addFlags(flags);
        } catch (Exception e) {
        }
    }

    /* 检验兑奖码 */
    public static boolean isExCodeType(String exCode) {
        String patten = "[A-Za-z0-9]{12}";

        Pattern p = Pattern.compile(patten);
        Matcher matcher = p.matcher(exCode);
        return matcher.matches();
    }

    // 从手机中获取图片
    public static Bitmap getBitMapPicDevice(String targetPath, String fileName) {
        Bitmap bitmap = null;
        String pathAndFileName = targetPath + "/" + fileName;
        File file = new File(targetPath);
        // 如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
        File mfile = new File(pathAndFileName);
        if (mfile.exists()) {// 若该文件存在
            bitmap = BitmapFactory.decodeFile(pathAndFileName);
        }
        return bitmap;
    }

    /* 半角转圆角 */
    public static String ToSBC(String input) {
        // 半角转全角：
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127) c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    // 格式化手机号码
    public static String formatPhoneCode(String phone) {
        if (phone == null) return "";
        int index = 0;
        StringBuffer buffer = new StringBuffer();
        buffer.append(phone);
        while (index < buffer.length()) {
            if ((index == 3 || index == 8)) {
                buffer.insert(index, ' ');
            }
            index++;
        }
        return buffer.toString();
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 first_circle 格式：154654313
     * @param str2 时间参数 second_circle 格式：154654313
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        str1 = df.format(Long.parseLong(str1));
        str2 = df.format(Long.parseLong(str2));
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 first_circle 格式：154654313
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes2(String str1) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        str1 = df.format(Long.parseLong(str1));
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            long time1 = one.getTime();
            long diff = time1;
            /*
             * day = diff / (24 * 60 * 60 * 1000); hour = (diff / (60 * 60 *
			 * 1000) - day * 24); min = ((diff / (60 * 1000)) - day * 24 * 60 -
			 * hour * 60); sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
			 */
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000));
            min = ((diff / (60 * 1000)) + 1);
            sec = (diff / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 first_circle 格式：154654313
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes3(String str1) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        str1 = df.format(Long.parseLong(str1));
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            long time1 = one.getTime();
            long diff = time1;
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }

    // 判断当前程序是在后台还是在前台
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    //	AnyHelper.systemOutPrintln("backgourd", "后台");
                    return true;
                } else {
                    //	AnyHelper.systemOutPrintln("backgourd", "前台");
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                // System.out.println("backgourd" + "后台");
                return true;
            }
        }
        // System.out.println("backgourd" + "前台");
        return false;

    }

    /**
     * 方法描述 判断是否锁屏
     *
     * @param context 描述 上下文
     * @return 方法返回参数说明
     */
    public static boolean isLockScreen(final Context context) {

        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);

        if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
            return true;
        } else {
            return false;
        }
    }

    public Bitmap getBitmapFromResources(Context context, int picId) {
        InputStream is = context.getResources().openRawResource(picId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 3; // width，hight设为原来的十分一
        Bitmap btp = BitmapFactory.decodeStream(is, null, options);
        return btp;
    }



    /**
     * 方法描述   比较两个 字符型的数字大小 ，前者大于等于后者返回true，反之返回false
     *
     * @param numStr0 描述 数字1
     * @param numStr1 描述 数字2
     * @return 方法返回参数说明
     */
    public static boolean compareTowNumStr(String numStr0, String numStr1) {
        boolean result = false;
        try {
            Double num1 = Double.valueOf(numStr0);
            Double num2 = Double.valueOf(numStr1);
            if (num1 >= num2) result = true;
            else result = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 方法描述 根据数字返回
     *
     * @param k 描述
     * @return 方法返回参数说明
     */
    public static String getSession(String k) {
        try {
            int value = Integer.valueOf(k);
            switch (value) {
                case 1:
                    return "一季报";
                case 2:
                    return "二季报";
                case 3:
                    return "三季报";
                case 4:
                    return "四季报";
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }

    }


    /**
     * 方法描述 给个人信息 隐藏部分信息
     *
     * @param ValueStr
     * @return 方法返回参数说明
     */
    public static String addStartChar(String ValueStr) {
        int length = ValueStr.length();
        String result = ValueStr;
        if (ValueStr != null && length > 8) {
            String str0 = ValueStr.substring(0, 4);
            String str1 = ValueStr.substring(4, length - 4);
            String str2 = ValueStr.substring(length - 4, length);
            String tmp = "";
            for (int i = 0; i < str1.length(); i++) {
                tmp += "*";
            }
            result = str0 + tmp + str2;
        }
        return result;
    }

    /**
     * 方法描述   乘以100
     *
     * @param value 描述
     * @return 方法返回参数说明
     */
    public static double get100Times(String value) {
        double result = 0.00;
        try {
            result = Double.valueOf(value);
            result *= 100;
        } catch (Exception e) {
            result = 0.00;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * TableLayout 清空
     * <p/>
     * 方法描述
     *
     * @param Tl 描述 表控件
     * @return 方法返回参数说明
     */
    public static void tableLayoutClear(TableLayout Tl) {
        int j = Tl.getChildCount();
        if (j > 1) {
            for (int i = j - 2; i > 0; i--) {
                Tl.removeView(Tl.getChildAt(i));//必须从后面减去子元素
            }
        }
    }

    /**
     * 方法描述  将要数值(字符串型)乘以相应的倍数
     *
     * @param oldValue 描述
     * @param num 描述
     * @return 方法返回参数说明
     */
    public static double multiplyByNum(String oldValue, int num) {
        Double result = 0.0;
        try {
            result = Double.valueOf(oldValue);
            result *= num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = sdf.format(new Date());
        return date;
    }

    public static String getDateFormat(String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String date = sdf.format(new Date());
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * @return yyyy-MM-dd
     */
    public static String getDateFormat2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return date;
    }

    /**
     * 将统计的用户信息写入到文件中
     *
     * @param context
     * @param info
     * @return
     */
    public static boolean saveStatisticsInfo(Context context, String info) {
        BufferedWriter bufw = null;
        try {
            //splash:=/data/data/com.dl.igwfund/files/statistics.txt
            // /data/data/<应用程序包名>/statistics.txt
            File file = new File(context.getFilesDir(), "statistics.txt");
            bufw = new BufferedWriter(new FileWriter(file, true));

            bufw.write(info);
            bufw.newLine();
            bufw.flush();
            bufw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufw != null) {
                    bufw.close();
                }
            } catch (Exception e2) {
            }
        }
        return false;
    }


    /**
     * 根据Activity名称获得对应的界面描述
     *
     * @param activityName
     * @return
     */
    public static String getPageDescription(String activityName) {
        String pageDescript = "";
        if ("MainActivity".equals(activityName)) {
            pageDescript = "主界面";
        } else if ("SplashActivity".equals(activityName)) {
            pageDescript = "splash界面";
        } else if ("ForgetPasswordActivity".equals(activityName)) {
            pageDescript = "忘记密码界面";
        } else if ("GestureActivity".equals(activityName)) {
            pageDescript = "手势界面";
        } else if ("GestureToLogActivity".equals(activityName)) {
            pageDescript = "手势密码登陆界面";
        } else if ("LoginActivity".equals(activityName)) {
            pageDescript = "登陆界面";
        } else if ("LoginForSetUpActivity".equals(activityName)) {
            pageDescript = "登陆界面";
        } else if ("LoginRemenberAccountActivity".equals(activityName)) {
            pageDescript = "记住密码登陆界面";
        } else if ("RegisterActivity".equals(activityName)) {
            pageDescript = "注册界面";
        } else if ("AllProfitAcitivity".equals(activityName)) {
            pageDescript = "收益界面";
        } else if ("ProfitQueryActivity".equals(activityName)) {
            pageDescript = "收益查询界面";
        } else if ("TransactionRecordsActivity".equals(activityName)) {
            pageDescript = "交易记录界面";
        } else if ("TranscationDetailActivity".equals(activityName)) {
            pageDescript = "交易详情界面";
        } else {
            pageDescript = "信息查询界面";
        }
        return pageDescript;
    }


    /**
     * @param pattren 正则表达式
     * @param str     数据源
     * @return String
     * @throws
     * @Title: getStrByPattern
     * @Description: TODO 正则表达式匹配字符串
     */
    public static String getStrByPattern(String pattren, String str) {
        String result = null;
        Pattern p = Pattern.compile(pattren);
        Matcher m = p.matcher(str);
        if (m.find()) {
            try {
                result = URLDecoder.decode(m.group(0), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean stringToBoolean(String value) {
        try {
            if ("1".equals(value) || "true".equals(value)) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNoNull(String value) {
        if (value == null || value.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String isNoNullToString(String value, String defaultStr) {
        if (!isNoNull(value)) {
            return defaultStr;
        } else {
            return value;
        }
    }

    //去掉空格
    public static String formatSpace(String value) {
        if (value != null) {
            value = value.replace(" ", "").replace("+", "");
        }
        return value;
    }

    //给银行卡加星号
    public static String formatBankCardCode(String value) {
        String result = "";
        int lenght = value != null ? value.length() : 0;
        for (int i = 0; i < lenght - 4; i++) {
            result += i % 4 == 0 ? " *" : "*";
        }
        if (!"".equals(result)) {
            result += " " + value.substring(lenght - 4, lenght);
        }
        return result.trim();
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 判断list是否为空
     */
    public static boolean listIsNoNull(List list) {
        if (list == null || list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Description: 判断String[]是否为空
     */
    public static boolean StringArrayIsNoNull(String[] array) {
        if (array == null || array.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static Integer IntegerVauleOf(String value) {
        try {
            if (isNoNull(value)) {
                return Integer.valueOf(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Double DoubleVauleOf(String value) {
        try {
            if (isNoNull(value)) {
                return Double.valueOf(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.00;
    }

    /**
     * @param input
     * @return String
     * @throws
     * @Title: ToDBC
     * @Description: TODO 将字符全角化
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * @param
     * @return 默认返回true，当值1value1小于值2value2时返回false
     * @throws
     * @Description: 判断另个值的大小（都为字符串）
     */
    public static boolean compareTowValues(String value1, String value2) {
        boolean result = true;
        if (!isNoNull(value1) || !isNoNull(value2)) {
            return result;
        }
        try {
            double value1Double = AnyHelper.DoubleVauleOf(value1);
            double value2Double = AnyHelper.DoubleVauleOf(value2);
            result = value1Double - value2Double >= 0;// ? true : false
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param
     * @return 默认返回-first_circle，
     * @throws
     * @Description: 值1value1与值2value2的差值（都为字符串）
     */
    public static double compareTowValuesDouble(String value1, String value2) {
        double result = -1;
        if (!isNoNull(value1) || !isNoNull(value2)) {
            return result;
        }
        try {
            double value1Double = AnyHelper.DoubleVauleOf(value1);
            double value2Double = AnyHelper.DoubleVauleOf(value2);
            return value1Double - value2Double;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

    }


    //当value大于0时前面添加+，否则不加工
    public static String AddPlus(String value) {

        try {
            String tmp = value.replace("%", "");
            float valueF = Float.valueOf(tmp);
            if (valueF > 0 && !value.contains("+")) {
                return value = "+" + value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean contains(String src, String target) {
        if (target == null || src == null) {
            return false;
        } else {
            return src.contains(target);
        }
    }

    //获取app第一次安装时间
    public static String getFirInsTime(Context context) {
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo= null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return stampToDate(packageInfo.firstInstallTime);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "20161028185234";
    }

    /**
     * 将时间戳转换为时间
     *
     * @param time
     * @return
     */
    public static String stampToDate(long time){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将String数据保存到相应的sharedPreferences中
     *
     * @param context
     * @param name sharedPreferences名字
     * @param key
     * @param value
     */
    public static void saveString(Context context, String name, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 从相应的sharedPreferences中获取String数据
     *
     * @param context
     * @param name sharedPreferences名字
     * @param key
     * @return
     */
    public static String getString(Context context, String name, String key) {
        SharedPreferences sp = context.getSharedPreferences(name, MODE_PRIVATE);
        return sp.getString(key, "");
    }

    //将String数据存储到公共sharedPreferences中
    public static void saveSharedString(Context context, String key, String value) {
        saveString(context, GENERAL, key, value);
    }

    //从公共sharedPreferences中获取String数据
    public static String getSharedString(Context context, String key) {
        return getString(context, GENERAL, key);
    }

    /**
     *
     * @param dateString 格式为yyyyMMdd hh:mm:ss 的时间格式
     * @return 格式是 yyyyMMddHHmmss
     */
    public static String formatDateString(String dateString) {
        return dateString.replace(" ", "").replace(":", "");
    }


    /**
     * 根据身份证 截取是否已成年
     * @param idno
     * @return
     */
    public static boolean judgeIsAdult(String idno){
        if (idno.length() != 18){
            //不是18位 无法判断 默认成年人
            return true;
        }
        try{
            String birthDay = idno.substring(6,14);
            Date date = new Date();
            SimpleDateFormat formatString = new SimpleDateFormat("yyyyMMdd");
            Date birthDate = formatString.parse(birthDay);
            long distanceTime = date.getTime() - birthDate.getTime();
            long eighteenYears = 18*365*24*60*60;
            if (distanceTime/1000 > eighteenYears){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][345678]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 验证密码格式
     * @param password
     * @return
     */
    public static boolean isPassword(String password){
        String pwdRegex = "^[_0-9a-zA-Z]{6,16}$";
        if (TextUtils.isEmpty(password)) return false;
        else return password.matches(pwdRegex);
    }


    /**
     * 以最省内存的方式读取本地资源的图片
     */
    public static Bitmap readBitmap565(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 从文件路径读取出位图
     *
     * @param context
     * @param imgPath
     * @return
     */
    public static Bitmap readBitMapByPath(Context context, File imgPath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(imgPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(fs, null, opt);
    }

    /**
     * 处理手机号 中间4位用*替代
     * @param phoneNum
     * @return
     */
    public static String displayPhoneNum(String phoneNum){
        if (!isMobileNO(phoneNum)){
            return "";
        }
        String result = phoneNum.substring(0,3) + "****" +phoneNum.substring(7,11);
        return result;
    }

    public static String[] splitTag(String tag){
        String[] result = tag.split(",");
        return result;
    }

    /**
     *根据年和月获取到date格式的日期
     * @return
     */
    public static String getDateFormatWithMonthAndYear(String year, String month){
        String result = year;
        if (month.length() == 1){
            result = result + "-0"+ month;
        }else if (month.length() == 2){
            result = result + "-" + month;
        }
        result = result + "-01";
        return result;
    }


    /**
     * 验证身份证是否合法
     */
    public static boolean validateCard(String idCard) {
        if(TextUtils.isEmpty(idCard))return false;
        String card = idCard.trim();
//        if (validateIdCard18(card)) {
//            return true;
//        }
        if (card.length() == 18){
            return true;
        }
        return false;
    }

    /** 中国公民身份证号码最小长度。 */
    public static final int CHINA_ID_MIN_LENGTH = 15;

    /** 中国公民身份证号码最大长度。 */
    public static final int CHINA_ID_MAX_LENGTH = 18;
    /**
     * 验证18位身份编码是否合法
     *
     * @param idCard 身份编码
     * @return 是否合法
     */
    public static boolean validateIdCard18(String idCard) {
        boolean bTrue = false;
        if (idCard.length() == CHINA_ID_MAX_LENGTH) {
            // 前17位
            String code17 = idCard.substring(0, 17);
            // 第18位
            String code18 = idCard.substring(17, CHINA_ID_MAX_LENGTH);
            if (isNum(code17)) {
                char[] cArr = code17.toCharArray();
                if (cArr != null) {
                    int[] iCard = converCharToInt(cArr);
                    int iSum17 = getPowerSum(iCard);
                    // 获取校验位
                    String val = getCheckCode18(iSum17);
                    if (val.length() > 0) {
                        if (val.equalsIgnoreCase(code18)) {
                            bTrue = true;
                        }
                    }
                }
            }
        }
        return bTrue;
    }

    /**
     * 验证15位身份编码是否合法
     *
     * @param idCard
     *            身份编码
     * @return 是否合法
     */
   /* public static boolean validateIdCard15(String idCard) {
        if (idCard.length() != CHINA_ID_MIN_LENGTH) {
            return false;
        }
        if (isNum(idCard)) {
            String proCode = idCard.substring(0, 2);
            if (cityCodes.get(proCode) == null) {
                return false;
            }
            String birthCode = idCard.substring(6, 12);
            Date birthDate = null;
            try {
                birthDate = new SimpleDateFormat("yy").parse(birthCode.substring(0, 2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            if (birthDate != null)
                cal.setTime(birthDate);
            if (!valiDate(cal.get(Calendar.YEAR), Integer.valueOf(birthCode.substring(2, 4)),
                    Integer.valueOf(birthCode.substring(4, 6)))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }*/

    /**
     * 数字验证
     *
     * @param val
     * @return 提取的数字。
     */
    public static boolean isNum(String val) {
        return val == null || "".equals(val) ? false : val.matches("^[0-9]*$");
    }

    /**
     * 将字符数组转换成数字数组
     *
     * @param ca
     *            字符数组
     * @return 数字数组
     */
    public static int[] converCharToInt(char[] ca) {
        int len = ca.length;
        int[] iArr = new int[len];
        try {
            for (int i = 0; i < len; i++) {
                iArr[i] = Integer.parseInt(String.valueOf(ca[i]));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iArr;
    }


    /** 每位加权因子 */
    public static final int power[] = {
            7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2
    };
    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param iArr
     * @return 身份证编码。
     */
    public static int getPowerSum(int[] iArr) {
        int iSum = 0;
        if (power.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                for (int j = 0; j < power.length; j++) {
                    if (i == j) {
                        iSum = iSum + iArr[i] * power[j];
                    }
                }
            }
        }
        return iSum;
    }

    /**
     * 将power和值与11取模获得余数进行校验码判断
     *
     * @param iSum
     * @return 校验位
     */
    public static String getCheckCode18(int iSum) {
        String sCode = "";
        switch (iSum % 11) {
            case 10:
                sCode = "2";
                break;
            case 9:
                sCode = "3";
                break;
            case 8:
                sCode = "4";
                break;
            case 7:
                sCode = "5";
                break;
            case 6:
                sCode = "6";
                break;
            case 5:
                sCode = "7";
                break;
            case 4:
                sCode = "8";
                break;
            case 3:
                sCode = "9";
                break;
            case 2:
                sCode = "x";
                break;
            case 1:
                sCode = "0";
                break;
            case 0:
                sCode = "1";
                break;
        }
        return sCode;
    }

}
