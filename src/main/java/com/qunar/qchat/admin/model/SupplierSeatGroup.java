package com.qunar.qchat.admin.model;

import java.util.ArrayList;
import java.util.List;

public class SupplierSeatGroup {
    private String supplierName;
    private long supplierId;
    private List<SeatGroupVO> seatGroups = new ArrayList<>();

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public List<SeatGroupVO> getSeatGroups() {
        return seatGroups;
    }

    public void setSeatGroups(List<SeatGroupVO> seatGroups) {
        this.seatGroups = seatGroups;
    }

}
