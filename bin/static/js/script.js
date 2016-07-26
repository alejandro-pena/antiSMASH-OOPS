function toggleRangeDisabling(itemId, valueOutput) {
	var element = document.getElementById(itemId);
	if (element.disabled == true) {
		element.disabled = false;
		element.value = 10;
		if (itemId == 'selfHomology') {
			$('#shAlert').show();
			toggleDisabling('minimumMatch');
		}
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
	document.getElementById(valueOutput).value = element.value;
};

function toggleDisabling(itemId) {
	var element = document.getElementById(itemId);
	if (element.disabled == true) {
		element.disabled = false;
	} else {
		element.disabled = true;
	}
};

function toggleParameterOrdering(iconId) {
	var icon = document.getElementById(iconId);
	var orderValue = document.getElementById(iconId + "Value");
	if (icon.className == 'glyphicon glyphicon-sort-by-attributes-alt') {
		icon.className = 'glyphicon glyphicon-sort-by-attributes';
		orderValue.value = 'a';
	} else {
		icon.className = 'glyphicon glyphicon-sort-by-attributes-alt'
		orderValue.value = 'd';
	}
};

function updateRangeValue(rangeElement, itemId) {
	document.getElementById(itemId).value = rangeElement.value;
};

function prioritise() {

	var numberOfGenes = $('#numberOfGenes').val();
	var sequenceLength = $('#sequenceLength').val();
	var gcContent = $('#gcContent').val();
	var codonBias = $('#codonBias').val();
	var kcs = $('#knownClustersSimilarity').val();
	var sh = $('#selfHomology').val();
	var pd = $('#phylogeneticDiversity').val();

	var pSim = $('#similarityPercentage').val();
	var minM = $('#minimumMatch').val();

	var nogOrderValue = $('#nogOrderValue').val();
	var slOrderValue = $('#slOrderValue').val();
	var gccOrderValue = $('#gccOrderValue').val();
	var cbOrderValue = $('#cbOrderValue').val();
	var kcsOrderValue = $('#kcsOrderValue').val();
	var shOrderValue = $('#shOrderValue').val();
	var pdOrderValue = $('#pdOrderValue').val();

	var refSpecies = $('#selectSpecies').val();

	var ignorePT = $('#ignorePT').is(":checked");
	var types = $('#preferredType').val();

	url = '/dashboardUpdate?geneCount=' + numberOfGenes + '&sequenceLength='
			+ sequenceLength + '&gcContent=' + gcContent + '&codonBias='
			+ codonBias + '&kcs=' + kcs + '&sh=' + sh + '&pd=' + pd
			+ '&nogOrderValue=' + nogOrderValue + '&slOrderValue='
			+ slOrderValue + '&gccOrderValue=' + gccOrderValue
			+ '&cbOrderValue=' + cbOrderValue + '&kcsOrderValue='
			+ kcsOrderValue + '&shOrderValue=' + shOrderValue
			+ '&pdOrderValue=' + pdOrderValue + '&pSim=' + pSim + '&minM='
			+ minM + '&refSpecies=' + refSpecies + '&ignorePT=' + ignorePT
			+ '&types=' + types;

	$("#outputData")
			.html(
					"<center><button class='btn btn-lg btn-success'><span class='glyphicon glyphicon-refresh glyphicon-refresh-animate'></span> Prioritising... Please wait...</button></center>");

	$("#outputData").load(url);
}

function loadSpecies() {

	var keyword = $('#speciesName').val();

	if (keyword == '') {
		alert("The species name cannot be empty!")
		return;
	}

	keyword = keyword.trim();
	keyword = keyword.replace(/\s+/g, '+');

	url = '/species/' + keyword;
	$("#speciesDD").load(url);
}

function getSpecies() {

	var keyword = $('#speciesInput').val();

	if (keyword == '') {
		alert("The name cannot be empty!")
		return;
	}

	keyword = keyword.trim();
	keyword = keyword.replace(/\s+/g, '+');

	url = '/codonUsage/' + keyword;
	$("#resultsBlock").html(
			"<center><br /><h3>Loading species list...</h3><br /></center>");
	$("#resultsBlock").load(url);
}

function submitEnter(e) {
	if (e.keyCode == 13) {
		e.preventDefault();
		$("#speciesInput").blur();
		getSpecies();
	}
}