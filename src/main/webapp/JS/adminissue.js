$(document).ready(function () {

    $('#task_submit').click(function () {
        var title = $('#title').val();//任务标题
        var teacher = '';//选择的老师
        teacher += $('#select_teacher').val();
        var details = $('#details').val();//任务详情
        var qq = $('#web_qq').val();//发布者qq
        if (title == "") {
            $('#title').focus();
            swal("发布错误", "标题未输入", "error");
        } else if (teacher == "") {
            swal("发布错误", "老师未选择", "error");
        } else if (details == "") {
            $('#details').focus();
            swal("发布错误", "任务详情未输入", "error");
        } else if (qq == "") {
            $('#web_qq').focus();
            swal("发布错误", "qq未输入", "error");
        } else {

            swal({
                title: "确认发布吗？",
                text: "提交运行ajax请求",
                type: "info",
                showCancelButton: true,
                closeOnConfirm: false,
                showLoaderOnConfirm: true,
                confirmButtonText: "确定",
                cancelButtonText: "取消",
            }, function () {
                ajax_task();
            });
        }
    });

    //ajax向后台发送请求
    function ajax_task() {
        var title = $('#title').val();//任务标题
        var teacher = '';//选择的老师
        teacher += $('#select_teacher').val();
        var details = $('#details').val();//任务详情
        var qq = $('#web_qq').val();//发布者qq
        $.post("/insertIssueTasks", {workName: title, teacher: teacher, workText: details, qq: qq}, function (data) {
            if(data != "101"){

                swal("发布成功", "任务已成功发布出去", "success");
                setTimeout(function () {
                    window.location.href = "/taskInfo?id="+data.split(",")[1];
                },2000)

            } else {
                swal("发布失败", "错误代码："+data, "error");
            }
        });
    }
});