$(function () {
    $('#fileupload').fileupload({
    	
    	dataType: 'json',
 
        done: function (e, data) {
            $("tr:has(td)").remove();
            $.each(data.result, function (index, file) {
            	var x = document.getElementById
                $("#uploaded-files").append(
                        $('<tr/>')
                        .append($('<td/>').text(file.fileName))
                        .append($('<td/>').text(file.fileSize))
                        .append($('<td/>').text(file.fileType))
                        .append($('<td/>').html("<a href='get/"+ index +"'>Click</a>"))
                        )//end $("#uploaded-files").append()
            });
        },
 
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress').css('width',progress + '%');
            $('#progress').html('<b>' + progress + '% Completed</b>');
            console.log(progress);
        },
 
        dropZone: $('#dropzone')
    });
});