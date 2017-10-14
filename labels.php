<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="css/paper.css"/>
	<link rel="stylesheet" href="css/labels.css"/>
	<title>KVS-Labels</title>
	<link REL="SHORTCUT ICON" href="img/site_icon.png"/>
	<!--jQuery-->
	<script type="text/javascript" src="thirdParty/jQuery/js/jquery.min.js"></script>
	<!--bootstrap-->
	<link rel="stylesheet" href="thirdParty/bootstrap/css/bootstrap.min.css"/>
	<script src="thirdParty/bootstrap/js/bootstrap.min.js"></script>
	<!--accountingJS-->
	<script type="text/javascript" src="thirdParty/accounting/js/accounting.min.js"></script>
	<script type="text/javascript" src="thirdParty/jsbarcode/js/jsbarcode.min.js"></script>
	<!--font awesome-->
	<link rel="stylesheet" href="thirdParty/font-awesome/css/font-awesome.min.css" />
	<script type="text/javascript" src="js/label.js"></script>
	<style>
		.tr-bold {
			font-weight:bold;
		}
	</style>
	<style>@page {size: A4}</style>
</head>
<body class="A4">
  <!-- Each sheet element should have the class "sheet" -->
  <!-- "padding-**mm" is optional: you can set 10, 15, 20 or 25 -->
	<section id="navigation" class="navbar navbar-inverse navbar-fixed-top navigation no-print">
		<ul>
			<li onclick="print_document()"><button class="btn btn-default btn-lg">Print</button></li>
			<li style="text-align:right" onclick="clear_labels()"><button class="btn btn-default btn-lg">Clear Labels</button></li>
			<li style="text-align:right" onclick="exportAsPdf()"><button class="btn btn-default btn-lg">PDF</button></li>
			<li style="text-align:right" onclick="back_to_till()"><button class="btn btn-default btn-lg">Back to till</button></li>
			<li style="text-align:right;float:right">
				<select id="label-style-select" class="form-control input-lg">
					<option></option>
				</select>
			</li>
		</ul>
	</section>
	<section class="container-fluid" style="padding-top:70px;">
		<section class="row">
			<section class="col-md-9" >
				<section id="viewport" class="viewport">
				
				</section>
			</section>
			<section class="col-md-3">
				<section class="navbar">
					<table class="table" id="properties">
						<tbody>
							<tr>
								<td><label>Example</label></td>
								<td><input type="text"></input></td>
							</tr>
						</tbody>
					</table>
				</section>
			</section>
		</section>
	</section>
</body>
</html>
