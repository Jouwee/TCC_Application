package com.github.jouwee.tcc_projeto.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Utilitário para compressão de descompressão
 */
public class Compressor {

    /**
     * Comprime um bytearray
     * 
     * @param bytes
     * @return byte[]
     */
    public static byte[] compress(byte[] bytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DeflaterOutputStream dos = new DeflaterOutputStream(baos)) {
                dos.write(bytes);
                dos.flush();
            }
            return baos.toByteArray();
        } catch (Exception e) {
            return bytes;
        }
    }

    /**
     * Descomprime um bytearray
     * 
     * @param bytes
     * @return byte[]
     */
    public static byte[] decompress(byte[] bytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(bytes));
            int rlen;
            byte[] buf = new byte[5];
            while ((rlen = iis.read(buf)) != -1) {
                baos.write(Arrays.copyOf(buf, rlen));
            }
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            return bytes;
        }
    }

}
