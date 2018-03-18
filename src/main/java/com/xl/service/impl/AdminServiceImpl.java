package com.xl.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.xl.entity.THngyAdminInfo;
import com.xl.entity.THngyNotice;
import com.xl.repository.AdminRepository;
import com.xl.repository.MainRepository;
import com.xl.service.AdminService;
import com.xl.utils.Config;
import com.xl.utils.MainUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<THngyNotice> list = mainRepository.getSession().createQuery(hql).list();
        THngyNotice notice = list.get(list.size() - 1);
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
        System.out.println(date1.toString() + "\n" + date2.toString());

        String hql = "select work.workTaskId,work.workTaskTime,work.workTaskName,teacher.teacherName,work.workTaskSchedule,teacher.teacherId,work.qq,work.workTaskText from THngyWorkTask as work ,THngyLink as link,THngyTeacherInfo as teacher where link.workTaskId = work.workTaskId and link.teacherId = teacher.teacherId and work.workTaskTime>=? and work.workTaskTime<=? order by work.workTaskTime desc";
        List<Map<String, Object>> list = MainUtil.getWorkInfoUtil(mainRepository.getSession().createQuery(hql).setParameter(0, date1).setParameter(1, date2).list());
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
        adminRepository.delete((long)1000);
        return Config.Code200;
    }
}
