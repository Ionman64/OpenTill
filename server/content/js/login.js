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
    $("#forgot-password").on("submit", function() {
    	forgotPassword();
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
function forgotPassword() {
    $.ajax({
        url:"api/kvs.jsp?function=FORGOTPASSWORD",
        data:{"email":$("#inputEmail").val()},
        success:function(data){
        	if (data.success) {
        		$("#serverMessage").html("An email has been sent to you").removeClass("hidden");
                return;
            }
            $("#serverMessage").html(data.reason).removeClass("hidden");
        }
    });
    return false;
}