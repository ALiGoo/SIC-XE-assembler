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
  String[] op_Tab = { "ADD", "AND", "COMP", "DIV", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDCH", "LDL", "LDX", "MUL",
      "OR", "RD", "RSUB", "STA", "STCH", "STL", "STSW", "STX", "SUB", "TD", "TIX", "WD" };
  String[] opCode = { "18", "40", "28", "24", "3C", "30", "34", "38", "48", "00", "50", "08", "04", "20", "44", "D8",
      "4C", "0C", "54", "14", "E8", "10", "1C", "E0", "2C", "DC" };
}

class Card {
  String Tcard = "";
}

public class SIC {

  public static void main(String[] args) throws IOException {

    ArrayList<Line> line = new ArrayList<>();
    Card card = new Card();

    Read(line);
    SetLoc(line);
    SetOpcode(line);
    SetCard(card, line);
    Write(line);

    System.out.println(card.Tcard);
  }

  public static void Read(ArrayList<Line> line) throws FileNotFoundException {

    OpTab opTab = new OpTab();
    FileReader fr = new FileReader("SIC.txt");
    BufferedReader br = new BufferedReader(fr);
    Scanner scanner = new Scanner(br);

    while (scanner.hasNext()) {
      String tmpLine = scanner.nextLine();
      Scanner scn = new Scanner(tmpLine);
      String tmpLabel = "", tmpOp = "", tmpOper = "";

      while (scn.hasNext()) {
        String tmpStr = scn.next();

        if (tmpStr.equals("START") || tmpStr.equals("END") || tmpStr.equals("WORD") || tmpStr.equals("BYTE")
            || tmpStr.equals("RESW") || tmpStr.equals("RESB")) {
          tmpOp = tmpStr;
        } else {
          for (int i = 0; i < opTab.op_Tab.length; i++) {
            if (tmpStr.equals(opTab.op_Tab[i])) {
              tmpOp = tmpStr;
              break;
            }
          }
        }

        if (tmpOp.equals("")) {
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
        if (line.get(i - 1).op.equals("BYTE") || line.get(i - 1).op.equals("RESW")
            || line.get(i - 1).op.equals("RESB")) {

          if (line.get(i - 1).op.equals("BYTE")) {
            if (line.get(i - 1).oper.charAt(0) == 'C') {
              line.get(i).loc = Integer
                  .toString(Integer.parseInt(line.get(i - 1).loc, 16) + line.get(i - 1).oper.length() - 3, 16);
            } else {
              line.get(i).loc = Integer.toString(Integer.parseInt(line.get(i - 1).loc, 16) + 1, 16);
            }
          } else if (line.get(i - 1).op.equals("RESB")) {
            line.get(i).loc = Integer
                .toString(Integer.parseInt(line.get(i - 1).loc, 16) + Integer.parseInt(line.get(i - 1).oper), 16);
          } else {
            line.get(i).loc = Integer.toString(
                Integer.parseInt(line.get(i - 1).loc, 16) + Integer.parseInt(line.get(i - 1).oper, 16) * 3, 16);
          }

        } else {
          line.get(i).loc = Integer.toString(Integer.parseInt(line.get(i - 1).loc, 16) + 3, 16);

        }
      }
    } catch (Exception e) {
    }
    line.get(line.size() - 1).loc = "";
  }

  public static void SetOpcode(ArrayList<Line> line) {

    OpTab opTab = new OpTab();

    for (int i = 1; i < line.size() - 1; i++) {

      int j, k;

      if (line.get(i).op.equals("START") || line.get(i).op.equals("END") || line.get(i).op.equals("WORD")
          || line.get(i).op.equals("BYTE") || line.get(i).op.equals("RESW") || line.get(i).op.equals("RESB")
          || line.get(i).op.equals("RSUB")) {

        if (line.get(i).op.equals("WORD")) {
          String zero = "";
          line.get(i).opcode = Integer.toString(Integer.parseInt(line.get(i).oper), 16);
          for (j = 0; j < 6 - line.get(i).opcode.length(); j++) {
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
          line.get(i).opcode = "4C0000";
        }
      } else {
        for (j = 0; j < opTab.op_Tab.length; j++) {
          if (line.get(i).op.equals(opTab.op_Tab[j])) {
            break;
          }
        }

        for (k = 0; k < line.size(); k++) {
          if (line.get(i).oper.equals(line.get(k).label)) {
            break;
          }
        }

        if (line.get(i).oper.charAt(line.get(i).oper.length() - 1) == 'X') {
          String[] tmp = line.get(i).oper.split(",");

          for (k = 0; k < line.size(); k++) {
            if (tmp[0].equals(line.get(k).label)) {
              break;
            }
          }

          line.get(i).opcode = opTab.opCode[j] + Integer.toString(Integer.parseInt(line.get(k).loc) + 8000);
          continue;

        }
        try {
          line.get(i).opcode = opTab.opCode[j] + line.get(k).loc;
        } catch (Exception e) {
        }
      }

    }
  }

  public static void Write(ArrayList<Line> line) throws IOException {

    FileWriter fw = new FileWriter("SIC_final.txt");

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

  public static void SetCard(Card card, ArrayList<Line> line) {

    int num = 0;
    String tmp = "";
    String tmpFirst = "";

    for (int i = 1; i < line.size() - 1; i++) {
      if (num == 0) {
        tmpFirst = line.get(i).loc;
      }

      if (line.get(i).op.equals("RESB") || line.get(i).op.equals("RESW")) {
        if (!tmp.equals("")) {
          card.Tcard = card.Tcard + "T^" + tmpFirst + "^"
              + Integer.toHexString(Integer.parseInt(line.get(i - 1).loc, 16) - Integer.parseInt(tmpFirst, 16)) + tmp
              + "\n";
          num = 0;
          tmp = "";
        }

      } else if (num == 10) {
        card.Tcard = card.Tcard + "T^" + tmpFirst + "^"
            + Integer.toHexString(Integer.parseInt(line.get(i - 1).loc, 16) - Integer.parseInt(tmpFirst, 16)) + tmp
            + "\n";
        num = 0;
        tmp = "";
        i--;

      } else {
        tmp = tmp + "^" + line.get(i).opcode;
        num++;
      }

    }
  }
}
