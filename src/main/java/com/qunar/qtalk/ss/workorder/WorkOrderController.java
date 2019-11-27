package com.qunar.qtalk.ss.workorder;


import com.alibaba.fastjson.JSONObject;
import com.qunar.qchat.admin.annotation.MustLogin;
import com.qunar.qchat.admin.util.AuthorityUtil;
import com.qunar.qchat.admin.util.HttpClientUtils;
import com.qunar.qtalk.ss.utils.JsonResultUtils;
import com.qunar.qtalk.ss.workorder.bo.WorkOrderRequestBO;
import com.qunar.qtalk.ss.session.model.JsonResult;
import com.qunar.qtalk.ss.utils.JacksonUtils;
import com.qunar.qtalk.ss.workorder.service.BuvUserUtil;
import com.qunar.qtalk.ss.workorder.service.WorkOrderService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class WorkOrderController {

    private static final Logger logger = LoggerFactory.getLogger(WorkOrderController.class);

    @Autowired
    WorkOrderService workOrderService;

    @MustLogin(MustLogin.ViewType.JSON)
    @RequestMapping("saveOrUpdateWorkOrder.qunar")
    @ResponseBody
    public JsonResult modifyWorkOrder(@RequestBody WorkOrderRequestBO workOrder, HttpServletRequest request) {
        logger.info("createWorkOrder begin param:{}", JacksonUtils.obj2String(workOrder));
        String operatorName = AuthorityUtil.getThirdPartyUserName(request);
        return workOrderService.modifyWorkOrder(workOrder, operatorName);
    }

    @MustLogin(MustLogin.ViewType.JSON)
    @RequestMapping("workOrderList.qunar")
    @ResponseBody
    public JsonResult workOrderList(@RequestBody String json , HttpServletRequest request) {
        logger.info("workOrderList begin param:{}", json);
        try {
            String operatorName = AuthorityUtil.getThirdPartyUserName(request);
            WorkOrderRequestBO orderRequestBO = JacksonUtils.string2Obj(json, WorkOrderRequestBO.class);
            if (orderRequestBO == null) {
                orderRequestBO = new WorkOrderRequestBO();
            }
            int page = orderRequestBO.getPage() < 1 ? 1 : orderRequestBO.getPage();
            int rowNum = orderRequestBO.getRowNum() < 5 ? 5 : orderRequestBO.getRowNum();
            orderRequestBO.setRowNum(rowNum);
            orderRequestBO.setOffset((page - 1) * rowNum);
            return workOrderService.selectWorderOrderList(orderRequestBO, operatorName);

        } catch (Exception e) {
            logger.error("workOrderList error", e);
        }

        return JsonResultUtils.fail(500, "server 端错误");
    }

    @MustLogin(MustLogin.ViewType.JSON)
    @RequestMapping("workOrderClass.qunar")
    @ResponseBody
    public JsonResult workOrderClass(@RequestBody String json, HttpServletRequest request) {
        try {
            String operatorName = AuthorityUtil.getThirdPartyUserName(request);
            JSONObject paramObject = JSONObject.parseObject(json);
            String ownClass = paramObject.getString("ownClass");
            String ownModule = paramObject.getString("ownModule");
            ownClass = StringUtils.trimToNull(ownClass);
            ownModule = StringUtils.trimToNull(ownModule);
            return workOrderService.selectWorkOrderClass(operatorName, ownClass, ownModule);
        } catch (Exception e) {
            logger.error("workOrderClass error", e);
        }
        return JsonResultUtils.fail(500, "server 端错误");
    }

    @MustLogin(MustLogin.ViewType.JSON)
    @RequestMapping("saveRemark.qunar")
    @ResponseBody
    public JsonResult saveRemark(@RequestBody String json, HttpServletRequest request) {
        try {
            String operatorName = AuthorityUtil.getThirdPartyUserName(request);
            JSONObject paramObject = JSONObject.parseObject(json);
            String remark = paramObject.getString("remark");
            String woNumber = paramObject.getString("woNumber");
            if (StringUtils.isAnyEmpty(remark, woNumber)) {
                return JsonResultUtils.fail(411, "参数错误");
            }
            if (StringUtils.isAnyEmpty(operatorName)) {
                return JsonResultUtils.fail(420, "未登录");
            }
            return workOrderService.saveRemark(remark, operatorName, woNumber);
        } catch (Exception e) {
            logger.error("saveRemark error", e);
        }
        return JsonResultUtils.fail(500, "server 端错误");
    }

    @RequestMapping("selectRemarkList.qunar")
    @ResponseBody
    public JsonResult selectRemarkList(@RequestBody String json, HttpServletRequest request) {

        JSONObject paramObject = JSONObject.parseObject(json);
        String woNumber = paramObject.getString("woNumber");
        if (StringUtils.isEmpty(woNumber)) {
            return JsonResultUtils.fail(411, "参数错误");
        }

        return workOrderService.selectRemarkList(woNumber);
    }

    @RequestMapping("selectStatusLog.qunar")
    @ResponseBody
    public JsonResult selectStatusLog(@RequestBody String json, HttpServletRequest request) {

        JSONObject paramObject = JSONObject.parseObject(json);
        String woNumber = paramObject.getString("woNumber");
        if (StringUtils.isEmpty(woNumber)) {
            return JsonResultUtils.fail(411, "参数错误");
        }

        return workOrderService.selectStatusLog(woNumber);
    }

    @MustLogin(MustLogin.ViewType.JSON)
    @RequestMapping("workOrderDetail.qunar")
    @ResponseBody
    public JsonResult workOrderDetail(@RequestBody String json, HttpServletRequest request) {
        String operatorName = AuthorityUtil.getThirdPartyUserName(request);
        JSONObject paramObject = JSONObject.parseObject(json);
        String woNumber = paramObject.getString("woNumber");
        if (StringUtils.isAnyEmpty(woNumber, operatorName)) {
            return JsonResultUtils.fail(411, "参数错误");
        }

        return workOrderService.selectWorkOrderByWoNumber(woNumber,operatorName);
    }

    @RequestMapping("searchUser.qunar")
    @ResponseBody
    public JsonResult searchUserByUserId(@RequestBody String json, HttpServletRequest request) {

        JSONObject paramObject = JSONObject.parseObject(json);
        String userId = paramObject.getString("userId");
        if (StringUtils.isEmpty(userId)) {
            return JsonResultUtils.fail(411, "参数错误");
        }

        return workOrderService.searchUserByUserId(userId);
    }

    @RequestMapping("dashboardCount.qunar")
    @ResponseBody
    public JsonResult orderDashboard(HttpServletRequest request) {
        String operatorName = AuthorityUtil.getThirdPartyUserName(request);

        return workOrderService.orderDashboard(operatorName);
    }

    @RequestMapping("toBeProcessedList.qunar")
    @ResponseBody
    public JsonResult toBeProcessedList(@RequestBody String json, HttpServletRequest request) {
        String operatorName = AuthorityUtil.getThirdPartyUserName(request);
        JSONObject paramObject = JSONObject.parseObject(json);
        int rowNum = paramObject.getIntValue("rowNum");
        int page = paramObject.getIntValue("page");
        rowNum = rowNum < 5 ? 5 : rowNum;
        page = page < 1 ? 1 : page;
        return workOrderService.getToBeProcessedList(operatorName, rowNum, page);
    }

    @RequestMapping("processingList.qunar")
    @ResponseBody
    public JsonResult processingList(@RequestBody String json, HttpServletRequest request) {
        String operatorName = AuthorityUtil.getThirdPartyUserName(request);
        JSONObject paramObject = JSONObject.parseObject(json);
        int rowNum = paramObject.getIntValue("rowNum");
        int page = paramObject.getIntValue("page");
        rowNum = rowNum < 5 ? 5 : rowNum;
        page = page < 1 ? 1 : page;
        return workOrderService.getProcessingList(operatorName, rowNum, page);
    }


    /**
     * 图片上传
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadImg.qunar", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult uploadImg(@RequestParam(name = "img") MultipartFile file, HttpServletRequest request) {

        try {
            if (request.getContentLength() > 0) {
                Map<String, Object> resultMap = new HashMap<>(3);
                byte[] bytes = file.getBytes();
                String fileKey = DigestUtils.md5Hex(bytes);

                String result = HttpClientUtils.postFile("http://127.0.0.1:9090/file/v2/upload/img?key=" + fileKey, file.getInputStream(), file.getOriginalFilename());
                if (StringUtils.isNotEmpty(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String uploadUrl = jsonObject.getString("data");
                    resultMap.put("uploadUrl", uploadUrl);
                    resultMap.put("md5Key", fileKey);
                    resultMap.put("size", file.getSize());
                    return JsonResultUtils.success(resultMap);
                }

            }
        } catch (IOException e) {
            logger.error("uploadFile error fileName", file.getOriginalFilename(), e);
            return JsonResultUtils.fail(500, "server端错误");
        }

        logger.warn("uploadFile warn fail name:{}", file.getOriginalFilename());
        return JsonResultUtils.fail(433, "上传失败");
    }

    @RequestMapping("cache.qunar")
    @ResponseBody
    public JsonResult searchUserByUserId(HttpServletRequest request) {
        String accessToken = BuvUserUtil.getAccessToken();
        return JsonResultUtils.success(accessToken);
    }

}
