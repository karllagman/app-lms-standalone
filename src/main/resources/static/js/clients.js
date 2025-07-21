$(document).ready(function() {
	var baseUrl = $('#header').data('base-url');

	$('.input-group-align').width(Math.max.apply(Math, $('.input-group-align').map(function() { return $(this).width(); }).get()));

	$('#txtSearch').donetyping(function() {
		loadClients($(this).val());
	});

	$('#clientsTable tbody').css('cursor', 'pointer');
	$('#clientsTable tbody').on('click touchend', 'tr', function() {
		var id = $(this).data('id');
		$('.required').removeClass('invalid');
		$.ajax({
			url: baseUrl + 'api/clients/' + id,
			type: "GET",
			success: function(data) {
				setClient(data);
				$('#btnConfirmDelete').removeClass('hidden');
				$('.edit-button').removeClass('hidden');
			},
			error: function(jqXHR, exception) {
				Msg.error(jqXHR.responseJSON.message);
			}
		});
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
				url: baseUrl + 'api/clients',
				type: "POST",
				data: JSON.stringify(getClient()),
				dataType: 'json',
				contentType: "application/json; charset=utf-8",
				success: function(data) {
					location = baseUrl + 'clients';
				},
				error: function(jqXHR, exception) {
					Msg.error(jqXHR.responseJSON.message);
				}
			});
		});
	});

	function loadClients(search) {
		$.ajax({
			url: baseUrl + 'api/clients',
			type: "GET",
			data: { search: search },
			success: function(data) {
				var table = $("#clientsTable tbody");
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

	$("#btnConfirmDelete").on("click", function() {
		$("#confirmModal").modal('show');
	});
	$("#btnDelete").on("click", function() {
		var id = $('#id').text();
		$.ajax({
			url: baseUrl + 'api/clients/' + id,
			type: "DELETE",
			success: function(data) {
				location = baseUrl + 'clients';
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
		$('#nickname').val('');
		$('#address').val('');
		$('#contactNo').val('');
		$('#email').val('');
		$('#fb').val('');
	}

	function setClient(client) {
		$('#id').text(client.id);
		$('#name').val(client.name);
		$('#nickname').val(client.nickname);
		$('#address').val(client.address);
		$('#contactNo').val(client.contactNo);
		$('#email').val(client.email);
		$('#fb').val(client.fb);
	}

	function getClient() {
		return {
			id: $('#id').text(),
			name: $('#name').val(),
			nickname: $('#nickname').val(),
			address: $('#address').val(),
			contactNo: $('#contactNo').val(),
			email: $('#email').val(),
			fb: $('#fb').val()
		}
	}

});