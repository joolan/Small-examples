//package com.example.tscsample;
package com.example.tsc.tsd_dll_test;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;

import com.example.tscdll.TSCUSBActivity;


public class MainActivity extends Activity {

    TSCUSBActivity TscUSB = new TSCUSBActivity();

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static UsbManager mUsbManager;
    private static PendingIntent mPermissionIntent;
    private static boolean hasPermissionToCommunicate = false;

    private Button test;
    private TextView tv1;
    private static UsbDevice device;


    IntentFilter filterAttached_and_Detached = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
    // Catches intent indicating if the user grants permission to use the USB device
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermissionToCommunicate = true;
                        }
                    }
                }
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);


        UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d("Detect ", deviceList.size()+" USB device(s) found");
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext())
        {
            device = deviceIterator.next();
            if(device.getVendorId() == 4611)
            {
                //Toast.makeText(MainActivity.this, device.toString(), 0).show();
                break;
            }
        }



        //-----------start-----------
        PendingIntent mPermissionIntent;
        mPermissionIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
        mUsbManager.requestPermission(device, mPermissionIntent);

        tv1 = (TextView) findViewById(R.id.textView);
        test = (Button) findViewById(R.id.button1);


        test.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if(mUsbManager.hasPermission(device))
                {
                    TscUSB.openport(mUsbManager,device);
                    /*
                    // 本5句是最小化测试打印机的语句
                    TscUSB.sendcommand("SIZE 3,1\r\n");
                    TscUSB.sendcommand("GAP 0,0\r\n");
                    TscUSB.sendcommand("CLS\r\n");
                    TscUSB.sendcommand("TEXT 100,100,\"3\",0,1,1,\"123456\"\r\n");
                    TscUSB.sendcommand("PRINT 1\r\n");
                    */
                    TscUSB.sendcommand("SIZE 58 mm, 60 mm\r\n"); //pos58小票机
                    TscUSB.sendcommand("GAP 0,0\r\n");//Gap 0,0连续纸
                    //  标签票 间隙纸的具体配置方式需要参考TSPL文档和实际门票尺寸来定

                    //TscUSB.sendcommand("BLINE 2 mm, 0 mm\r\n");//blackmark media

                    TscUSB.sendcommand("CLS\r\n");
                    //TscUSB.clearbuffer();
                    TscUSB.sendcommand("CODEPAGE UTF-8\r\n");  //936=GB2312
                    //TscUSB.sendcommand("CODEPAGE UTF-8\r\n");


                    TscUSB.sendcommand("BAR 0,12,684,2\r\n");

                    int paperWidth = 58; // 假设纸张宽度为58毫米（你需要根据实际情况调整）
                    int qrcodeX = (int) ((paperWidth - 10) * 11.8); // 转换为毫米并计算X坐标
                    int qrcodeY = 60; // 距离打印原点/起始点的Y坐标
                    int qrcodeCellWidth = 8; // 条码的最小方块颗粒大小
                    int qrcodeRotation = 0; // 旋转角度
                    String qrcodeJustification = "J3"; // 居中点位
                    String qrcodeArea = "X360"; // 打印区域
                    String qrcodeContent = "http://www.nlxxsx-test.com/mobile/bus/tickettest?sn=As5sdaQOCKF-45das6d4"; // 条码内容
                    // 构建命令字符串
                    String command1 = "QRCODE "+qrcodeX+","+qrcodeY+",Q,"+qrcodeCellWidth+",A,"+qrcodeRotation+","+qrcodeJustification+",M2,"+qrcodeArea+",\""+qrcodeContent+"\"\r\n";
                    TscUSB.sendcommand(command1);


                    int text_x =  (int)((15*11.8)-50);// # 这里是第二行，要避开与第一行字重叠
                    int text_y = 60;
                    String text_name2 = "3";// # 默认字体3，不能打印中文
                    int font_rotation = 90;// # 注意这里是顺时针90度
                    String    font_content = "time 2024-11-08 12:00:07";
                    String command2 = "TEXT " + text_x + "," + text_y + ",\"" + text_name2 + "\"," + font_rotation + ",2,2,\"" + font_content + "\"\r\n";
                    TscUSB.sendcommand(command2);


                    text_x =  (int)((15*11.8)-50-30);// # 这里是第二行，要避开与第一行字重叠
                    text_y = 60;
                    text_name2 = "simhei.TTF";// # 【最好能安卓sdk直接将字体下载到打印机】现在是通过软件将字体下载到打印机,新打印机需要按说明文档安装字体
                    font_rotation = 90;// # 注意这里是顺时针90度
                    font_content = "请在出票2小时内核销使用 2024-11-08 12:00:07";
                    command2 = "TEXT " + text_x + "," + text_y + ",\"" + text_name2 + "\"," + font_rotation + ",8,8,\"" + font_content + "\"\r\n";
                    // 将命令字符串转换为GB2312编码的字节数组

                    //byte[] commandBytes2 = command2.getBytes(Charset.forName("GB2312"));
                    //TscUSB.sendcommand(commandBytes2);
                    TscUSB.sendcommand(command2);


                    TscUSB.sendcommand("PRINT 1,1\r\n");//确定打印一份
                    TscUSB.closeport(5000);//关闭端口

                }

            }



        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


}
