<section class="modal" id="export-takings" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <section class="modal-dialog modal-lg">
	<section class="modal-content">
	  <section class="modal-header">
		<button type="button" class="btn btn-default pull-right" data-dismiss="modal" aria-label="Close">Close</button>
		<h4 class="modal-title">Export Takings</h4>
	  </section>
	  <section class="modal-body">
	  	<section class="container-fluid">
	  		<section class="row">
	  			<section class="col-md-12">
	  				<section class="form-group">
					  <label>File Type</label>
					  <select id="takings-export-type" class="form-control input-lg">
					  	<option value="xls">Microsoft Excel (.xls)</option>
					  </select>
					 </section>
				</section>
				<section class="col-md-6">
					<section class="form-group">
				      <label>To Date</label>
				       <input type="date" id="takings-date-start" class="span2 form-control input-lg" placeholder="Search for...">
					</section>
				</section>
				<section class="col-md-6">
					<section class="form-group">
				      <label>From Date</label>
				      <input type="date" id="takings-date-end" class="span2 form-control input-lg" placeholder="Search for...">
					</section>
				</section>
				<section class="col-md-12">
					<section class="form-group">
						<label>Departments</label>
				  		<ul id="takings-departments-export" class="checkbox-select form-control"></ul>
				 	 </section>
  				</section>
  				<section class="col-md-12">
  					<section class="panel panel-info hidden" id="takings-export-progress">
						<section class="panel-body">
							<i class="fa fa-spinner fa-spin"></i> Exporting...
						</section>
					</section>
					<section class="panel panel-success hidden" id="takings-export-success">
						<section class="panel-body">
							Export Successful! File should download automatically, otherwise click <a href="#" id="takings-export-alt-download">here</a>
						</section>
					</section>
					<section class="panel panel-danger hidden" id="takings-export-failure">
						<section class="panel-body">
							Export Failure
						</section>
					</section>
  				</section>
  		</section>
  	  </section>
  	  </section>
	  <section class="modal-footer">
		<button type="button" class="btn btn-success btn-lg" id="takings-export">Export</button>
	  </section>
  </section>
 </section>
 </section>
