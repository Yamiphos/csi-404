import java.util.*;

public class Processor {
    private Memory mem;
    public List<String> output = new LinkedList<>();
    private Word32[] registers = new Word32[32];
    Stack<Word32> callStack = new Stack<>();
    private Word32 pc = new Word32(); // program counter
    private Word32 instruction = new Word32(); // current instruction has 2
    private boolean secondInstruction = false; // toggles between the two halves
    private int destReg = -1;
    private int sourceReg = -1;
    private boolean shouldHalt;
    private ALU alu = new ALU();
    public static int clock_cycles;
    private InstructionCache instructionCache;
    private L2Cache l2;


    public Processor(Memory m) {
        clock_cycles = 0;
        mem = m;
        l2 = new L2Cache(mem);
        this.instructionCache = new InstructionCache(mem,l2);
        for (int i = 0; i < 32; i++) {
            registers[i] = new Word32();
        }
    }


    public void run() {
        while (true) {
            if (!secondInstruction) {
                fetch();
            }

            if (shouldHalt){
             System.out.println("Clock cycles: " + clock_cycles);
             break;
            }
            decode();
            execute();
            store();

            secondInstruction = !secondInstruction; // toggle for next round

            if (shouldHalt){
                System.out.println("Clock cycles: " + clock_cycles);
                break;
            }
        }
    }


    private void fetch() {
//        mem.address.copy(pc); //copy address into program counter
//        mem.read(); //read adrress for spot in dram stores in mem.value
//        instruction.copy(mem.value);//puts the value in instruction, 2 instructions stored
//        Processor.addCycles(300);

        int pcAsInt = pcAsInt(); // Convert PC to an int address
        Word32 fetched = instructionCache.read(pcAsInt); // Read from cache
        instruction.copy(fetched); // Store instruction


    }

    private void decode() {
        Bit steering = new Bit(false);
        //getting instruction and opcode
        if (!secondInstruction) {
            instruction.getTopHalf(alu.instruction);
        } else {
            instruction.getBottomHalf(alu.instruction);
        }
        alu.opcode = getOPCodeandRegisters(0, alu.instruction);
        //case 1-7 is 2R or immediate
        //case 8-10 is call return
        //case 11 is 2R or immediate
        //case 12-17 is call return
        switch (alu.opcode) {
            case 0: //halt
                shouldHalt =true;
                break;
            case 1: //add
            case 2: //and
            case 3: //multiply
            case 4: //left shift
            case 5: //subtract
            case 6: //or
            case 7: //right shift
            case 11: //compare
            case 18: //load
            case 19: //store
            case 20: //copy
                alu.instruction.getBitN(5,steering);
                if(steering.getValue() == Bit.boolValues.FALSE){
                    //get which register is being used
                    sourceReg = getOPCodeandRegisters(6, alu.instruction);
                    registers[sourceReg].copy(alu.op1);
                }else{
                    //immediate value copy over
                    for(int i=6; i<11;i++){
                        alu.op1.setBitN(i+21, alu.instruction.bits[i]);
                    }
                    //immediate value sign extend
                    for(int i=0; i<27;i++){
                        alu.op1.setBitN(i,alu.op1.bits[27]);
                    }
                }

                //get which register is being used
                destReg = getOPCodeandRegisters(11, alu.instruction);
                //registers[destReg].copy(alu.op2); //put that in the register
                break;
            case 8: //syscall 0 prints reg syscall 1 prints mem, checking done in execute
                break;
            case 9: //call
            case 10: //return
            case 17: //bne
                alu.op1 =copyLast11BitsToWord32(alu.instruction); //getting the immediate
                break;
            default:
                System.out.println("(decode)Invalid opcode: "+ alu.opcode);
                break;
        }
    }

    private void execute() {
        Bit str = new Bit(false);

        switch (alu.opcode) {
            case 0: //halt
                Processor.addCycles(2);
                shouldHalt = true;
                break;
            case 1: //add
                Processor.addCycles(2);
                alu.doInstruction();
                break;
            case 2: //and
                Processor.addCycles(2);
                alu.doInstruction();
                break;
            case 3: //multiply
                Processor.addCycles(10);
                alu.doInstruction();
                break;
            case 4: //leftshift
                Processor.addCycles(2);
                alu.doInstruction();
                break;
            case 5: //subtract
                Processor.addCycles(2);
                alu.doInstruction();
                break;
            case 6: //or
                Processor.addCycles(2);
                alu.doInstruction();
                break;
            case 7: //rightshift
                Processor.addCycles(2);
                alu.doInstruction();
                break;
            case 8: //syscall
                Processor.addCycles(2);
                Bit last = new Bit(false);
                alu.instruction.getBitN(15,last);
                if(last.getValue() == Bit.boolValues.FALSE){
                    printReg();
                }
                else{printMem();}
                break;
            case 9: //call
                Processor.addCycles(2);
                //push address +1 onto the stack:
                Word32 tempaddr = pc;
                incrementWord32(tempaddr,1);
                callStack.push(tempaddr);

                //setting pc
                int imm = word32ToSignedInt(alu.op1); //getting immediate
                incrementWord32(pc,imm); //adding immediate to program counter
                break;
            case 10: //return
                Processor.addCycles(2);
                 pc = callStack.pop(); //setting pc to the popped value
                break;
            case 11: //compare
                Processor.addCycles(2);
                alu.doInstruction(); //sets flags in alu
                break;
            case 17: //bne
                Processor.addCycles(2);
                if(alu.equal.getValue() == Bit.boolValues.FALSE){
                    incrementWord32(pc,word32ToSignedInt(alu.op1));
                }
                break;
            case 18: // load
                Processor.addCycles(2);
                //check the format bit
                alu.instruction.getBitN(5,str);
                if(str.getValue() == Bit.boolValues.FALSE){ //2R
                    registers[destReg] = registers[sourceReg];
                }else{ //immediate
                    registers[destReg] = registers[destReg+word32ToSignedInt(registers[destReg])];
                }
                break;
            case 19: // store
                Processor.addCycles(2);
                //check the format bit
                alu.instruction.getBitN(5,str);
                if(str.getValue() == Bit.boolValues.FALSE){ //2R
                    registers[destReg] = registers[sourceReg];
                }else{// immediate
                    registers[destReg] = alu.op1;
                }
                break;
            case 20: //copy
                Processor.addCycles(2);
                alu.doInstruction(); //will just add op1, to op2
                break;
            default:
                System.out.println("(execute)Invalid opcode: "+ alu.opcode);
                break;
        }

    }

