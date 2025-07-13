public class Word16 {
    //make the 16bit array
    public Bit[] bits = new Bit[16];

    public Word16() {
        //set all the bits in the array to false
        for(int i=0; i< 16; i++){
            bits[i] = new Bit(false);
        }
    }

    public Word16(Bit[] in) {
        for(int i=0; i<16; i++){
            bits[i]= new Bit(in[i].getValue()==Bit.boolValues.TRUE);
        }
    }

    public void copy(Word16 result) {
        // sets the values in "result" to be the same as the values in this instance; use "bit.assign"
        for(int i=0;i<16;i++){
            result.bits[i].assign(bits[i].getValue());

        }
    }

    public void setBitN(int n, Bit source) {
        // sets the nth bit of this word to the source bit
        bits[n].assign(source.getValue());
    }

    public void getBitN(int n, Bit result) {
        // sets result to be the same value as the nth bit of this word
        result.assign(bits[n].getValue());
    }

    public boolean equals(Word16 other) {
        //calling method below
        return equals(this,other);
    }

    public static boolean equals(Word16 a, Word16 b) {
        // is a equal to b
        for(int i=0; i<16; i++){
            if (a.bits[i].getValue() != b.bits[i].getValue()) {
                return false;
            }
        }
        return true;
    }

    public void and(Word16 other, Word16 result) {
        //calling static method below
        and(this,other,result);
    }

    public static void and(Word16 a, Word16 b, Word16 result) {
        //loop through all the bits in the word16 and call the and method from the bit class on them
        for(int i=0; i<16; i++){
            Bit.and(a.bits[i],b.bits[i],result.bits[i] );
        }
    }

    public void or(Word16 other, Word16 result) {
        //call the static method
        or(this,other,result);
    }

    public static void or(Word16 a, Word16 b, Word16 result) {
        //loop through and call the or method from the bit class
        for(int i=0; i<16; i++){
            Bit.or(a.bits[i],b.bits[i],result.bits[i]);
        }
    }

    public void xor(Word16 other, Word16 result) {
        //call the static method
        xor(this, other, result);
    }

    public static void xor(Word16 a, Word16 b, Word16 result) {
        //loop through and call the xor method from the bit class
        for(int i=0; i<16; i++){
            Bit.xor(a.bits[i],b.bits[i],result.bits[i]);
        }
    }

    public void not( Word16 result) {
        //call the static method
        not(this,result);
    }

    public static void not(Word16 a, Word16 result) {
        //loop through and call the not method from the bit class
        for(int i=0; i<16; i++){
            Bit.not(a.bits[i], result.bits[i]);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bit bit : bits) {
            sb.append(bit.toString());
            sb.append(",");
        }
        // Removing the extra comma at the end
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}