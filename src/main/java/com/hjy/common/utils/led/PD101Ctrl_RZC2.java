package com.hjy.common.utils.led;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import sun.awt.CharsetString;

/**
 * @author liuchun
 * @Package com.hjy.common.utils.led
 * @date 2020/12/7 11:13
 * description:如果dll是以stdcall方式输出函数，那么就继承StdCallLibrary。否则就继承默认的Library接口。
 */
public interface PD101Ctrl_RZC2 extends Library {

    PD101Ctrl_RZC2 instanceDll  = (PD101Ctrl_RZC2)Native.loadLibrary("PD101Ctrl_RZC2", PD101Ctrl_RZC2.class);
    /**
     * 打开串口
     * nComPort: 串口号 1-255
     * nBaudrate: 普通是9600
     */
    void pd101a_rzc2_Open(int nComPort, int nBaudrate);
    /**
     * 带返回值的打开串口
     * nComPort: 串口号 1-255
     * nBaudrate: 普通是9600
     * 正常返回串口句柄
     * 出错时返回-1
     */
    int pd101a_rzc2_OpenEx(int nComPort, int nBaudrate);
    /**
     * 关闭串口
     */
    void pd101a_rzc2_Close();
    /**
     * 发送带字符颜色描述的字串
     * nCardId: 卡号 0-255
     * szText: 要发送的字串,每个要显示的汉字或ASCII符后紧跟一个字符表示颜色
     * 	'0'：红色
     *  	'1'：绿色
     *  	'2'：黄色
     *  	'3'：蓝色
     *  	'4'：洋红色
     *  	'5'：青色
     *  	'6'：白色
     *  例如: 中0国1A2   显示内容为中国A,颜色依次为红绿黄
     *  C语言的函数参数是：wchar_t*。 JNA中对应的Java类型是WStirng
     */
    void pd101a_rzc2_SendText(int nCardId, WString value);

    /**
     * 发送单一颜色的字串
     * int nCardId: 卡号 0-255
     * char* szText: 要发送的字串
     * int	nColor：该字串显示颜色
     * 	0：红色
     *  1：绿色
     *  2：黄色
     *  3：蓝色
     *  4：洋红色
     *  5：青色
     *  6：白色
     */
    //原方法void pd101a_rzc2_SendSingleColorText(int nCardId,char* value, int nColor);
//    void pd101a_rzc2_SendSingleColorText(int nCardId,WString value, int nColor);
//    void pd101a_rzc2_SendSingleColorText(int nCardId,String value, int nColor);
    void pd101a_rzc2_SendSingleColorText(int nCardId,byte[] value, int nColor);
//    void pd101a_rzc2_SendSingleColorText(int nCardId, Pointer value, int nColor);


}
