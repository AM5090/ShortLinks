package Shared;

public class ColorPrinter {

  public enum Color {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    WHITE("\u001B[37m");

    private final String code;

    Color(String code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return code;
    }
  }

  public static void println(Color color, String text) {
    System.out.println(color + text + Color.RESET);
  }

  public static void print(Color color, String text) {
    System.out.print(color + text + Color.RESET);
  }

  public static void printf(Color color, String format, Object... args) {
    System.out.print(color);
    System.out.printf(format, args);
    System.out.print(Color.RESET);
  }
}