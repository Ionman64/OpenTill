<section class="container-fluid" style="padding-top:10px;">
		<section class="row hidden">
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
		</section>
		<section class="row" id="takings-container">
			<section class="col-md-12">
				<section class="col-md-2">
					<section class="panel panel-info">
						<section class="panel-heading">
							<section class="date-container">
								<h4 class="text-center">24th Jul</h4>
								<h6 class="text-center">2018</h6>
							</section>
						</section>
						<section class="panel-body">
							
						</section>
					</section>
				</section>
			</section>
			<!-- <section class="col-lg-10 col-md-10 col-sm-10 col-xs-10">
				<button id="takings-export-btn" class="btn btn-default navbar-btn btn-lg">Export</button>
			</section>
			<section class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
				<!--<a href="index.jsp"><button class="btn btn-default btn-lg pull-right" style="margin:10px">Register</button></a>
			</section>-->
		</section>
	</section>
<section class="container-fluid">
	
	<section class="custom-chart-medium">
		<canvas id="takings-canvas">

		</canvas>
	</section>
	<section class="row viewport">
		<section id="takings-table">
			
		</section>
	</section>
</section>
<section class="import-modal"  page="modals/takings.jsp"></section>
<section class="import-modal"  page="modals/export-takings.jsp"></section>