    private void printReg() {
        for (int i = 0; i < 32; i++) {
            var line = "r"+ i + ":" + registers[i]; // TODO: add the register value here...
            output.add(line);
            System.out.println(line);
        }
    }

    private void printMem() {
        mem.address = new Word32();
        for (int i = 0; i < 1000; i++) {
            Word32 addr = new Word32();
            Word32 value = new Word32();
            // Convert i to Word32 here...
            fromIntSmall(i,mem.address); //increment mem.address
            addr.copy(mem.address);
            mem.read();
            value.copy(mem.value); //had to flip around
            var line = i + ":" + value + "(" + TestConverter.toInt(value) + ")";
            output.add(line);
            System.out.println(line);
        }
        clock_cycles = clock_cycles +300;
    }

    private void store() {
        switch (alu.opcode){
            case 0: //halt
            case 1: //add
            case 4: //leftshift
            case 5: //subtract
                if (destReg >= 0 && destReg < 32) {
                    registers[destReg].copy(alu.result);
                } else if(destReg==-1 || sourceReg==-1) {
                    //do nothing
                }else{
                    System.err.println("Invalid destination register: " + destReg);
                }

                if (secondInstruction) {
                    incrementWord32(pc,1); // only increment after both halves are done
                }
                //reset source and destination registers
                destReg = -1;
                sourceReg = -1;
                break;
            case 8: //syscall
            case 11: //compare
            case 17: //BNE
            case 18: //load
            case 19: //store
            case 20: //copy
                if (secondInstruction) {
                    incrementWord32(pc,1); // only increment after both halves are done
                }
                destReg = -1;
                sourceReg = -1;
                break;
            default:
                System.out.println("(store)invalid opcode: " + alu.opcode);
                break;
        }
    }

    private int getOPCodeandRegisters(int start, Word16 instruction){
        int result = 0;

        for (int i = 0; i < 5; i++) {
            if (instruction.bits[start + i].getValue() == Bit.boolValues.TRUE) {
                result |= (1 << (4 - i)); // shift into correct spot for 5-bit number
            }
        }

        return result;
    }


    private Word32 copyLast11BitsToWord32(Word16 input) {
        Word32 result = new Word32();

        // Copy last 11 bits (positions 5 to 15 in Word16) into last 11 bits of Word32 (positions 21 to 31)
        for (int i = 0; i < 11; i++) {
            result.bits[21 + i] = input.bits[5 + i];  // 21+i gives 21–31 range
        }

        return result;
    }

    public void incrementWord32(Word32 word, int amount) {
        for (int j = 0; j < amount; j++) {
            boolean carry = true;

            for (int i = 31; i >= 0; i--) {
                Bit current = word.bits[i];

                if (carry) {
                    if (current.getValue() == Bit.boolValues.FALSE) {
                        word.bits[i] = new Bit(true);
                        carry = false;
                    } else {
                        word.bits[i] = new Bit(false);
                        // carry stays true
                    }
                } else {
                    break;
                }
            }
        }
    }

    public int word32ToSignedInt(Word32 word) {
        int value = 0;

        // Check if MSB is 1 (negative number)
        boolean isNegative = word.bits[0].getValue() == Bit.boolValues.TRUE;

        for (int i = 0; i < 32; i++) {
            if (word.bits[i].getValue() == Bit.boolValues.TRUE) {
                value += (1 << (31 - i));
            }
        }

        if (isNegative) {
            // Interpret as signed by casting to signed int
            value -= (1 << 32);
        }

        return value;
    }

    public static void fromIntSmall(int value, Word32 result) {
        if (value < 0 || value > 999) {
            throw new IllegalArgumentException("Value must be between 0 and 999");
        }

        // Only fill the last 10 bits (bits[22] to bits[31])
        for (int i = 0; i < 10; i++) {
            boolean bitValue = (value & (1 << (9 - i))) != 0;
            result.bits[22 + i] = new Bit(bitValue);
        }

        // Optional, zero out the first 22 bits if necessary
        for (int i = 0; i < 22; i++) {
            result.bits[i] = new Bit(false);
        }
    }

    public static void addCycles(int c) {
        System.out.println("i got called, adding : " + c + " Clock cycles.");
        clock_cycles += c;
    }

    private int pcAsInt() {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            if (pc.bits[i].getValue() == Bit.boolValues.TRUE) {
                result |= (1 << (31 - i));
            }
        }

        return result;
    }

}