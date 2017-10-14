﻿<section class="modal" id="productSee" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" data-backdrop="static" data-keyboard="false">
  <section class="modal-dialog modal-lg">
	<section class="modal-content">
	  <section class="modal-header">
		<button type="button" class="btn btn-default pull-right" data-dismiss="modal" aria-label="Close">Close</button>
		<ul class="nav nav-pills pull-left" id="productModalNav">
		  <li class="active"><a href="#" data-page="details">Details</a></li>
		  <li><a href="#" data-page="stock">Inventory</a></li> 
		</ul>
	  </section>
	  <section class="modal-body">
	  	<section class="container-fluid page hidden" data-id="stock">
	  		<section class="row">
	  			<section class="col-md-3">
					<label>Current Level</label>
					<input type="number" id="currentLevel" value=0 class="form-control"/>
				</section>
	  			<section class="col-md-3">
					<label>Max Volume</label>
					<input type="number" id="graphMaxLevel" value=0 class="form-control"/>
				</section>
				<section class="col-md-3">
					<label>On Display</label>
					<input type="number" id="graphDisplayLevel" value=0 class="form-control"/>
				</section>
				<section class="col-md-3">
					<label>Re-order Level</label>
					<input type="number" id="graphReorderLevel" value=0 class="form-control"/>
				</section>
	  		</section>
	  		<section class="row">
	  			<section class="col-md-12">
					<canvas id="canvas"></canvas>
				</section>
	  		</section>
	  	</section>
	  	<section class="container-fluid page" data-id="details">
	  		<section class="row">
	  			<section class="col-md-6 keypad" data-focus="#ProductPrice">
	  				<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-danger no-digit" data-function="clear-button">Clear</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default no-digit" data-function="no-sale" disabled>N/S</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-info no-digit" data-function="pay-out" disabled>P/O</button>
						</section>
					</section>
	  				<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">7</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">8</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">9</button>
						</section>
					</section>
					<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">4</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">5</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">6</button>
						</section>
					</section>
					<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">1</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">2</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">3</button>
						</section>
					</section>
					<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-default">0</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default">00</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default no-digit" disabled="disabled">REFUND</button>
						</section>
					</section>
	  			</section>
	  			<section class="col-md-6">
	  				<form>
					  <section class="form-group">
						<label for="ProductBarcode" class="control-label">Barcode:</label>
						<input type="text" class="form-control input-lg" id="ProductBarcode" readonly>
					  </section>
					  <section class="form-group">
					  	<label for="ProductName" class="control-label text-default">Name:</label>
		  				<section class="input-group">
							<input id="ProductName" class="form-control input-lg" placeholder="Name" type="text">
							<section class="input-group-btn">
								<button class="btn btn-default btn-lg" id="clearProductName">Clear</button>
							</section>
						</section>
					  </section>
					   <section class="form-group">
						<label for="ProductName" class="control-label">Department:</label>
						<select class="form-control input-lg" id="ProductDepartment">
			
						</select>
					  </section>
					  <section class="form-group">
						  <label for="ProductPrice" class="control-label text-success">Price:</label>
						  <section class="input-group">
							<span class="input-group-addon">£</span>
							<input type="text" class="form-control input-lg" id="ProductPrice">
						  </section>
					  </section>
					</form>
	  			</section>
	  		</section>
	  	</section>
	  </section>
	  <section class="modal-footer">
		<button type="button" class="btn btn-success btn-lg" id="PrintLabel">Print Label</button>
		<button type="button" class="btn btn-primary btn-lg" id="update-product">Update Product</button>
	  </section>
	</section>
  </section>
 </section>