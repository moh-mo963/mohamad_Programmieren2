package catcafe;

import java.util.ArrayList;
import java.util.List;

public class CatCafe {

  private final List<String> cats = new ArrayList<>();

  public void addCat(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException();
    }
    if (!cats.contains(name)) {
      cats.add(name);
    }
  }

  public void removeCat(String name) {
    cats.remove(name);
  }

  public int getCatCount() {
    return cats.size();
  }
}
