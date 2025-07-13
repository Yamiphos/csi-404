public class InstructionCache {
    private Word32[] data;         // Holds 8 words
    private int tag = -1;        // Address of the first word in data, -1 = invalid
    private Memory mem; //mem
    private L2Cache l2; //l2 cache

    public InstructionCache(Memory mem, L2Cache l2) {
        this.mem = mem;
        this.l2 = l2;
        this.data = new Word32[8];
        for (int i = 0; i < 8; i++) {
            data[i] = new Word32(); // Initialize all 8 Words
        }
    }

    public Word32 read(int address) {
        int baseAddress = address - (address % 8); // Align to 8-word block

        if (tag == baseAddress) {
            // Cache hit
            Processor.addCycles(10);
            return data[address % 8];
        }

        // Cache miss - check L2
        if (l2.contains(baseAddress)) {
            // Load block from L2
            for (int i = 0; i < 8; i++) {
                //data[i].copy(l2.getWord(baseAddress + i));
                data[i].copy(l2.read(baseAddress + i));
            }
            tag = baseAddress;
            Processor.addCycles(50); // L2 to instruction cache
        } else {
            // Load block from main memory
            for (int i = 0; i < 8; i++) {
                mem.address = IntToWord32(baseAddress + i);// Assuming `set(int)` sets address as 32-bit Word
                mem.read();
                data[i].copy(mem.value);
            }
            tag = baseAddress;
            Processor.addCycles(350);// Memory to instruction cache
        }

        return data[address % 8];
    }

    private Word32 IntToWord32 (int value){ //helper method
        Word32 result = new Word32();
        for (int i = 0; i < 32; i++) {
            boolean bitValue = (value & (1 << (31 - i))) != 0;
            result.bits[i] = new Bit(bitValue);
        }

        return result;
    }
}
