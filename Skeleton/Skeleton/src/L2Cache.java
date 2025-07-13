public class L2Cache {
    private Word32[][] data; // 4 groups of 8 words: [group][word index]
    private int[] tags;    // Base address of each group
    private Memory memory;

    public L2Cache(Memory memory) {
        this.memory = memory;
        data = new Word32[4][8];
        tags = new int[4];
        for (int i = 0; i < 4; i++) {
            tags[i] = -1; // means empty
            for (int j = 0; j < 8; j++) {
                data[i][j] = new Word32();
            }
        }
    }


    public Word32 read(int address) {
        int baseAddress = address - (address % 8);

        for (int i = 0; i < 4; i++) {
            if (tags[i] == baseAddress) {
                Processor.addCycles(20); // L2 hit
                return data[i][address % 8];
            }
        }

        // Miss: Pick a group to replace
        int groupToReplace = 0;
        tags[groupToReplace] = baseAddress;

        for (int i = 0; i < 8; i++) {
            int addr = baseAddress + i;
            memory.address = IntToWord32(addr);
            memory.read();
            data[groupToReplace][i].copy(memory.value);
        }

        Processor.addCycles(350); //main memory read
        return data[groupToReplace][address % 8];
    }

    private Word32 IntToWord32 (int value){
        Word32 result = new Word32();
        for (int i = 0; i < 32; i++) {
            boolean bitValue = (value & (1 << (31 - i))) != 0;
            result.bits[i] = new Bit(bitValue);
        }

        return result;
    }

    public boolean contains(int baseAddress) {
        for (int i = 0; i < 4; i++) {
            if (tags[i] == baseAddress) {
                return true;
            }
        }
        return false;
    }

    public Word32 getWord(int address) {
        int baseAddress = address - (address % 8);
        for (int i = 0; i < 4; i++) {
            if (tags[i] == baseAddress) {
                return data[i][address % 8];
            }
        }
        return null; // Shouldn't happen unless called without checking contains() method
    }




}
