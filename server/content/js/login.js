$(document).ready(function() {
    $.ajaxSetup({
        dataType:"JSON",
        method:"POST"
    });
    $.ajax({
        url:"api/kvs.jsp?function=ISLOGGEDIN",
        data:{},
        success:function(data){
            if (data.success) {
                window.location = "dashboard.jsp";
                return;
            }
        }
    });
    $("#signin").on("submit", function() {
        login();
        return false;
    });
});
function login() {
    $.ajax({
        url:"api/kvs.jsp?function=LOGIN",
        data:{"email":$("#inputEmail").val(), "password":$("#inputPassword").val()},
        success:function(data){
            if (data.success) {
                window.location = "dashboard2.jsp";
                return;
            }
            $("#serverMessage").html(data.reason).removeClass("hidden");
        }
    });
    return false;
}