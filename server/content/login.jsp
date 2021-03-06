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
	<link rel="stylesheet" href="thirdParty/bootstrap4/css/bootstrap.min.css"/>
	<script src="thirdParty/bootstrap4/js/bootstrap.min.js"></script>				
	<title>OpenTill-Login</title>
	<script type="text/javascript" src="js/login.js"></script>
	<link rel="stylesheet" href="css/login.css"/>
</head>
<body class="bg-dark">
<section class="container py-5">
    <section class="row">
        <section class="col-md-12">
            <h2 class="text-center text-white mb-4">OpenTill</h2>
            <h5 class="text-center text-light mb-4">Please enter your details below</h5>
            <section class="row">
                <section class="col-lg-6 col-md-8 col-sm-10 col-xs-12 mx-auto">
                    <!-- form card login -->
                    <section class="card rounded-0">
                        <section class="card-header">
                            <h3 class="mb-0">Login</h3>
                        </section>
                        <section class="card-body">
                            <form class="form" role="form" autocomplete="off" id="signin" novalidate="" method="POST">
                                <section class="form-group">
                                    <label for="inputEmail">Email</label>
                                    <input type="email" id="inputEmail" class="form-control form-control-lg rounded-0" placeholder="Email address" value="peterpickerill2016@gmail.com" required autofocus>
                                    <section class="invalid-feedback">Oops, you missed this one.</section>
                                </section>
                                <section class="form-group">
                                    <label>Password</label>
                                    <input type="password" class="form-control form-control-lg rounded-0" id="inputPassword" placeholder="Password" value="x" required autocomplete="new-password">
                                    <section class="invalid-feedback">Enter your password too!</section>
                                </section>
                                <section>
                                    <label class="custom-control custom-checkbox">
                                      <a href="forgotPassword.jsp"><span class="custom-control-description small text-info pull-right">Forgotten Password</span></a>
                                    </label>
                                </section>
                                <label class="text-info italic hidden" id="serverMessage"></label>
                                <button type="submit" class="btn btn-success btn-lg float-right" id="btnLogin">Login</button>
                            </form>
                        </section>
                    </section>
                </section>
            </section>
        </section>
    </section>
</section>
</body>
</html>
