public class Main {
    public static void main(String[] args) {
        MyMap<String, String> stringMap = new MyMap();
        stringMap.insert("a", "b");
        stringMap.insert("b", "c");
        stringMap.insert("c", "d");
        stringMap.insert("d", "e");
        stringMap.insert("e", "f");
        stringMap.insert("f", "g");
        stringMap.insert("g", "h");
        System.out.println(stringMap);
    }
}