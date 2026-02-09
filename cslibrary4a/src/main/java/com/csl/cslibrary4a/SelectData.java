package com.csl.cslibrary4a;

import android.content.Context;

public class SelectData {
    public String selectMaskEpc;
    public int selectBank = 1;
    public int selectOffset = 32;
    public String selectPassword;
    public int selectPower;

    public SelectData(String selectMaskEpc, String selectPassword, int selectPower) {
        this.selectMaskEpc = selectMaskEpc;
        this.selectPassword = selectPassword;
        this.selectPower = selectPower;
    }
    public SelectData(String selectMaskEpc, int selectBank, int selectOffset, String selectPassword, int selectPower) {
        this.selectMaskEpc = selectMaskEpc;
        this.selectBank = selectBank;
        this.selectOffset = selectOffset;
        this.selectPassword = selectPassword;
        this.selectPower = selectPower;
    }
}
