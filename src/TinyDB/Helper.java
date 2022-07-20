package TinyDB;

public class Helper {
    public static String byteArrayToString(byte[] arr) {
        String res = "";
        for (int i = 0; i < arr.length && arr[i] != 0; i++)
            res += (char) arr[i];
        return res;
    }

    public static byte[] stringToByteArray(Object value, int length) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        } else {
            byte[] arr = new byte[length];
            String str = (String) value;
            for (int i = 0; i < str.length(); i++)
                arr[i] = (byte) str.charAt(i);
            return arr;
        }
    }
}
