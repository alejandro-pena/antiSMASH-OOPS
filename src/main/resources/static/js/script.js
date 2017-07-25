/* --------------------------------------------------------------------------------------------- */
/* 								FUNCTIONS FOR THE INDEX VIEW 									 */
/* --------------------------------------------------------------------------------------------- */

function addNewWs() {

    var wsName = prompt("Enter the new Workspace name...", "Workspace name...");

    if (wsName === null || wsName.trim() === "") {
        alert("Please enter a valid Workspace name...");
        return;
    }

    wsName = wsName.trim().split(" ").join("_");

    var url = "/addWorkspace/" + wsName;

    $.ajax({
        type: "GET",
        url: url
    }).done(function () {
        location.reload();
    });
}

function deleteWs() {

    var wsName = $("input[name='wsRadio']:checked").val();
    if (wsName) {
        var flag = confirm("Delete the workspace: " + wsName + " ?");
        if (!flag) {
            return;
        }
        var url = "/deleteWorkspace/" + wsName;
        $.ajax({
            type: "GET",
            url: url
        }).done(function () {
            location.reload();
        });

    } else {
        alert("Please select a Workspace to delete.");
    }
}

function continueToFU() {
    var wsName = $("input[name='wsRadio']:checked").val();

    if (wsName) {

        $('#ws').submit();

    } else {
        alert("Please select a Workspace or create a new one.");
    }
}

/* --------------------------------------------------------------------------------------------- */
/* FUNCTIONS FOR THE FILE UPLOAD VIEW */
/* --------------------------------------------------------------------------------------------- */

/**
 * Handles the zip file load functionality and prints back in a table the loaded
 * files. Updates the progress bar according to the load process progress.
 */

$(function () {
    $('#fileupload')
        .fileupload(
            {
                stop: function () {
                    console.log("I finished...");
                    location.reload();
                },

                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total
                        * 100, 10);
                    $('#progress').css('width', progress + '%');
                    $('#progress').html(
                        '<b>' + progress + '% Completed</b>');
                },

                dropZone: $('#dropzone')
            });
});

/**
 * Changes the 'Go to Priorisation Dashboard' button text to a loading message
 * for the user. Includes an animated refresh image to indicate that the
 * application is busy working.
 */

function loading() {
    var buttonText = '<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate">'
        + '</span> Decompressing files and processing data... Please wait...';
    $('#submitBtn').html(buttonText);
}

/* --------------------------------------------------------------------------------------------- */
/* FUNCTIONS FOR THE PRIORITISATION VIEW */
/* --------------------------------------------------------------------------------------------- */

/**
 * This code is executed as soon as the page loads. It enables the Tooltips in
 * the UI. If no tooltips exist nothing it will have no effect.
 */

$(document).ready(function () {
    if ($('[data-toggle="tooltip"]').length) {
        $('[data-toggle="tooltip"]').tooltip();
    }
});

/**
 * This function encapsulates the logic for disabling and enabling the view
 * elements when the Ignore checkbox of each parameter is clicked
 */

function toggleRangeDisabling(itemId, valueOutput) {
    var element = document.getElementById(itemId);
    if (element.disabled == true) {
        element.disabled = false;
        element.value = 10;
        if (itemId == 'selfHomology') {
            $('#shAlert').show();
            toggleDisabling('minimumMatch');
        }
        if (itemId == 'phylogeneticDiversity')
            $('#pdAlert').show();
        if (itemId == 'knownClustersSimilarity')
            toggleDisabling('similarityPercentage');
    } else {
        element.disabled = true;
        element.value = 0;
        if (itemId == 'selfHomology')
            toggleDisabling('minimumMatch');
        if (itemId == 'knownClustersSimilarity')
            toggleDisabling('similarityPercentage');
    }
    $('#' + valueOutput).html(element.value);
};

/**
 * Receives the id of any element in the web page and disables or enables said
 * element depending on its previous state.
 */

function toggleDisabling(itemId) {
    $('#' + itemId).prop('disabled', function (i, value) {
        return !value;
    });
};

/**
 * Toggles the class and order value for the specified icon to change the image
 * from ascending to descending and vice versa.
 */

function toggleParameterOrdering(icon) {

    var iconId = '#' + icon;
    var orderValId = '#' + icon + 'Value';

    if ($(orderValId).attr('value') == 'd') {
        $(iconId).attr('class', 'glyphicon glyphicon-sort-by-attributes');
        $(orderValId).attr('value', 'a');
    } else {
        $(iconId).attr('class', 'glyphicon glyphicon-sort-by-attributes-alt');
        $(orderValId).attr('value', 'd');
    }

};

/**
 * Synchronises the Slider Value with its Output Value
 */

function updateRangeValue(rangeElement, itemId) {
    $('#' + itemId).html(rangeElement.value);
};

/**
 * Sends the AJAX call to the Dashboard Controller with all the selected
 * parameters to perform the prioritisation.
 */

