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
	var gcContent = $('#gcContent').val();
	var codonBias = $('#codonBias').val();

	url = '/dashboardUpdate?geneCount=' + numberOfGenes + '&gcContent='
			+ gcContent + '&codonBias=' + codonBias;
	$("#outputData")
			.html(
					"<center><br /><h3>Prioritising... Please wait...</h3><br /></center>");
	$("#outputData").load(url);
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