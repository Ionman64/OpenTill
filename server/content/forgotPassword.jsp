<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<link rel="shortcut icon" href="img/site/icon.ico">
	<meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="robots" content="noindex, nofollow"/>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<!--bootstrap-->
	<link rel="stylesheet" href="thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="thirdParty/bootstrap/js/bootstrap.min.js"></script>				
	<title>OpenTill-Forgotten Password</title>
	<script type="text/javascript" src="js/login.js"></script>
	<link rel="stylesheet" href="css/login.css"/>
</head>
<body>
	<section class="container-fluid">
		<section class="row">
			<section class="col-md-4 col-md-offset-4">
			  <form class="signin" id="forgot-password">
				<h2 class="form-signin-heading text-center">OpenTill</h2>
				<h5 class="form-signin-heading text-center">Please enter your details below</h5>
				<section class="form-group">
					<label for="inputEmail" class="sr-only">Email address</label>
					<input type="email" id="inputEmail" class="form-control input-lg" placeholder="Email address" required autofocus>
				</section>
				<h1 id="serverMessage" class="text-info hidden"></h1>
				<label class="text-info italic hidden" id="serverMessage"></label>
				<button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
			  </form>
			</section>
	  </section>
    </section> <!-- /container -->
</body>
</html>
