package com.qunar.qtalk.ss.workorder.model;

import java.sql.Timestamp;

public class WorkOrder {

    private Long id;
    private String woNumber;
    private String woTitle;
    private String faultLocation;
    private String faultDesc;
    private String[] faultImages;
    private Integer woStatus;
    private String woType;
    private String submitter;
    private String assignee;
    private String[] processPerson;
    private Object extendInfo;
    private String ownClass;
    private String ownModule;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWoNumber() {
        return woNumber;
    }

    public void setWoNumber(String woNumber) {
        this.woNumber = woNumber;
    }

    public String getWoTitle() {
        return woTitle;
    }

    public void setWoTitle(String woTitle) {
        this.woTitle = woTitle;
    }

    public String getFaultLocation() {
        return faultLocation;
    }

    public void setFaultLocation(String faultLocation) {
        this.faultLocation = faultLocation;
    }

    public String getFaultDesc() {
        return faultDesc;
    }

    public void setFaultDesc(String faultDesc) {
        this.faultDesc = faultDesc;
    }

    public String[] getFaultImages() {
        return faultImages;
    }

    public void setFaultImages(String[] faultImages) {
        this.faultImages = faultImages;
    }

    public Integer getWoStatus() {
        return woStatus;
    }

    public void setWoStatus(Integer woStatus) {
        this.woStatus = woStatus;
    }

    public String getWoType() {
        return woType;
    }

    public void setWoType(String woType) {
        this.woType = woType;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String[] getProcessPerson() {
        return processPerson;
    }

    public void setProcessPerson(String[] processPerson) {
        this.processPerson = processPerson;
    }

    public Object getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(Object extendInfo) {
        this.extendInfo = extendInfo;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getOwnClass() {
        return ownClass;
    }

    public void setOwnClass(String ownClass) {
        this.ownClass = ownClass;
    }

    public String getOwnModule() {
        return ownModule;
    }

    public void setOwnModule(String ownModule) {
        this.ownModule = ownModule;
    }
}
