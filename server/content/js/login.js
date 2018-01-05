$(document).ready(function() {
    $.ajaxSetup({
        dataType:"JSON",
        method:"POST"
    });
    $.ajax({
        url:"api/kvs.php?function=ISLOGGEDIN",
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
        url:"api/kvs.php?function=LOGIN",
        data:{"email":$("#inputEmail").val(), "password":$("#inputPassword").val()},
        success:function(data){
            if (data.success) {
                window.location = "dashboard.jsp";
                return;
            }
            $("#serverMessage").html(data.reason).removeClass("hidden");
        }
    });
    return false;
}