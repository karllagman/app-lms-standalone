$(document).ready(function() {
	var baseUrl = $('#header').data('base-url');
	$('.input-group-align').width(Math.max.apply(Math, $('.input-group-align').map(function() { return $(this).width(); }).get()));

	$(".decimal").on('change', function() {
		$(this).val(parseFloat($(this).val(), true).toFixed(2));
	});
	$(".decimal").trigger('change');

	$("#btnMakePayment").on("click", function() {
		$('#paymentModal').modal('show');
		$('#paymentAmount').val(parseFloat($('#amountToPay').text()).toFixed(2));
	});

	$("#btnCancelLoan").on("click", function() {
		$('#cancelModal').modal('show');
	});

	$("#btnPay").on("click", function() {
		$.ajax({
			url: baseUrl + 'api/loans/pay',
			type: 'POST',
			data: JSON.stringify({ loanId: $("#loanId").text(), amount: $("#paymentAmount").val() }),
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			success: function(data) {
				location.replace(baseUrl);
			},
			error: function(jqXHR, exception) {
				if (jqXHR.responseJSON.result) {
					Msg.warning('Unpaid amount detected. Loan # ' + jqXHR.responseJSON.newLoanCode + ' has been created. Click <a href="' + baseUrl + 'loan?id=' + jqXHR.responseJSON.newLoanId + '">here</a> to view.');
				} else {
					Msg.error(jqXHR.responseJSON.message);
				}
			}
		});
	});

	$("#btnCancel").on("click", function() {
		$.ajax({
			url: baseUrl + 'api/loans/cancel',
			type: 'POST',
			data: JSON.stringify({ loanId: $("#loanId").text() }),
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			success: function(data) {
				location.replace(baseUrl);
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	});
	
	$("#btnSubmitNote").on("click", function() {
		$.ajax({
			url: baseUrl + 'api/loans/note',
			type: 'PUT',
			data: JSON.stringify({ id: $("#loanId").text(), message:  $("#note").val() }),
			contentType: "application/json; charset=utf-8",
			success: function() {
				location.replace(baseUrl + 'loan?id=' + $("#loanId").text());
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	});
	
});