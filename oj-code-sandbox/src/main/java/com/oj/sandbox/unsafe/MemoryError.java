package com.oj.sandbox.unsafe;

import java.util.ArrayList;
import java.util.List;

/**
 * 占用资源不释放
 */
public class MemoryError {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while (true) {
            bytes.add(new byte[10000]);
        }
    }
}
