<section class="modal" id="product-modal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" data-backdrop="static" data-keyboard="false">
  <section class="modal-dialog modal-lg">
	<section class="modal-content">
	  <section class="modal-header">
		<button class="btn btn-default pull-right" data-dismiss="modal" aria-label="Close">Close</button>
		<ul class="nav nav-pills pull-left" id="productModalNav">
		  <li class="active"><a href="#" data-page="details">Details</a></li>
		  <li><a href="#" data-page="stock">Inventory</a></li>
		  <li><a href="#" data-page="additional">Auto Price Update</a></li> 
		</ul>
	  </section>
	  <section class="modal-body">
	  	<section class="container-fluid page hidden" data-id="stock">
	  		<section class="row">
	  			<section class="col-md-12">
	  				<section class="form-group">
						<label>Max Stock Level</label>
						<input type="number" id="maxStockLevel" value="0" class="form-control input-lg"></input>
					</section>
				</section>
				<section class="col-md-12">
					<section class="form-group">
						<label>Current Stock Level</label>
						<input type="number" id="currentLevel" value="0" class="form-control input-lg"></input>
					</section>
				</section>
	  		</section>
	  	</section>
	  	<section class="container-fluid page hidden" data-id="additional">
	  		<section class="row">
	  			<section class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
				  	<section class="form-group">
						<section class="input-group">
							<input type="checkbox" data-toggle="toggle" id="auto-pricing-enabled" checked="checked"/>
							<label id="auto-pricing-enabled-message" class="text-success italic">Auto Pricing Enabled</label>
						</section>
					  </section>
				  </section>
  			</section>
	  		<section class="row well" id="auto-price-update-container">
	  			<section class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
	  			 <section class="form-group">
					<label for="SupplierPrice" class="control-label">Cost From Supplier:</label>
					<section class="input-group">
						<span class="input-group-addon">�</span>
						<input type="number" step=".01" value="0.00" class="form-control input-lg" id="SupplierPrice"></input>
				  	</section>
				  </section>
				  <section class="form-group">
					<label for="SupplierPrice" class="control-label">Number of products in case:</label>
					<input type="number" value="1" class="form-control input-lg" id="unitsInCase"></input>
				  </section>
				  </section>
				  <section class="col-lg-2 col-md-2 col-sm-2 col-xs-4">
				  	<section class="form-group">
						<label for="includesVAT" class="control-label">VAT:</label>
						<section class="input-group">
							<input type="checkbox" data-toggle="toggle" id="includesVAT" checked="checked"/>
						</section>
					  </section>
				  </section>
				  <section class="col-lg-10 col-md-10 col-sm-10 col-xs-8">
				  	<section class="form-group">
						<label for="VATamount" class="control-label">VAT Amount:</label>
						<section class="input-group">
							<input type="number" step=".01" class="form-control input-lg" id="VATamount" value="20.00"/>
					  		<span class="input-group-addon">%</span>
					  	</section>
					  </section>
				  </section>
				  <section class="col-lg-2 col-md-2 col-sm-2 col-xs-4">
				  	<section class="form-group">
						<label for="includesVAT" class="control-label">Target:</label>
						<section class="input-group">
							<input type="checkbox" data-toggle="toggle" id="targetPercentage" checked="checked"/>
						</section>
					  </section>
				  </section>
				  <section class="col-lg-10 col-md-10 col-sm-10 col-xs-8">
				  	<section class="form-group">
						<label for="VATamount" class="control-label">Target Profit Margin</label>
						<section class="input-group">
							<input type="number" step=".01" class="form-control input-lg" id="targetProfitMargin" value="50.00"/>
					  		<span class="input-group-addon">%</span>
					  	</section>
					  </section>
				  </section>
				  <section class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
				  	<section id="por-over-100" class="panel panel-danger hidden">
				  		<section class="panel-body">
				  			Cannot calculate POR at 100% or higher!
				  		</section>
				  	</section>
				  </section>
				  <section class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
				  	<section id="autopriceupdate-modal" class="panel panel-info hidden">
				  		<section class="panel-body">
				  			The price of this product will be automatically updated to <label id="autopriceupdate" class="bold">0.00</label> 
				  		</section>
				  	</section>
				  </section>
	  		</section>
	  	</section>
	  	<section class="container-fluid page" data-id="details">
	  		<section class="row">
	  			<section class="col-lg-6 col-md-6 hidden-sm hidden-xs keypad" data-focus="#ProductPrice">
	  				<section class="row" style="padding-top:10px">
						<section class="col-md-4">
							<button class="btn btn-danger no-digit" data-function="clear-button">Clear</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-default no-digit" data-function="no-sale" disabled="disabled">N/S</button>
						</section>
						<section class="col-md-4">
							<button class="btn btn-info no-digit" data-function="pay-out" disabled="disabled">P/O</button>
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
	  			<section class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
	  				<form>
					  <section class="form-group">
						<label for="ProductBarcode" class="control-label">Barcode:</label>
						<input type="text" class="form-control input-lg" id="ProductBarcode" readonly="readonly"></input>
					  </section>
					  <section class="form-group">
					  	<label for="ProductName" class="control-label text-default">Name:</label>
		  				<section class="input-group">
							<input id="ProductName" class="form-control input-lg" placeholder="Name" type="text"></input>
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
						<label for="ProductSupplier" class="control-label">Supplier:</label>
						<select class="form-control input-lg" id="ProductSupplier">
			
						</select>
					  </section>
					  <section class="form-group">
						  <label for="ProductPrice" class="control-label text-success">Price:</label>
						  <section class="input-group">
							<span class="input-group-addon">�</span>
							<input type="number" step=".01" class="form-control input-lg" id="ProductPrice"></input>
						  </section>
					  </section>
					</form>
	  			</section>
	  		</section>
	  	</section>
	  </section>
	  <section class="modal-footer">
		<button type="button" class="btn btn-danger btn-lg pull-left" id="delete-product">Discontinue Product</button>
		<button type="button" class="btn btn-success btn-lg" id="PrintLabel">Print Label</button>
		<button type="button" class="btn btn-primary btn-lg" id="update-product">Update Product</button>
	  </section>
	</section>
  </section>
 </section>
