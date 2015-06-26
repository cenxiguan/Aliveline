import java.util.Arrays;
import java.util.Scanner;
import java.lang.IllegalArgumentException;


public class Distributor {
	
	private static int estimatedTime;
	private static int numOfDays;
	private static int maxHours;
	private static Scanner console;
	private static double[] distributedTime;
	private static final double increment = 0.25;


	public static void main(String [] args) {
		console = new Scanner(System.in);
		enterData();
		double lastDayTime = calcLastDayTime();
		distributedTime = new double[numOfDays];

		if (maxHours < (estimatedTime * 1.0 / numOfDays) ) {
			throw new IllegalArgumentException("Not enough time to finish project");
		}

		if (lastDayTime <= maxHours) {
			System.out.println("True");
			calcDistribution(lastDayTime);
		}else {
			System.out.println("False");
			lastDayTime = maxHours;
			calcLimitedDistribution(lastDayTime);
		}

		printData();
		
		System.out.println("Done...");
		console.close();
	}

	public static void enterData() {
		System.out.print("Estimated time: ");
		estimatedTime = console.nextInt();
		System.out.print("Number of Days: ");
		numOfDays = console.nextInt();
		System.out.print("Max hours of work: ");
		maxHours = console.nextInt();
	}

	public static void printData() {
		System.out.println("Array: ");
		System.out.println(Arrays.toString(distributedTime));
		double totalTime = 0;
		for (double time : distributedTime) {
			totalTime += time;
		}
		System.out.println("Total: " + totalTime);
		if (totalTime != estimatedTime){
			dealWithExtra(estimatedTime - totalTime);
			System.out.println(Arrays.toString(distributedTime));
		}
	}


	public static double calcLastDayTime() {
		return (estimatedTime * 2.0) / (numOfDays + 1);
	}

	public static void calcDistribution(double lastTime) {
		double difference = lastTime / numOfDays;
		for (int i = 0; i < numOfDays; i++ ) {
			double time = (i + 1) * difference;
			distributedTime[i] = round(time, increment);
		}
	}

	public static void calcLimitedDistribution(double lastTime) {
		double firstTime = (estimatedTime * 2.0 / numOfDays) - lastTime;
		double difference = (lastTime - firstTime) / (numOfDays - 1);
		for (int i = 0; i < numOfDays; i++ ) {
			double time = ( i * difference ) + firstTime;
			distributedTime[i] = round(time, increment);
		}
	}

	public static void dealWithExtra(double extraTime) {
		boolean positive = true;
		if (extraTime < 0) {
			positive = false;
			extraTime *= -1;
			distributeExtra(extraTime, positive);
		}else 
			distributeExtra(extraTime, positive);

	}

	public static void distributeExtra(double extra, boolean positive) {
		double counter = extra / increment;
		int index = 0;
		int distributionSize = distributedTime.length;
		while (counter > 0) {

			if (positive && distributedTime[distributionSize - index % distributionSize - 1] < maxHours) {
				distributedTime[distributionSize - (index % distributionSize) - 1] += increment;
				counter--;
			}else if (!positive && distributedTime[index % distributionSize] >= increment) {
				distributedTime[index % distributionSize] -= increment;
				counter--;
			}
			index++;
		}
	}

	public static double round(double value, double increment) {
		double remainder = value % increment;
		double roundVal = increment / 2.0;
		double result = value - remainder;

		if (remainder >= roundVal) {
			result += increment;
		}

		return result;
	}

}