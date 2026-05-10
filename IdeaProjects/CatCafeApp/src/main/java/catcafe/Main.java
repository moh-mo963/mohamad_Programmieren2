package catcafe;

public class Main {

  public static void main(String[] args) {
    CatCafe cafe = new CatCafe();

    cafe.addCat("Milo");
    cafe.addCat("Luna");

    System.out.println("Anzahl Katzen im Café: " + cafe.getCatCount());
  }
}
