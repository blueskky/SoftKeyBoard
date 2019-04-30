package com.whty.eschoolbag.softkeyboard.keyboard;

import android.graphics.Color;


/**
 * 键盘相关配置
 *
 *
 */
public class KeyboardConfig {

    /**
     * 键盘类型选中颜色
     */
    private int selectedColor = 0xff66aeff;

    /**
     * 键盘类型未选中颜色
     */
    private int unselectedColor = Color.BLACK;

    /**
     * 数字键盘使能
     */
    private boolean isNumberEnabled = true;

    /**
     * 字母键盘使能
     */
    private boolean isLetterEnabled = true;

    /**
     * 符号键盘使能
     */
    private boolean isSymbolEnabled = true;

    /**
     * 默认选中键盘
     */
    private KeyboardType defaultKeyboardType = KeyboardType.LETTER_ONLY;

    public KeyboardConfig() {
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public KeyboardConfig setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        return this;
    }

    public int getUnselectedColor() {
        return unselectedColor;
    }

    public KeyboardConfig setUnselectedColor(int unselectedColor) {
        this.unselectedColor = unselectedColor;
        return this;
    }

    public boolean isNumberEnabled() {
        return isNumberEnabled;
    }

    public KeyboardConfig setNumberEnabled(boolean numberEnabled) {
        this.isNumberEnabled = numberEnabled;
        return this;
    }

    public boolean isLetterEnabled() {
        return isLetterEnabled;
    }

    public KeyboardConfig setLetterEnabled(boolean letterEnabled) {
        this.isLetterEnabled = letterEnabled;
        return this;
    }

    public boolean isSymbolEnabled() {
        return isSymbolEnabled;
    }

    public KeyboardConfig setSymbolEnabled(boolean symbolEnabled) {
        this.isSymbolEnabled = symbolEnabled;
        return this;
    }

    public KeyboardType getDefaultKeyboardType() {
        return defaultKeyboardType;
    }

    public KeyboardConfig setDefaultKeyboardType(KeyboardType defaultKeyboardType) {
        this.defaultKeyboardType = defaultKeyboardType;
        return this;
    }
}
