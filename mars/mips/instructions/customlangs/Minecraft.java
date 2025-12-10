package mars.mips.instructions.customlangs;

import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.*;
import mars.*;
import mars.util.*;
import java.util.*;

public class Minecraft extends CustomAssembly {

    @Override
    public String getName() {
        return "Minecraft";
    }

    @Override
    public String getDescription() {
        return "Minecraft-themed custom MIPS instruction set.";
    }

    @Override
    protected void populate() {

        // -----------------------------------------------------
        // BASIC INSTRUCTIONS
        // -----------------------------------------------------

        // craft (add)
        instructionList.add(
                new BasicInstruction("craft $rd,$rs,$rt",
                        "Add: rd = rs + rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                int result = RegisterFile.getValue(o[1]) + RegisterFile.getValue(o[2]);
                                RegisterFile.updateRegister(o[0], result);
                            }
                        })
        );

        // punch (subtract)
        instructionList.add(
                new BasicInstruction("punch $rd,$rs,$rt",
                        "Subtract: rd = rs - rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0],
                                        RegisterFile.getValue(o[1]) - RegisterFile.getValue(o[2]));
                            }
                        })
        );

        // stack (AND)
        instructionList.add(
                new BasicInstruction("stack $rd,$rs,$rt",
                        "Bitwise AND: rd = rs & rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0],
                                        RegisterFile.getValue(o[1]) & RegisterFile.getValue(o[2]));
                            }
                        })
        );

        // fletch (OR)
        instructionList.add(
                new BasicInstruction("fletch $rd,$rs,$rt",
                        "Bitwise OR: rd = rs | rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0],
                                        RegisterFile.getValue(o[1]) | RegisterFile.getValue(o[2]));
                            }
                        })
        );

        // move (addi)
        instructionList.add(
                new BasicInstruction("move $rt,$rs,imm",
                        "Load immediate: rt = rs + imm",
                        BasicInstructionFormat.I_FORMAT,
                        "001000 sssss ttttt tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0],
                                        RegisterFile.getValue(o[1]) + (o[2] << 16 >> 16));
                            }
                        })
        );

        // chest (store) - fixed memory handling
        instructionList.add(
                new BasicInstruction("chest $rt,imm($rs)",
                        "Store word: Mem[rs + imm] = rt",
                        BasicInstructionFormat.I_FORMAT,
                        "101011 sssss ttttt tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) throws ProcessingException {
                                int[] o = st.getOperands();
                                int addr = RegisterFile.getValue(o[2]) + (o[1] << 16 >> 16);
                                try {
                                    Globals.memory.setWord(addr, RegisterFile.getValue(o[0]));
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(st, e);
                                }
                            }
                        })
        );

        // enchant (load) - fixed memory handling
        instructionList.add(
                new BasicInstruction("enchant $rt,imm($rs)",
                        "Load word: rt = Mem[rs + imm]",
                        BasicInstructionFormat.I_FORMAT,
                        "100011 sssss ttttt tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) throws ProcessingException {
                                int[] o = st.getOperands();
                                int addr = RegisterFile.getValue(o[2]) + (o[1] << 16 >> 16);
                                try {
                                    int val = Globals.memory.getWord(addr);
                                    RegisterFile.updateRegister(o[0], val);
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(st, e);
                                }
                            }
                        })
        );

        // split (divide)
        instructionList.add(
                new BasicInstruction("split $rd,$rs,$rt",
                        "Divide: rd = rs / rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 011010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                int divisor = RegisterFile.getValue(o[2]);
                                int result = divisor == 0 ? 0 : RegisterFile.getValue(o[1]) / divisor;
                                RegisterFile.updateRegister(o[0], result);
                            }
                        })
        );

        // hop (jump)
        instructionList.add(
                new BasicInstruction("hop addr",
                        "Jump: PC = addr",
                        BasicInstructionFormat.J_FORMAT,
                        "000010 ttttttttttttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) { /* MARS handles jump */ }
                        })
        );

        // torch (NOT)
        instructionList.add(
                new BasicInstruction("torch $rd,$rs",
                        "Bitwise NOT: rd = ~rs",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 100111",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], ~RegisterFile.getValue(o[1]));
                            }
                        })
        );

        // -----------------------------------------------------
        // UNIQUE MINECRAFT INSTRUCTIONS
        // -----------------------------------------------------

        // potion strength
        instructionList.add(
                new BasicInstruction("potion $rd,$rs,0",
                        "Strength potion: rd = rs * 2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 110000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], RegisterFile.getValue(o[1]) * 2);
                            }
                        })
        );

        // potion weakness
        instructionList.add(
                new BasicInstruction("potion $rd,$rs,1",
                        "Weakness potion: rd = rs / 2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 110001",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], RegisterFile.getValue(o[1]) / 2);
                            }
                        })
        );

        // potion speed +5
        instructionList.add(
                new BasicInstruction("potion $rd,$rs,2",
                        "Speed potion: rd = rs + 5",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 110010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], RegisterFile.getValue(o[1]) + 5);
                            }
                        })
        );

        // potion slowness -5
        instructionList.add(
                new BasicInstruction("potion $rd,$rs,3",
                        "Slowness potion: rd = rs - 5",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 110011",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], RegisterFile.getValue(o[1]) - 5);
                            }
                        })
        );

        // summon
        instructionList.add(
                new BasicInstruction("summon $rd,$rs",
                        "Summon entity: rd = rs + 1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 110100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], RegisterFile.getValue(o[1]) + 1);
                            }
                        })
        );

        // build (store block) - fixed memory
        instructionList.add(
                new BasicInstruction("build $rt,imm($rs)",
                        "Place block: Mem[rs+imm] = rt",
                        BasicInstructionFormat.I_FORMAT,
                        "101100 sssss ttttt tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) throws ProcessingException {
                                int[] o = st.getOperands();
                                int addr = RegisterFile.getValue(o[2]) + (o[1] << 16 >> 16);
                                try {
                                    Globals.memory.setWord(addr, RegisterFile.getValue(o[0]));
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(st, e);
                                }
                            }
                        })
        );

        // break (load then clear) - fixed memory
        instructionList.add(
                new BasicInstruction("break $rd,imm($rs)",
                        "Break block: rd = Mem[...] then clear",
                        BasicInstructionFormat.I_FORMAT,
                        "101101 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) throws ProcessingException {
                                int[] o = st.getOperands();
                                int addr = RegisterFile.getValue(o[2]) + (o[1] << 16 >> 16);
                                try {
                                    int val = Globals.memory.getWord(addr);
                                    RegisterFile.updateRegister(o[0], val);
                                    Globals.memory.setWord(addr, 0);
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(st, e);
                                }
                            }
                        })
        );

        // redstone toggle (XOR 1)
        instructionList.add(
                new BasicInstruction("redstone $rd,$rs",
                        "Toggle bit: rd = rs XOR 1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 110101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], RegisterFile.getValue(o[1]) ^ 1);
                            }
                        })
        );

        // creeper (destroy memory range) - fixed memory
        instructionList.add(
                new BasicInstruction("creeper $rs,$rt,imm",
                        "Explosion: memory from rs to imm = 0",
                        BasicInstructionFormat.I_FORMAT,
                        "001100 sssss ttttt tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) throws ProcessingException {
                                int[] o = st.getOperands();
                                int start = RegisterFile.getValue(o[0]);
                                int end = o[2];
                                try {
                                    for (int a = start; a <= end; a += 4) {
                                        Globals.memory.setWord(a, 0);
                                    }
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(st, e);
                                }
                            }
                        })
        );

        // farm (increment)
        instructionList.add(
                new BasicInstruction("farm $rd,$rs",
                        "Auto-farm: rd = rs + 1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss 00000 fffff 00000 110110",
                        new SimulationCode() {
                            public void simulate(ProgramStatement st) {
                                int[] o = st.getOperands();
                                RegisterFile.updateRegister(o[0], RegisterFile.getValue(o[1]) + 1);
                            }
                        })
        );
    }
}