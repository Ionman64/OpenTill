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