public class Adder {

    public static void add(Word32 a, Word32 b, Word32 result) {
        Bit sum = new Bit(false);
        Bit carry = new Bit(false);
        Word32 tempResult = new Word32();  // Temporary object to store the result

        // Loop through from LSB to MSB
        for (int i = 31; i >= 0; i--) {
            // XOR a and b store in sum
            Bit.xor(a.bits[i], b.bits[i], sum);
            Bit.xor(sum, carry, tempResult.bits[i]);

            // Update carry
            if (a.bits[i].getValue() == Bit.boolValues.TRUE && b.bits[i].getValue() == Bit.boolValues.TRUE
                    || b.bits[i].getValue() == Bit.boolValues.TRUE && carry.getValue() == Bit.boolValues.TRUE
                    || a.bits[i].getValue() == Bit.boolValues.TRUE && carry.getValue() == Bit.boolValues.TRUE) {
                carry.assign(Bit.boolValues.TRUE);
            } else {
                carry.assign(Bit.boolValues.FALSE);
            }
        }

        // Copy the temporary result back to `result`
        for (int i = 0; i < 32; i++) {
            result.bits[i].assign(tempResult.bits[i].getValue());
        }
    }


    public static void subtract(Word32 a, Word32 b, Word32 result) {
        Word32 negB = new Word32(); // Store -B (NOT B + 1)
        Bit carry = new Bit(true); // Start carry as 1 for +1 in two's complement

        // Step 1: Compute NOT B
        for (int i = 0; i < 32; i++) {
            b.bits[i].not(negB.bits[i]); // negB[i] = NOT B[i]
        }

        // Step 2: Add 1 to NOT B (Two's Complement)
        for (int i = 31; i >= 0; i--) { // Start from LSB (rightmost bit)
            Bit sum = new Bit(false);

            // Sum of negB[i] and carry
            negB.bits[i].xor(carry, sum);

            // Compute new carry: carry = negB[i] & carry
            negB.bits[i].and(carry, carry);

            // Store the sum in negB
            negB.bits[i] = sum;

            // If carry becomes 0, we can stop
            if (carry.getValue() == Bit.boolValues.FALSE) {
                break;
            }
        }

        // Step 3: Add A + (-B)
        add(a, negB, result); // Use existing add function
    }


}
