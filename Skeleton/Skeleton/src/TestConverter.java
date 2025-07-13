public class TestConverter {
    public static void fromInt(int value, Word32 result) {
        for (int i = 0; i < 32; i++) {
            boolean bitValue = (value & (1 << (31 - i))) != 0;
            result.bits[i] = new Bit(bitValue);
        }
    }

    public static int toInt(Word32 value) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            if(value.bits[i].getValue() == Bit.boolValues.TRUE){
                result |= (1 << (31 - i));
            }
        }
        return result;
    }
}
