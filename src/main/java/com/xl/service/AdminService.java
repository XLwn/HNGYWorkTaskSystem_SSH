package com.xl.service;

public interface AdminService {
    /**
     * 以Json形式返回管理员个人信息以及公告
     *
     * @param id HttpSession中的id
     * @return 返回json格式的数据
     */
    String getAdminHomePageInfo(String id);

    /**
     * 以Json形式返回管理员主页最近任务信息
     * @return 返回json格式的数据
     */
    String getAdminHomePageInfo();

    /**
     * 修改管理员信息
     * @param id 管理员基本信息
     * @return 返回CONFIG类中的状态码
     */
    String updateAdminInfo(String id,String email, String qq, String pwd);
}
