import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Line {
  String loc = "";
  String label = "";
  String op = "";
  String oper = "";
  String opcode = "";

  public Line(String label, String op, String oper) {
    super();
    this.label = label;
    this.op = op;
    this.oper = oper;
  }

}

class OpTab {
  String[] op_Tab = { "ADD", "ADDF", "ADDR", "AND", "CLEAR", "COMP", "COMPF", "COMPR", "DIV", "DIVF", "DIVR", "FIX",
      "FLOAT", "HIO", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDB", "LDCH", "LDF", "LDL", "LDS", "LDT", "LDX", "LPS",
      "UML", "MULF", "MULR", "NORM", "OR", "RD", "RMO", "RSUB", "SHIFTL", "SHIFTR", "SIO", "SSK", "STA", "STB", "STCH",
      "STF", "STI", "STL", "STS", "STSW", "STT", "STX", "SUB", "SUBF", "SUBR", "SVC", "TD", "TIO", "TIX", "TIXR",
      "WD" };
  String[] opCode = { "18", "58", "90", "40", "B4", "28", "88", "A0", "24", "64", "9C", "C4", "C0", "F4", "3C", "30",
      "34", "38", "48", "00", "68", "50", "70", "08", "6C", "74", "04", "E0", "20", "60", "98", "C8", "44", "D8", "AC",
      "4C", "A4", "A8", "F0", "EC", "0C", "78", "54", "80", "D4", "14", "7C", "E8", "84", "10", "1C", "5C", "94", "B0",
      "E0", "F8", "2C", "B8", "DC" };
  String[] opPlus1 = { "FIX", "FLOAT", "HIO", "NORM", "SIO", "TIO" };
  String[] opPlus2 = { "ADDR", "CLEAR", "COMPR", "DIVR", "MULR", "RMO", "SHIFTL", "SHIFTR", "SUBR", "SVC", "TIXR" };
}

class Register {
  String[] register = { "A", "X", "L", "B", "S", "T", "F" };
};

public class XE {

  public static void main(String[] args) throws IOException {

    ArrayList<Line> line = new ArrayList<>();

    Read(line);
    SetLoc(line);
    SetOpcode(line);
    sortOut(line);
    Write(line);

  }

  public static void Read(ArrayList<Line> line) throws FileNotFoundException {

    FileReader fr = new FileReader("XE.txt");
    BufferedReader br = new BufferedReader(fr);
    Scanner scanner = new Scanner(br);

    while (scanner.hasNext()) {
      String tmpLine = scanner.nextLine();
      Scanner scn = new Scanner(tmpLine);
      String tmpLabel = "", tmpOp = "", tmpOper = "";

      while (scn.hasNext()) {
        String tmpStr = scn.next();

        if (tmpStr.equals("START") || tmpStr.equals("END") || tmpStr.equals("WORD") || tmpStr.equals("BYTE")
            || tmpStr.equals("RESW") || tmpStr.equals("RESB") || tmpStr.equals("BASE")) {
          tmpOp = tmpStr;
        } else if (checkOp(tmpStr)) {
          tmpOp = tmpStr;
        } else if (tmpOp.equals("")) {
          tmpLabel = tmpStr;
        } else {
          tmpOper = tmpStr;
        }
      }

      line.add(new Line(tmpLabel, tmpOp, tmpOper));
      scn.close();
    }

    scanner.close();
  }

