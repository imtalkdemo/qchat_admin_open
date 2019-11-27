package com.qunar.qtalk.ss.workorder.dao;

import com.qunar.qtalk.ss.workorder.bo.WorkOrderClassBO;
import com.qunar.qtalk.ss.workorder.bo.WorkOrderRequestBO;
import com.qunar.qtalk.ss.workorder.model.WorkOrder;
import com.qunar.qtalk.ss.workorder.model.WorkOrderRemark;
import com.qunar.qtalk.ss.workorder.model.WorkOrderStatusLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Set;

/**
 * Created by qyhw on 01/12/17.
 */
@Repository
public interface WorkOrderDao {

    String insertWorkOrder(WorkOrder workOrder);

    int updateWorkerOrder(WorkOrder workOrder);

    WorkOrder selectByWoNumber(@Param("woNumber") String woNumber);

    int selectWoListCount(WorkOrderRequestBO workOrderRequestBO);

    List<WorkOrder> selectWoList(WorkOrderRequestBO workOrderRequestBO);

    Set<String> selectWorkOrderModule(@Param("loginUser") String loginUser, @Param("ownClass") String ownClass, @Param("ownModule") String ownModule);

    int saveRemark(@Param("operatorName") String operatorName, @Param("remark") String remark, @Param("woNumber") String woNumber);

    List<WorkOrderRemark> selectRemarkList(@Param("woNumber") String woNumber);

    Set<String> selectWoClass(@Param("loginUser")String loginUser);

    Set<String> selectWorkOrderClass(@Param("loginUser")String loginUser);

    int saveWorkOrderLog(WorkOrderStatusLog workOrderStatusLog);

    List<WorkOrderStatusLog> selectWorkOrderLogList(@Param("woNumber") String woNumber);

    WorkOrder selectWorkOrderByWoNumber(@Param("woNumber") String woNumber);

    int selectLastWeekCount(@Param("userClass") Set<String> userClass);

    int selectOrderCountByType(@Param("woType") String woType, @Param("userClass") Set<String> userClass);

    int selectOwnCount(@Param("assignee") String assignee);

    int selectOrderCountByClass(@Param("userClass") Set<String> userClass, @Param("woStatus") int woStatus);

    List<WorkOrder> selectOrderListByClass(@Param("userClass") Set<String> userClass, @Param("woStatus") int woStatus,
                                           @Param("rowNum") int rowNum, @Param("offset") int offset);

}

