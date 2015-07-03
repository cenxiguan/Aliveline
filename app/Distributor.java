import java.lang.Object;
import java.lang.System;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
import java.lang.IllegalArgumentException;

public class Distributor {
	
	private static int estimatedTime;
	private static int numOfDays;
	private static int maxHours;
	private static int slope;
	private static boolean posNeg;
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
			calcDistribution(lastDayTime);
		}else {
			lastDayTime = maxHours;
			calcLimitedDistribution(lastDayTime);
		}



		double[] databaseTimes = new double[numOfDays];
		Random rand = new Random();
		for(int i = 0; i < databaseTimes.length; i++ ) {
			databaseTimes[i] += (double) rand.nextInt(5);
		}

		addTimes(databaseTimes, distributedTime);

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
		System.out.print("Positive Slope? ");
		String input = console.nextLine();
		input = console.nextLine();
		if (input.toUpperCase().contains("N")) {
			slope = -1;
			posNeg = false;
		} else {
			slope = 1;
			posNeg = true;
		}
	}

	public static void printData() {
		System.out.println("Array: ");
		System.out.println(Arrays.toString(distributedTime));
		double totalTime = arraySum(distributedTime);
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
		if (slope == -1) {
			reverseArray(distributedTime);
		}
	}

	public static void calcLimitedDistribution(double lastTime) {
		double firstTime = (estimatedTime * 2.0 / numOfDays) - lastTime;
		double difference = (lastTime - firstTime) / (numOfDays - 1);
		for (int i = 0; i < numOfDays; i++ ) {
			double time = ( i * difference ) + firstTime;
			distributedTime[i] = round(time, increment);
		}
		if (estimatedTime != arraySum(distributedTime)) {
			if (slope == -1) {
				reverseArray(distributedTime);
			}
			dealWithExtra(estimatedTime - arraySum(distributedTime));
		}
	}

	public static void dealWithExtra(double extraTime) {
		if (extraTime < 0) {
			extraTime *= -1;
			removeExtra(distributedTime, extraTime);
		}else 
			distributeExtra(distributedTime, extraTime, posNeg);

	}

	public static void removeExtra(double[] arr, double extra) {
		double counter = extra / increment;
		int stopCounter = 0;
		int index = 0;
		int distributionSize = arr.length;
		while (counter > 0 && stopCounter <= arr.length) {
			stopCounter++;
			if (arr[index % distributionSize] >= increment) {
				arr[index % distributionSize] -= increment;
				counter--;
				stopCounter = 0;
			}
			index++;
		}
	}

	public static void distributeExtra(double[] arr, double extra, boolean positive) {
		double counter = extra / increment;
		int stopCounter = 0;
		int index = 0;
		int distributionSize = arr.length;
		while (counter > 0 && stopCounter <= arr.length) {
			stopCounter++;
			if (positive && arr[distributionSize - index % distributionSize - 1] < maxHours) {
				arr[distributionSize - (index % distributionSize) - 1] += increment;
				counter--;
				stopCounter = 0;
			}else if (!positive && arr[index % distributionSize] < maxHours) {
				arr[index % distributionSize] += increment;
				counter--;
				stopCounter = 0;
			}
			index++;
		}

		if (stopCounter > arr.length)
			System.out.println("Not Possiblee!!! Too much work T^T");

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

	/**
	 * In place addition of two arrays. Does not instantiate new array.
	 * @param result
	 * @param arr2
	 */
	private static void addTimes(double[] results, double[] other) {
		if (results.length != other.length)
			return;

		System.out.println("Rslts " + Arrays.toString(results) + " = " + arraySum(results));
		System.out.println("Other " + Arrays.toString(other) + " = " + arraySum(other));
		double leftovers = 0;

		for(int i = 0; i < results.length; i++) {
			double result = results[i] + other[i];
			if (result > maxHours) {
				leftovers += result - maxHours;
				results[i] = maxHours;
			} else {
				results[i] = result;
			}
		}
		System.out.println("Results w/o Left " + Arrays.toString(results) + " = " + arraySum(results));

		if (leftovers > 0) {
			distributeExtra(results, leftovers, true);
			System.out.println("Results w/ Left " + Arrays.toString(results) + " = " + arraySum(results));
		}

	}

	public static void reverseArray(double[] array) {
		for(int i = 0; i < array.length / 2; i++ ) {
			double temp = array[i];
			array[i] = array[array.length - 1 - i];
			array[array.length - 1 - i] = temp;
		}
	}

	public static double arraySum(double[] array) {
		double sum = 0;
		for(double item : array) {
			sum += item;
		}
		return sum;
	}

}