function toggleRangeDisabling(itemId, valueOutput) {
	var element = document.getElementById(itemId);
	if (element.disabled == true) {
		element.disabled = false;
		element.value = 10;

	} else {
		element.disabled = true;
		element.value = 0;
	}
	document.getElementById(valueOutput).value = element.value;
};

function updateRangeValue(rangeElement, itemId) {
	document.getElementById(itemId).value = rangeElement.value;
};

function prioritise() {

	var numberOfGenes = $('#numberOfGenes').val();
	var sequenceLength = $('#sequenceLength').val();
	var gcContent = $('#gcContent').val();
	var codonBias = $('#codonBias').val();
	var refSpecies = $('#selectSpecies').val();

	url = '/dashboardUpdate?geneCount=' + numberOfGenes + '&sequenceLength='
			+ sequenceLength + '&gcContent=' + gcContent + '&codonBias=' + codonBias + '&refSpecies=' + refSpecies;
	$("#outputData")
			.html(
					"<center><br /><h3>Prioritising... Please wait...</h3><br /></center>");
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