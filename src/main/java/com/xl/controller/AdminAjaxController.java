package com.xl.controller;

import com.alibaba.fastjson.JSONArray;
import com.xl.dao.MainDao;
import com.xl.dao.MainDaoImpl;
import com.xl.entity.THngyWorkTask;
import com.xl.service.AdminService;
import com.xl.utils.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AdminAjaxController {
    @Autowired
    private AdminService adminService;

    /***
     * 修改公告
     * @param notice 公告内容
     * @return 返回状态码200成功，201失败
     */
    @RequestMapping(value = "/modifyNotice")
    @ResponseBody
    public String modifyNotice(String notice) {
        return adminService.updateNotice(notice);
    }

    /***
     * 修改管理员信息，管理员密码可以为空，其他信息不能为空，其他信息一开始就传过去了的
     * @param email 管理员邮箱
     * @param qq 管理员qq
     * @param pwd 管理员密码
     * @return 状态码，200成功，201失败
     */
    @PostMapping(value = "/modifyAdminInfo")
    @ResponseBody
    public String modifyAdminInfo(HttpSession session, String email, String qq, String pwd) {
        String code = Config.Code201;
        if(email.length()>0&&qq.length()>0){
            String id = String.valueOf(session.getAttribute("id"));
            code = adminService.updateAdminInfo(id,email,qq,pwd);
        }
        return  code;
    }

    /**
     * 发布任务，插入任务的数据
     *
     * @param workName 工作名称
     * @param teacher  教师名字
     * @param workText 工作详情
     * @param qq       发布者qq号
     * @return
     */
    @RequestMapping(value = "/insertIssueTasks")
    @ResponseBody
    public String insertIssueTasks(String workName, String teacher, String workText, String qq) {
        String statusCode = Config.Code201;
        String taskId = "";
        if(workName.length()>0 && teacher.length() > 0 && workText.length()>0 && qq.length()>0){
            taskId = adminService.saveTaskTeacherLinkInfo(workName,teacher,workText,qq);
            statusCode = Config.Code200;
        }
        String json = "{\"sCode\":\""+statusCode+"\",\"taskId\":\""+taskId+"\"}";
        System.out.println(json);
        return json;
    }

    /**
     * 编辑保存任务，更新任务的数据
     *
     * @param workId    工作id
     * @param workName  工作名称
     * @param workText  工作详情
     * @param qq        发布者qq号
     * @param workState 工作状态
     * @return
     */
    @RequestMapping(value = "/editSaveTask")
    @ResponseBody
    public String editSaveTask(String workId, String workName, String workText, String qq, String workState, String workTime) {
        MainDao dao = new MainDaoImpl();

        System.out.println(workName + "\n" + workText + "\n" + qq + "\n" + workState + "\n");
        THngyWorkTask tHngyWorkTask = new THngyWorkTask();
        tHngyWorkTask.setWorkTaskId(Long.parseLong(workId));
        tHngyWorkTask.setWorkTaskName(workName);
        tHngyWorkTask.setWorkTaskText(workText);
        tHngyWorkTask.setWorkTaskSchedule(workState);
        tHngyWorkTask.setQq(qq);
        tHngyWorkTask.setWorkTaskTime(java.sql.Date.valueOf(workTime));
        int M = Integer.parseInt(workTime.substring(5, 7));
        if (M < 2 && M > 8)//上学期
        {
            tHngyWorkTask.setWorkTaskTerm("上学期");
        } else//下学期
        {
            tHngyWorkTask.setWorkTaskTerm("下学期");
        }

        return dao.updataTask(tHngyWorkTask);
    }

    /***
     * 根据时间查询任务报表a
     * @return
     */
    @RequestMapping(value = "getInfo/recentTaskInfo", produces = "text/html;charset=UTF-8;")
    @ResponseBody//表示直接输出返回内容，不进行jsp或html跳转，本例是为了写接口，这里直接返回json
    public String recentTaskInfo() {
        return adminService.getAdminHomePageInfo();
    }

    /***
     * 根据时间查询用户工作报表
     * @param year 年
     * @param hyear 学期
     * @return
     */
    @RequestMapping(value = "teacherQuery", produces = "text/html;charset=UTF-8;")
    @ResponseBody//表示直接输出返回内容，不进行jsp或html跳转，本例是为了写接口，这里直接返回json
    public String teacherQuery(String year, String hyear) {
        MainDao dao = new MainDaoImpl();
        String dateStr1 = year + ("上学期".equals(hyear) ? "-02-01" : "-08-01");
        String dateStr2 = ("上学期".equals(hyear) ? year + "-08-01" : String.valueOf(Integer.parseInt(year) + 1) + "-02-01");
        java.sql.Date date1 = java.sql.Date.valueOf(dateStr1);
        java.sql.Date date2 = java.sql.Date.valueOf(dateStr2);
        System.out.println(date1 + "\n" + date2);
        List<Map<String, Object>> list = dao.QueryUserID_Name_WorkCount(date1, date2);
        if (list == null) {
            return "101";
        }
        String json = JSONArray.toJSONString(list);
        System.out.println(json);
        return json;
    }

    /***
     * 从session中获取管理员id查询管理员所有信息并以json形式返回
     * @return
     */
    @RequestMapping(value = "/getInfo/adminInfo", produces = "text/html;charset=UTF-8;")
    @ResponseBody//表示直接输出返回内容，不进行jsp或html跳转，本例是为了写接口，这里直接返回json
    public String getAdminInfo(HttpSession httpSession) {
        String id = String.valueOf(httpSession.getAttribute("id"));
        return adminService.getAdminHomePageInfo(id);
    }

    /**
     * 根据id获取任务数据
     *
     * @return
     */
    @RequestMapping(value = "getTaskInfo", produces = "text/html;charset=UTF-8;")
    @ResponseBody
    public String getTaskInfo(String id) {
        String json = adminService.getTaskInfoForAdmin(Long.valueOf(id));
        return json;
    }
}
