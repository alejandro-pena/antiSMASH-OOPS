package uk.ac.mib.antismashoops.core.domainobject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class CodonUsageTable {
	private static final Logger logger = LoggerFactory.getLogger(CodonUsageTable.class);
	private final LinkedHashMap<String, Detail> usage = new LinkedHashMap<>();
    private String species;

	/**
	 * 
	 * Class constructor. Creates a Detail stub for all the codons and
	 * respective amino acids
	 * 
	 */

	public CodonUsageTable() {
		// ALANINE
		usage.put("GCG", new Detail("Alanine", "Ala", "A"));
		usage.put("GCA", new Detail("Alanine", "Ala", "A"));
		usage.put("GCT", new Detail("Alanine", "Ala", "A"));
		usage.put("GCC", new Detail("Alanine", "Ala", "A"));

		// ARGININE
		usage.put("AGG", new Detail("Arginine", "Arg", "R"));
		usage.put("AGA", new Detail("Arginine", "Arg", "R"));
		usage.put("CGG", new Detail("Arginine", "Arg", "R"));
		usage.put("CGA", new Detail("Arginine", "Arg", "R"));
		usage.put("CGT", new Detail("Arginine", "Arg", "R"));
		usage.put("CGC", new Detail("Arginine", "Arg", "R"));

		// ASPARAGINE
		usage.put("AAT", new Detail("Asparagine", "Asn", "N"));
		usage.put("AAC", new Detail("Asparagine", "Asn", "N"));

		// ASPARTIC ACID
		usage.put("GAT", new Detail("Aspartic Acid", "Asp", "D"));
		usage.put("GAC", new Detail("Aspartic Acid", "Asp", "D"));

		// CYSTEINE
		usage.put("TGT", new Detail("Cysteine", "Cys", "C"));
		usage.put("TGC", new Detail("Cysteine", "Cys", "C"));

		// GLUTAMIC ACID
		usage.put("GAG", new Detail("Glutamic Acid", "Glu", "E"));
		usage.put("GAA", new Detail("Glutamic Acid", "Glu", "E"));

		// GLUTAMINE
		usage.put("CAG", new Detail("Glutamine", "Gln", "Q"));
		usage.put("CAA", new Detail("Glutamine", "Gln", "Q"));

		// GLYCINE
		usage.put("GGG", new Detail("Glycine", "Gly", "G"));
		usage.put("GGA", new Detail("Glycine", "Gly", "G"));
		usage.put("GGT", new Detail("Glycine", "Gly", "G"));
		usage.put("GGC", new Detail("Glycine", "Gly", "G"));

		// HISTIDINE
		usage.put("CAT", new Detail("Histidine", "His", "H"));
		usage.put("CAC", new Detail("Histidine", "His", "H"));

		// ISOLEUCINE
		usage.put("ATA", new Detail("Isoleucine", "Ile", "I"));
		usage.put("ATT", new Detail("Isoleucine", "Ile", "I"));
		usage.put("ATC", new Detail("Isoleucine", "Ile", "I"));

		// LEUCINE
		usage.put("CTG", new Detail("Leucine", "Leu", "L"));
		usage.put("CTA", new Detail("Leucine", "Leu", "L"));
		usage.put("CTT", new Detail("Leucine", "Leu", "L"));
		usage.put("CTC", new Detail("Leucine", "Leu", "L"));
		usage.put("TTG", new Detail("Leucine", "Leu", "L"));
		usage.put("TTA", new Detail("Leucine", "Leu", "L"));

		// LYSINE
		usage.put("AAG", new Detail("Lysine", "Lys", "K"));
		usage.put("AAA", new Detail("Lysine", "Lys", "K"));

		// METHIONINE
		usage.put("ATG", new Detail("Methionine", "Met", "M"));

		// PHENYLALANINE
		usage.put("TTT", new Detail("Phenylalanine", "Phe", "F"));
		usage.put("TTC", new Detail("Phenylalanine", "Phe", "F"));

		// PROLINE
		usage.put("CCG", new Detail("Proline", "Pro", "P"));
		usage.put("CCA", new Detail("Proline", "Pro", "P"));
		usage.put("CCT", new Detail("Proline", "Pro", "P"));
		usage.put("CCC", new Detail("Proline", "Pro", "P"));

		// SERINE
		usage.put("AGT", new Detail("Serine", "Ser", "S"));
		usage.put("AGC", new Detail("Serine", "Ser", "S"));
		usage.put("TCG", new Detail("Serine", "Ser", "S"));
		usage.put("TCA", new Detail("Serine", "Ser", "S"));
		usage.put("TCT", new Detail("Serine", "Ser", "S"));
		usage.put("TCC", new Detail("Serine", "Ser", "S"));

		// STOP
		usage.put("TGA", new Detail("Stop", "Stp", "*"));
		usage.put("TAG", new Detail("Stop", "Stp", "*"));
		usage.put("TAA", new Detail("Stop", "Stp", "*"));

		// THREONINE
		usage.put("ACG", new Detail("Threonine", "Thr", "T"));
		usage.put("ACA", new Detail("Threonine", "Thr", "T"));
		usage.put("ACT", new Detail("Threonine", "Thr", "T"));
		usage.put("ACC", new Detail("Threonine", "Thr", "T"));

		// TRYPTOPHAN
		usage.put("TGG", new Detail("Tryptophan", "Trp", "W"));

		// TYROSINE
		usage.put("TAT", new Detail("Tyrosine", "Tyr", "Y"));
		usage.put("TAC", new Detail("Tyrosine", "Tyr", "Y"));

		// VALINE
		usage.put("GTG", new Detail("Valine", "Val", "V"));
		usage.put("GTA", new Detail("Valine", "Val", "V"));
		usage.put("GTT", new Detail("Valine", "Val", "V"));
		usage.put("GTC", new Detail("Valine", "Val", "V"));
	}

	public CodonUsageTable(String species) {
		this.species = species;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public LinkedHashMap<String, Detail> getUsage() {
		return usage;
	}

	/**
	 * 
	 * Sets the codon frequency data of the codon usage table.
	 * 
	 * @param cdsLength The length of the associated sequence
	 * 
	 */

	public void setCodonFrequencies(int cdsLength) {
		for (Entry<String, Detail> codon : this.usage.entrySet()) {
			Detail d = codon.getValue();
			d.setFrequency((d.getCodonNumber() * 1000.0) / cdsLength);
		}
	}

	/**
	 * 
	 * Calculates and sets in the Detail object the usage percentage per amino
	 * acid
	 * 
	 */

	public void setUsagePerAminoacids() {

		Map<String, Integer> map = new HashMap<>();

		map.put("Alanine", 0);
		map.put("Arginine", 0);
		map.put("Asparagine", 0);
		map.put("Aspartic Acid", 0);
		map.put("Cysteine", 0);
		map.put("Glutamic Acid", 0);
		map.put("Glutamine", 0);
		map.put("Glycine", 0);
		map.put("Histidine", 0);
		map.put("Isoleucine", 0);
		map.put("Leucine", 0);
		map.put("Lysine", 0);
		map.put("Methionine", 0);
		map.put("Phenylalanine", 0);
		map.put("Proline", 0);
		map.put("Serine", 0);
		map.put("Stop", 0);
		map.put("Threonine", 0);
		map.put("Tryptophan", 0);
		map.put("Tyrosine", 0);
		map.put("Valine", 0);

		for (Entry<String, Detail> codon : this.usage.entrySet()) {
			Detail d = codon.getValue();
			map.put(d.getAminoacid(), map.get(d.getAminoacid()) + d.getCodonNumber());
		}

		for (Entry<String, Detail> codon : this.usage.entrySet()) {
			Detail d = codon.getValue();
			d.setAminoacidUsage(d.getCodonNumber() * 100.0 / map.get(d.getAminoacid()));
		}
	}

	@Override
	public String toString() {
		return "CodonUsage [species=" + species + ", usage=" + usage + "]";
	}


    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest req, Exception exception)
    {
        req.setAttribute("message", exception.getClass() + " - " + exception.getMessage());
        logger.error("Exception thrown: " + exception.getClass());
        logger.error("Exception message: " + exception.getMessage());
        exception.printStackTrace();
        return "error";
    }

	public class Detail implements Comparable<Detail> {
		private String aminoacid;
		private String abbr;
		private String letterAbbr;
		private int codonNumber = 0;
		private double frequency = 0.0;
		private double aminoacidUsage = 0.0;

		/**
         *
         * Class constructor.
         *
         * @param aminoacid The amino acid full name.
		 * @param abbr The amino acid abbreviation (3 letters)
		 * @param letterAbbr The amino acid single letter abbreviation
         *
         */

		public Detail(String aminoacid, String abbr, String letterAbbr) {
			this.aminoacid = aminoacid;
			this.abbr = abbr;
			this.letterAbbr = letterAbbr;
		}

		/**
         *
         * Class constructor.
         *
         * @param aminoacid The amino acid full name.
		 * @param abbr The amino acid abbreviation (3 letters)
		 * @param letterAbbr The amino acid single letter abbreviation
		 * @param codonNumber Number of this codon in the sequence
		 * @param frequency of this codon /1000 in the sequence
         *
         */

		public Detail(String aminoacid, String abbr, String letterAbbr, int codonNumber, double frequency) {
			this.aminoacid = aminoacid;
			this.abbr = abbr;
			this.letterAbbr = letterAbbr;
			this.codonNumber = codonNumber;
			this.frequency = frequency;
		}

		public String getAminoacid() {
			return aminoacid;
		}

		public void setAminoacid(String aminoacid) {
			this.aminoacid = aminoacid;
		}

		public String getAbbr() {
			return abbr;
		}

		public void setAbbr(String abbr) {
			this.abbr = abbr;
		}

		public String getLetterAbbr() {
			return letterAbbr;
		}

		public void setLetterAbbr(String letterAbbr) {
			this.letterAbbr = letterAbbr;
		}

		public int getCodonNumber() {
			return codonNumber;
		}

		public void setCodonNumber(int codonNumber) {
			this.codonNumber = codonNumber;
		}

		public double getFrequency() {
			return frequency;
		}

		public void setFrequency(double frequency) {
			this.frequency = frequency;
		}

		public double getAminoacidUsage() {
			return aminoacidUsage;
		}

		public void setAminoacidUsage(double aminoacidUsage) {
			if (Double.isNaN(aminoacidUsage))
				this.aminoacidUsage = 0.0;
			else
				this.aminoacidUsage = aminoacidUsage;
		}


		@Override
		public int compareTo(Detail d) {
			if (d == null)
				return 0;
			if (d == this)
				return 0;
			return this.aminoacid.compareTo(d.getAminoacid());
        }


        @Override
        public String toString()
        {
            return "Detail [aminoacid=" + aminoacid + ", abbr=" + abbr + ", letterAbbr=" + letterAbbr + ", codonNumber="
                + codonNumber + ", frequency=" + frequency + "]";
        }


    }
}
