package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public class AppointmentCalendarDto {
    Long outpatientDepartmentId;

    Date startDate;

    Date endDate;

    int count;

    public AppointmentCalendarDto(Long outpatientDepartmentId, Date startDate, Date endDate, int count) {
        this.outpatientDepartmentId = outpatientDepartmentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.count = count;
    }

    public AppointmentCalendarDto() {
    }

    public void increaseCount() {
        this.count++;
    }

    public Long getOutpatientDepartmentId() {
        return outpatientDepartmentId;
    }

    public void setOutpatientDepartmentId(Long outpatientDepartmentId) {
        this.outpatientDepartmentId = outpatientDepartmentId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
