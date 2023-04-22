package com.example.code.model.modelUtils;

import com.example.code.model.entities.Order;
import com.example.code.model.entities.User;
import com.example.code.model.exceptions.IncorrectTimePeriodException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TimePeriod {
    private int start;
    private int end;

    public TimePeriod(int start, int end) throws IncorrectTimePeriodException {
        this.start = start;
        this.end = end;

        checkTime();
    }

    public void setStart(int start) throws IncorrectTimePeriodException {
        this.start = start;
        checkTime();
    }

    public void setEnd(int end) throws IncorrectTimePeriodException {
        this.end = end;
        checkTime();
    }

    public boolean isAvailableForCouriersInThisDay(List<User> couriers, int day) {
        boolean isAvailable = false;
        for (User courier : couriers) {
            List<Order> currentOrders = courier.getOrdersFromCourierByDay(day);
            isAvailable = isFitForOrder(currentOrders);
            if (isAvailable) break;
        }
        return isAvailable;
    }

    public static List<TimePeriod> createDefaultListOfTimePeriods() throws IncorrectTimePeriodException {
        final List<TimePeriod> resultList = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            resultList.add(new TimePeriod(i, i + 1));
        }
        return resultList;
    }

    private boolean isFitForOrder(List<Order> orders) {
        boolean isAvailable = true;
        for (Order currentOrder : orders) {
            if (currentOrder.getStartTime() <= this.getStart()
                    && currentOrder.getEndTime() >= this.getStart()) {
                isAvailable = false;
                break;
            }
        }
        return isAvailable;
    }

    private void checkTime() throws IncorrectTimePeriodException {
        if (start < 0 || end < 0 || end > 23 || start > end) {
            throw new IncorrectTimePeriodException();
        }
    }
}
