package edu.dlut.software.cagetian;/*package com.nmbox.common;*/

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/*
import org.apache.commons.dbcp2.BasicDataSource;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
*/


public class Tool {
	private static final String hex = "0123456789ABCDEF";
	private static final java.text.SimpleDateFormat sdf_datetime = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final java.text.SimpleDateFormat sdf_short_datetime = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static final java.text.SimpleDateFormat sdf_date = new java.text.SimpleDateFormat("yyyy-MM-dd");
	private static final java.text.SimpleDateFormat sdf_time = new java.text.SimpleDateFormat("HH:mm:ss");

	public static String toString(Object o) {
		if (o == null) {
			return "";
		} else
			return o.toString();
	}

	public boolean isDigits(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean isEnglishLetter(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if ((s.charAt(i) >= 'a' && s.charAt(i) <= 'z') || (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z')) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/*
	public static String toBase64(byte[] data) {
		if (data == null || data.length == 0) {
			return new String();
		}
		try {
			return new String(org.apache.commons.codec.binary.Base64.encodeBase64(data, false), "ISO-8859-1");
		} catch (Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	public static String toBase64Chunked(byte[] data) {
		if (data == null || data.length == 0) {
			return new String();
		}
		try {
			return new String(org.apache.commons.codec.binary.Base64.encodeBase64(data, true), "ISO-8859-1");
		} catch (Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	public static byte[] convertBase64ToByteArray(String data) {
		if (data == null || data.length() == 0) {
			return new byte[0];
		}
		return org.apache.commons.codec.binary.Base64.decodeBase64(data);
	}

	*/
	
	public static String trimNonHex(String hexStringWithBlank) {
		if (hexStringWithBlank == null || hexStringWithBlank.length() == 0) {
			return new String();
		} else {
			StringBuffer sb = new StringBuffer();
			hexStringWithBlank = hexStringWithBlank.toUpperCase();
			for (int i = 0; i < hexStringWithBlank.length(); i++) {
				if (hexStringWithBlank.charAt(i) >= '0' && hexStringWithBlank.charAt(i) <= '9') {
					sb.append(hexStringWithBlank.charAt(i));
				} else if (hexStringWithBlank.charAt(i) >= 'A' && hexStringWithBlank.charAt(i) <= 'F') {
					sb.append(hexStringWithBlank.charAt(i));
				}
			}
			return sb.toString();
		}
	}

	public static String toHex(byte[] data) {
		if (data == null || data.length == 0) {
			return new String();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i != data.length; i++) {
			int v = data[i] & 0xff;

			buf.append(hex.charAt(v >> 4));
			buf.append(hex.charAt(v & 0xf));
		}
		return buf.toString();
	}

	public static String toHex(byte data) {
		StringBuffer buf = new StringBuffer();
		int v = data & 0xff;

		buf.append(hex.charAt(v >> 4));
		buf.append(hex.charAt(v & 0xf));
		return buf.toString();
	}

	public static byte[] convertHexToByteArray(String hexString) {
		if (hexString == null || hexString.length() == 0) {
			return new byte[0];
		} else {
			if (hexString.length() % 2 == 1) {
				throw new IllegalArgumentException("hex string length is odd number");
			}
			hexString = hexString.toUpperCase();
			byte[] buffer = new byte[hexString.length() / 2];
			int i1 = 0;
			int i2 = 0;

			for (int i = 0; i < hexString.length();) {
				char c1 = hexString.charAt(i);
				char c2 = hexString.charAt(i + 1);
				if (c1 >= '0' && c1 <= '9') {
					i1 = c1 - '0';
				} else {
					i1 = c1 - 'A' + 10;
				}
				if (c2 >= '0' && c2 <= '9') {
					i2 = c2 - '0';
				} else {
					i2 = c2 - 'A' + 10;
				}
				buffer[i / 2] = (byte) (i1 * 16 + i2);
				i = i + 2;
			}
			return buffer;
		}
	}

	public static String toString(byte[] data) {
		if (data == null || data.length == 0) {
			return new String();
		}

		char[] chars = new char[data.length];

		for (int i = 0; i != chars.length; i++) {
			chars[i] = (char) (data[i] & 0xff);
		}

		return new String(chars);
	}

	public static String toDecimalString(byte[] data) {
		if (data == null || data.length == 0) {
			return new String();
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			sb.append(data[i] + ",");
		}

		String s = sb.toString();
		if (s.charAt(s.length() - 1) == ',') {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	public static byte[] parseByteArray(String decimalString) {
		if (decimalString == null) {
			return new byte[0];
		}
		decimalString = decimalString.trim();
		if (decimalString.length() == 0) {
			return new byte[0];
		}
		if (decimalString.startsWith("[") || decimalString.startsWith("{") || decimalString.startsWith("(")) {
			decimalString = decimalString.substring(1);
		}
		if (decimalString.endsWith("]") || decimalString.endsWith("}") || decimalString.endsWith(")")
				|| decimalString.endsWith(",")) {
			decimalString = decimalString.substring(0, decimalString.length() - 1);
		}

		if (decimalString.length() == 0) {
			return new byte[0];
		}

		String[] da = decimalString.split(",");
		byte[] buffer = new byte[da.length];
		for (int i = 0; i < da.length; i++) {
			try {
				buffer[i] = (byte) Integer.parseInt(da[i]);
			} catch (Exception ex) {
				buffer[i] = 0;
			}
		}
		return buffer;
	}

	public static byte[] toByteArray(String data) {
		if (data == null || data.length() == 0) {
			return new byte[0];
		}

		byte[] bytes = new byte[data.length()];
		char[] chars = data.toCharArray();

		for (int i = 0; i != chars.length; i++) {
			bytes[i] = (byte) chars[i];
		}

		return bytes;
	}

	public static byte toByte(String oneByteString) {
		if (oneByteString == null || oneByteString.length() == 0) {
			return 0;
		}
		if (oneByteString.length() == 1) {
			oneByteString = "0".concat(oneByteString);
		}
		oneByteString = oneByteString.toUpperCase();

		int i1 = 0, i2 = 0;
		char c1 = oneByteString.charAt(0);
		char c2 = oneByteString.charAt(1);
		if (c1 >= '0' && c1 <= '9') {
			i1 = c1 - '0';
		} else {
			i1 = c1 - 'A' + 10;
		}
		if (c2 >= '0' && c2 <= '9') {
			i2 = c2 - '0';
		} else {
			i2 = c2 - 'A' + 10;
		}
		return (byte) (i1 * 16 + i2);

	}

	public static byte[] concat(byte[] a, byte[] b) {
		if (a == null && b == null) {
			return new byte[0];
		} else if (a == null && b != null) {
			return copy(b);
		} else if (a != null && b == null) {
			return copy(a);
		} else {
			byte[] buffer = new byte[a.length + b.length];
			System.arraycopy(a, 0, buffer, 0, a.length);
			System.arraycopy(b, 0, buffer, a.length, b.length);
			return buffer;
		}
	}

	public static byte[] subByteArray(byte[] data, int start, int end) {
		if (data == null) {
			return new byte[0];
		} else {
			if (start < 0 || end < 0 || end > data.length || start > end || start > data.length) {
				throw new IndexOutOfBoundsException();
			}
			byte[] buffer = new byte[end - start];
			System.arraycopy(data, start, buffer, 0, (end - start));
			return buffer;
		}

	}

	public static byte[] subByteArray(byte[] data, int start) {
		return subByteArray(data, start, data.length);
	}

	public static int indexOf(byte[] data, byte s) {
		return indexOf(data, s, 0);
	}

	public static int indexOf(byte[] data, byte s, int fromIndex) {
		int result = -1;
		if (data == null) {
			return result;
		} else {
			if (fromIndex >= data.length || fromIndex < 0) {
				return -1;
			}
			for (int i = fromIndex; i < data.length; i++) {
				if (data[i] == s) {
					result = i;
					break;
				}
			}
			return result;
		}
	}

	public static int indexOf(byte[] data, byte[] search) {
		return indexOf(data, search, 0);
	}

	public static int indexOf(byte[] data, byte[] search, int fromIndex) {
		if (data == null || data.length == 0 || search == null || search.length == 0) {
			return -1;
		}
		if (data.length < search.length) {
			return -1;
		}
		if (fromIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (fromIndex >= data.length) {
			throw new IndexOutOfBoundsException();
		}
		if (fromIndex > data.length - search.length) {
			return -1;
		}
		for (int i = fromIndex; i <= data.length - search.length; i++) {
			boolean equal = true;
			int temp = i;
			for (int j = 0; j < search.length; j++) {
				if (!(search[j] == data[temp])) {
					equal = false;
					break;
				}
				temp++;
			}
			if (equal) {
				return i;
			}
		}
		return -1;
	}

	public static boolean equals(byte[] a, byte[] b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null && b != null) {
			return false;
		} else if (a != null && b == null) {
			return false;
		} else {
			if (a.length != b.length) {
				return false;
			} else {
				for (int i = 0; i < a.length; i++) {
					if (a[i] != b[i]) {
						return false;
					}
				}
				return true;
			}
		}
	}

	public static boolean startsWith(byte[] source, byte[] starts) {
		if (source == null && starts == null) {
			return true;
		} else if (source == null && starts != null) {
			return false;
		} else if (source != null && starts == null) {
			return false;
		} else {
			if (starts.length == 0 && source.length == 0) {
				return true;
			} else if (starts.length == 0 && source.length != 0) {
				return false;
			} else if (starts.length != 0 && source.length == 0) {
				return false;
			} else {
				for (int i = 0; i < starts.length; i++) {
					if (source[i] != starts[i]) {
						return false;
					}
				}
				return true;
			}
		}
	}

	public static boolean endsWith(byte[] source, byte[] ends) {
		if (source == null && ends == null) {
			return true;
		} else if (source == null && ends != null) {
			return false;
		} else if (source != null && ends == null) {
			return false;
		} else {
			if (ends.length == 0 && source.length == 0) {
				return true;
			} else if (ends.length == 0 && source.length != 0) {
				return false;
			} else if (ends.length != 0 && source.length == 0) {
				return false;
			} else {
				for (int i = ends.length - 1; i >= 0; i--) {
					if (source[source.length - ends.length + i] != ends[i]) {
						return false;
					}
				}
				return true;
			}
		}
	}

	public static byte[] copy(byte[] a) {
		if (a == null) {
			return new byte[0];
		} else {
			byte[] buffer = new byte[a.length];
			System.arraycopy(a, 0, buffer, 0, a.length);
			return buffer;
		}
	}

	public static byte[] reverse(byte[] a) {
		if (a == null || a.length == 0) {
			return new byte[0];
		} else {
			byte[] buffer = new byte[a.length];
			for (int i = 0; i < a.length; i++) {
				buffer[a.length - i - 1] = a[i];
			}
			return buffer;
		}
	}

	public static byte[] append(byte[] a, byte c) {
		if (a == null || a.length == 0) {
			return new byte[] { c };
		} else {
			byte[] buffer = new byte[a.length + 1];
			System.arraycopy(a, 0, buffer, 0, a.length);
			buffer[a.length] = c;
			return buffer;
		}
	}

	public static byte[] insert(byte[] a, byte c, int index) {
		if (a == null) {
			a = new byte[0];
		}
		byte[] buffer = new byte[a.length + 1];

		if (index < 0 || index > a.length) {
			throw new IndexOutOfBoundsException("invalid index: " + index);
		}

		if (index == 0) {
			buffer[0] = c;
			System.arraycopy(a, 0, buffer, 1, a.length);
		} else {
			System.arraycopy(a, 0, buffer, 0, index);
			buffer[index] = c;
			if (index != a.length) {
				System.arraycopy(a, index, buffer, index + 1, a.length - index);
			}
		}
		return buffer;
	}

	public static byte[] insert(byte[] a, byte[] b, int index) {
		if (a == null) {
			a = new byte[0];
		}
		if (b == null || b.length == 0) {
			byte[] buffer = copy(a);
			return buffer;
		}

		if (index < 0 || index > a.length) {
			throw new IndexOutOfBoundsException("invalid index: " + index);
		}

		if (index == 0) {
			return concat(b, a);
		} else if (index == a.length) {
			return concat(a, b);
		} else {
			byte[] b1 = subByteArray(a, 0, index);
			byte[] b2 = concat(b1, b);
			byte[] b3 = subByteArray(a, index);
			return concat(b2, b3);
		}
	}

	public static byte[][] split(byte[] data, int size) {

		if (data == null || data.length == 0) {
			return new byte[0][0];
		} else {
			if (size <= 0) {
				byte[][] result = new byte[1][];
				result[0] = copy(data);
				return result;
			} else {
				ArrayList<byte[]> temp = new ArrayList<byte[]>();
				int remain = data.length;
				int index = 0;
				while (remain >= size) {
					byte[] buffer = new byte[size];
					System.arraycopy(data, index, buffer, 0, buffer.length);
					index = index + size;
					remain = remain - size;
					temp.add(buffer);
				}
				if (remain != 0) {
					byte[] buffer = new byte[remain];
					System.arraycopy(data, index, buffer, 0, buffer.length);
					temp.add(buffer);
				}
				return temp.toArray(new byte[0][]);
			}
		}
	}

	public static byte[] removeFirst(byte[] data, byte delete) {
		if (data == null || data.length == 0) {
			return new byte[0];
		} else {
			for (int i = 0; i < data.length; i++) {
				if (data[i] == delete) {
					return concat(subByteArray(data, 0, i), subByteArray(data, i + 1));
				}
			}
			return copy(data);
		}
	}

	public static byte[] removeAll(byte[] data, byte delete) {
		if (data == null || data.length == 0) {
			return new byte[0];
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < data.length; i++) {
				if (data[i] != delete) {
					sb.append(toHex(data[i]));
				}
			}
			return convertHexToByteArray(sb.toString());
		}
	}

	public static byte[] removeFirst(byte[] data, byte[] delete) {
		if (data == null || data.length == 0) {
			return new byte[0];
		}
		if (delete == null || delete.length == 0) {
			return copy(data);
		}

		int p1 = indexOf(data, delete);
		if (p1 != -1) {
			return remove(data, p1, p1 + delete.length);
		} else {
			return copy(data);
		}
	}

	public static byte[] removeAll(byte[] data, byte[] delete) {
		if (data == null || data.length == 0) {
			return new byte[0];
		}
		if (delete == null || delete.length == 0) {
			return copy(data);
		}

		int p1 = -1;
		p1 = indexOf(data, delete);
		while (p1 != -1) {
			data = removeFirst(data, delete);
			p1 = indexOf(data, delete);
		}

		return copy(data);
	}

	public static byte[] remove(byte[] data, int start, int end) {
		if (data == null || data.length == 0) {
			return new byte[0];
		} else {
			if (start == end) {
				return copy(data);
			} else {
				if (start > end || start < 0 || end > data.length || start >= data.length) {
					throw new IndexOutOfBoundsException("start=" + start + ", end=" + end);
				}
				return concat(subByteArray(data, 0, start), subByteArray(data, end));
			}
		}
	}

	public static byte[] remove(byte[] data, int index) {
		return remove(data, index, index + 1);
	}

	public static short reverseBytes(short s) {
		return Short.reverseBytes(s);
	}

	public static int reverseBytes(int i) {
		return Integer.reverseBytes(i);
	}

	public static long reverseBytes(long l) {
		return Long.reverseBytes(l);
	}

	public static float reverseBytes(float f) {
		return Float.intBitsToFloat(Integer.reverseBytes(Float.floatToIntBits(f)));
	}

	public static byte[] toByteArray(short s) {
		byte[] buffer = new byte[2];
		buffer[0] = (byte) (s >> 8 & 0xff);
		buffer[1] = (byte) (s & 0xff);
		return buffer;

	}

	public static byte[] toByteArray(int i) {
		byte[] buffer = new byte[4];
		buffer[0] = (byte) (i >> 24 & 0xff);
		buffer[1] = (byte) (i >> 16 & 0xff);
		buffer[2] = (byte) (i >> 8 & 0xff);
		buffer[3] = (byte) (i & 0xff);
		return buffer;

	}

	public static byte[] toByteArray(long l) {
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (l >> ((8 - i - 1) * 8) & 0xff);
		}
		return buffer;
	}

	public static byte[] toByteArray(float f) {
		return toByteArray(Float.floatToIntBits(f));
	}

	public static byte[] toByteArray(double d) {
		return toByteArray(Double.doubleToLongBits(d));
	}

	public static short getShort(byte[] data) {
		short s = 0;
		s = (short) (s | (data[0] << 8 & 0xff00));
		s = (short) (s | (data[1] & 0xff));
		return s;

	}

	public static int getInt(byte[] data) {
		int i = 0;
		i = i | (data[0] << 24 & 0xff000000);
		i = i | (data[1] << 16 & 0xff0000);
		i = i | (data[2] << 8 & 0xff00);
		i = i | (data[3] & 0xff);
		return i;
	}

	public static long getLong(byte[] data) {
		long l = 0;
		for (int i = 0; i < 8; i++) {
			l = l | ((long) data[i] << ((8 - i - 1) * 8) & (0xffL << (8 - i - 1) * 8));
		}
		return l;
	}

	public static float getFloat(byte[] data) {
		return Float.intBitsToFloat(getInt(data));
	}

	public static double getDouble(byte[] data) {
		return Double.longBitsToDouble(getLong(data));
	}

	public static byte[] getGbkBytes(String s) {
		try {
			return s.getBytes("GBK");
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static String toStringFromGbk(byte[] buffer) {
		try {
			return new String(buffer, "GBK");
		} catch (Exception e) {
			return "";
		}
	}

	public static byte[] getUtf8Bytes(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static String toStringFromUtf8(byte[] buffer) {
		try {
			return new String(buffer, "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}

	public static byte[] getUnicodeBytes(String s) {
		try {
			return s.getBytes("UnicodeBigUnmarked");
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static String toStringFromUnicode(byte[] buffer) {
		try {
			return new String(buffer, "UnicodeBigUnmarked");
		} catch (Exception e) {
			return "";
		}
	}

	public static byte[] serialize(Serializable object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
			baos.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static void serialize(Serializable object, OutputStream os) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(object);
			oos.flush();
		} catch (Exception e) {
		}
	}

	public static Object deserialize(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	public static Object deserialize(InputStream is) {
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			return ois.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	/*
	public static String toJSONString(Object o) {
		return com.alibaba.fastjson.JSON.toJSONString(o);
	}

	public static <T> T parseJSONString(String jsonString, Class<T> t) {
		return (T) com.alibaba.fastjson.JSON.parseObject(jsonString, t);
	}
	*/

	public static String getDateTimeString(Date d) {
		return sdf_datetime.format(d);
	}

	public static String getShortDateTime(Date d) {
		return sdf_short_datetime.format(d);
	}

	public static String getCurrentDateTimeString() {
		return sdf_datetime.format(new Date());
	}

	public static String getDateString(Date d) {
		return sdf_date.format(d);
	}

	public static String getCurrentDateString() {
		return sdf_date.format(new Date());
	}

	public static String getTimeString(Date d) {
		return sdf_time.format(d);
	}

	public static String getCurrentTimeString() {
		return sdf_time.format(new Date());
	}

	public static Date parseDateTime(String dt) {
		try {
			return sdf_datetime.parse(dt);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date parseDate(String date) {
		try {
			return sdf_date.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getLunarDay(Date date) {
		return null;
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String getUUID32() {
		String uuid = getUUID();
		return uuid.replace("-", "");
	}

	public static String getID() { // 36-length character
		return String.valueOf((int) (Math.random() * 9000 + 1000)) + getUUID32();
	}

	private static char[] digits = { '9', '2', '5', '0', '3', '8', '6', '7', '1', '4' };

	public static String getAuthCode() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 6; i++) {
			int s = r.nextInt(10);
			sb.append(digits[s]);
		}
		return sb.toString();
	}

	public static String getSerialNo() {
		StringBuffer sb = new StringBuffer();
		String s = getUUID();
		s = s.replace("-", "");
		s = s.toUpperCase();
		char[] ca = new char[26 + 10 + 26];
		for (int i = 0; i < 26; i++) {
			ca[i] = (char) ('A' + i);
		}
		for (int i = 0; i <= 9; i++) {
			ca[i + 26] = (char) ('0' + i);
		}
		for (int i = 0; i < 26; i++) {
			ca[10 + i + 26] = (char) ('A' + i);
		}

		Random r = new Random();
		String s2 = new String(ca);
		s2 = s2.concat(s);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				sb.append(s2.charAt(r.nextInt(s2.length())));
			}
			if (i != 4) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	public static final int MAX_BUFFER_SIZE = 16 * 1024 * 1024;

	public static byte[] compress(byte[] input) {
		byte[] output = new byte[MAX_BUFFER_SIZE];
		// Deflater compresser = new Deflater(Deflater.BEST_SPEED, true);
		Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION, true);

		compresser.setInput(input);
		compresser.finish();

		int len = compresser.deflate(output);
		compresser.end();

		byte[] result = new byte[len];
		System.arraycopy(output, 0, result, 0, len);
		return result;
	}

	public static byte[] decompress(byte[] input) {
		try {
			Inflater decompresser = new Inflater(true);
			decompresser.setInput(input, 0, input.length);
			byte[] output = new byte[MAX_BUFFER_SIZE];
			int len = decompresser.inflate(output);
			decompresser.end();
			byte[] result = new byte[len];
			System.arraycopy(output, 0, result, 0, len);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	private static final String pkey = "O8XFAYT9JB4ETH87OXWF5Q06OEW6Z6EXLQKB4361U68AC4D93FX9KZ14Y2523BFTSF"
			+ "52YF9U6W5AX34D42D1C8X8896H88E4C198E7EXEB080XHB5V95Z1K0JRA4BOFF94K621I6BBC8347SFFRFRF39C2C7V"
			+ "JASEC24B48L0F4IB1K9B8FBTMRH9CS80EN6N68X71U64V834GQ6FCXT7Z375FS9OI3HBM6F2FFWH0AF5CC51FE20BN1"
			+ "2792B2R51G44C75334AV1168I9OB07AAWIO31BABX4G98A22AB7TA5EZE4B4REYCPF53C4A6R34RK5O8774D3QJBC5G"
			+ "27Y4V3BH2CUET71B9W24N11N45A8B92V6X69VVD37LYG9CUW614B7U9D32F3352F26R075I7VH9CL7V6EX0AN557EBY"
			+ "G4O9M5UQ62CJ83AVF162EZZV4S1O80ZP437163BPE631ECCCB0FK55223ZMC639E29940P6O31HGEEIE9M8B0ADB1H7"
			+ "G9NEQ1CG8DAY13J1C1Q731C7HZVAC1LMAR15EUDAP2IYNA9KH2KYLAQFBE3Q3539D1M5OAJ2F4BAFL70SEQYBSCBQD3"
			+ "BFAC64SD04PSGW06XF1ADARYDB93105116DB59I2B34Q6IE948P2349SPHBA8Z8F081VB04FJUZ80JHUPX54BCE58D4"
			+ "P81357A8RWAMN44DY8A49581AWBVAB7B2AKVN1SUB1B8NB4I6B5439VB54FBH9FFFFZCO804F8MF4912LD87ZAVBJSW"
			+ "FLFHR5189597X5DBR9DF5Q83E7Q1T62ND0DZQF67PB83DELQ6MQGXSUZLL3J6JY0TF41JIXRKAO245V515R38G373Z1"
			+ "N30WVPOB481QDQEGEJ9W883R3C242RYLZ8RD3IT31D7H1FE9VW9X9ADK3QU1BC5HS05B77P8A90B5RC7RK03PW7BX4N"
			+ "2C6F1FA9146ZE5Y8BAAT3B4SF04F71OH5D5C4ARCD7D70N74";
	private static final String aeskey = "ACED00057372001F6A617661782E63727970746F2E737065632E5365637265744"
			+ "B6579537065635B470B66E230614D0200024C0009616C676F726974686D7400124C6A6176612F6C616E672F53747"
			+ "2696E673B5B00036B65797400025B427870740003414553757200025B42ACF317F8060854E002000078700000001"
			+ "05F226F6452F56B28FEB96947587F6787";

	private static Key getKey() {
		Key key = (Key) deserialize(convertHexToByteArray(aeskey));
		return key;
	}

	private static Key aesKey = null;
	private static Cipher eCipher = null;
	private static byte[] bpkey = null;

	public static byte[] quickEncrypt(byte[] input) {
		if (aesKey == null) {
			aesKey = getKey();
		}
		if (bpkey == null) {
			bpkey = toByteArray(pkey);
		}
		if (eCipher == null) {
			try {
				eCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		try {
			eCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			byte[] e1 = eCipher.doFinal(input);
			for (int i = 0; i < e1.length; i++) {
				e1[i] = (byte) (e1[i] ^ bpkey[(i + e1.length) % bpkey.length]);
				e1[i] = (byte) (e1[i] >> 4 & 0x0F | e1[i] << 4 & 0xF0);
			}
			return e1;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] quickDecrypt(byte[] input) {

		if (aesKey == null) {
			aesKey = getKey();
		}
		if (bpkey == null) {
			bpkey = toByteArray(pkey);
		}

		if (eCipher == null) {
			try {
				eCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		try {

			for (int i = 0; i < input.length; i++) {
				input[i] = (byte) (input[i] >> 4 & 0x0F | input[i] << 4 & 0xF0);
				input[i] = (byte) (input[i] ^ bpkey[(i + input.length) % bpkey.length]);
			}

			eCipher.init(Cipher.DECRYPT_MODE, aesKey);

			return eCipher.doFinal(input);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] encrypt(byte[] input) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, getKey());
			byte[] e1 = cipher.doFinal(input);
			byte[] k1 = toByteArray(pkey);

			for (int i = 0; i < e1.length; i++) {
				e1[i] = (byte) (e1[i] ^ k1[(i + e1.length) % k1.length]);
				e1[i] = (byte) (e1[i] >> 4 & 0x0F | e1[i] << 4 & 0xF0);
			}
			return e1;
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] decrypt(byte[] input) {
		try {
			byte[] k1 = toByteArray(pkey);
			for (int i = 0; i < input.length; i++) {
				input[i] = (byte) (input[i] >> 4 & 0x0F | input[i] << 4 & 0xF0);
				input[i] = (byte) (input[i] ^ k1[(i + input.length) % k1.length]);
			}

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, getKey());

			return cipher.doFinal(input);
		} catch (Exception e) {
			return null;
		}
	}

	public static String encryptPassword(String password) {
		try {
			if (password == null) {
				password = "";
			}
			byte[] bp = password.getBytes("GBK");
			byte[] ebp = quickEncrypt(bp);
			return toHex(ebp);
		} catch (Exception ex) {
			return "";
		}
	}

	public static KeyPair generateRSAKeyPair(int keySize) {
		try {
			KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance("RSA");
			rsaKeyGen.initialize(keySize, new SecureRandom());
			KeyPair rsaKeyPair = rsaKeyGen.genKeyPair();
			return rsaKeyPair;
		} catch (Exception e) {
			return null;
		}
	}

	public static RSAPublicKey getRSAPublicKey(KeyPair rsaKeyPair) {
		return (RSAPublicKey) rsaKeyPair.getPublic();
	}

	public static RSAPrivateKey getRSAPrivateKey(KeyPair rsaKeyPair) {
		return (RSAPrivateKey) rsaKeyPair.getPrivate();
	}

	public static byte[] encryptByRSA(byte[] input, Key rsaKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, rsaKey);
			byte[] e1 = cipher.doFinal(input);
			return e1;
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] decryptByRSA(byte[] input, Key rsaKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, rsaKey);

			return cipher.doFinal(input);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] encryptByDES(byte[] input, Key desKey) {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			byte[] e1 = cipher.doFinal(input);
			return e1;
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] decryptByDES(byte[] input, Key desKey) {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, desKey);

			return cipher.doFinal(input);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] encryptByAES(byte[] input, Key aesKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			byte[] e1 = cipher.doFinal(input);
			return e1;
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] decryptByAES(byte[] input, Key aesKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, aesKey);

			return cipher.doFinal(input);
		} catch (Exception e) {
			return null;
		}
	}

	// 256 need policy.jar
	public static SecretKey generateAESKey(int keySize) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(keySize, new SecureRandom());
			return kgen.generateKey();
		} catch (Exception e) {
			return null;
		}
	}

	public static SecretKey generateDESKey() {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("DES");
			kgen.init(56, new SecureRandom());
			return kgen.generateKey();
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] getMD5(byte[] data) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data);
			return md5.digest();

		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] getSHA1(byte[] data) {
		try {
			MessageDigest sha_1 = MessageDigest.getInstance("SHA-1");
			sha_1.update(data);
			return sha_1.digest();

		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] getSHA256(byte[] data) {
		try {
			MessageDigest sha_256 = MessageDigest.getInstance("SHA-256");
			sha_256.update(data);
			return sha_256.digest();

		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] getSignature(byte[] data, PrivateKey privKey) {
		if (data == null || privKey == null) {
			return new byte[0];
		}
		try {
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(privKey);
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static boolean verifySign(byte[] data, byte[] sign, PublicKey pubKey) {
		if (sign == null || sign.length == 0) {
			return false;
		}
		try {
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initVerify(pubKey);
			signature.update(data);
			return signature.verify(sign);
		} catch (Exception e) {
			return false;
		}

	}

	public static byte[] getSignature(byte[] data, PrivateKey privKey, String algorithm) {
		if (data == null || privKey == null) {
			return new byte[0];
		}
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(privKey);
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public static boolean verifySign(byte[] data, byte[] sign, PublicKey pubKey, String algorithm) {
		if (sign == null || sign.length == 0 || pubKey == null || algorithm == null || algorithm.length() == 0) {
			return false;
		}
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initVerify(pubKey);
			signature.update(data);
			return signature.verify(sign);
		} catch (Exception e) {
			return false;
		}

	}

	public static String convertToSize(long length) {
		if (length / 1024 < 1) {
			return "" + length + "B";
		} else if (length / 1024 / 1024 < 1) {
			double d = 1.0 * length / 1024;
			d = 1.0 * Math.round(d * 10) / 10;
			String sd = "" + d;
			int p = sd.indexOf(".");
			if (p != -1) {
				sd = sd.substring(0, p + 2);
			}
			return "" + sd + "KB";
		} else if (length / 1024 / 1024 / 1024 < 1) {
			double d = 1.0 * length / 1024 / 1024;
			d = 1.0 * Math.round(d * 10) / 10;
			String sd = "" + d;
			int p = sd.indexOf(".");
			if (p != -1) {
				sd = sd.substring(0, p + 2);
			}
			return "" + sd + "MB";
		} else if (length / 1024 / 1024 / 1024 / 1024 < 1) {
			double d = 1.0 * length / 1024 / 1024 / 1024;
			d = 1.0 * Math.round(d * 10) / 10;
			String sd = "" + d;
			int p = sd.indexOf(".");
			if (p != -1) {
				sd = sd.substring(0, p + 2);
			}
			return "" + sd + "GB";
		} else {
			double d = 1.0 * length / 1024 / 1024 / 1024 / 1024;
			d = 1.0 * Math.round(d * 10) / 10;
			String sd = "" + d;
			int p = sd.indexOf(".");
			if (p != -1) {
				sd = sd.substring(0, p + 2);
			}
			return "" + sd + "TB";
		}

	}

	public static Properties loadProperties(String configFile) {
		Properties p = new Properties();

		try {
			p.load(Tool.class.getClassLoader().getResourceAsStream(configFile));
		} catch (Exception e) {
		}
		try {
			String parent = System.getProperty("user.home");
			File f = new File(parent, configFile);
			p.load(new FileInputStream(f));
		} catch (Exception e) {
		}
		try {
			String parent = System.getProperty("user.dir");
			File f = new File(parent, configFile);
			p.load(new FileInputStream(f));
		} catch (Exception e) {
		}

		p.putAll(System.getProperties());

		return p;
	}


	
	public static int getLen(byte[] data) {
		return (data[0] & 0xff) | (data[1] << 8 & 0xff00) | (data[2] << 16 & 0xff0000) | (data[3] << 24 & 0xff000000);
	}

	public static byte[] getData(byte[] data) {
		byte[] result = new byte[data.length - 4];
		System.arraycopy(data, 4, result, 0, result.length);
		return result;
	}

	public static byte[] concat(int len, byte[] data) {
		byte[] result = new byte[data.length + 4];
		System.arraycopy(data, 0, result, 4, data.length);

		result[0] = (byte) (len & 0xff);
		result[1] = (byte) (len >> 8 & 0xff);
		result[2] = (byte) (len >> 16 & 0xff);
		result[3] = (byte) (len >> 24 & 0xff);

		return result;
	}
	
	public static byte[] concat2(int len, byte[] data) {
		byte[] result = new byte[data.length + 4];
		System.arraycopy(data, 0, result, 4, data.length);
		result[3] = (byte) (len & 0xff);
		result[2] = (byte) (len >> 8 & 0xff);
		result[1] = (byte) (len >> 16 & 0xff);
		result[0] = (byte) (len >> 24 & 0xff);

		return result;
	}
	
	

	//pay attention to little endian, big endian
	public static byte[] getTLV(int tag, byte[] data) {
		if (data == null) {
			data = new byte[0];
		}
		data = concat2(data.length, data); // data length, not include tag.
		data = concat2(tag, data);
		return data;
	}
	
	public static void main(String[] args) throws Exception {
		byte[] test = new byte[] { 0, 9, 2, 1, 5, 3 };
		int tag = 5;
		byte[] buffer = getTLV(tag, test);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		DataInputStream dis = new DataInputStream(bais);
		System.out.println(dis.readInt());
		System.out.println(dis.readInt());
		byte[] test2 = new byte[3];
		dis.readFully(test2);
		System.out.println(test2[0]);
		System.out.println(test2[1]);
		
		KeyPair kp = Tool.generateRSAKeyPair(2048);
		byte[] result = Tool.encryptByRSA(test, kp.getPrivate());
		System.out.println(result.length);
		
				
	}

}
