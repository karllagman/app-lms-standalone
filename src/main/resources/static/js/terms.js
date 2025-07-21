$(document).ready(function() {
	var baseUrl = $('#header').data('base-url');
	$('.input-group-align').width(Math.max.apply(Math, $('.input-group-align').map(function() { return $(this).width(); }).get()));

	$(".decimal").on('change', function() {
		$(this).val(getFloatValue($(this).val(), true));
	});
	$(".decimal").trigger('change');

	$('#termsTable tbody').css('cursor', 'pointer');
	$('#termsTable tbody').on('click touchend', 'tr', function() {
		var id = $(this).data('id');
		$.ajax({
			url: baseUrl + 'api/loan/terms/' + id,
			type: "GET",
			success: function(data) {
				setTerm(data);
				$('#btnConfirmDelete').removeClass('hidden');
				$('.edit-button').removeClass('hidden');
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	});

	$('#termRatesTable tbody').css('cursor', 'pointer');
	$('#termRatesTable tbody').on('click touchend', 'tr', function(evt) {
		var $cell = $(evt.target).closest('td');
		var $td = $('td', this);
		if ($cell.index() == 0) {
			$td.eq(0).find('input').prop("checked", true);
		} else if ($cell.index() == 6) {
			$(this).remove()
		} else {
			var row = $(this).parent().children().index($(this));
			$('#rateRow').text(row);
			$('#rateDays').val(parseFloat($td.eq(1).text()));
			$('#rateFrequency').val($td.eq(2).text().toUpperCase());
			$('#ratePayableIn').val(parseFloat($td.eq(3).text()));
			$('#rateInterval').val(parseFloat($td.eq(4).text()));
			$('#rateInterestRate').val(parseFloat($td.eq(5).text().replace(' %', '')));
			$('#btnRateRemove').removeClass('hidden');
			$('#rateModal').modal('show');
		}

	});

	$("#addRate").on("click", function() {
		$('#rateRow').text(-1);
		$('#rateDays').val('');
		$('#ratePayableIn').val('');
		$('#rateInterval').val('');
		$('#rateInterestRate').val('');
		$('#btnRateRemove').addClass('hidden');
		$('#rateModal').modal('show');
	});


	$('#btnRateSave').on("click", function() {
		var row = $('#rateRow').text();
		var rate = {
			default: false,
			frequency: $('#rateFrequency').find(":selected").val(),
			days: $('#rateDays').val(),
			payableIn: $('#ratePayableIn').val(),
			interval: $('#rateInterval').val(),
			interestRateInt: $('#rateInterestRate').val()
		}
		if (row == '-1') {
			appendRate($('#termRatesTable tbody'), rate)
		} else {
			var $td = $('#termRatesTable tbody tr').eq($('#rateRow').text()).find('td');
			$td.eq(1).text(rate.days);
			$td.eq(2).text(capitalize(rate.frequency));
			$td.eq(3).text(rate.payableIn);
			$td.eq(4).text(rate.interval)
			$td.eq(5).text(rate.interestRateInt + ' %');
		}
		$('#rateModal').modal('hide');
	});

	$("#btnNew").on("click", function() {
		clear();
		$('.edit-button').removeClass('hidden');
		$('#btnConfirmDelete').addClass('hidden');
	});

	$("#btnCancel").on("click", function() {
		clear();
		$('#btnConfirmDelete').addClass('hidden');
		$('.edit-button').addClass('hidden');
	});

	$("#btnSave").on("click", function() {
		validate(function() {
			$.ajax({
				url: baseUrl + 'api/loan/terms/',
				type: "POST",
				data: JSON.stringify(getTerm()),
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				success: function(data) {
					location = baseUrl + 'terms';
				},
				error: function(jqXHR, exception) {
					Msg.error(jqXHR.responseJSON.message);
				}
			});
		});
	});

	$("#btnConfirmDelete").on("click", function() {
		$("#confirmModal").modal('show');
	});
	$("#btnDelete").on("click", function() {
		var id = $('#id').text();
		$.ajax({
			url: baseUrl + 'api/loan/terms/' + id,
			type: "DELETE",
			success: function(data) {
				location = baseUrl + 'terms';
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
	});

	function validate(callback) {
		$('.required').removeClass('invalid');
		$('.required').each(function() {
			if ($(this).val().trim() == '')
				$(this).addClass('invalid')
		});
		if ($('.invalid').length == 0)
			callback.call();
		else
			Msg.error("Please fill in all required fields.");
	}

	function clear() {
		$('.required').removeClass('invalid');
		$('#id').text('');
		$('#name').val('');
		$('#fromAmount').val('');
		$('#toAmount').val('');
		$("#termRatesTable tbody").empty();
	}

	function setTerm(term) {
		$('#id').text(term.id);
		$('#name').val(term.name);
		$('#fromAmount').val(getFloatValue(term.fromAmount, true));
		$('#toAmount').val(getFloatValue(term.toAmount, true));
		$('#interestType').val(term.interestType);
		setRates(term.rates);

	}

	function getTerm() {
		return {
			id: $('#id').text(),
			name: $('#name').val(),
			fromAmount: $('#fromAmount').val(),
			toAmount: $('#toAmount').val(),
			interestType: $('#interestType').val(),
			rates: getRates()
		}
	}

	function setRates(rates) {
		var table = $('#termRatesTable tbody');
		table.empty();
		$.each(rates, function(i, rate) {
			appendRate(table, rate);
		});
	}

	function getRates() {
		return $('#termRatesTable tbody tr:has(td)').map(function(i, v) {
			var $td = $('td', this);
			return {
				default: $('input', $td.eq(0)).is(':checked'),
				days: parseFloat($td.eq(1).text()),
				frequency: $td.eq(2).text().toUpperCase(),
				payableIn: parseFloat($td.eq(3).text()),
				interval: parseFloat($td.eq(4).text()),
				interestRate: parseFloat($td.eq(5).text()) / 100,
			};
		}).get();
	}


	function appendRate(table, rate) {
		table.append('<tr>' +
			'<td style="width: 1%;"><input class="form-check-input rates-radio disabled" type="radio" name="ratesRadio" ' + (rate.default ? 'checked' : '') + ' ></td>' +
			'<td>' + rate.days + '</td>' +
			'<td>' + capitalize(rate.frequency) + '</td>' +
			'<td class="col-hide-on-mobile">' + rate.payableIn + '</td>' +
			'<td class="col-hide-on-mobile">' + rate.interval + '</td>' +
			'<td>' + rate.interestRateInt + ' %</td>' +
			'<td class="bi bi-x-square-fill" style="width: 1%;"></td>' +
			'</tr>');
	}

	function getFloatValue(val, toDecimal) {
		if (val != '') {
			var num = parseFloat(val);
			val = toDecimal ? num.toFixed(2) : num;
		} else
			val = null;
		return val;
	}

	function capitalize(string) {
		if (string != null)
			return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
		else
			return '';
	}

});