  public static void SetLoc(ArrayList<Line> line) throws NumberFormatException {

    line.get(0).loc = line.get(0).oper;
    line.get(1).loc = line.get(0).loc;

    try {
      for (int i = 2; i < line.size(); i++) {
        if (line.get(i - 1).op.equals("BYTE") || line.get(i - 1).op.equals("RESW") || line.get(i - 1).op.equals("RESB")
            || line.get(i).op.equals("BASE")) {

          if (line.get(i - 1).op.equals("BYTE")) {
            if (line.get(i - 1).oper.charAt(0) == 'C') {
              line.get(i).loc = Integer
                  .toString(Integer.parseInt(line.get(i - 1).loc, 16) + line.get(i - 1).oper.length() - 3, 16);
            } else if (line.get(i - 1).oper.charAt(0) == 'X') {
              line.get(i).loc = Integer
                  .toString(Integer.parseInt(line.get(i - 1).loc, 16) + (line.get(i - 1).oper.length() - 3) / 2, 16);
            } else {
              line.get(i).loc = Integer.toString(Integer.parseInt(line.get(i - 1).loc, 16) + 1, 16);
            }
          } else if (line.get(i - 1).op.equals("RESB")) {
            line.get(i).loc = Integer
                .toString(Integer.parseInt(line.get(i - 1).loc, 16) + Integer.parseInt(line.get(i - 1).oper), 16);
          } else if (line.get(i).op.equals("BASE")) {
            line.get(i).loc = line.get(i - 1).loc;
          } else {
            line.get(i).loc = Integer.toString(
                Integer.parseInt(line.get(i - 1).loc, 16) + Integer.parseInt(line.get(i - 1).oper, 16) * 3, 16);
          }

        } else if (line.get(i - 1).op.charAt(0) == '+') {
          line.get(i).loc = Integer.toString(Integer.parseInt(line.get(i - 1).loc, 16) + 4, 16);
        } else if (checkOpPlus1(line, i)) {
          line.get(i).loc = Integer.toString(Integer.parseInt(line.get(i - 1).loc, 16) + 1, 16);
        } else if (checkOpPlus2(line, i)) {
          line.get(i).loc = Integer.toString(Integer.parseInt(line.get(i - 1).loc, 16) + 2, 16);
        } else {
          line.get(i).loc = Integer.toString(Integer.parseInt(line.get(i - 1).loc, 16) + 3, 16);
        }
      }
    } catch (Exception e) {
    }
    line.get(line.size() - 1).loc = "";
    for (int i = 2; i < line.size(); i++) {
      if (line.get(i).op.equals("BASE")) {
        line.get(i).loc = "";
      }
    }
  }

