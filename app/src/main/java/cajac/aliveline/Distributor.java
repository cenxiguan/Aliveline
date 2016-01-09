package cajac.aliveline;


/**
 * Created by Chungyuk Takahashi on 7/6/2015.
 */

// import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Distributor {

    private double estimatedTime;
    private int numOfDays;
    private int maxHours;
    private int timeUsage;
    private static final int positiveSlope = 1;
    private static final int neutralSlope = 2;
    private static final int negativeSlope = 3;
    private List<Double> distributedTime;
    private static final double increment = 0.25;
    private static final String LOG_TAG = "DISTRIBUTOR";

    public Distributor(double estimatedTime, int numOfDays, int maxHours, int timeUsage) {
        this.estimatedTime = estimatedTime;
        this.numOfDays = numOfDays;
        this.maxHours = maxHours;
        this.timeUsage = timeUsage;
    }

    public Distributor() { }

    public List<Double> distribute(double estimatedTime, int numOfDays, int maxHours, int timeUsage) {
        this.estimatedTime = estimatedTime;
        this.numOfDays = numOfDays;
        this.maxHours = maxHours;
        this.timeUsage = timeUsage;
        return distribute();
    }

    public List<Double> distribute() {
        double lastDayTime = calcLastDayTime();
        distributedTime = new ArrayList<>(Collections.nCopies(numOfDays, 0.0));

//        if (maxHours < (estimatedTime / numOfDays) ) {
//            /*
//            Make dialog pop up, which will ask for whether or not user would like to work overtime
//             or go back and change settings.
//             */
//            // Temporary
//            Log.e(LOG_TAG, "Not enough time to work");
//            for (int i = 0; i < numOfDays; i++) {
//                distributedTime.set(i, maxHours * 1.0);
//            }
//            distributedTime.add(distributedTime.size(), -1.0 * (estimatedTime - numOfDays * maxHours) );
//            return distributedTime;
//        }

        if (timeUsage == neutralSlope)
            evenDistribution();
        else {
            if (lastDayTime <= maxHours)
                calcDistribution(lastDayTime);
            else {
                lastDayTime = maxHours;
                calcLimitedDistribution(lastDayTime);
            }

            if (timeUsage == negativeSlope)
                Collections.reverse(distributedTime);
        }

        if (estimatedTime != arraySum(distributedTime))
            dealWithExtra(estimatedTime - arraySum(distributedTime));

        return distributedTime;
    }

    private double calcLastDayTime() {
        return (estimatedTime * 2.0) / (numOfDays + 1);
    }

    private void evenDistribution() {
        double hoursPerDay = round(estimatedTime / numOfDays, increment);
        for (int i = 0; i < distributedTime.size(); i++) {
            distributedTime.set(i, hoursPerDay);
        }
    }

    private void calcDistribution(double lastTime) {
        double difference = lastTime / numOfDays;
        for (int i = 0; i < numOfDays; i++ ) {
            double time = (i + 1) * difference;
            distributedTime.set(i, round(time, increment));
        }
    }

    public void calcLimitedDistribution(double lastTime) {
        double firstTime = (estimatedTime * 2.0 / numOfDays) - lastTime;
        double difference = (lastTime - firstTime) / (numOfDays - 1);
        for (int i = 0; i < numOfDays; i++ ) {
            double time = ( i * difference ) + firstTime;
            distributedTime.set(i, round(time, increment));
        }
    }

    public void dealWithExtra(double extraTime) {
        double overtime;
        if (extraTime < 0) {
            extraTime *= -1;
            if (timeUsage == positiveSlope)
                overtime = removeExtraPos(distributedTime, extraTime);
            else
                overtime = removeExtraNeg(distributedTime, extraTime);
        }else {
            if (timeUsage == positiveSlope)
                overtime = distributeExtraPos(distributedTime, extraTime);
            else
                overtime = distributeExtraNeg(distributedTime, extraTime);
        }
        if (overtime > 0)
            distributedTime.add(distributedTime.size(), -1.0 * overtime);

    }

    public double removeExtraPos(List<Double> list, double extra) {
        double counter = extra / increment;
        int stopCounter = 0, index = 0, distributionSize = list.size(), head;
        while (counter > 0 && stopCounter <= list.size()) {
            head = index % distributionSize;
            stopCounter++;
            if (list.get(head) >= increment) {
                list.set(head, list.get(head) - Math.min(increment, counter));
                counter--;
                stopCounter = 0;
            }
            index++;
        }
        if (stopCounter > list.size())	return counter * increment;
        else return 0.0;
    }

    public double removeExtraNeg(List<Double> list, double extra) {
        double counter = extra / increment;
        int stopCounter = 0, index = 0, distributionSize = list.size(), tail;
        while (counter > 0 && stopCounter <= list.size()) {
            tail = distributionSize - (index % distributionSize) - 1;
            stopCounter++;
            if (list.get(tail) >= increment) {
                list.set(tail, list.get(tail) - Math.min(increment, counter));
                counter--;
                stopCounter = 0;
            }
            index++;
        }
        if (stopCounter > list.size())	return counter * increment;
        else return 0.0;

    }

    public double distributeExtraPos(List<Double> list, double extra) {
        double counter = extra / increment;
        int stopCounter = 0, index = 0, distributionSize = list.size(), tail;
        while (counter > 0 && stopCounter <= list.size()) {
            tail = distributionSize - index % distributionSize - 1;
            stopCounter++;
            if (list.get(tail) < maxHours) {
                list.set(tail, list.get(tail) + Math.min(increment, counter));
                counter--;
                stopCounter = 0;
            }
            index++;
        }
        if (stopCounter > list.size())	return counter * increment;
        else return 0.0;
    }

    public double distributeExtraPos(List<Double> list, List<Double> other, double extra) {
        double counter = extra / increment;
        int stopCounter = 0, index = 0, distributionSize = list.size(), tail;
        while (counter > 0 && stopCounter <= list.size()) {
            tail = distributionSize - index % distributionSize - 1;
            stopCounter++;
            if (list.get(tail) < maxHours && other.get(tail) < maxHours) {
                list.set(tail, list.get(tail) + Math.min(increment, counter));
                other.set(tail, other.get(tail) + Math.min(increment, counter));
                counter--;
                stopCounter = 0;
            }
            index++;
        }
        if (stopCounter > list.size())	return counter * increment;
        else return 0.0;
    }

    public double distributeExtraNeg(List<Double> list, double extra) {
        double counter = extra / increment;
        int stopCounter = 0, index = 0, distributionSize = list.size(), head;
        while (counter > 0 && stopCounter <= list.size()) {
            head = index % distributionSize;
            stopCounter++;
            if (list.get(head) < maxHours) {
                list.set(head, list.get(head) + Math.min(increment, counter));
                counter--;
                stopCounter = 0;
            }
            index++;
        }
        if (stopCounter > list.size())	return counter * increment;
        else return 0.0;
        // Will bring up dialog which asks user to either work overtime or go back to change settings

    }

    public double distributeExtraNeg(List<Double> list, List<Double> other, double extra) {
        double counter = extra / increment;
        int stopCounter = 0, index = 0, distributionSize = list.size(), head;
        while (counter > 0 && stopCounter <= list.size()) {
            head = index % distributionSize;
            stopCounter++;
            if (list.get(head) < maxHours && other.get(head) < maxHours) {
                list.set(head, list.get(head) + Math.min(increment, counter));
                other.set(head, other.get(head) + Math.min(increment, counter));
                counter--;
                stopCounter = 0;
            }
            index++;
        }
        if (stopCounter > list.size())	return counter * increment;
        else return 0.0;
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
     * @param database
     * @param todoHours
     */
    public void addTimes(List<Double> database, List<Double> todoHours) {
        if (database.size() != todoHours.size())
            return;

        double leftovers = 0;
        for(int i = 0; i < database.size(); i++) {
            double result = database.get(i) + todoHours.get(i);
            if (result > maxHours) {
                leftovers += result - maxHours;
                database.set(i, (double) maxHours);
                todoHours.set(i, todoHours.get(i) - (result - maxHours));
            } else {
                database.set(i, result);
            }
        }
        if (leftovers > 0) {
            double overtime;
            if (timeUsage == positiveSlope)
                overtime = distributeExtraPos(database, todoHours, leftovers);
            else
                overtime = distributeExtraNeg(database, todoHours, leftovers);

            if (overtime > 0) todoHours.add(distributedTime.size(), -1.0 * overtime);
        }
    }

    public static double arraySum(List<Double> list) {
        double sum = 0;
        for(double item : list) {
            sum += item;
        }
        return sum;
    }



}