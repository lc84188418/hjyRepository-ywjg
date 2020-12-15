package com.hjy.common.utils.led;

import com.sun.jna.ptr.ByReference;

/**
 * @author liuchun
 * @Package com.hjy.common.utils.led
 * @date 2020/12/14 18:43
 * description:
 */
public class CharReference extends ByReference {
    public CharReference() {
        this((byte)0);//无
    }

    public CharReference(byte value) {
        super(1000);
        this.setValue(value);
    }

    public void setValue(byte value) {
        this.getPointer().setByte(0L, value);
    }

    public byte getValue() {
        return this.getPointer().getByte(0L);
    }

    public void Init(String input) {
        for(int i=0;i<input.length();i++) {
            this.getPointer().setChar((long)i, input.charAt(i));//字符串初始化
        }
    }

    public void Init(byte[] bytes) {
        for(int i=0;i<bytes.length;i++) {
            this.getPointer().setByte((long)i, bytes[i]);//字节数组初始化
        }
    }
}
