package com.qunar.qtalk.ss.workorder.bo;

import com.qunar.qtalk.ss.workorder.model.WorkOrder;

import java.util.Set;

public class WorkOrderRequestBO extends WorkOrder {
    private String processUser;
    private Set<String> userClass;
    private String remark;
    private int rowNum;
    private int page;
    private int offset;


    public String getProcessUser() {
        return processUser;
    }

    public void setProcessUser(String processUser) {
        this.processUser = processUser;
    }

    public Set<String> getUserClass() {
        return userClass;
    }

    public void setUserClass(Set<String> userClass) {
        this.userClass = userClass;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
