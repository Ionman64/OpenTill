<section class="modal" id="newProduct" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" data-backdrop="static" data-keyboard="false">
  <section class="modal-dialog modal-lg">
	<section class="modal-content">
	  <section class="modal-header">
		<button class="btn btn-default pull-right" data-dismiss="modal" aria-label="Close">Close</button>
		<h4 class="modal-title" id="TansactionName">You have added a new Product</h4>
	  </section>
	  <section class="modal-body">
		<section class="container-fluid">
	  		<section class="row">
	  			<section class="col-md-6 keypad" data-focus="#newCost">
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
	  			<section class="col-md-6">
	  				<section class="form-group">
	  					<label for="newName" class="control-label text-default">Name of this item?</label>
		  				<section class="input-group">
							<input id="newName" class="form-control input-lg" placeholder="Name" type="text"></input>
							<section class="input-group-btn">
								<button class="btn btn-default btn-lg" id="clearNewProductName">Clear</button>
							</section>
						</section>
					</section>
					<section class="form-group">
						<label for="ProductName" class="control-label">Department:</label>
						<select class="form-control input-lg" id="newProductDepartment">
						</select>
					</section>
					<section class="form-group">
				  		<label for="ProductCost" class="control-label">Cost:</label>
						<section class="input-group">
							<span class="input-group-addon">£</span>
							<input type="text" class="form-control input-lg" id="newCost" placeholder="Cost"></input>
						</section>	
				  	</section>
	  			</section>
	  		</section>
	  	</section>
	  	
	  </section>
	  <section class="modal-footer">
		<button type="button" id="closeCocktailButton" class="btn btn-default btn-lg" data-dismiss="modal">Skip</button>
		<button type="button" class="btn btn-info btn-lg" id="newProductSave">Save</button>
	  </section>
	</section>
  </section>
 </section>
