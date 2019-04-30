package com.whty.eschoolbag.softkeyboard.keyboard;

/**
 * KeyboardType 键盘类型
 *
 *
 */
public enum KeyboardType {


    /**
     * 中文
     */
    INPUT_ZH(-1,"中文"),

    /**
     * 数字键盘
     */
    NUMBER_ONLY(0, "数字"),

    /**
     * 字母键盘
     */
    LETTER_ONLY(1, "字母"),


    /**
     * 综合_字母
     */
    MULTI_LETTR(2,"综合_字母"),

    /**
     * 符号键盘
     */
    MULTI_SYMBOL(3, "综合_符号"),


    /**
     * 综合_经常使用
     */
    MULTI_MAXIMUM(4,"综合_常用"),


    /**
     * 综合_阿尔法
     */
    MULTI_ALPHA(5,"综合_希腊字母"),


    /**
     * 综合_公式
     */
    MULTI_FORMULA(5,"综合_公式");



    private int code;
    private String name;

    KeyboardType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
