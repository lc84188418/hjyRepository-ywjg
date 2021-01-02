package com.hjy.common.utils.led;

import java.util.ArrayList;

import javax.swing.*;

import com.hjy.common.utils.led.SerialPortManager;
import com.hjy.common.utils.led.appConfig;
import org.springframework.boot.web.server.PortInUseException;

import java.awt.event.*;

public class serialwindow {
    private JLabel lblSerial, lblSend, lblRev;
    private JComboBox cmbSerial;
    private JButton btnSerial, btnSendData;
    private JTextArea txtSend, txtRev;

    public serialwindow() {
        JFrame frame = new JFrame("串口测试");
        frame.setSize(720, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        init();
        frame.setVisible(true);
        appConfig.mainFrame = frame;
    }

    /**
     * 初始化数据
     */
    private void init() {
        ArrayList<String> serials = SerialPortManager.findPorts();
        for (String str : serials) {
            cmbSerial.addItem(str);
        }

        btnSerial.addActionListener(new SerialButtonAction());
        btnSendData.addActionListener(new SendButtonAction());
    }

    /**
     * 发送数据按钮监听事件
     */
    private class SendButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String str = txtSend.getText().replace(" ", "");
            if (str.isEmpty() || str.length() <= 0) {
                return;
            }
            try{
                SerialPortManager.sendToPort(appConfig.serial, util.stringToBytes(str));
            }catch(Exception ex){
                util.errorMessage(ex.getMessage(), "错误");
            }
        }

    }

    /**
     * 打开/关闭串口按钮监听事件
     */
    private class SerialButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (btnSerial.getText().equals("打开串口")) {
                if (cmbSerial.getSelectedIndex() < 0) {
                    util.errorMessage("请选择需要打开的串口", "选择串口");
                    return;
                }
                try {
                    appConfig.serial = SerialPortManager.openPort(cmbSerial.getSelectedItem().toString(), 9600);
                    SerialPortManager.addListener(appConfig.serial, new SerialDataActioin());
                    btnSerial.setText("关闭串口");
                    cmbSerial.setEnabled(false);
                } catch (Exception e1) {
                    //PortInUseException
                    util.errorMessage(e1.getMessage(), "打开串口错误");
                }
            } else {
                SerialPortManager.closePort(appConfig.serial);
                btnSerial.setText("打开串口");
                cmbSerial.setEnabled(true);
            }
        }
    }

    private class SerialDataActioin implements SerialPortManager.DataAvailableListener {

        @Override
        public void dataAvailable() {
            byte[] bys = SerialPortManager.readFromPort(appConfig.serial);
            if (bys != null && bys.length > 0) {
                txtRev.setText(util.bytesToString(bys));
            }
        }
    }

    /**
     * 界面控件添加
     * 
     * @param panel
     */
    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // 创建创建串口文本
        lblSerial = new JLabel("选择串口：");
        lblSerial.setBounds(10, 10, 80, 30);

        // 创建串口选择
        cmbSerial = new JComboBox<>();
        cmbSerial.setBounds(90, 10, 140, 30);

        // 创建串口按钮
        btnSerial = new JButton("打开串口");
        btnSerial.setBounds(240, 10, 100, 30);

        // 发送数据文本
        lblSend = new JLabel("发送内容：");
        lblSend.setBounds(10, 50, 100, 30);

        // 发送数据文本框
        txtSend = new JTextArea();
        txtSend.setBounds(90, 50, 590, 200);

        // 接收数据文本
        lblRev = new JLabel("接收数据：");
        lblRev.setBounds(10, 260, 100, 30);

        // 接收数据文本框
        txtRev = new JTextArea();
        txtRev.setBounds(90, 260, 590, 200);

        // 发送数据按钮
        btnSendData = new JButton("发送数据");
        btnSendData.setBounds(350, 10, 100, 30);

        panel.add(lblSerial);
        panel.add(cmbSerial);
        panel.add(btnSerial);
        panel.add(lblSend);
        panel.add(lblRev);
        panel.add(txtSend);
        panel.add(txtRev);
        panel.add(btnSendData);
    }
}