<?php
	session_start();
	if (isset($_SESSION['user'])) {
		header('Location: dashboard.php');
	}
?>
<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<script src="thirdParty/popper/popper.min.js"></script>
	<link rel="stylesheet" type="text/css" href="thirdParty/simple-sidebar/simple-sidebar.css"/>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<!--bootstrap-->
	<link rel="stylesheet" href="thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="thirdParty/bootstrap/js/bootstrap.min.js"></script>				
	<title>KVS-Login</title>
	<script type="text/javascript" src="js/login.js"></script>
	<style>
		body {
		  padding-top:10px;
		  padding-bottom: 40px;
		  background-color: #eee;
		}

		.form-signin {
		  max-width: 330px;
		  padding: 15px;
		  margin: 0 auto;
		}
		.form-signin .form-signin-heading,
		.form-signin .checkbox {
		  margin-bottom: 10px;
		}
		.form-signin .form-control {
		  position: relative;
		  height: auto;
		  -webkit-box-sizing: border-box;
			 -moz-box-sizing: border-box;
				  box-sizing: border-box;
		  padding: 10px;
		  font-size: 16px;
		}
		.form-signin .form-control:focus {
		  z-index: 2;
		}
		.form-signin input[type="email"] {
		  margin-bottom: -1px;
		  border-bottom-right-radius: 0;
		  border-bottom-left-radius: 0;
		}
		.form-signin input[type="password"] {
		  margin-bottom: 10px;
		  border-top-left-radius: 0;
		  border-top-right-radius: 0;
		}
	</style>
	<script>
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
						window.location = "dashboard.php";
						return;
					}
				}
			});
		});
		function login() {
			$.ajax({
				url:"api/kvs.php?function=LOGIN",
				data:{"email":$("#inputEmail").val(), "password":$("#inputPassword").val()},
				success:function(data){
					if (data.success) {
						window.location = "dashboard.php";
						return;
					}
					$("#serverMessage").html(data.reason).removeClass("hidden");
				}
			});
			return false;
		}
	</script>
</head>
<body>
	<section class="container-fluid">
		<section class="row">
			<section class="col-md-12">
				<label class="forgotten-password pull-right">Forgot Password?: <a href="forgotPassword">Click Here</a></label>
			</section>
			<section class="col-md-4 col-md-offset-4">
			  <form class="form-signin" onsubmit="login();return false;">
				<h2 class="form-signin-heading text-center">OpenTill</h2>
				<h5 class="form-signin-heading text-center">Please enter your details below</h5>
				<label for="inputEmail" class="sr-only">Email address</label>
				<input type="email" id="inputEmail" class="form-control" placeholder="Email address" required autofocus>
				<label for="inputPassword" class="sr-only">Password</label>
				<input type="password" id="inputPassword" class="form-control" placeholder="Password" required>
				<label id="capsLockIndicator" class="text-warning hidden">Warning: Caps Lock On</label>
				<label id="serverMessage" class="text-info hidden"></label>
				<label class="text-info italic hidden" id="serverMessage"></label>
				<button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
			  </form>
			</section>
	  </section>
    </section> <!-- /container -->
</body>
</html>
