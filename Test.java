import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Test
 */
public class Test {

	public static void main(String[] args) {
		BiConsumer<String, Integer> bc = (s, i) -> System.out.println(s + " - " + i);
		bc.accept("a", 2); // a - 2
		BiPredicate<String, String> bp = (s1, s2) -> s1.contains(s2);
		System.out.println(bp.test("toto", "to")); // true
		System.out.println(bp.test("toto", "ta")); // false
	}
}