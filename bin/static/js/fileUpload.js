$(document).ready(
		function() {
			'use strict';

			// UPLOAD CLASS DEFINITION
			// ======================

			var dropZone = document.getElementById('drop-zone');
			var uploadForm = document.getElementById('js-upload-form');

			var startUpload = function(files) {
				var uploadForm = document.getElementById('js-upload-form');
				if (document.getElementById('js-upload-files').files.length == 0)
					alert("No files for submitting!");
				uploadForm.submit();
				//var input = $("<input>").attr("type", "hidden").attr("name",
				//		"mydata").val("bla");
				//$('#form1').append($(input));
				console.log(files);

			}

			uploadForm.addEventListener('submit',
					function(e) {
						var uploadFiles = document
								.getElementById('js-upload-files').files;
						e.preventDefault()
						startUpload(uploadFiles)
					})

			dropZone.ondrop = function(e) {
				e.preventDefault();
				this.className = 'upload-drop-zone';
				startUpload(e.dataTransfer.files)
			}

			dropZone.ondragover = function() {
				this.className = 'upload-drop-zone drop';
				return false;
			}

			dropZone.ondragleave = function() {
				this.className = 'upload-drop-zone';
				return false;
			}

		});