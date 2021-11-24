package com.example.calendar;

public class ScheduleDTO {
    private String scheduleID;

    // getter&setter
    public String getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
    }

    public ScheduleDTO(String scheduleID) {
        this.scheduleID = scheduleID;
    }
}
