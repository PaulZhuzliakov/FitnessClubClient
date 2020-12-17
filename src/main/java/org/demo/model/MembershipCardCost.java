package org.demo.model;

public class MembershipCardCost {
    int periodOfTimeInDays;
    int daysVisitedInPeriodOfTime;
    float percentageOfVisitedDaysInPeriodOfTime;
    int membershipCardCostWithoutDiscount;
    int discount;
    int membershipCardCostWithDiscount;

    public MembershipCardCost() {
    }

    public MembershipCardCost(int periodOfTimeInDays, int daysVisitedInPeriodOfTime, float percentageOfVisitedDaysInPeriodOfTime,
                              int membershipCardCostWithoutDiscount, int discount, int membershipCardCostWithDiscount) {
        this.periodOfTimeInDays = periodOfTimeInDays;
        this.daysVisitedInPeriodOfTime = daysVisitedInPeriodOfTime;
        this.percentageOfVisitedDaysInPeriodOfTime = percentageOfVisitedDaysInPeriodOfTime;
        this.membershipCardCostWithoutDiscount = membershipCardCostWithoutDiscount;
        this.discount = discount;
        this.membershipCardCostWithDiscount = membershipCardCostWithDiscount;
    }

    public int getPeriodOfTimeInDays() {
        return periodOfTimeInDays;
    }

    public void setPeriodOfTimeInDays(int periodOfTimeInDays) {
        this.periodOfTimeInDays = periodOfTimeInDays;
    }

    public int getDaysVisitedInPeriodOfTime() {
        return daysVisitedInPeriodOfTime;
    }

    public void setDaysVisitedInPeriodOfTime(int daysVisitedInPeriodOfTime) {
        this.daysVisitedInPeriodOfTime = daysVisitedInPeriodOfTime;
    }

    public float getPercentageOfVisitedDaysInPeriodOfTime() {
        return percentageOfVisitedDaysInPeriodOfTime;
    }

    public void setPercentageOfVisitedDaysInPeriodOfTime(float percentageOfVisitedDaysInPeriodOfTime) {
        this.percentageOfVisitedDaysInPeriodOfTime = percentageOfVisitedDaysInPeriodOfTime;
    }

    public int getMembershipCardCostWithoutDiscount() {
        return membershipCardCostWithoutDiscount;
    }

    public void setMembershipCardCostWithoutDiscount(int membershipCardCostWithoutDiscount) {
        this.membershipCardCostWithoutDiscount = membershipCardCostWithoutDiscount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getMembershipCardCostWithDiscount() {
        return membershipCardCostWithDiscount;
    }

    public void setMembershipCardCostWithDiscount(int membershipCardCostWithDiscount) {
        this.membershipCardCostWithDiscount = membershipCardCostWithDiscount;
    }
}
