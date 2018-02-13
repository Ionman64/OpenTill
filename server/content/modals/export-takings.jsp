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
				       <input type="text" id="takings-date-start" class="span2 form-control input-lg" placeholder="Search for...">
					</section>
				</section>
				<section class="col-md-6">
					<section class="form-group">
				      <label>From Date</label>
				      <input type="text" id="takings-date-end" class="span2 form-control input-lg" placeholder="Search for...">
					</section>
				</section>
				<section class="col-md-12">
					<section class="form-group">
					<label>Departments</label>
				  <ul id="takings-departments-export" class="checkbox-select form-control">  	
                       <li><label for="chk1"><input name="chk1" id="chk1" type="checkbox">First</label></li>
                       <li><label for="chk2"><input name="chk2" id="chk2" type="checkbox">Second</label></li>
                       <li><label for="chk3"><input name="chk3" id="chk3" type="checkbox">Third</label></li>
                       <li><label for="chk4"><input name="chk4" id="chk4" type="checkbox">Fourth</label></li>
                       <li><label for="chk5"><input name="chk5" id="chk5" type="checkbox">Fifth</label></li>
                       <li><label for="chk6"><input name="chk6" id="chk6" type="checkbox">Sixth</label></li>
                       <li><label for="chk7"><input name="chk7" id="chk7" type="checkbox">Seventh</label></li>
				  </ul>
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