  public static void SetOpcode(ArrayList<Line> line) {

    String baseLoc = "";
    OpTab opTab = new OpTab();

    for (int i = 1; i < line.size() - 1; i++) {

      int opLoc, operLoc;
      String ansOp = "0";
      String ansXbpe = "0";
      String ansOper = "0";
      boolean plus = false;
      boolean mouse = false;
      boolean sharp = false;
      boolean num = false;

      if (line.get(i).op.equals("BASE")) {
        for (int l = 0; l < line.size(); l++) {
          if (line.get(l).label.equals(line.get(i).oper)) {
            baseLoc = line.get(l).loc;
            break;
          }
        }
        continue;
      }

      if (line.get(i).op.equals("START") || line.get(i).op.equals("END") || line.get(i).op.equals("WORD")
          || line.get(i).op.equals("BYTE") || line.get(i).op.equals("RESW") || line.get(i).op.equals("RESB")
          || line.get(i).op.equals("RSUB")) {
        falseInstructions(line, i);
      } else {
        if (line.get(i).op.charAt(0) == '+') {
          plus = true;
          ansXbpe = Integer.toString(Integer.parseInt(ansXbpe, 16) + 1, 16);
        }

        if (line.get(i).oper.charAt(0) == '#' || line.get(i).oper.charAt(0) == '@') {
          if (line.get(i).oper.charAt(0) == '#') {
            ansOp = Integer.toString(Integer.parseInt(ansOp, 16) + 1, 16);
            sharp = true;
            if (Character.getNumericValue(line.get(i).oper.charAt(1)) < 9) {
              num = true;
            }

          } else {
            ansOp = Integer.toString(Integer.parseInt(ansOp, 16) + 2, 16);
            mouse = true;
          }
        } else {
          ansOp = Integer.toString(Integer.parseInt(ansOp, 16) + 3, 16);
        }

        opLoc = findOpLoc(line, i, plus);
        ansOp = Integer.toString(Integer.parseInt(ansOp, 16) + Integer.parseInt(opTab.opCode[opLoc], 16), 16);

        while (ansOp.length() != 2) {
          ansOp = "0" + ansOp;
        }
        operLoc = findOperLoc(line, i, sharp, mouse);
        if (line.get(i).oper.charAt(line.get(i).oper.length() - 1) == 'X') {
          ansXbpe = Integer.toString(Integer.parseInt(ansXbpe, 16) + 8, 16);
        }

        if (line.get(i).op.equals("CLEAR") || line.get(i).op.equals("COMPR") || line.get(i).op.equals("TIXR")) {
          register(line, i, opLoc);
          continue;
        }

        if (num) {
          if (plus) {
            line.get(i).opcode = ansOp + "10"
                + Integer.toString(Integer.parseInt(line.get(i).oper.substring(1, line.get(i).oper.length())), 16);
          } else {
            line.get(i).opcode = ansOp + line.get(i).oper.substring(1, line.get(i).oper.length());
            while (line.get(i).opcode.length() != 6) {
              ansOp += "0";
              line.get(i).opcode = ansOp + line.get(i).oper.substring(1, line.get(i).oper.length());
            }
          }

          continue;
        }

        if (plus) {
          line.get(i).opcode = ansOp + ansXbpe + "0" + line.get(operLoc).loc;
          continue;
        }

        if (!line.get(i + 1).loc.equals("")) {

          if (Integer.parseInt(line.get(operLoc).loc, 16) < Integer.parseInt(line.get(i + 1).loc, 16)) {
            if (Integer.parseInt(line.get(operLoc).loc, 16) - Integer.parseInt(line.get(i + 1).loc, 16) < -2048) {
              ansXbpe = Integer.toString(Integer.parseInt(ansXbpe, 16) + 4, 16);
              ansOper = Integer
                  .toHexString(Integer.parseInt(line.get(operLoc).loc, 16) - Integer.parseInt(baseLoc, 16));
              while (ansOper.length() < 3) {
                ansOper = "0" + ansOper;
              }
              ansOper = ansOper.substring(ansOper.length() - 3, ansOper.length());

              line.get(i).opcode = ansOp + ansXbpe + ansOper;
            } else {
              ansXbpe = Integer.toString(Integer.parseInt(ansXbpe, 16) + 2, 16);
              ansOper = Integer.toHexString(
                  Integer.parseInt(line.get(operLoc).loc, 16) + Integer.parseInt(complement(line.get(i + 1).loc), 16));
              ansOper = ansOper.substring(ansOper.length() - 3, ansOper.length());

              line.get(i).opcode = ansOp + ansXbpe + ansOper;
            }

          } else {
            ansXbpe = Integer.toString(Integer.parseInt(ansXbpe, 16) + 2, 16);
            ansOper = Integer
                .toString(Integer.parseInt(line.get(operLoc).loc, 16) - Integer.parseInt(line.get(i + 1).loc, 16), 16);
            while (ansOper.length() < 3) {
              ansOper = "0" + ansOper;
            }
            line.get(i).opcode = ansOp + ansXbpe + ansOper;
          }

        } else {
          ansXbpe = Integer.toString(Integer.parseInt(ansXbpe, 16) + 2, 16);
          ansOper = Integer
              .toString(Integer.parseInt(line.get(operLoc).loc, 16) - Integer.parseInt(line.get(i + 2).loc, 16), 16);
          while (ansOper.length() < 3) {
            ansOper = "0" + ansOper;
          }
          line.get(i).opcode = ansOp + ansXbpe + ansOper;
        }

      }
    }

  }

  public static void Write(ArrayList<Line> line) throws IOException {

    FileWriter fw = new FileWriter("XE_final.txt");

    for (int i = 0; i < line.size(); i++) {
      fw.write(line.get(i).loc);
      for (int j = 0; j < 10 - line.get(i).loc.length(); j++) {
        fw.write(" ");
      }
      fw.write(line.get(i).label);
      for (int j = 0; j < 10 - line.get(i).label.length(); j++) {
        fw.write(" ");
      }
      fw.write(line.get(i).op);
      for (int j = 0; j < 10 - line.get(i).op.length(); j++) {
        fw.write(" ");
      }
      fw.write(line.get(i).oper);
      for (int j = 0; j < 10 - line.get(i).oper.length(); j++) {
        fw.write(" ");
      }
      fw.write(line.get(i).opcode);
      for (int j = 0; j < 10 - line.get(i).opcode.length(); j++) {
        fw.write(" ");
      }
      fw.write("\r\n");
    }
    fw.close();
  }

  public static boolean checkOpPlus1(ArrayList<Line> line, int i) {

    boolean ans = false;
    OpTab opTab = new OpTab();

    for (int j = 0; j < opTab.opPlus1.length; j++) {
      if (line.get(i - 1).op.equals(opTab.opPlus1[j])) {
        ans = true;
        break;
      }
    }

    return ans;
  }

