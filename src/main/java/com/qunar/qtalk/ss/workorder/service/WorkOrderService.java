package com.qunar.qtalk.ss.workorder.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qunar.qchat.admin.util.CollectionUtil;
import com.qunar.qtalk.ss.sift.dao.HostUserDao;
import com.qunar.qtalk.ss.sift.entity.HostUsers;
import com.qunar.qtalk.ss.utils.JacksonUtils;
import com.qunar.qtalk.ss.workorder.bo.HostUserBO;
import com.qunar.qtalk.ss.workorder.bo.WoStatusEnum;
import com.qunar.qtalk.ss.workorder.bo.WorkOrderRequestBO;
import com.qunar.qtalk.ss.workorder.dao.WorkOrderDao;
import com.qunar.qtalk.ss.workorder.model.WorkOrder;
import com.qunar.qtalk.ss.session.model.JsonResult;
import com.qunar.qtalk.ss.utils.JsonResultUtils;
import com.qunar.qtalk.ss.workorder.model.WorkOrderRemark;
import com.qunar.qtalk.ss.workorder.model.WorkOrderStatusLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.qunar.qtalk.ss.workorder.bo.WoStatusEnum.TO_BE_PROCESSED;

@Service("workOrderService")
@Transactional
public class WorkOrderService {
    private static final Logger logger = LoggerFactory.getLogger(WorkOrderService.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Resource
    WorkOrderDao workOrderDao;
    @Resource
    HostUserDao hostUserDao;

    public JsonResult modifyWorkOrder(WorkOrderRequestBO workOrderBO, String operatorName) {
        WorkOrder workOrder = new WorkOrder();
        BeanUtils.copyProperties(workOrderBO, workOrder);

        if (StringUtils.isNotEmpty(workOrder.getWoNumber())) {
            WorkOrder order = workOrderDao.selectByWoNumber(workOrder.getWoNumber());
            if (order != null) {
                int updateWorkerOrder = workOrderDao.updateWorkerOrder(workOrder);
                saveUpdateStatus(order, workOrder, operatorName);
                logger.info("WorkOrderService updateWorkerOrder:{}", updateWorkerOrder);
                if (StringUtils.isNotEmpty(workOrderBO.getRemark())) {
                    saveRemark(workOrderBO.getRemark(), operatorName, workOrder.getWoNumber());
                }
                return JsonResultUtils.success();
            }
        }
        if (!checkParam(workOrder)) {
            return JsonResultUtils.fail(411, "参数错误");
        }

        String woNumber = workOrderDao.insertWorkOrder(workOrder);
        logger.info("WorkOrderService insertWorkOrder:{}", woNumber);
        saveOperatorLog(operatorName, "创建工单", woNumber);
        return JsonResultUtils.success();
    }

    private boolean checkParam(WorkOrder workOrder) {
        if (StringUtils.isAnyEmpty(workOrder.getOwnClass(), workOrder.getWoTitle(), workOrder.getFaultLocation(), workOrder.getFaultDesc(), workOrder.getSubmitter())) {
            return false;
        }
        return true;
    }

    private void saveUpdateStatus(WorkOrder source, WorkOrder target, String operatorName) {
        Map<String, Object> modifies = new HashMap<>();
        if (null == source || null == target) {
            if (null == source && null == target) return;
            else if (null == target) return;
            else {
                modifies = mapper.convertValue(target, new TypeReference<Map<String, Object>>() {
                });
            }
        }

        Map<String, Object> sourceMap = mapper.convertValue(source, new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> targetMap = mapper.convertValue(target, new TypeReference<Map<String, Object>>() {
        });

        for (Map.Entry<String, Object> entry : targetMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object soruceObject = sourceMap.get(key);
            if (value != null && !Objects.equals(value, soruceObject)) {
                if (soruceObject != null && soruceObject.toString().replaceAll(" ", "").equalsIgnoreCase(JacksonUtils.obj2String(value))) {
                    continue;
                }
                modifies.put(key, value);
            }
        }
        if (MapUtils.isEmpty(modifies)) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(operatorName);
        int editWo = 0;
        int distributeWo = 0;
        for (Map.Entry<String, Object> entry : modifies.entrySet()) {
            switch (entry.getKey()) {
                case "faultLocation":
                case "faultDesc":
                case "faultImages":
                case "extendInfo":
                    if (editWo == 0) {
                        stringBuilder.append(":编辑工单");
                    }
                    editWo++;
                    break;
                case "woStatus":
                    if (entry.getValue().equals("1")) {
                        stringBuilder.append(":受理工单");
                    } else if (entry.getValue().equals("2")) {
                        stringBuilder.append(":关闭工单");
                    }
                    break;
                case "woClass":
                    stringBuilder.append(":转工单");
                    break;
                case "owner":
                case "processPerson":
                    if (distributeWo == 0) {
                        stringBuilder.append(":分配工单");
                    }
                    distributeWo++;
                    break;
            }
        }

        System.out.println("-----------------" + stringBuilder.toString());
        saveOperatorLog(operatorName, stringBuilder.toString(), target.getWoNumber());
    }

    private void saveOperatorLog(String operatorName, String operatorLog, String woNumber) {
        WorkOrderStatusLog statusLog = new WorkOrderStatusLog();
        statusLog.setOperatorName(getUserName(operatorName));
        statusLog.setStatusLog(operatorLog);
        statusLog.setWoNumber(woNumber);
        int orderLog = workOrderDao.saveWorkOrderLog(statusLog);
        logger.info("saveUpdateStatus update:{}", orderLog);
    }

    public JsonResult selectWorderOrderList(WorkOrderRequestBO requestBO, String loginUser) {
        Set<String> woClass = getWoClass(loginUser);
        requestBO.setProcessUser(loginUser);
        requestBO.setUserClass(woClass);
        int listCount = workOrderDao.selectWoListCount(requestBO);
        System.out.println(listCount);
        List<WorkOrder> workOrders = workOrderDao.selectWoList(requestBO);
        Map<String, Object> result = new HashMap<>(2);
        result.put("total", listCount);
        result.put("workOrders", workOrders);
//        System.out.println(JacksonUtils.obj2String(workOrders));
        return JsonResultUtils.success(result);

    }

    public JsonResult selectWorkOrderClass(String operatorName, String ownClass, String ownModule) {
        Map<String, Object> result = new HashMap<>(2);
        Set<String> workOrderClass = workOrderDao.selectWorkOrderClass(operatorName);
        Set<String> workOrderClassBOS = workOrderDao.selectWorkOrderModule(operatorName, ownClass, ownModule);
        workOrderClass = workOrderClass == null ? new HashSet<>() : workOrderClass;
        workOrderClassBOS = workOrderClassBOS == null ? new HashSet<>() : workOrderClassBOS;
        CollectionUtil.filterNull(workOrderClass);
        CollectionUtil.filterNull(workOrderClassBOS);
        result.put("ownClassSet", workOrderClass);
        result.put("ownModuleSet", workOrderClassBOS);

        return JsonResultUtils.success(result);
    }

    public JsonResult saveRemark(String remark, String operatorId, String woNumber) {
        String userName = getUserName(operatorId);

        int saveRemark = workOrderDao.saveRemark(userName, remark, woNumber);
        if (saveRemark != 0) {
            return JsonResultUtils.success("success");
        } else {
            return JsonResultUtils.fail(412, "保存出错");
        }
    }

    public JsonResult selectRemarkList(String woNumber) {
        List<WorkOrderRemark> workOrderRemarks = workOrderDao.selectRemarkList(woNumber);
        return JsonResultUtils.success(workOrderRemarks);
    }

    public JsonResult selectStatusLog(String woNumber) {
        List<WorkOrderStatusLog> orderStatusLogs = workOrderDao.selectWorkOrderLogList(woNumber);
        return JsonResultUtils.success(orderStatusLogs);
    }

    public JsonResult selectWorkOrderByWoNumber(String woNumber, String operatorName) {
        Map<String, Object> result = new HashMap<>();
        WorkOrder workOrder = workOrderDao.selectWorkOrderByWoNumber(woNumber);
        List<WorkOrderRemark> workOrderRemarks = workOrderDao.selectRemarkList(woNumber);
        workOrder = workOrder == null ? new WorkOrder() : workOrder;
        workOrderRemarks = workOrderRemarks == null ? new ArrayList<>() : workOrderRemarks;
        result.put("workOrder", workOrder);
        result.put("remarks", workOrderRemarks);
        result.put("isAssignee", operatorName.equalsIgnoreCase(workOrder.getAssignee()));
        return JsonResultUtils.success(result);
    }


    private Set<String> getWoClass(String loginUser) {
        return workOrderDao.selectWoClass(loginUser);
    }


    public JsonResult searchUserByUserId(String userId) {
        List<HostUserBO> hostUsers = hostUserDao.searchUsersByUserId(userId);
        return JsonResultUtils.success(hostUsers);
    }

    public JsonResult orderDashboard(String userId) {
        Map<String, Object> result = new HashMap<>(4);
        int ownCount = workOrderDao.selectOwnCount(userId);
        int lastWeekCount = 0;
        Set<String> woClass = null;
        if (!isSuperAdmin(userId)) {
            woClass = getWoClass(userId);
        }
        lastWeekCount = workOrderDao.selectLastWeekCount(woClass);
        int countAdvisoryByType = workOrderDao.selectOrderCountByType("advisory", woClass);
        int countRepairByType = workOrderDao.selectOrderCountByType("repair", woClass);

        result.put("advisoryCount", countAdvisoryByType);
        result.put("repairCount", countRepairByType);
        result.put("lastWeekCount", lastWeekCount);
        result.put("ownCount", ownCount);

        return JsonResultUtils.success(result);
    }

    public JsonResult getToBeProcessedList(String userId, int rowNum, int page) {
        Map<String, Object> result = new HashMap<>(2);
        int offset = (page - 1) * rowNum;
        Set<String> woClass = null;
        Integer status = WoStatusEnum.TO_BE_PROCESSED.getStatus();
        if (!isSuperAdmin(userId)) {
            woClass = getWoClass(userId);
        }
        int countByClass = workOrderDao.selectOrderCountByClass(woClass, status);
        List<WorkOrder> workOrderList = workOrderDao.selectOrderListByClass(woClass, status, rowNum, offset);
        result.put("total", countByClass);
        result.put("toBeProcessedList", workOrderList);

        return JsonResultUtils.success(result);
    }

    public JsonResult getProcessingList(String userId, int rowNum, int page) {
        Map<String, Object> result = new HashMap<>(2);
        int offset = (page - 1) * rowNum;
        Set<String> woClass = null;
        Integer status = WoStatusEnum.PROCESSING.getStatus();
        if (!isSuperAdmin(userId)) {
            woClass = getWoClass(userId);
        }
        int countByClass = workOrderDao.selectOrderCountByClass(woClass, status);
        List<WorkOrder> workOrderList = workOrderDao.selectOrderListByClass(woClass, status, rowNum, offset);
        result.put("total", countByClass);
        result.put("processingList", workOrderList);

        return JsonResultUtils.success(result);
    }


    private boolean isSuperAdmin(String userId) {
        // TODO 是否是超级管理员
        if (StringUtils.isNotEmpty(userId)) {
            return true;
        }
        return false;
    }


    private String getUserName(String operatorId) {
        // TODO 调接口获取中文用户名
        List<HostUsers> hostUsers = hostUserDao.selectUsersByUserIds(Arrays.asList(operatorId));
        if (CollectionUtils.isNotEmpty(hostUsers)) {
            String userName = hostUsers.get(0).getUserName();
            return userName;
        }
        logger.warn("getUserName :{} not get", operatorId);
        return operatorId;
    }

}
