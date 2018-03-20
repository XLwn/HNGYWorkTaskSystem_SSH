package com.xl.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.xl.entity.THngyAdminInfo;
import com.xl.entity.THngyImportInfo;
import com.xl.entity.THngyNotice;
import com.xl.entity.THngyTeacherInfo;
import com.xl.repository.AdminRepository;
import com.xl.repository.MainRepository;
import com.xl.service.AdminService;
import com.xl.utils.Config;
import com.xl.utils.ExcelUtil;
import com.xl.utils.MainUtil;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private MainRepository mainRepository;

    /**
     * 以Json形式返回管理员个人信息以及公告
     *
     * @param id HttpSession中的id
     * @return 返回json格式的数据
     */
    @Override
    public String getAdminHomePageInfo(String id) {
        String json = Config.Code101;
        THngyAdminInfo admin = adminRepository.get(Long.parseLong(id));
        String hql = "from THngyNotice";
        List<Object> list = mainRepository.simpleQuery(null,hql);
        THngyNotice notice = (THngyNotice) list.get(list.size() - 1);
        if (admin != null && notice != null) {
            Map<String,Object> map = new HashMap();
            map.put("adminName", admin.getAdminInfoName());
            map.put("adminId", admin.getAdminInfoId());
            map.put("adminEmail", admin.getAdminInfoEmail());
            map.put("adminQQ", admin.getAdminInfoQq());
            map.put("notice_text", notice.getNoticeText());
            json = JSONArray.toJSONString(map);
        }
        return json;
    }

    /**
     * 以Json形式返回管理员主页最近任务信息
     *
     * @return 返回json格式的数据
     */
    @Override
    public String getAdminHomePageInfo() {
        String json = Config.Code101;
        //获取当前时间和前一个月的时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        java.sql.Date date2 = java.sql.Date.valueOf(format.format(c.getTime()));
        c.add(Calendar.MONTH, -1);
        java.sql.Date date1 = java.sql.Date.valueOf(format.format(c.getTime()));

        String hql = "select work.workTaskId,work.workTaskTime,work.workTaskName,teacher.teacherName,work.workTaskSchedule,teacher.teacherId,work.qq,work.workTaskText from THngyWorkTask as work ,THngyLink as link,THngyTeacherInfo as teacher where link.workTaskId = work.workTaskId and link.teacherId = teacher.teacherId and work.workTaskTime>=? and work.workTaskTime<=? order by work.workTaskTime desc";
        List<Map<String, Object>> list = MainUtil.getWorkInfoUtil(mainRepository.dateQuery(date1,date2,hql));
        if (list.size() > 0) {
            json = JSONArray.toJSONString(list);
        }
        return json;
    }

    /**
     * 修改管理员信息
     *
     * @param id    管理员基本信息
     * @param email
     * @param qq
     * @param pwd
     * @return 返回CONFIG类中的状态码
     */
    @Override
    public String updateAdminInfo(String id, String email, String qq, String pwd) {
        THngyAdminInfo admin = adminRepository.get(Long.parseLong(id));
        admin.setAdminInfoEmail(email);
        admin.setAdminInfoQq(qq);
        if (pwd.length() > 6)
            admin.setAdminInfoPassWord(pwd);
        adminRepository.saveOrUpdate(admin);
        return Config.Code200;
    }

    /***
     * 修改公告
     * @param notice 公告内容
     * @return 返回状态码200成功，201失败
     */
    @Override
    public String updateNotice(String notice) {
        String code = Config.Code201;
        THngyNotice notice1 = new THngyNotice();
        notice1.setNoticeText(notice);
        if(mainRepository.save(notice1)!=-1){
            code = Config.Code200;
        }
        return  code;
    }

    /***
     * 获取全部教师当前学期工作状态
     * 将List放入request中,前台调用
     * @param req
     */
    @Override
    public void getTeacherWrokStatus(HttpServletRequest req) {
        //获取当前时间
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int M = Integer.parseInt((String) time.subSequence(5, 7));
        int Y = Integer.parseInt((String) time.subSequence(0, 4));
        String dateStr1 = null;
        String dateStr2 = null;
        if (M < 2 && M > 8)//下学期
        {
            dateStr1 = String.valueOf(Y) + "-08-01";
            dateStr2 = String.valueOf(Y + 1) + "-02-01";
        } else//上学期
        {
            dateStr1 = String.valueOf(Y) + "-02-01";
            dateStr2 = String.valueOf(Y) + "-08-01";

        }
        java.sql.Date date1 = java.sql.Date.valueOf(dateStr1);
        java.sql.Date date2 = java.sql.Date.valueOf(dateStr2);
        System.out.println(date1 + "\n" + date2);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        //查没有任务的
        String hql = "select teacher.teacherId,teacher.teacherName from THngyTeacherInfo as teacher where teacher.teacherId not in (select link.teacherId from THngyLink as link,THngyWorkTask as work where link.workTaskId=work.workTaskId and work.workTaskTime>=? and work.workTaskTime<=?)";
        List<Object[]> listTeacher = mainRepository.dateQuery(date1,date2,hql);
        for (int i = 0; i < listTeacher.size(); ++i) {
            Object[] object1 = listTeacher.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("teacherId", String.valueOf(object1[0]));
            map.put("teacherName", String.valueOf(object1[1]));
            map.put("taskCount", 0);
            map.put("unfinished", 0);
            list.add(map);
        }
        //查有任务的
        hql = "select teacher.teacherId,teacher.teacherName,count (*) ,( select count(*) from THngyWorkTask as w,THngyLink as l where l.teacherId = teacher.teacherId and l.workTaskId = w.workTaskId and w.workTaskSchedule = '未完成') from THngyTeacherInfo as teacher,THngyLink as link,THngyWorkTask as work where teacher.teacherId = link.teacherId and  link.workTaskId = work.workTaskId and work.workTaskTime>=? and work.workTaskTime<=? group by teacher.teacherId,teacher.teacherName order by count (work.workTaskId) asc ";
        List<Object[]> listWork = mainRepository.dateQuery(date1,date2,hql);
        for (int i = 0; i < listWork.size(); ++i) {
            Object[] object1 = listWork.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("teacherId", String.valueOf(object1[0]));
            map.put("teacherName", String.valueOf(object1[1]));
            map.put("taskCount", object1[2]);
            map.put("unfinished", object1[3]);
            list.add(map);
        }
        req.setAttribute("allTeacherInfo", list);
    }

    /***
     * Excel批量注册 教师信息
     *
     * @param filePath
     * @return
     */
    @Override
    public String importExcelInfo(String filePath) {
        String statusCode = Config.Code201;
        List<THngyTeacherInfo> list = ExcelUtil.getTeacherInfo(filePath);
        for (int i = 0; i < list.size(); i++) {
            if(mainRepository.save(list.get(i)) == -1){
                statusCode = Config.Code201;
            }
        }
        return statusCode;
    }
}

