public class Bit {
    public enum boolValues { FALSE, TRUE }

    private boolValues value;

    public Bit(boolean value) {
        this.value = value ? boolValues.TRUE : boolValues.FALSE;
    }

    public boolValues getValue() {
        return this.value;
    }

    public void assign(boolValues value) {
        this.value = value;
    }

    public void and(Bit b2, Bit result) {
        //calling static version
        and(this,b2,result);
    }

    public static void and(Bit b1, Bit b2, Bit result) {
        //checking if b1 and b2 are equal, so false and false or true and true
        if(b1.getValue() == b2.getValue()){

            //now that they are the same check what they are, false or true
            if(b1.getValue()== boolValues.FALSE){
                result.assign((boolValues.FALSE));
            }
            else{
                result.assign(boolValues.TRUE);
            }
        }
        else{
            //if not equal so true and false or false and true, result is false
            result.assign(boolValues.FALSE);
        }
    }

    public void or(Bit b2, Bit result) {
        //calling static version
        or(this, b2, result);
    }

    public static void or(Bit b1, Bit b2, Bit result) {
        //if both are false, return false
        if(b1.getValue()==boolValues.FALSE){
            if(b2.getValue()==boolValues.FALSE){
                result.assign(boolValues.FALSE);
            }
        }
        //if both aren't false atleast one must be true, so return true
        else{
            result.assign(boolValues.TRUE);
        }
    }

    public void xor(Bit b2, Bit result) {
        //calling static version
        xor(this, b2, result);
    }

    public static void xor(Bit b1, Bit b2, Bit result) {
        //if b1 and b2 are equal, result is false
        if (b1.getValue() == b2.getValue()) {
            result.assign(boolValues.FALSE);
        } else {
            //if they are not equal, result is true
            result.assign(boolValues.TRUE);
        }
    }

    public static void not(Bit b2, Bit result) {
        //if b2 is true, result is false; if b2 is false, result is true
        if (b2.getValue() == boolValues.TRUE) {
            result.assign(boolValues.FALSE);
        } else {
            result.assign(boolValues.TRUE);
        }
    }

    public void not(Bit result) {
        //calling static version
        not(this,result);
    }

    public String toString() {
        //if the value of the bit is true, return t for true, if not f for false
        if(this.value == boolValues.TRUE){
            return "t";
        }
        else{
            return "f";
        }
    }
}
