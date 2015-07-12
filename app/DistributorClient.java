import java.util.List;
import java.util.ArrayList;


public class DistributorClient {

	public static void main(String[] args) {
		List<Double> dbTimes = new ArrayList<Double>();
		dbTimes.add(1.0);
		dbTimes.add(4.0);
		dbTimes.add(2.25);
		dbTimes.add(7.0);
		dbTimes.add(6.0);
		dbTimes.add(1.5);

		Distributor dist = new Distributor(24, 6, 8, 2);
		List<Double> distributedHours = dist.distribute();
		System.out.println("Neutral " + distributedHours.toString());
		System.out.println("Positive " + dist.distribute(24, 6, 8, 1));
		System.out.println("Negative " + dist.distribute(24, 6, 8, 3));

		dist.addTimes(dbTimes, distributedHours);
		System.out.println("\nAdded " + dbTimes);
		System.out.println("todo times " + distributedHours);

		System.out.println("\nOver " + dist.distribute(100, 20, 8, 1));
		System.out.println("Over " + dist.distribute(110, 20, 8, 1));
		System.out.println("Over " + dist.distribute(120, 20, 8, 1));
		System.out.println("Over " + dist.distribute(130, 20, 8, 1));
		// System.out.println("Little " + dist.distribute(5.25, 10, 8, 1));
		// System.out.println("Little " + dist.distribute(5, 10, 8, 2));
		// System.out.println("Little " + dist.distribute(5, 10, 8, 3));

	}


}