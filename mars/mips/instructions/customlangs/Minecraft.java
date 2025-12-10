package mars.mips.instructions.customlangs;

import mars.simulator.*;
import mars.mips.hardware.*;
import mars.*;
import mars.util.*;
import mars.mips.instructions.*;


public class Minecraft extends CustomAssembly{

    private int summonCount = 0;

    @Override
    public String getName(){
        return "Minecraft";
    }

    @Override
    public String getDescription(){
        return "A MIPS-based assembly language with Minecraft-themed instructions for crafting, potions, and redstone.";
    }

    @Override
    protected void populate(){

        // craft (MIPS: add) - R-Format, Funct 32
        instructionList.add(new BasicInstruction("craft $t1,$t2,$t3",
                "Craft: Addition with overflow.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[1]);
                        int add2 = RegisterFile.getValue(operands[2]);
                        int sum = add1 + add2;
                        if ((add1 >= 0 && add2 >= 0 && sum < 0) || (add1 < 0 && add2 < 0 && sum >= 0)) {
                            throw new ProcessingException(statement, "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }
        ));

        // punch (MIPS: sub) - R-Format, Funct 34
        instructionList.add(new BasicInstruction("punch $t1,$t2,$t3",
                "Punch: Subtraction with overflow.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int val1 = RegisterFile.getValue(operands[1]);
                        int val2 = RegisterFile.getValue(operands[2]);
                        int diff = val1 - val2;
                        if ((val1 >= 0 && val2 < 0 && diff < 0) || (val1 < 0 && val2 >= 0 && diff >= 0)) {
                            throw new ProcessingException(statement, "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], diff);
                    }
                }
        ));

        // stack (MIPS: and) - R-Format, Funct 36
        instructionList.add(new BasicInstruction("stack $t1,$t2,$t3",
                "Stack: Bitwise AND.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int result = RegisterFile.getValue(operands[1]) & RegisterFile.getValue(operands[2]);
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
        ));

        // fletch (MIPS: or) - R-Format, Funct 37
        instructionList.add(new BasicInstruction("fletch $t1,$t2,$t3",
                "Fletch: Bitwise OR.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100101",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int result = RegisterFile.getValue(operands[1]) | RegisterFile.getValue(operands[2]);
                        RegisterFile.updateRegister(operands[0], result);
                    }
                }
        ));

        // move (MIPS: addi) - I-Format, Opcode 8
        instructionList.add(new BasicInstruction("move $t1,$t2,-100",
                "Move: Load immediate.",
                BasicInstructionFormat.I_FORMAT,
                "001000 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[1]);
                        int add2 = operands[2] << 16 >> 16;
                        int sum = add1 + add2;
                        if ((add1 >= 0 && add2 >= 0 && sum < 0) || (add1 < 0 && add2 < 0 && sum >= 0)) {
                            throw new ProcessingException(statement, "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }
        ));

        // chest (MIPS: sw) - I-Format, Opcode 43
        instructionList.add(new BasicInstruction("chest $t1, 0($t2)",
                "Chest: Store Word.",
                BasicInstructionFormat.I_FORMAT,
                "101011 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int address = RegisterFile.getValue(operands[1]) + operands[2];
                        try {
                            Globals.memory.setWord(address, RegisterFile.getValue(operands[0]));
                        } catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }
        ));

        // enchant (MIPS: lw) - I-Format, Opcode 35
        instructionList.add(new BasicInstruction("enchant $t1, 0($t2)",
                "Enchant: Load Word.",
                BasicInstructionFormat.I_FORMAT,
                "100011 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int address = RegisterFile.getValue(operands[1]) + operands[2];
                        try {
                            RegisterFile.updateRegister(operands[0], Globals.memory.getWord(address));
                        } catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }
        ));

        // split (MIPS: div) - R-Format, Funct 26
        instructionList.add(new BasicInstruction("split $t1,$t2,$t3",
                "Split: Division.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt 00000 00000 011010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int numerator = RegisterFile.getValue(operands[1]);
                        int denominator = RegisterFile.getValue(operands[2]);
                        RegisterFile.updateRegister(operands[0], numerator / denominator);
                    }
                }
        ));

        // hop (MIPS: j) - J-Format, Opcode 2
        instructionList.add(new BasicInstruction("hop target",
                "Hop: Jump unconditionally.",
                BasicInstructionFormat.J_FORMAT,
                "000010 ffffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        Globals.instructionSet.processJump(
                                ((RegisterFile.getProgramCounter() & 0xF0000000)
                                        | (operands[0] << 2)));
                    }
                }
        ));

        // torch (MIPS: nor with $zero) - R-Format, Funct 39
        instructionList.add(new BasicInstruction("torch $t1,$t2",
                "Torch: Bitwise NOT.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00000 100111",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int value = RegisterFile.getValue(operands[1]);
                        RegisterFile.updateRegister(operands[0], ~(value | 0));
                    }
                }
        ));

        // potion.strength (rd = rs * 2) - R-Format, Funct 48, Shamt 1
        instructionList.add(new BasicInstruction("potion.strength $t1,$t2",
                "Strength Potion: Multiplication by 2.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00001 110000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int value = RegisterFile.getValue(operands[1]);
                        RegisterFile.updateRegister(operands[0], value << 1);
                    }
                }
        ));

        // potion.weakness (rd = rs / 2) - R-Format, Funct 49, Shamt 1
        instructionList.add(new BasicInstruction("potion.weakness $t1,$t2",
                "Weakness Potion: Division by 2.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00001 110001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int value = RegisterFile.getValue(operands[1]);
                        RegisterFile.updateRegister(operands[0], value >> 1);
                    }
                }
        ));

        // potion.speed (rd = rs + 5) - R-Format, Funct 50, Shamt 5
        instructionList.add(new BasicInstruction("potion.speed $t1,$t2",
                "Speed Potion: Addition by 5.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00101 110010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sum = RegisterFile.getValue(operands[1]) + 5;
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }
        ));

        // potion.slowness (rd = rs - 5) - R-Format, Funct 51, Shamt 5
        instructionList.add(new BasicInstruction("potion.slowness $t1,$t2",
                "Slowness Potion: Subtraction by 5.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00101 110011",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int diff = RegisterFile.getValue(operands[1]) - 5;
                        RegisterFile.updateRegister(operands[0], diff);
                    }
                }
        ));

        // summon (Custom Counter) - R-Format, Funct 52
        instructionList.add(new BasicInstruction("summon $t1,$t2",
                "Summon: Creates entity.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00000 110100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        summonCount++;
                        RegisterFile.updateRegister(operands[0], summonCount);
                    }
                }
        ));

        // build (Custom sw) - I-Format, Unique Opcode 44 (101100)
        instructionList.add(new BasicInstruction("build $t1, 0($t2)",
                "Build: Stores block ID to memory.",
                BasicInstructionFormat.I_FORMAT,
                "101100 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int address = RegisterFile.getValue(operands[1]) + operands[2];
                        try {
                            Globals.memory.setWord(address, RegisterFile.getValue(operands[0]));
                        } catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }
        ));

        // break (Custom lw/store 0) - I-Format, Unique Opcode 45 (101101)
        instructionList.add(new BasicInstruction("break $t1, 0($t2)",
                "Break: Sets block ID to 0.",
                BasicInstructionFormat.I_FORMAT,
                "101101 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int address = RegisterFile.getValue(operands[1]) + operands[2];
                        try {
                            Globals.memory.setWord(address, 0);
                            RegisterFile.updateRegister(operands[0], 0);
                        } catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }
        ));

        // redstone (rd = rs xor 1) - R-Format, Funct 53
        instructionList.add(new BasicInstruction("redstone $t1,$t2",
                "Redstone: Toggles least significant bit.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00000 110101",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int value = RegisterFile.getValue(operands[1]);
                        RegisterFile.updateRegister(operands[0], value ^ 1);
                    }
                }
        ));

        // creeper (Memory range zeroing) - I-Format, Unique Opcode 12 (001100)
        instructionList.add(new BasicInstruction("creeper $t1,$t2,-100",
                "Creeper: Explosion! Sets memory range to 0.",
                BasicInstructionFormat.I_FORMAT,
                "001100 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int startAddress = RegisterFile.getValue(operands[1]);
                        int endAddress = RegisterFile.getValue(operands[0]) + (operands[2] << 16 >> 16);
                        int currentAddress = startAddress;

                        while (currentAddress <= endAddress) {
                            try {
                                Globals.memory.setWord(currentAddress, 0);
                                currentAddress += 4;
                            } catch (AddressErrorException e) {
                                throw new ProcessingException(statement, e);
                            }
                        }
                    }
                }
        ));

        // Auto-farm (rd = rs + 1) - R-Format, Funct 54
        instructionList.add(new BasicInstruction("farm $t1,$t2",
                "Auto-Farm: Growth (increment by 1).",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00001 110110",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sum = RegisterFile.getValue(operands[1]) + 1;
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }
        ));

        // reset.world (Custom Reset) - R-Format, Funct 55
        instructionList.add(new BasicInstruction("reset.world",
                "Reset: Resets the summon counter.",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 00000 00000 110111",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        summonCount = 0;
                    }
                }
        ));
    }
}
