package exemple;

public class Person {

	private String prenom;
	private int age;
	private boolean homme;

	public Person() {
	}

	public Person(String prenom, int age, boolean homme) {
		this.prenom = prenom;
		this.age = age;
		this.homme = homme;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isHomme() {
		return homme;
	}

	public void setHomme(boolean homme) {
		this.homme = homme;
	}

	@Override
	public String toString() {
		return "Person [age=" + age + ", homme=" + homme + ", prenom=" + prenom + "]";
	}

}
