import java.util.HashMap;
import java.util.LinkedList;

public class Assembler {
    private static HashMap<String, String> OPCODES = new HashMap<>();

    static {
        OPCODES.put("halt", "fffff"); //00000
        OPCODES.put("add", "fffft"); //00001
        OPCODES.put("and", "ffftf"); //00010
        OPCODES.put("multiply", "ffftt"); //00011
        OPCODES.put("leftshift", "fftff"); //00100
        OPCODES.put("subtract", "fftft"); //00101
        OPCODES.put("or", "ffttf"); //00110
        OPCODES.put("rightshift", "ffttt"); //00111
        OPCODES.put("syscall", "ftfff"); //01000
        OPCODES.put("call", "ftfft"); //01001
        OPCODES.put("return", "ftftf"); //01010
        OPCODES.put("compare", "ftftt"); //01011
        OPCODES.put("ble", "fttff"); //01100
        OPCODES.put("blt", "fttft"); //01101
        OPCODES.put("bge", "ftttf"); //01110
        OPCODES.put("bgt", "ftttt"); //01111
        OPCODES.put("beq", "tffff"); //10000
        OPCODES.put("bne", "tffft"); //10001
        OPCODES.put("load", "tfftf"); //10010
        OPCODES.put("store", "tfftt"); //10011
        OPCODES.put("copy", "tftff"); //10100
    }

    public static String[] assemble(String[] input) {
        LinkedList<String> output = new LinkedList<>();
        //loop through all strings in the array
        for (int i = 0; i < input.length; i++) {
            //splitting string
            String temp = input[i];
            String[] parts = temp.trim().toLowerCase().split(" ");
            //matching opcode
            String opcode = OPCODES.get(parts[0]);
            //now determine format
            if(parts.length ==2 || parts.length ==1){ // call/return format
                if(parts.length==1){output.add( opcode + "fffffffffff");}//if it is return
                else{// if it is a call but not a return EX: syscall 100
                    int reg = Integer.parseInt(parts[1]);
                    output.add(opcode + toBinary(reg,11).replace('0', 'f').replace('1', 't'));

//                    if(reg >0){//for positive numbers
//                        output.add(opcode + "f" + toBinary(reg,10).replace('0', 'f').replace('1', 't'));
//                    } else{//for negative numbers
//                        reg = Integer.parseInt(parts[1].substring(1));
//                        output.add(opcode +"t"+ toBinary(reg,10).replace('0', 'f').replace('1', 't'));
//                    }
                }
            }
            else { //go here always if not call/return
                if(parts[1].contains("r") && parts[2].contains("r")){ //2R format: 2 registers
                    int reg1 =Integer.parseInt(parts[1].substring(1));//get the integers from the 'r's
                    int reg2 =Integer.parseInt(parts[2].substring(1));//get the integers from the 'r's
                    output.add(opcode + "f" + toBinary(reg1, 5).replace('0', 'f').replace('1', 't') + toBinary(reg2, 5).replace('0', 'f').replace('1', 't'));
                }
                else{// immediate format: 1 register 1 number
                    int imm = Integer.parseInt(parts[1]); //immediate value
                    int reg = Integer.parseInt(parts[2].substring(1)); //register
                    output.add(opcode + 't' + toBinary(imm,5).replace('0', 'f').replace('1', 't') + toBinary(reg,5).replace('0', 'f').replace('1', 't'));
                }

            }
        }
        return output.toArray(new String[0]);
    }

    public static String[] finalOutput(String[] input) {
        //output from assemble is an array we just have to merge two lines together to get 32 bits total,
        //if there is an odd number of lines, merge the last one with 16 f's

        LinkedList<String> output = new LinkedList<>();
        for (int i = 0; i < input.length; i += 2) {
            if (i + 1 < input.length) {
                output.add(input[i] + input[i + 1]);
            } else {
                output.add(input[i] + "ffffffffffffffff");
            }
        }
        return output.toArray(new String[0]);
    }


    private static String toBinary(int value, int bits) {
        String binary = Integer.toBinaryString(value & ((1 << bits) - 1));
        while (binary.length() < bits) binary = "0" + binary;
        return binary.replace('0', 'f').replace('1', 't');
    }

}
