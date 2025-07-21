$(document).ready(function() {
	var baseUrl = $('#header').data('base-url');
	var filter = $('.all-filter').data('default-filter');
	var sort = null;

	$('#dueLoans').removeAttr('href');
	$('#dueLoans').css('cursor', 'pointer');

	$('#dueLoans').on('click', function() {
		$('#cboStatus').val('DUE');
		$('#cboStatus').trigger('change');
	});

	$('#cboStatus').change(function() {
		loadLoans(1, true);
	});

	$('#txtSearch').donetyping(function() {
		loadLoans(1, true);
	});

	$('#loanTable tbody').css('cursor', 'pointer');
	$('#loanTable tbody').on('click', 'tr', function() {
		location = baseUrl + 'loan?id=' + $(this).data('id');
	});
	$('.pointer').on('click',
		function() {
			$('.pointer').not(this).each(function() {
				$(this).removeClass('asc').removeClass('desc');
			});

			var th = $(this);
			if (th.hasClass('asc')) {
				th.addClass('desc');
				th.removeClass('asc');
				sortColumn(th, 'desc');
			} else if (th.hasClass('desc')) {
				th.removeClass('asc').removeClass('desc');
				sortColumn(th, '');
			} else {
				th.addClass('asc');
				sortColumn(th, 'asc');
			}
		}
	);

	$('#btnNewLoan').on('click', function() {
		location = baseUrl + 'loan';
	});


	$('#btnNewTransaction').on('click', function() {
		$('#transactionAmount').val('');
		$('#radioDeposit').prop('checked', true);
		$('#checkInvestment').prop('checked', false);
		$('#transactionModal').modal('show');
	});

	$('#btnProceed').on('click', function() {
		var url = baseUrl + 'api/account/' + ($('#radioDeposit').is(':checked') ? 'deposit' : 'withdraw');
		$.ajax({
			url: url,
			type: 'POST',
			data: JSON.stringify({ amount: $("#transactionAmount").val(), investment: $('#checkInvestment').is(':checked') }),
			contentType: "application/json; charset=utf-8",
			success: function() {
				location.replace(baseUrl + '');
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	});

	$('.all-filter .dropdown-item').on('click', function() {
		filter = $(this).data('filter');
		reloadSummary(filter);
		
		$('.all-filter .dropdown-item').removeClass('active');
		$(this).addClass('active');
		$('#specifyYear').val('');
	});
	
	$('#specifyYear').donetyping(function() {
		filter = $(this).val();
		reloadSummary(filter);
	});

	initLoanPagination(parseInt($('#pageCount').val())).on("page", function(event, num) {
		loadLoans(num, false);
	});

	initTransactionsPagination(parseInt($('#transactionPageCount').val())).on("page", function(event, num) {
		loanTransactions(num, false);
	});

	function reloadSummary(filter) {
		$.ajax({
			url: baseUrl + 'api/dashboard/summary',
			type: 'GET',
			data: { filter: filter },
			contentType: "application/json; charset=utf-8",
			success: function(data) {
				$('#onLoan').text(data.onLoan);
				$('#expectedReturn').text(data.expectedReturn);
				if (data.expectedReturn != null)
					$('#expectedReturnText').removeClass('hidden');
				else
					$('#expectedReturnText').addClass('hidden');

				$('#interestGained').text(data.interestGained);
				$('#interestIncreased').text(data.interestIncreased);
				if (data.interestIncreased != null)
					$('#interestIncreasedText').removeClass('hidden');
				else
					$('#interestIncreasedText').addClass('hidden');

				$('#balance').text(data.balance);
				$('#investment').text(data.investment);
				if (data.investment != null)
					$('#investmentText').removeClass('hidden');
				else
					$('#investmentText').addClass('hidden');

				$('.filterable').text('| ' + data.filterDisplay);
				showTransactions(data.recentTransactions);
				initTransactionsPagination(data.transactionPageCount);
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	}

	function sortColumn(th, order) {
		if (order != '')
			sort = th.data('field') + ":" + order;
		else
			sort = null;
		loadLoans(parseFloat($('.page-item.active').data('lp')), false);
	}

	function initLoanPagination(pageCount) {
		return $('#page-selection').bootpag({
			total: pageCount,
			page: 1,
			maxVisible: 5,
			wrapClass: 'pagination justify-content-center pagination-sm'
		});
	}

	function initTransactionsPagination(pageCount) {
		return $('#transaction-page-selection').bootpag({
			total: pageCount,
			page: 1,
			maxVisible: 5,
			wrapClass: 'pagination justify-content-center pagination-sm'
		});
	}

	function loadLoans(page, refreshPagination) {
		$.ajax({
			url: baseUrl + 'api/loans',
			type: "POST",
			data: JSON.stringify({ search: $('#txtSearch').val(), status: $('#cboStatus').val(), page: page, sort: sort }),
			dataType: "json",
			contentType: "application/json; charset=utf-8",
			success: function(data) {
				var table = $("#loanTable tbody");
				table.empty();
				$.each(data.list, function(idx, loan) {
					table.append('<tr data-id="' + loan.id + '">' +
						'<td class="col-hide-on-mobile" scope="row">' + loan.code + '</td>' +
						'<td class="col-hide-on-mobile">' + cleanString(loan.customer) + '</td>' +
						'<td class="col-show-on-mobile">' + cleanString(loan.nickname) + '</td>' +
						'<td>' + cleanString(loan.dateDue) + '</td>' +
						'<td>' + formatNumber(loan.balance) + '</td>' +
						'<td>' + getStatusElement(loan.status) + '</td>' +
						'</tr>');
				});
				if (refreshPagination)
					initLoanPagination(data.totalCount);
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	}

	function loanTransactions(page, refreshPagination) {
		$.ajax({
			url: baseUrl + 'api/account/transactions',
			type: "GET",
			data: { page: page },
			dataType: "json",
			contentType: "application/json; charset=utf-8",
			success: function(data) {
				showTransactions(data.list);
				if (refreshPagination)
					initTransactionsPagination(data.totalCount);
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	}

	function showTransactions(list) {
		var div = $(".activity");
		div.empty();
		$.each(list, function(idx, transaction) {
			div.append('<div class="activity-item d-flex">' +
				'<span class="activite-label">' + transaction.date + '</span>' +
				'<i class="bi bi-circle-fill activity-badge align-self-start ' + (transaction.deposit ? 'text-success' : 'text-danger') + '"></i>' +
				'<div class="activity-content">' +
				'<div class="activity-details">' +
				'<span class="activity-left">' + transaction.customer + '</span>' +
				'<span class="activity-amount activity-right">' + transaction.amount + '</span> <br />' +
				'<span class="activity-additional activity-left">' + transaction.loanCode + '</span>' +
				'<span class="activity-additional activity-amount activity-right"> Running Balance: ' + transaction.runningBalance + '</span>' +
				'</div> ' +
				'</div>' +
				'</div>');
		});
	}

	function getStatusElement(status) {
		var statusObj = null;
		if (status == 'DRAFT')
			statusObj = { display: 'Draft', style: 'bg-secondary' };
		else if (status == 'TO_PAY')
			statusObj = { display: 'To Pay', style: 'bg-warning' };
		else if (status == 'PARTIAL')
			statusObj = { display: 'Partially Paid', style: 'bg-warning' };
		else if (status == 'DUE')
			statusObj = { display: 'Due', style: 'bg-danger' };
		else if (status == 'CANCELLED')
			statusObj = { display: 'Cancelled', style: 'bg-cancelled' };
		else if (status == 'PAID')
			statusObj = { display: 'Paid', style: 'bg-success' };
		return '<span class="badge ' + statusObj.style + '">' + statusObj.display + '</span>';
	}

	function cleanString(value) {
		if (value === null)
			return "";
		return value;
	}

	function formatNumber(value) {
		if (value === null)
			return '';
		if (value === 0)
			return '';
		return parseFloat(value).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,");
	}
});