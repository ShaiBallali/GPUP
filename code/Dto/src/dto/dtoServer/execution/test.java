package dto.dtoServer.execution;

public class test {

    public static void main(String[] args) {
        String s = "moshe alfasi 11";
        String[] s1 = s.split(" ");
        s1[s1.length - 1] = "45";

        String v = "";
        for (String x : s1) {
            v = v + " " + x;
        }

        System.out.println(v);

    }
}
