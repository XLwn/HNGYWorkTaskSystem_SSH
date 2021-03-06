package com.xl.controller;

import com.alibaba.fastjson.JSONArray;
import com.xl.dao.MainDao;
import com.xl.dao.MainDaoImpl;
import com.xl.service.MainService;
import com.xl.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 本类主要是用来查询数据再显示视图的，任何需要显示视图的页面都需要通过本类显示，不可以直接跳转过去
 * author:臧胜
 */
@Controller
public class MainController {

    @Autowired
    private MainService mainService;

    /***
     * 显示首页
     * @return
     */
    @GetMapping(value = "")
    public String index() {
        return "index";
    }



    /***
     * 显示登录页面
     * @return 登录页面的路径
     */
    @GetMapping(value = "/login")
    public String login(HttpServletRequest request, HttpSession session) {
        String userType = "1";
        boolean flag = false;
        // 获得当前路径以及"直接父路径"的所有Cookie对象,如果没有任何Cookie的话,则返回null
        Cookie[] cookies = request.getCookies();

        // 遍历数组,获得具体的Cookie
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                // 获得具体的Cookie
                Cookie cookie = cookies[i];
                // 获得Cookie的名称
                String name = cookie.getName();
                String value = cookie.getValue();
                if(name.equals("userType")){
                    userType = value;
                    flag = true;
                }
                session.setAttribute(name,value);
            }
            if(flag){
                if (userType.equals("0")) {
                    return "redirect:admin";
                } else if (userType.equals("1")) {
                    return "redirect:user";
                }
            }

        }
        return "login";
    }

    /***
     * 退出登录
     * @return 主页的路径
     */
    @RequestMapping("loginOut")
    public String loginOut(HttpSession httpSession,HttpServletRequest request,HttpServletResponse response) {
        // 获得当前路径以及"直接父路径"的所有Cookie对象,如果没有任何Cookie的话,则返回null
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            // 获得具体的Cookie
            Cookie cookie = cookies[i];
            // 获得Cookie的名称
//            String name = cookie.getName();
//            String value = cookie.getValue();
//            System.out.println(name+"_____"+value);
            // 清除
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        httpSession.invalidate();
        return "redirect:/";
    }
    /**
     * 根据用户类型返回相应的界面
     *
     * @return
     */
    @GetMapping(value = "/taskInfo")
    public String taskInfo(HttpSession session) {
        String userType = (String) session.getAttribute("userType");
        System.out.println(userType);

        if (userType.equals("0")) {
            return "taskInfo_admin";
        } else {
            return "taskInfo_user";
        }
    }

    /**
     * 404页面
     *
     * @return
     */
    @GetMapping(value = "/404")
    public String Error404(HttpSession session) {

        return "404";

    }

    /**
     * TEST测试
     *
     * @return
     */
    @GetMapping(value = "/test")
    public String testNew(HttpSession session) {

            return "test";

    }

    @GetMapping(value = "notLogin")
    public String notLogin(HttpServletRequest request) {
        return "notLogin";
    }





}