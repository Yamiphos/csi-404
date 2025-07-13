public class Multiplier {
    public static void multiply(Word32 a, Word32 b, Word32 result) {
        Word32 accumulator= result; //to hold value while adding

       //first loop through b then from LSB to MSB and check if its a 1
        for(int i=31; i>=0; i--){

            //only when b is 1, do addition
            if(b.bits[i].getValue()== Bit.boolValues.TRUE){
                    Adder.add(a,accumulator,accumulator);
            }
            //shift a by 1 for each loop through b
            Shifter.LeftShift(a,1,a);
        }

        result.copy(accumulator);
    }
}

