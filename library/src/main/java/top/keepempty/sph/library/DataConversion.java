package top.keepempty.sph.library;


/**
 * 数据转换工具
 * @author ：frey
 * @date：2019/4/03 16:15
 */
public class DataConversion {

    /**
     * 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
     * @param num
     * @return
     */
    public static int isOdd(int num) {
        return num & 0x1;
    }

    /**
     * 将int转成byte
     * @param number
     * @return
     */
    public static byte intToByte(int number){
        return hexToByte(intToHex(number));
    }

    /**
     * 将int转成hex字符串
     * @param number
     * @return
     */
    public static String intToHex(int number){
        String st = Integer.toHexString(number).toUpperCase();
        return String.format("%2s",st).replaceAll(" ","0");
    }

    /**
     * 字节转十进制
     * @param b
     * @return
     */
    public static int byteToDec(byte b){
        String s = byteToHex(b);
        return (int) hexToDec(s);
    }

    /**
     * 字节数组转十进制
     * @param bytes
     * @return
     */
    public static int bytesToDec(byte[] bytes){
        String s = encodeHexString(bytes);
        return (int)  hexToDec(s);
    }

    /**
     * Hex字符串转int
     *
     * @param inHex
     * @return
     */
    public static int hexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    /**
     * 字节转十六进制字符串
     * @param num
     * @return
     */
    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits).toUpperCase();
    }

    /**
     * 十六进制转byte字节
     * @param hexString
     * @return
     */
    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static  int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    /**
     * 字节数组转十六进制
     * @param byteArray
     * @return
     */
    public static String encodeHexString(byte[] byteArray) {
        if(byteArray==null){
            return "";
        }
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString().toUpperCase();
    }

    /**
     * 十六进制转字节数组
     * @param hexString
     * @return
     */
    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    /**
     * 十进制转十六进制
     * @param dec
     * @return
     */
    public static String decToHex(int dec){
        String hex = Integer.toHexString(dec);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toLowerCase();
    }

    /**
     * 十六进制转十进制
     * @param hex
     * @return
     */
    public static long hexToDec(String hex){
        return Long.parseLong(hex, 16);
    }

}