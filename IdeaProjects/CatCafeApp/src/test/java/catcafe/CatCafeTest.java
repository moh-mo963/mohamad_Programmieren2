package catcafe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CatCafeTest {

  @Test
  void givenEmptyCafe_whenCatArrives_thenCafeIsNotEmpty() {
    // gegeben
    CatCafe cafe = new CatCafe();

    // wenn
    cafe.addCat("Milo");

    // dann
    assertEquals(1, cafe.getCatCount());
  }

  @Test
  void givenCafeWithOneCat_whenSameCatAddedAgain_thenCountDoesNotIncrease() {
    CatCafe cafe = new CatCafe();
    cafe.addCat("Milo");

    cafe.addCat("Milo");

    assertEquals(1, cafe.getCatCount());
  }

  @Test
  void givenCafeWithCats_whenCatLeaves_thenCountDecreases() {
    CatCafe cafe = new CatCafe();
    cafe.addCat("Luna");
    cafe.removeCat("Luna");

    assertEquals(0, cafe.getCatCount());
  }

  @Test
  void givenEmptyCafe_whenCatLeaves_thenNothingChanges() {
    CatCafe cafe = new CatCafe();

    cafe.removeCat("Ghost");

    assertEquals(0, cafe.getCatCount());
  }

  @Test
  void givenMultipleCats_whenQueryCount_thenCorrectValueReturned() {
    CatCafe cafe = new CatCafe();
    cafe.addCat("A");
    cafe.addCat("B");
    cafe.addCat("C");

    assertEquals(3, cafe.getCatCount());
  }

  @Test
  void givenCafe_whenNullCatAdded_thenExceptionThrown() {
    CatCafe cafe = new CatCafe();

    assertThrows(IllegalArgumentException.class, () -> cafe.addCat(null));
  }

  @Test
  void givenCafe_whenEmptyNameAdded_thenExceptionThrown() {
    CatCafe cafe = new CatCafe();

    assertThrows(IllegalArgumentException.class, () -> cafe.addCat(""));
  }

  @Test
  void givenCafeWithCats_whenRemoveUnknownCat_thenNoException() {
    CatCafe cafe = new CatCafe();
    cafe.addCat("Milo");

    assertDoesNotThrow(() -> cafe.removeCat("Unknown"));
  }

  @Test
  void givenCafe_whenAddingManyCats_thenAllAreStored() {
    CatCafe cafe = new CatCafe();

    for (int i = 0; i < 10; i++) {
      cafe.addCat("Cat" + i);
    }

    assertEquals(10, cafe.getCatCount());
  }

  @Test
  void givenCafe_whenCreated_thenInitiallyEmpty() {
    CatCafe cafe = new CatCafe();

    assertEquals(0, cafe.getCatCount());
  }
}
