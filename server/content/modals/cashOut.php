<section class="modal" id="CashOut" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" data-backdrop="static" data-keyboard="false">
  <section class="modal-dialog modal-lg">
	<section class="modal-content">
	  <section class="modal-header">
		<button type="button" class="btn btn-default pull-right" data-dismiss="modal" aria-label="Close">Close</button>
		<h4 class="modal-title">Cash Out</h4>
	  </section>
	  <section class="modal-body" id="change">
		<section class="container-fluid">
	  		<section class="row">
	  			<section class="col-md-6 keypad" id="cash-out-keypad" data-focus="#moneyText">
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
			  	
				  <button class="btn btn-default money-given-button" moneyvalue="50"><img src="img/currency/uk/Fifty_Pound.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="20"><img src="img/currency/uk/Twenty_Pound.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="10"><img src="img/currency/uk/Ten_Pound.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="5"><img src="img/currency/uk/Five_Pound.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="2"><img src="img/currency/uk/Two_Pound.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="1"><img src="img/currency/uk/One_Pound.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="0.5"><img src="img/currency/uk/Fifty_Pence.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="0.2"><img src="img/currency/uk/Twenty_Pence.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="0.1"><img src="img/currency/uk/Ten_Pence.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="0.05"><img src="img/currency/uk/Five_Pence.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="0.02"><img src="img/currency/uk/Two_Pence.jpg"></button><button class="btn btn-default money-given-button" moneyvalue="0.01"><img src="img/currency/uk/One_Penny.jpg"></button>			  
			  </section>
			  <section class="form-group">
				<label for="costText" class="control-label text-danger">Total</label>
				<input type="text" id="costText" class="form-control input-lg" placeholder="Cost" disabled="disabled"></input>
				<label for="moneyText" class="control-label text-danger">Money Given</label>
				<section class="input-group">
					<input type="text" id="moneyText" class="form-control input-lg" placeholder="0.00" disabled="disabled"></input>
					<section class="input-group-btn">
						<button class="btn btn-default btn-lg" id="clearChange">Clear</button>
					</section>
				</section>
				<label for="changeText" class="control-label text-danger">Change Due</label>
				<input type="text" id="changeText" class="form-control input-lg" placeholder="0.00" disabled="disabled"></input>
			  </section>
			</section>
		</section>
	 </section>
		</section>
	  <section class="modal-footer">
		<button type="button" class="btn btn-info btn-lg pull-left" id="completeCard">Paid with Card</button>
		<button type="button" class="btn btn-success btn-lg" id="completeCash">Paid with Cash</button>
	  </section>
	</section>
  </section>
 </section>
