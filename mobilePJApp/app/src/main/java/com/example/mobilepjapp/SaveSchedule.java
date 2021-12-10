package com.example.mobilepjapp;

public class SaveSchedule {
    public String scheduleName;
    public String scheduleDate;
    //public String scheduleTime;
    public String scheduleMemo;

    // 생성자
    public SaveSchedule(String scheduleName, String scheduleDate, String scheduleMemo) {
        this.scheduleName = scheduleName;
        this.scheduleDate = scheduleDate;
        //this.scheduleTime = scheduleTime;
        this.scheduleMemo = scheduleMemo;
    }

    public String getScheduleName(){ return scheduleName; }

    public String getScheduleDate() { return scheduleDate; }

    //public String getScheduleTime() { return scheduleTime; }

    public String getScheduleMemo() { return scheduleMemo; }
}