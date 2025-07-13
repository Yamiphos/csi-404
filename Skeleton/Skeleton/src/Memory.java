public class Memory {
    public Word32 address= new Word32();
    public Word32 value = new Word32();

    private final Word32[] dram= new Word32[1000];

    public int addressAsInt() {
        //to hold result
        int result =0;

        //loop through address
        for(int i=0; i<32; i++){
            //check if the bit is true, if it is shift it over
            if(address.bits[i].getValue() == Bit.boolValues.TRUE){
                result |= (1 << (31-i));
            }
        }
        if(result >999){
            //if its out of bounds throw exception
            throw new IllegalArgumentException("out of bounds");
        }
        return result;
    }

    public Memory() {
        // Initialize memory with empty Word32 objects, all bits set to false at creation
        for (int i = 0; i < 1000; i++) {
            dram[i] = new Word32();
        }
    }

    public void read() {
        //read address for spot in dram, value becomes value in dram at that spot
        int addr = addressAsInt();

        for(int i=0; i<32; i++){
            value.bits[i].assign(dram[addr].bits[i].getValue());
        }
        //value.copy(dram[addr]);
    }

    public void write() {
        //similar to read
        int addr = addressAsInt();

        for(int i=0; i<32; i++){
            dram[addr].bits[i].assign(value.bits[i].getValue());
        }
    }

    public void load(String[] data) {
        if (data.length > 1000) {
            throw new IllegalArgumentException("Too many data entries for memory.");
        }

        //loop through entries in the string array
        for(int i=0; i< data.length; i++){

            if (data[i].length() != 32) {
                throw new IllegalArgumentException("Invalid word length at index " + i);
            }

            //loop through each char in the string in the array
            Word32 tempWord = new Word32();
            for(int j=0; j<32; j++){
                //checking for t
                if(data[i].charAt(j)=='t'){
                    tempWord.bits[j].assign(Bit.boolValues.TRUE);
                }
            }
            //set the word 32 at dram[i] equal to the tempWord
            dram[i].copy(tempWord);
        }
    }


}
