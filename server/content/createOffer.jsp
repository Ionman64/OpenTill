<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8"/>
	<link rel="shortcut icon" href="img/site/icon.ico">
	<!-- <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'"/> -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta name="viewport" content="width=device-width, initial-scale=1"/>
	<meta name="robots" content="noindex, nofollow"/>
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<script type="text/javascript" src="thirdParty/moment/js/moment.min.js"></script>
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<link rel="stylesheet" href="thirdParty/bootstrap/css/bootstrap.min.css"/>
	<link rel="stylesheet" href="css/index.css"/>
	<!--jscrollpane-->
	<script type="text/javascript" src="thirdParty/jscrollpane/js/jquery.jscrollpane.min.js"></script>
	<link rel="stylesheet" href="thirdParty/jscrollpane/css/jquery.jscrollpane.css"/>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<!--accountingJS-->
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<!--ChartJS-->
	<script type="text/javascript" src="thirdParty/chartJS/js/chart.bundle.js"></script>
	<!--bootstrap-->
	<link rel="stylesheet" href="thirdParty/animate/css/animate.css"/>
	<script src="thirdParty/bootstrap/js/bootstrap.min.js"></script>
	<!--Bootbox-->
	<script src="thirdParty/bootbox/js/bootbox.min.js"></script>
	<!--Mithril-->
	<script src="thirdParty/mithril/js/mithril.min.js"></script>
	<script src="thirdParty/requirejs/require.js"></script>
	<script src="js/labels.js"></script>
	
	<script type="application/javascript" src="js/product_class.js"></script>
	
	<link href="css/offers.css" rel="stylesheet" type="text/css"/>
	<style>
	.col-md-* {
		margin:0;
		padding:0;
	}
	</style>
	<title>KVS Offers</title>
</head>
<body class="no-select">
	<section class="container-fluid no-padding">
		<section class="row">
		<article class="col-lg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 col-sm-12 col-xs-12 no-padding">
			<div class="container">
				<div class="row form-group">
			        <div class="col-xs-12">
			            <ul class="nav nav-pills nav-justified thumbnail setup-panel">
			                <li class="active"><a data-id="step-1">
			                    <h4 class="list-group-item-heading">Step 1</h4>
			                    <p class="list-group-item-text">Select Offers</p>
			                </a></li>
			                <!--  <li class="disabled"><a href="#step-2">
			                    <h4 class="list-group-item-heading">Step 2</h4>
			                    <p class="list-group-item-text">Select Style</p>
			                </a></li>-->
			                <li><a data-id="step-3">
			                    <h4 class="list-group-item-heading">Step 3</h4>
			                    <p class="list-group-item-text">Download and Print</p>
			                </a></li>
			            </ul>
			        </div>
				</div>
			    <div class="row setup-content well" id="step-1">
			   		<h1>Create Offers</h1>
			    	<section class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
		               <section class="offers-drag-and-drop-container">
		               		<section class="block condition">
		               			IF
		               		</section>
	               			<section class="block parenthesis">
		               			(
		               		</section>
		               		<section class="block target" data-id="d071b11a-6bd2-11e7-b34e-426562cc935f">
		               			Coca Cola (1.75ltr)
		               		</section>
		               		<section class="block bitwise-op">
		               			AND
		               		</section>
		               		<section class="block target" data-id="d071b11a-6bd2-11e7-b34e-426562cc935f">
		               			Coca Cola (1.75ltr)
		               		</section>
		               		<section class="block parenthesis">
		               			)
		               		</section>
		               		<section class="block condition">
		               			THEN
		               		</section>
		               		<section class="block transaction">
		               			Cost
		               		</section>
		               		<section class="block assignment">
		               			=
		               		</section>
		               		<section class="block value">
		               			3.30
		               		</section>
		               </section>
					</section>
		            <section class="col-lg-12 col-md-12 col-sm-12 col-xs-12 text-center">
		               <textarea id="offers" class="form-control">
		               		
		               </textarea>
					</section>
	            </div>
			    <div class="row setup-content hidden" id="step-2">
			        <div class="col-xs-12">
			            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 well">
			                <h1 class="text-center">Select a Style</h1>
			            </div>
			        </div>
			    </div>
			    <div class="row setup-content hidden" id="step-3">
			        <div class="col-xs-12">
			            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 well">
			           		<section class="panel panel-info" id="downloading-pdf-panel">
			                	<section class="panel-body">
			                		<i class="fa fa-spinner fa-2x fa-spin pull-right"></i><h5>Generating PDF! Please wait.</h5>
			                	</section>
			                </section>
			                <section class="panel panel-info hidden" id="downloading-pdf-success-panel">
			                	<section class="panel-body">
			                		<i class="fa fa-spinner fa-2x fa-spin pull-right"></i><h5>PDF Generated! If it does not download automatically <a href="" id="labels-alt-download">Click Here</a></h5>
			                	</section>
			                </section>
			                <section class="panel panel-danger hidden" id="downloading-pdf-error-panel">
			                	<section class="panel-body">
			                		<h5>There has been an error!</h5>
			                	</section>
			                </section>
			                <button class="btn btn-success btn-lg hidden" id="download-pdf">Generate PDF</button>
			                
			            </div>
			        </div>
			    </div>
			</div>
		</article>
    </section>
    </section>
</body>
</html>
