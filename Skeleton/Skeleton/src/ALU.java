public class ALU {
    public Word16 instruction = new Word16();
    public Word32 op1 = new Word32();
    public Word32 op2 = new Word32();
    public Word32 result = new Word32();
    public Bit less = new Bit(false);
    public Bit equal = new Bit(false);
    public int opcode;

    public void doInstruction() {
        resetResult();
        opcode = getOpcode();

        switch (opcode) {
            case 2: // And
                bitwiseAnd();
                break;
            case 3: // Multiply
                Multiplier.multiply(op1, op2, result);
                break;
            case 4: // LeftShift
                Shifter.LeftShift(op1, getShiftAmount(op2), result);
                break;
            case 5: // Subtract
                Adder.subtract(op1, op2, result);
                break;
            case 6: // Or
                bitwiseOr();
                break;
            case 7: // RightShift
                Shifter.RightShift(op1, getShiftAmount(op2), result);
                break;
            case 11: // Compare
                compare();
                break;
            case 1: // Add
            default:
                //System.err.println("got to the default case because of syscall");
                Adder.add(op1, op2, result);
                break;
        }
        resetop1();
        resetop2();
    }

    private int getOpcode() {
        //bits 0-5 copy over into the last 5 of a word 32
        Word32 temp = new Word32();

        for (int i = 0; i < 5; i++) {
            temp.bits[i + 27].assign(instruction.bits[i].getValue());
        }

        //convert to int
        int result = 0;
        for (int i = 0; i < 32; i++) {

            if (temp.bits[i].getValue() == Bit.boolValues.TRUE) {
                result |= (1 << (31 - i));
            }
        }

        return result;
    }

    private void bitwiseAnd() {
        for (int i = 0; i < 32; i++) {
            Bit.and(op1.bits[i], op2.bits[i], result.bits[i]);
        }
    }

    private void bitwiseOr() {
        for (int i = 0; i < 32; i++) {
            Bit.or(op2.bits[i], op1.bits[i], result.bits[i]);
        }
    }

    private void compare() {
        //assume they are equal to begin with
        less.assign(Bit.boolValues.FALSE);
        equal.assign(Bit.boolValues.TRUE);

        for (int i = 0; i < 32; i++) { //go through bits

            if (op1.bits[i].getValue() != op2.bits[i].getValue()) {
                equal.assign(Bit.boolValues.FALSE); //if bits from op1 and op2 arent the exact same, set equal to false

                // Correctly handle sign and magnitude comparison
                if (op1.bits[i].getValue() == Bit.boolValues.FALSE &&
                        op2.bits[i].getValue() == Bit.boolValues.TRUE) {

                    less.assign(Bit.boolValues.TRUE);
                    return; // No need to check further, op1 is definitely smaller
                }
                return; // If op1 > op2 at this bit, we don't set less
            }
        }
    }


    private int getShiftAmount(Word32 word) {
        int shiftAmount = 0;

        for (int i = 0; i < 32; i++) {
            //check if the bit is true, if it is shift it over
            if (word.bits[i].getValue() == Bit.boolValues.TRUE) {
                shiftAmount |= (1 << (31 - i));
            }
        }
        return shiftAmount;
    }

    public void resetResult() {
        for (int i = 0; i < 32; i++) {
            result.bits[i].assign(Bit.boolValues.FALSE);
        }
    }
    public void resetop1() {
        for (int i = 0; i < 32; i++) {
            op1.bits[i].assign(Bit.boolValues.FALSE);
        }
    }
    public void resetop2() {
        for (int i = 0; i < 32; i++) {
            op2.bits[i].assign(Bit.boolValues.FALSE);
        }
    }


}
