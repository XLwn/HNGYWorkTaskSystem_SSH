package com.xl.controller;

import com.xl.dao.MainDao;
import com.xl.dao.MainDaoImpl;
import com.xl.entity.THngyImportInfo;
import com.xl.service.AdminService;
import com.xl.utils.Config;
import com.xl.utils.ExcelUtil;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    /***
     * 显示管理员首页信息
     * @param httpSession
     * @return
     */
    @GetMapping(value = "/admin")
    public String admin(HttpSession httpSession) {
        return "admin";
    }


    /***
     * 管理员发布任务的页面
     * @param httpServletRequest
     * @return
     */
    @GetMapping(value = "/adminIssue")
    public String adminissue(HttpServletRequest httpServletRequest) {
        adminService.getTeacherWrokStatus(httpServletRequest);
        return "adminIssue";
    }

    /***
     * 下载任务详情表
     * @param request
     * @param response
     * @param year 年
     * @param hyear 学期
     * @return
     */
    @RequestMapping(value = "/downloadTask")
    public String downloadTask(HttpServletRequest request, HttpServletResponse response, String year, String hyear)
            throws IOException {
        try {
            String dateStr1 = year + ("上学期".equals(hyear) ? "-02-01" : "-08-01");
            String dateStr2 = ("上学期".equals(hyear) ? year + "-08-01" : String.valueOf(Integer.parseInt(year) + 1) +
                    "-02-01");
            java.sql.Date date1 = java.sql.Date.valueOf(dateStr1);
            java.sql.Date date2 = java.sql.Date.valueOf(dateStr2);
            //输出
            System.out.println("下载" + date1 + "~" + date2 + "任务详情表");
            String fileName = year + hyear + "任务详情表";
            String columnNames[] = {"时间(年/月/日)", "任务名称", "所属教师", "状态"};// 列名
            String keys[] = {"taskDate", "taskName", "teachers", "taskState"};// map中的key
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            MainDao dao = new MainDaoImpl();
            //调用工具类创建excel工作簿
            ExcelUtil.createWorkbook(dao.QueryPersonalAdminWorkHomepageInformation(date1, date2), keys, columnNames)
                    .write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);

            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
            OutputStream out = response.getOutputStream();
            //下载传输
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) > 0) {
                out.write(b, 0, length);
            }

            // 关闭
            os.flush();
            os.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 下载教师详情表
     * @param request
     * @param response
     * @param year 年
     * @param hyear 学期
     * @return
     */
    @RequestMapping(value = "/downloadTeacher")
    public String downloadTeacher(HttpServletRequest request, HttpServletResponse response, String year, String
            hyear) throws IOException {
        try {
            String dateStr1 = year + ("上学期".equals(hyear) ? "-02-01" : "-08-01");
            String dateStr2 = ("上学期".equals(hyear) ? year + "-08-01" : String.valueOf(Integer.parseInt(year) + 1) +
                    "-02-01");
            java.sql.Date date1 = java.sql.Date.valueOf(dateStr1);
            java.sql.Date date2 = java.sql.Date.valueOf(dateStr2);
            //输出
            System.out.println("下载" + date1 + "~" + date2 + "教师详情表");
            String fileName = year + hyear + "教师详情表";
            String columnNames[] = {"ID", "姓名", "已安排任务数", "未完成数"};// 列名
            String keys[] = {"teacherId", "teacherName", "taskCount", "unfinished"};// map中的key
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            MainDao dao = new MainDaoImpl();
            //调用工具类创建excel工作簿
            ExcelUtil.createWorkbook(dao.QueryUserID_Name_WorkCount(date1, date2), keys, columnNames).write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);

            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
            OutputStream out = response.getOutputStream();
            //下载传输
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) > 0) {
                out.write(b, 0, length);
            }

            // 关闭
            os.flush();
            os.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Excel批量注册
     * NOTE:THngyImportInfo 为教师用户信息实体类，追加字符段需要修改此实体类
     * 并需要在 选择插入判断 处对应修改
     *
     * @param request
     * @param response
     * @param loginName
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("fileupload")
    public String fileupload(HttpServletRequest request, HttpServletResponse response, String loginName) throws
            ServletException, IOException {
//        上传文件的全路径
        String loaclPath = "";

        File newFile = null;
        String path = request.getSession().getServletContext().getRealPath("");
        System.out.println(path);
        CommonsMultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (resolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (request);
            Iterator<String> it = multipartHttpServletRequest.getFileNames();
            while (it.hasNext()) {
                MultipartFile file = multipartHttpServletRequest.getFile(it.next());
                String fileName = file.getOriginalFilename();
                loaclPath = path + fileName;
                System.out.println("即将删除" + loaclPath);
                newFile = new File(loaclPath);
                try {
                    file.transferTo(newFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        if (adminService.importExcelInfo(loaclPath) == Config.Code201){
            return null;
        }
        if (newFile.exists()) {
            if (newFile.delete()) {
                System.out.println("Excel表删除成功");
            } else {
                System.out.println("Excel表删除失败");
            }

        } else {
            System.out.println("警告！Excel表不存在！无法删除！");
        }

        return Config.Code200;
    }

    /***
     * Excel批量导入页面
     *
     * @param response
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/importInfo")
    public String importInfo(HttpServletResponse response, HttpServletRequest httpServletRequest) {

        if (httpServletRequest.getParameter("type") != null) {
            String type = httpServletRequest.getParameter("type");
            if (type.equals("upload")) {
//                String workName = httpServletRequest.getParameter("workName");
//                String teacher = httpServletRequest.getParameter("teacher");
//                System.out.println("测试" + workName + " " + teacher);

            }
        }


        httpServletRequest.setAttribute("importInfo", null);

        return "importInfo";
    }

    @GetMapping(value = "adminquery")
    public String adminquery() {
        return "adminquery";
    }
}
