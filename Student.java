import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Demo of Comparable and Comparator
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, Fall 2016, updated to use Java's PQ, more examples
 */
public class Student implements Comparable<Student> {
	private String name;
	private int year;
	
	public Student(String name, int year) {
		this.name = name;
		this.year = year;
	}

	/**
	 * Comparable: just use String's version (lexicographic)
	 */
	@Override
	public int compareTo(Student s2) {
		return name.compareTo(s2.name);
	}

	@Override
	public String toString() {
		return name + " '"+year;
	}
	
	public static void main(String[] args) {
		ArrayList<Student> students = new ArrayList<Student>();
		students.add(new Student("charlie", 18));
		students.add(new Student("alice", 20));
		students.add(new Student("bob", 19));
		students.add(new Student("elvis", 17));
		students.add(new Student("denise", 20));
		System.out.println("original:" + students);
		
		// Use the compareTo method (lexicographic order)
		PriorityQueue<Student> pq = new PriorityQueue<Student>();
		pq.addAll(students);
		
		System.out.println("\nlexicographic:");
		while (!pq.isEmpty()) System.out.println(pq.remove());
		
		// Use a custom Comparator.compare (length of name)
		class NameLengthComparator implements Comparator<Student> {
			public int compare(Student s1, Student s2) {
				return s1.name.length() - s2.name.length();
			}
		} 
		Comparator<Student> lenCompare = new NameLengthComparator();
		pq = new PriorityQueue<Student>(lenCompare);
		pq.addAll(students);
		System.out.println("\nlength:");
		while (!pq.isEmpty()) System.out.println(pq.remove());
		
		// Use a custom Comparator via Java 8 anonymous function (year)
		pq = new PriorityQueue<Student>((Student s1, Student s2) -> s1.year - s2.year);
		pq.addAll(students);
		System.out.println("\nyear:");
		while (!pq.isEmpty()) System.out.println(pq.remove());
	}
}
