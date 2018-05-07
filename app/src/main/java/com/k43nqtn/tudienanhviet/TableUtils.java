package com.k43nqtn.tudienanhviet;

/**
 * Created by User on 5/7/2018.
 */

public class TableUtils {
    final static String letters = "abcdefghijklmnopqrstuvwxyz#";
    final static String a_based = "àáạảãâầấậẩẫăằắặẳẵ";
    final static String e_based = "èéẹẻẽêềếệểễ";
    final static String i_based = "ìíịỉĩ";
    final static String o_based = "òóọỏõôồốộổỗơờớợởỡ";
    final static String u_based = "ùúụủũưừứựửữ";
    final static String y_based = "ỳýỵỷỹ";
    final static String d_based = "đ";

    public static int getTableIndex(String str)
    {
        char ch = str.toLowerCase().charAt(0);
        if (letters.indexOf(ch) == -1) {
            if (a_based.indexOf(ch) > -1) {
                ch = 'a';
            } else if (e_based.indexOf(ch) > -1) {
                ch = 'e';
            } else if (i_based.indexOf(ch) > -1) {
                ch = 'i';
            } else if (o_based.indexOf(ch) > -1) {
                ch = 'o';
            } else if (u_based.indexOf(ch) > -1) {
                ch = 'u';
            } else if (y_based.indexOf(ch) > -1) {
                ch = 'y';
            } else if (d_based.indexOf(ch) > -1) {
                ch = 'd';
            } else {
                ch = '#';
            }
        }
        return letters.indexOf(ch);
    }
}
