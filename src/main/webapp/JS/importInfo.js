$(document).ready(function () {


    $('#bt_import').click(function () {
        var fileName = $('#file_input').val();
        if (fileName === '') {
            swal("请选择文件", "", "error")
            return false;
        }
        var fileType = (fileName.substring(fileName
            .lastIndexOf(".") + 1, fileName.length))
            .toLowerCase();
        if (fileType !== 'xls') {
            swal("格式不支持", "仅支持.xls", "error")
            return false;
        }
        swal({
            title: "确认提交吗？",
            text: "提交Excel数据到数据库",
            type: "info",
            showCancelButton: true,
            closeOnConfirm: false,
            showLoaderOnConfirm: true,
            confirmButtonText: "确定",
            cancelButtonText: "取消",
        }, function () {
            setTimeout(function () {
                // doUpload();
                ajax_task();
            }, 2000);
        });
    });

    //ajax向后台发送请求
    function ajax_task() {
        // var title = $('#title').val();//任务标题
        var formData = new FormData($("#file_form")[0]);
        $.ajax({
            url: '/fileupload',
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (returndata) {
                swal("提交成功", "注册数据已成功录入", "success");
            },
            error: function (returndata) {
                swal("提交失败", "错误代码：" + returndata, "error");
            }
        });

        // $.post("/importInfo?type=upload", {workName: "123", teacher: "321"}, function (data) {
        //     if (data != "101") {
        //
        //         setTimeout(function () {
        //             window.location.href = "/importInfo";
        //         }, 2000)
        //
        //     } else {
        //     }
        // });
    }
});

function doUpload() {
    var formData = new FormData($("#file_form")[0]);
    $.ajax({
        url: '/fileupload',
        type: 'POST',
        data: formData,
        async: false,
        cache: false,
        contentType: false,
        processData: false,
        success: function (returndata) {
            alert(returndata);
        },
        error: function (returndata) {
            alert(returndata);
        }
    });
}