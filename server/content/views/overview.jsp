<section class="container-fluid padding-right" style="overflow-y:auto;">
<section class="row" >
	<section class="col-md-6">
		<h1>Overview for <span id="date-of-overview"></span></h1>
	</section>
	<section class="col-md-6">
		<section class="auto-update-container pull-right hidden">
			<input type="checkbox" id="autoupdateoverview"/>
		</section>
		
	</section>
</section>
<section class="row">
	<section class="col-md-3">
		<section class="panel panel-success">
			<section class="panel-heading">
				<section class="container-fluid">
					<section class="row">
						<section class="col-md-6">
							<h4>Revenue</h4>
							<h1 id="revenue-total">0</h1>
						</section>
						<section class="col-md-6 border-left">
							<h4>Change (Day)</h4>
							<h1>+34%</h1>
						</section>
					</section>
				</section>
			</section>
		</section>
	</section>
	<section class="col-md-3">
		<section class="panel panel-info">
			<section class="panel-heading">
				<section class="container-fluid">
					<section class="row">
						<section class="col-md-6">
							<h4>Transactions</h4>
							<h1 id="transactions-count">0</h1>
						</section>
						<section class="col-md-6 border-left">
							<h4>Change (Day)</h4>
							<h1>+34%</h1>
						</section>
					</section>
				</section>
			</section>
		</section>
	</section>
	<section class="col-md-3">
		<section class="panel panel-danger">
			<section class="panel-heading">
				<section class="container-fluid">
					<section class="row">
						<section class="col-md-6">
							<h4>Payouts</h4>
							<h1 id="payouts-total">0</h1>
						</section>
						<section class="col-md-6 border-left">
							<h4>Change (Day)</h4>
							<h1>+34%</h1>
						</section>
					</section>
				</section>
			</section>
		</section>
	</section>
	<section class="col-md-3">
		<section class="panel panel-warning">
			<section class="panel-heading">
				<section class="container-fluid">
					<section class="row">
						<section class="col-md-12">
							<h4>Cash In Drawer</h4>
							<h1 id="cash-in-drawer">0</h1>
						</section>
					</section>
				</section>
			</section>
		</section>
	</section>
	<section class="col-md-12">
		<section class="panel panel-primary">
			<section class="panel-heading clearfix">
				Takings
				 <div class="btn-group pull-right hidden" id="takings-interval">
			 		<button type="button" class="btn btn-default active" data-id="day">Day</button>
				  	<button type="button" class="btn btn-default" data-id="week">Week</button>
				  	<button type="button" class="btn btn-default" data-id="month">Month</button>
				  	<button type="button" class="btn btn-default"data-id="year">Year</button>
				</div> 
			</section>
			<section class="panel-body">
				<section class="graph-container-wide">
					<canvas id="takings-graph"></canvas>
				</section>
			</section>
		</section>
	</section>
	<section class="col-md-6 col-sm-8">
		<section class="panel panel-primary">
			<section class="panel-heading">
				Department Takings
			</section>
			<section class="panel-body">
				<section class="container-fluid">
					<section class="row">
						<section class="col-md-12">
							<section class="graph-container-wide">
								<canvas id="departments-takings-graph"></canvas>
							</section>
						</section>
						<section class="col-md-12" style="padding-top:1em;">
							<table class="table">
								<tbody id="department-totals">
									<tr>
										<td>Hello WOrld</td>
									</tr>
								</tbody>
							</table>
						</section>
					</section>
				</section>
				
			</section>
		</section>
	</section>
	<section class="col-md-3 col-sm-4">
		<section class="panel panel-primary">
			<section class="panel-heading">
				Operator Totals
			</section>
			<section class="panel-body">
				<table class="table">
					<tbody id="operators-totals">
		
					</tbody>
				</table>
			</section>
		</section>
	</section>
	<section class="col-md-3 col-sm-4">
		<section class="panel panel-primary">
			<section class="panel-heading">
				Best Selling Products
			</section>
			<section class="panel-body">
				<table class="table">
					<tbody id="best-selling-products">
		
					</tbody>
				</table>
			</section>
		</section>
	</section>
</section>
</section>