  public static boolean checkOpPlus2(ArrayList<Line> line, int i) {

    boolean ans = false;
    OpTab opTab = new OpTab();

    for (int j = 0; j < opTab.opPlus2.length; j++) {
      if (line.get(i - 1).op.equals(opTab.opPlus2[j])) {
        ans = true;
        break;
      }
    }

    return ans;
  }

  public static boolean checkOp(String tmpStr) {

    boolean ans = false;
    OpTab opTab = new OpTab();

    for (int i = 0; i < opTab.op_Tab.length; i++) {
      if (tmpStr.equals(opTab.op_Tab[i]) || tmpStr.equals('+' + opTab.op_Tab[i])) {
        ans = true;
        break;
      }
    }

    return ans;
  }

  public static int findOpLoc(ArrayList<Line> line, int i, boolean plus) {

    int opLoc;
    OpTab opTab = new OpTab();

    for (opLoc = 0; opLoc < opTab.op_Tab.length; opLoc++) {

      if (plus) {

        if (line.get(i).op.substring(1, line.get(i).op.length()).equals(opTab.op_Tab[opLoc])) {
          break;
        }
      } else {
        if (line.get(i).op.equals(opTab.op_Tab[opLoc])) {
          break;
        }
      }
    }
    return opLoc;
  }

  public static int findOperLoc(ArrayList<Line> line, int i, boolean sharp, boolean mouse) {

    int operLoc;

    for (operLoc = 1; operLoc < line.size() - 1; operLoc++) {
      if (mouse || sharp) {
        if (line.get(i).oper.substring(1, line.get(i).oper.length()).equals(line.get(operLoc).label))
          break;
      } else if (line.get(i).oper.charAt(line.get(i).oper.length() - 1) == 'X') {

        String[] tmp = line.get(i).oper.split(",");

        if (tmp[0].equals(line.get(operLoc).label))
          break;
      } else {

        if (line.get(i).oper.equals(line.get(operLoc).label))
          break;
      }
    }

    return operLoc;
  }

  public static void falseInstructions(ArrayList<Line> line, int i) {

    if (line.get(i).op.equals("WORD")) {
      String zero = "";
      line.get(i).opcode = Integer.toString(Integer.parseInt(line.get(i).oper), 16);
      for (int j = 0; j < 6 - line.get(i).opcode.length(); j++) {
        zero += "0";
      }
      line.get(i).opcode = zero + line.get(i).opcode;

    } else if (line.get(i).op.equals("BYTE")) {
      if (line.get(i).oper.charAt(0) == 'X') {
        for (int l = 2; l < line.get(i).oper.length() - 1; l++) {
          line.get(i).opcode += line.get(i).oper.charAt(l);
        }
      } else {
        for (int l = 2; l < line.get(i).oper.length() - 1; l++) {
          line.get(i).opcode += Integer.toString((int) line.get(i).oper.charAt(l), 16);
        }
      }
    } else if (line.get(i).op.equals("RSUB")) {
      line.get(i).opcode = "4F0000";
    }

  }

  public static void register(ArrayList<Line> line, int i, int opLoc) {

    OpTab opTab = new OpTab();
    Register register = new Register();

    if (line.get(i).oper.length() == 1) {

      for (int j = 0; j < register.register.length; j++) {
        if (line.get(i).oper.equals(register.register[j])) {
          line.get(i).opcode = opTab.opCode[opLoc] + Integer.toString(j) + "0";
          break;
        }
      }
    } else {

      String[] tmp = line.get(i).oper.split(",");

      for (int j = 0; j < register.register.length; j++) {
        if (tmp[0].equals(register.register[j])) {
          line.get(i).opcode = opTab.opCode[opLoc] + Integer.toString(j);
          break;
        }
      }

      for (int j = 0; j < register.register.length; j++) {
        if (tmp[1].equals(register.register[j])) {
          line.get(i).opcode = line.get(i).opcode + Integer.toString(j);
          break;
        }
      }
    }
  }

  public static String complement(String num) {

    String FFFF = "FFFF";

    num = Integer.toHexString(Integer.parseInt(FFFF, 16) - Integer.parseInt(num, 16) + 1);
    num = num.substring(num.length() - 3, num.length());

    return num;
  }

  public static void sortOut(ArrayList<Line> line) {

    for (int i = 0; i < line.size(); i++) {
      while (line.get(i).loc.length() != 4) {
        line.get(i).loc = "0" + line.get(i).loc;
      }
      line.get(i).opcode = line.get(i).opcode.toUpperCase();
    }

  }
}
