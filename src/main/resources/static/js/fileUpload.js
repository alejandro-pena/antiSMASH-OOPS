$(document).ready(function() {
	'use strict';

	// UPLOAD CLASS DEFINITION
	// ======================

	var dropzone = document.getElementById('dropzone');

	var upload = function(files) {
		var formData = new FormData(), xhr = new XMLHttpRequest;
		if (files.length > 1) {
			alert("Please upload only the output .zip file from antiSMASH!");
		}
		formData.append('zipFile', files[0]);
		
		
		
		xhr.onload = function()
		{
			document.open();
			document.write(this.responseText);
			document.close();
		};
		
		xhr.open('POST', 'saveFile');
		xhr.send(formData);
	}

	dropzone.ondrop = function(e) {
		e.preventDefault();
		this.className = 'upload-drop-zone';
		upload(e.dataTransfer.files)
	};

	dropzone.ondragover = function() {
		this.className = 'upload-drop-zone drop';
		return false;
	};

	dropzone.ondragleave = function() {
		this.className = 'upload-drop-zone';
		return false;
	};

});