function prioritise() {

    // NUMBER OF GENES

    var numberOfGenes = $('#numberOfGenes').val();
    var nogOrderValue = $('#nogOrderValue').val();

    // CDS LENGTH

    var sequenceLength = $('#sequenceLength').val();
    var slOrderValue = $('#slOrderValue').val();

    // GC CONTENT

    var gcContent = $('#gcContent').val();
    var gccOrderValue = $('#gccOrderValue').val();

    // CODON BIAS

    var codonBias = $('#codonBias').val();
    var cbOrderValue = $('#cbOrderValue').val();

    // REFERENCE SPECIES

    var refSpecies = $('#selectSpecies').val();
    if (refSpecies === 'No data found.' || refSpecies === undefined)
        refSpecies = 'undefined';

    // CLUSTER TYPE

    var types = $('#preferredType').val();

    var ignorePT = $('#ignorePT').is(":checked");

    // KNOWN CLUSTER SIMILARITY

    var kcSim = $('#knownClustersSimilarity').val();
    var pSim = $('#similarityPercentage').val();
    var kcsOrderValue = $('#kcsOrderValue').val();

    // SELF-HOMOLOGY

    var sHom = $('#selfHomology').val();
    var minM = $('#minimumMatch').val();
    if (minM === "") {
        minM = 0;
    }
    var shOrderValue = $('#shOrderValue').val();

    // PHYLOGENETIC DIVERSITY

    var pDiv = $('#phylogeneticDiversity').val();
    var pdOrderValue = $('#pdOrderValue').val();

    // BUILDS THE URL FOR THE AJAX CALL

    var data = {
        nog: numberOfGenes,
        nogo: nogOrderValue,
        sl: sequenceLength,
        slo: slOrderValue,
        gcc: gcContent,
        gcco: gccOrderValue,
        cb: codonBias,
        cbo: cbOrderValue,
        rs: refSpecies,
        t: types,
        ipt: ignorePT,
        kcs: kcSim,
        psim: pSim,
        kcso: kcsOrderValue,
        sh: sHom,
        minm: minM,
        sho: shOrderValue,
        pd: pDiv,
        pdo: pdOrderValue
    };

    var url = '/dashboardUpdate';

    // CHANGES THE PRIORITISE BUTTON INTO AN ANIMATED REFRESH ICON

    var buttonText = '<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate">'
        + '</span> Prioritising... Please wait...';

    $('#prioritiseBtn').html(buttonText);
    $("#outputData").html("");

    // LOADS THE PRIORITISED DATA INTO THE OUTPUT SECTION

    $("#outputData").load(url, data, function (response, status, xhr) {
        if (xhr.status != '200') {
            var message = 'The Application cannot fulfill your request.';
            $('#gaMessage').html(message);
            $('#genericAlert').show();
        }
    });
}

/**
 * Returns the prioritise button to its original state after the AJAX call is
 * completed and removes the spinner in case the AJAX call came from the species
 * look up.
 */

$(document).ajaxComplete(function () {
    $('#prioritiseBtn').html('PRIORITISE');
    $('#sSpinner').addClass('hidden');
});

/**
 * Creates an AJAX request to load the species list according to the species
 * specified in the text box.
 */

function loadSpecies() {

    var keyword = $('#speciesName').val();
    if (keyword == '') {
        $('#speciesName').addClass('error-field');
        return;
    }
    $('#sSpinner').removeClass('hidden');
    $('#speciesName').removeClass('error-field');
    keyword = keyword.trim().replace(/\s+/g, '+');
    url = '/species/' + keyword;
    $("#speciesDD")
        .load(
            url,
            function (response, status, xhr) {
                if (xhr.status != '200') {
                    var message = 'The Application cannot fulfil your request. The Kazusa Website is not available or you are not connected to the Internet.';
                    $('#gaMessage').html(message);
                    $('#genericAlert').show();
                }
            });
}

/* --------------------------------------------------------------------------------------------- */
/* FUNCTIONS FOR THE CODON USAGE */
/* --------------------------------------------------------------------------------------------- */

/**
 * Calls with AJAX the CodonUsageController to get the species specified in the
 * input box.
 */

function getSpecies() {

    var keyword = $('#speciesInput').val();
    if (keyword == '') {
        $('#speciesInput').addClass('error-field');
        return;
    }
    $('#speciesInput').removeClass('error-field');
    keyword = keyword.trim().replace(/\s+/g, '+');
    url = '/codonUsage/' + keyword;

    // CHANGES THE SEARCH BUTTON INTO AN ANIMATED REFRESH ICON FOR LOADING

    $("#resultsBlock")
        .html(
            '<center><h3><span class="glyphicon glyphicon-refresh glyphicon-refresh-animate">'
            + '</span> Loading species... Please wait....</h3></center>');

    $("#resultsBlock")
        .load(
            url,
            function (response, status, xhr) {
                if (xhr.status != '200') {
                    var message = 'The Application cannot fulfil your request. The Kazusa Website is not available or you are not connected to the Internet.';
                    $('#gaMessage').html(message);
                    $('#genericAlert').show();
                }
            });
}

/**
 * Calls the getSpecies() function when the enter key is pressed in the Codon
 * Usage view to look for a custom species.
 */

function submitEnter(e) {
    if (e.keyCode == 13) {
        e.preventDefault();
        $("#speciesInput").blur();
        getSpecies();
    }
}

/* --------------------------------------------------------------------------------------------- */
/* FUNCTIONS FOR THE FEEDBACK VIEW */
/* --------------------------------------------------------------------------------------------- */

function validateAndSend() {

    if ($('#name').val().length == 0) {
        alert("Please enter a name...")
        return;
    }
    if (!isEmail($('#email').val())) {
        alert("Please enter a valid email address...")
        return;
    }
    if ($('#subject').val().length == 0) {
        alert("Please enter a subject...")
        return;
    }
    if ($('#body').val().length == 0) {
        alert("Please enter some feedback...")
        return;
    }

    $('#feedbackForm').submit();
}

function isEmail(email) {
    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    return regex.test(email);
}