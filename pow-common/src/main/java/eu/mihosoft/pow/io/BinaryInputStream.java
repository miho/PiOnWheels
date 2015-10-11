/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.mihosoft.pow.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryInputStream extends InputStream {

    protected boolean debug = false;

    public final void setDebug(boolean b) {
        debug = b;
    }

    public final boolean getDebug() {
        return debug;
    }
    private final InputStream is;

    public BinaryInputStream(byte bytes[], ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        this.is = new ByteArrayInputStream(bytes);
    }

    public BinaryInputStream(byte bytes[]) {
        this.is = new ByteArrayInputStream(bytes);
    }

    public BinaryInputStream(InputStream is, ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        this.is = is;
    }

    public BinaryInputStream(InputStream is) {
        this.is = is;
    }
    // default byte order for Java.
    // -> used for many file formats.
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    protected void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    protected ByteOrder getByteOrder() {
        return byteOrder;
    }

    private static double[] toDoubleArray(byte[] byteArray, ByteOrder byteOrder) {
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(byteArray, i * times, times).order(byteOrder).getDouble();
        }
        return doubles;
    }

    private static int[] toIntArray(byte[] byteArray, ByteOrder byteOrder) {
        int times = Integer.SIZE / Byte.SIZE;
        int[] ints = new int[byteArray.length / times];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = ByteBuffer.wrap(byteArray, i * times, times).order(byteOrder).getInt();
        }
        return ints;
    }

    public int toInt(byte[] byteArray) throws IOException {
        int times = Integer.SIZE / Byte.SIZE;
        return ByteBuffer.wrap(byteArray, 0, times).order(byteOrder).getInt();
    }

    public short toShort(byte[] byteArray) throws IOException {
        int times = Short.SIZE / Byte.SIZE;
        return ByteBuffer.wrap(byteArray, 0, times).order(byteOrder).getShort();
    }

    public double toDouble(byte[] byteArray) throws IOException {
        int times = Double.SIZE / Byte.SIZE;
        return ByteBuffer.wrap(byteArray, 0, times).order(byteOrder).getDouble();
    }

    public float toFloat(byte[] byteArray) throws IOException {
        int times = Float.SIZE / Byte.SIZE;
        return ByteBuffer.wrap(byteArray, 0, times).order(byteOrder).getFloat();
    }

    public int readInt() throws IOException {
        int times = Integer.SIZE / Byte.SIZE;
        return toInt(readByteArray(new byte[times]));
    }

    public short readShort() throws IOException {
        int times = Short.SIZE / Byte.SIZE;
        return toShort(readByteArray(new byte[times]));
    }

    public float readFloat() throws IOException {
        int times = Float.SIZE / Byte.SIZE;
        return toFloat(readByteArray(new byte[times]));
    }

    public int[] readIntArray(int length) throws IOException {
        int times = Integer.SIZE / Byte.SIZE;
        return toIntArray(readByteArray(new byte[length * times]), byteOrder);
    }

    public double[] readDoubleArray(int length) throws IOException {
        int times = Double.SIZE / Byte.SIZE;
        return toDoubleArray(readByteArray(new byte[length * times]), byteOrder);
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

//    protected final int convertByteArrayToInt(String name, byte bytes[]) {
//        return convertByteArrayToInt(name, bytes, byteOrder);
//    }
//
//    public final int convertByteArrayToShort(String name, byte bytes[]) {
//        return convertByteArrayToShort(name, bytes, byteOrder);
//    }
//
//    public final int convertByteArrayToShort(String name, int start,
//            byte bytes[]) {
//        return convertByteArrayToShort(name, start, bytes, byteOrder);
//    }
//    public final int read4Bytes(String name, String exception)
//            throws IOException {
//        return read4Bytes(name, exception, byteOrder);
//    }
//
//    public final int read3Bytes(String name, String exception)
//            throws IOException {
//        return read3Bytes(name, exception, byteOrder);
//    }
//
//    public final int read2Bytes(String name, String exception)
//            throws IOException {
//        return read2Bytes(name, exception, byteOrder);
//    }
    public final void debugNumber(String msg, int data) {
        debugNumber(msg, data, 1);
    }

    public final void debugNumber(String msg, int data, int bytes) {
        System.out.print(msg + ": " + data + " (");
        int byteData = data;
        for (int i = 0; i < bytes; i++) {
            if (i > 0) {
                System.out.print(",");
            }
            int singleByte = 0xff & byteData;
            System.out.print((char) singleByte + " [" + singleByte + "]");
            byteData >>= 8;
        }
        System.out.println(") [0x" + Integer.toHexString(data) + ", "
                + Integer.toBinaryString(data) + "]");
    }

    public final void readAndVerifyBytes(byte expected[], String exception)
            throws IOException {
        for (int i = 0; i < expected.length; i++) {
            int data = is.read();
            byte b = (byte) (0xff & data);
            if ((data < 0) || (b != expected[i])) {
                System.out.println("i" + ": " + i);
                this.debugByteArray("expected", expected);
                debugNumber("data[" + i + "]", b);
                // debugNumber("expected[" + i + "]", expected[i]);
                throw new IOException(exception);
            }
        }
    }

    protected final void readAndVerifyBytes(String name, byte expected[],
            String exception) throws IOException {
        byte bytes[] = readByteArray(name, expected.length, exception);
        for (int i = 0; i < expected.length; i++) {
            if (bytes[i] != expected[i]) {
                System.out.println("i" + ": " + i);
                debugNumber("bytes[" + i + "]", bytes[i]);
                debugNumber("expected[" + i + "]", expected[i]);
                throw new IOException(exception);
            }
        }
    }

    public final void skipBytes(int length, String exception)
            throws IOException {
        long total = 0;
        while (length != total) {
            long skipped = is.skip(length - total);
            if (skipped < 1) {
                throw new IOException(exception + " (" + skipped + ")");
            }
            total += skipped;
        }
    }

    protected final void scanForByte(byte value) throws IOException {
        int count = 0;
        for (int i = 0; count < 3; i++) // while(count<3)
        {
            int b = is.read();
            if (b < 0) {
                return;
            }
            if ((0xff & b) == value) {
                System.out.println("\t" + i + ": match.");
                count++;
            }
        }
    }

    public final byte readByte(String name, String exception)
            throws IOException {
        int result = is.read();
        if ((result < 0)) {
            System.out.println(name + ": " + result);
            throw new IOException(exception);
        }
        if (debug) {
            debugNumber(name, result);
        }
        return (byte) (0xff & result);
    }

//    protected final int convertByteArrayToInt(String name, byte bytes[],
//            ByteOrder byteOrder) {
//        return convertByteArrayToInt(name, bytes, 0, 4, byteOrder);
//    }
//
//    protected final int convertByteArrayToInt(String name, byte bytes[],
//            int start, int length, ByteOrder byteOrder) {
//        byte byte0 = bytes[start + 0];
//        byte byte1 = bytes[start + 1];
//        byte byte2 = bytes[start + 2];
//        byte byte3 = 0;
//        if (length == 4) {
//            byte3 = bytes[start + 3];
//        }
//        // return convert4BytesToInt(name, byte0, byte1, byte2, byte3,
//        // byteOrder);
//        int result;
//        if (byteOrder == ByteOrder.BIG_ENDIAN) // motorola, big endian
//        {
//            result = ((0xff & byte0) << 24) + ((0xff & byte1) << 16)
//                    + ((0xff & byte2) << 8) + ((0xff & byte3) << 0);
//        } // result = (( byte0) << 24) + ((byte1) << 16)
//        // + (( byte2) << 8) + (( byte3) << 0);
//        else // intel, little endian
//        {
//            result = ((0xff & byte3) << 24) + ((0xff & byte2) << 16)
//                    + ((0xff & byte1) << 8) + ((0xff & byte0) << 0);
//        }
//        // result = (( byte3) << 24) + (( byte2) << 16)
//        // + (( byte1) << 8) + (( byte0) << 0);
//        if (debug) {
//            debugNumber(name, result, 4);
//        }
//        return result;
//    }
//
//    protected final int[] convertByteArrayToIntArray(String name, byte bytes[],
//            int start, int length, ByteOrder byteOrder) {
//        int expectedLength = start + length * 4;
//        if (bytes.length < expectedLength) {
//            System.out.println(name + ": expected length: " + expectedLength
//                    + ", actual length: " + bytes.length);
//            return null;
//        }
//        int result[] = new int[length];
//        for (int i = 0; i < length; i++) {
//            result[i] = convertByteArrayToInt(name, bytes, start + i * 4, 4,
//                    byteOrder);
//        }
//        return result;
//    }
//
//    protected final int convertByteArrayToShort(String name, byte bytes[],
//            ByteOrder byteOrder) {
//        return convertByteArrayToShort(name, 0, bytes, byteOrder);
//    }
//
//    protected final int convertByteArrayToShort(String name, int start,
//            byte bytes[], ByteOrder byteOrder) {
//        byte byte0 = bytes[start + 0];
//        byte byte1 = bytes[start + 1];
//        // return convert2BytesToShort(name, byte0, byte1, byteOrder);
//        int result;
//        if (byteOrder == ByteOrder.BIG_ENDIAN) // motorola, big endian
//        {
//            result = ((0xff & byte0) << 8) + ((0xff & byte1) << 0);
//        } else // intel, little endian
//        {
//            result = ((0xff & byte1) << 8) + ((0xff & byte0) << 0);
//        }
//        if (debug) {
//            debugNumber(name, result, 2);
//        }
//        return result;
//    }
//
//    protected final int[] convertByteArrayToShortArray(String name,
//            byte bytes[], int start, int length, ByteOrder byteOrder) {
//        int expectedLength = start + length * 2;
//        if (bytes.length < expectedLength) {
//            System.out.println(name + ": expected length: " + expectedLength
//                    + ", actual length: " + bytes.length);
//            return null;
//        }
//        int result[] = new int[length];
//        for (int i = 0; i < length; i++) {
//            result[i] = convertByteArrayToShort(name, start + i * 2, bytes,
//                    byteOrder);
//            // byte byte0 = bytes[start + i * 2];
//            // byte byte1 = bytes[start + i * 2 + 1];
//            // result[i] = convertBytesToShort(name, byte0, byte1, byteOrder);
//        }
//        return result;
//    }
    public final byte[] readByteArray(byte[] result) throws IOException {
        int read = 0;
        while (read < result.length) {
            int count = is.read(result, read, result.length - read);
            if (count < 1) {
                throw new IOException("Cannot read specified number of bytes!");
            }
            read += count;
        }
        if (debug) {
            for (int i = 0; ((i < result.length) && (i < 150)); i++) {
                debugNumber("byte-array" + " (" + i + ")", 0xff & result[i]);
            }
        }
        return result;
    }

    public final byte[] readByteArray(String name, int length, String exception)
            throws IOException {
        byte result[] = new byte[length];
        int read = 0;
        while (read < length) {
            int count = is.read(result, read, length - read);
            if (count < 1) {
                throw new IOException(exception);
            }
            read += count;
        }
        if (debug) {
            for (int i = 0; ((i < length) && (i < 150)); i++) {
                debugNumber(name + " (" + i + ")", 0xff & result[i]);
            }
        }
        return result;
    }

    protected final void debugByteArray(String name, byte bytes[]) {
        System.out.println(name + ": " + bytes.length);
        for (int i = 0; ((i < bytes.length) && (i < 50)); i++) {
            debugNumber(name + " (" + i + ")", bytes[i]);
        }
    }

    protected final void debugNumberArray(String name, int numbers[], int length) {
        System.out.println(name + ": " + numbers.length);
        for (int i = 0; ((i < numbers.length) && (i < 50)); i++) {
            debugNumber(name + " (" + i + ")", numbers[i], length);
        }
    }
//
//
//    public final int read2ByteInteger(String exception)
//            throws IOException {
//        int byte0 = is.read();
//        int byte1 = is.read();
//        if (byte0 < 0 || byte1 < 0) {
//            throw new IOException(exception);
//        }
//        if (byteOrder == ByteOrder.BIG_ENDIAN) // motorola, big endian
//        {
//            return ((0xff & byte0) << 8) + ((0xff & byte1) << 0);
//        } else // intel, little endian
//        {
//            return ((0xff & byte1) << 8) + ((0xff & byte0) << 0);
//        }
//    }
//
//    public final int read4ByteInteger(String exception)
//            throws IOException {
//        int byte0 = is.read();
//        int byte1 = is.read();
//        int byte2 = is.read();
//        int byte3 = is.read();
//        if (byte0 < 0 || byte1 < 0 || byte2 < 0 || byte3 < 0) {
//            throw new IOException(exception);
//        }
//        if (byteOrder == ByteOrder.BIG_ENDIAN) // motorola, big endian
//        {
//            return ((0xff & byte0) << 24) + ((0xff & byte1) << 16)
//                    + ((0xff & byte2) << 8) + ((0xff & byte3) << 0);
//        } else // intel, little endian
//        {
//            return ((0xff & byte3) << 24) + ((0xff & byte2) << 16)
//                    + ((0xff & byte1) << 8) + ((0xff & byte0) << 0);
//        }
//    }

    protected final void printCharQuad(String msg, int i) {
        System.out.println(msg + ": '" + (char) (0xff & (i >> 24))
                + (char) (0xff & (i >> 16)) + (char) (0xff & (i >> 8))
                + (char) (0xff & (i >> 0)) + "'");
    }

    protected final void printByteBits(String msg, byte i) {
        System.out.println(msg + ": '" + Integer.toBinaryString(0xff & i));
    }

    protected final static int charsToQuad(char c1, char c2, char c3, char c4) {
        return (((0xff & c1) << 24) | ((0xff & c2) << 16) | ((0xff & c3) << 8) | ((0xff & c4) << 0));
    }

    public final int findNull(byte src[]) {
        return findNull(src, 0);
    }

    public final int findNull(byte src[], int start) {
        for (int i = start; i < src.length; i++) {
            if (src[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    protected final byte[] getRAFBytes(RandomAccessFile raf, long pos,
            int length, String exception) throws IOException {
        byte result[] = new byte[length];
        if (debug) {
            System.out.println("getRAFBytes pos" + ": " + pos);
            System.out.println("getRAFBytes length" + ": " + length);
        }
        raf.seek(pos);
        int read = 0;
        while (read < length) {
            int count = raf.read(result, read, length - read);
            if (count < 1) {
                throw new IOException(exception);
            }
            read += count;
        }
        return result;
    }

    protected void skipBytes(int length) throws IOException {
        skipBytes(length, "Couldn't skip bytes");
    }
}
