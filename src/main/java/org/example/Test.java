package org.example;

public class Test {
    public static void main(String[] args) {
        int shiftInt = 0;
        int a = 6;
        int count = 48329172;
        int shiftCnt = 33;
        shiftInt += (int) ((int) (count / a) << shiftCnt & 0xFFFFFFFFL);
        System.out.println(shiftInt);

    }
}
