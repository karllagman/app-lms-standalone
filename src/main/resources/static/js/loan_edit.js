$(document).ready(function() {
	var baseUrl = $('#header').data('base-url');
	$('.input-group-align').width(Math.max.apply(Math, $('.input-group-align').map(function() { return $(this).width(); }).get()));

	if ($('#loanId').text() == '') {
		$('#btnDeleteLoan').addClass('hidden');
	}

	$(".decimal").on('change', function() {
		$(this).val(getFloatValue($(this).val(), true));
	});
	$(".decimal").trigger('change');

	$("#btnClientSearch").on("click", function() {
		$('#clientModal').modal('show');
		$('#txtSearchClient').val("");
		loadClients();
	});

	$('#clientTable tbody').css('cursor', 'pointer');
	$('#clientTable tbody').on('click', 'tr', function() {
		var id = $(this).data('id');
		var name = $(this).data('name');

		$("#clientId").text(id);
		$("#clientName").text(name);

		$('#clientModal').modal('hide');
	});

	$('#txtSearchClient').donetyping(function() {
		loadClients($(this).val());
	});

	$('#termPayableIn, #termInterestRate, #termInterval').donetyping(function() {
		populatePayables(false);
	});
	$('#termFrequency, #startDate').on('change', function() {
		populatePayables(false);
	});
	$('#endDate, #loanDate, #termInterestType').on('change', function() {
		populatePayables(true);
	});
	$('#loanAmount').donetyping(function() {
		populatePayables(true);
	});

	$('#payablesTable tbody').css('cursor', 'pointer');
	$('#payablesTable tbody').on('click', 'tr', function() {
		$('#payableModal').modal('show');
		var row = $(this).parent().children().index($(this));
		var $td = $('td', this);

		$('#payableRow').text(row);
		$('#payableDueDate').val(formatDatePicker($td.eq(0).text()));
		$('#payablePrincipal').val($td.eq(1).data('value').toFixed(2));
		$('#payableInterest').val($td.eq(2).data('value').toFixed(2));
	});

	$("#btnPayableSave").on("click", function() {
		var $td = $('#payablesTable tbody tr').eq($('#payableRow').text()).find('td');
		$td.eq(0).text(formatDate($('#payableDueDate').val()));
		$td.eq(1).text(formatNumber($('#payablePrincipal').val()));
		$td.eq(1).data('value', $('#payablePrincipal').val());
		$td.eq(2).text(formatNumber($('#payableInterest').val()));
		$td.eq(2).data('value', $('#payableInterest').val());
		$('#payableModal').modal('hide');
	});

	$("#btnSaveLoan").on("click", function() {
		validate(function() {
			$.ajax({
				url: baseUrl + 'api/loans',
				type: "PUT",
				data: JSON.stringify(getLoan()),
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				success: function(data) {
					location.replace(baseUrl + 'loan?id=' + data.id);
				},
				error: function(jqXHR, exception) {
					Msg.error(jqXHR.responseJSON.message);
				}
			});
		});
	});

	$('#btnDeleteLoan').on('click', function() {
		$("#confirmModal").modal('show');
	});

	$('#btnConfirmSubmit').on('click', function() {
		validate(function() {
			$("#submitModal").modal('show');
		});
	});

	$("#btnDelete").on("click", function() {
		var id = $('#loanId').text();
		$.ajax({
			url: baseUrl + 'api/loans/' + id,
			type: "DELETE",
			success: function(data) {
				location = baseUrl + '';
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	});

	$("#btnSubmitLoan").on("click", function() {

		$.ajax({
			url: baseUrl + 'api/loans/submit',
			type: "POST",
			data: JSON.stringify(getLoan()),
			dataType: 'json',
			contentType: "application/json; charset=utf-8",
			success: function(data) {
				location.replace(baseUrl + 'loan?id=' + data.id);
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	});

	function validate(callback) {
		$('.required').removeClass('invalid');
		$('#clientName').removeClass('invalid');

		$('.required').each(function() {
			if ($(this).val().trim() == '')
				$(this).addClass('invalid')
		});

		if ($('#clientName').text() == '')
			$('#clientName').addClass('invalid');

		if ($('.invalid').length == 0)
			callback.call();
		else
			Msg.error("Please fill in all required fields.");
	}

	function loadClients(search) {
		$.ajax({
			url: baseUrl + 'api/clients',
			type: "GET",
			data: { search: search },
			success: function(data) {
				var table = $("#clientTable tbody");
				table.empty();
				$.each(data, function(idx, client) {
					table.append('<tr data-id="' + client.id + '" data-name="' + client.name + '">' +
						'<td>' + client.name + '</td>' +
						'</tr>');
				});
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	}

	function populatePayables(reload) {
		$.ajax({
			url: baseUrl + 'api/loans/populate?reload=' + reload,
			type: 'POST',
			data: JSON.stringify(getLoan()),
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
			success: function(data) {
				$('.required').removeClass('invalid');
				$('#clientName').removeClass('invalid');

				var table = $('#payablesTable tbody');
				table.empty();
				$('#totalPrincipal').text(formatNumber(data.totalPrincipal));
				$('#totalInterest').text(formatNumber(data.totalInterest));
				$('#termInterval').val(data.term.interval);
				$('#termFrequency').val(data.term.frequency);
				$('#termPayableIn').val(data.term.payableIn);
				$('#termInterestRate').val(data.term.interestRate * 100);
				$.each(data.payables, function(idx, payable) {
					table.append('<tr>' +
						'<td data-id="' + payable.id + '">' + payable.dateDue + '</td>' +
						'<td data-value="' +payable.principal + '">' + formatNumber(payable.principal) + '</td>' +
						'<td data-value="' +payable.interest + '">' + formatNumber(payable.interest) + '</td>' +
						'<td>' + getStatusElement(payable.status) + '</td>' +
						'</tr>');
				});
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	}

	function getLoan() {
		var payables = $('#payablesTable tbody tr:has(td)').map(function(i, v) {
			var $td = $('td', this);
			return {
				id: $td.eq(0).data('id'),
				dateDue: $td.eq(0).text(),
				principal: parseFloat($td.eq(1).data('value')),
				interest: parseFloat($td.eq(2).data('value')),
				status: $td.eq(3).data('status'),
			}
		}).get();

		var interestRate = parseFloat($('#termInterestRate').val()) / 100;
		return {
			id: getText($('#loanId')),
			dateCreated: getText($('#createdDate')),
			continuedFrom: getText($('#continuedFrom')),
			continueTo: getText($('#continueTo')),
			code: getText($('#code')),
			status: $('#status').data('value'),
			client: getClient(),
			loanAmount: getFloatValue($('#loanAmount').val(), false),
			dateLoaned: formatDate($('#loanDate').val()),
			dateStart: formatDate($('#startDate').val()),
			dateEnd: formatDate($('#endDate').val()),
			term: {
				interestType: $('#termInterestType').find(":selected").data('value'),
				payableIn: getFloatValue($('#termPayableIn').val(), false),
				interestRate: interestRate > 0 ? interestRate : null,
				interval: getFloatValue($('#termInterval').val(), false),
				frequency: $('#termFrequency').find(":selected").data('value')
			},
			payables: payables
		};
	}

	function getClient() {
		var id = $('#clientId').text();
		var name = $('#clientName').text();
		var client = null;
		if (id != '' && name != '')
			client = {
				id: id, name: name
			}
		return client;
	}

	function formatDate(date) {
		if (date != '') {
			var parts = date.split('-');
			return parts[1] + '/' + parts[2] + '/' + parts[0];
		}
		return null;
	}

	function formatDatePicker(date) {
		if (date != '') {
			var parts = date.split('/');
			return parts[2] + '-' + parts[0] + '-' + parts[1];
		}
		return null;
	}

	function getText(value) {
		value = value.text();
		return value != '' ? value : null;
	}

	function getFloatValue(val, toDecimal) {
		if (val != '') {
			var num = parseFloat(val);
			val = toDecimal ? num.toFixed(2) : num;
		} else
			val = null;
		return val;
	}

	function formatNumber(val) {
		var num = parseFloat(val);
		return num.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,");
	}

	function getStatusElement(status) {
		var statusObj = null;
		if (status == 'PENDING')
			statusObj = { display: 'Pending', style: 'bg-secondary' };
		else if (status == 'TO_PAY')
			statusObj = { display: 'To Pay', style: 'bg-warning' };
		else if (status == 'DUE')
			statusObj = { display: 'Due', style: 'bg-danger' };
		else if (status == 'CANCELLED')
			statusObj = { display: 'Cancelled', style: 'bg-cancelled' };
		else if (status == 'PAID')
			statusObj = { display: 'Paid', style: 'bg-success' };
		return '<span class="badge ' + statusObj.style + '">' + statusObj.display + '</span>';
	}

});