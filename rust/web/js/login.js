function getParam(name) {
  	var params = {};
	if (location.search) {
		var parts = location.search.substring(1).split('&');
		for (var i = 0; i < parts.length; i++) {
			var nv = parts[i].split('=');
			if (!nv[0]) continue;
			params[nv[0]] = nv[1] || true;
		}
	}
	return (params[name] ? params[name] : null);	
}

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
    $("#reset-password").on("submit", function() {
    	resetPassword();
    	return false;
    });
});
function login() {
    $.ajax({
        url:"api/auth/login",
        data:{"email":$("#inputEmail").val(), "password":$("#inputPassword").val()},
        success:function(data){
            window.location = "dashboard";
        },
        error: function() {
            $("#serverMessage").html("You could not be logged in").removeClass("hidden");
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
function resetPassword() {
	var newPassword = $("#newPassword").val();
	var confirmPassword = $("#confirmNewPassword").val();
	if (newPassword != confirmPassword) {
		$("#serverMessage").html("Passwords do not match").removeClass("hidden");
		return false;
	}
    $.ajax({
        url:"api/kvs.jsp?function=RESETPASSWORD",
        data:{"newPassword":newPassword, "id":getParam("id"), "token":getParam("token")},
        success:function(data){
        	if (data.success) {
        		$("#serverMessage").html("Password Reset").removeClass("hidden");
        		$("#goto-login-btn").removeClass("hidden");
        		$("#submit-btn").addClass("hidden");
                return;
            }
            $("#serverMessage").html(data.reason).removeClass("hidden");
        }
    });
    return false;
}