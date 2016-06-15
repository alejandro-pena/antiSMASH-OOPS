package uk.ac.mib.antismashoops.core.model;

import java.util.LinkedHashMap;

public class CodonUsage
{
	private String species;
	private LinkedHashMap<String, Detail> usage = new LinkedHashMap<>();

	public CodonUsage()
	{
		// G BASED TRIPLETS
		usage.put("GGG", new Detail("Glycine", "Gly", "G"));
		usage.put("GGA", new Detail("Glycine", "Gly", "G"));
		usage.put("GGT", new Detail("Glycine", "Gly", "G"));
		usage.put("GGC", new Detail("Glycine", "Gly", "G"));

		usage.put("GAG", new Detail("Glutamic Acid", "Glu", "E"));
		usage.put("GAA", new Detail("Glutamic Acid", "Glu", "E"));
		usage.put("GAT", new Detail("Aspartic Acid", "Asp", "D"));
		usage.put("GAC", new Detail("Aspartic Acid", "Asp", "D"));

		usage.put("GTG", new Detail("Valine", "Val", "V"));
		usage.put("GTA", new Detail("Valine", "Val", "V"));
		usage.put("GTT", new Detail("Valine", "Val", "V"));
		usage.put("GTC", new Detail("Valine", "Val", "V"));

		usage.put("GCG", new Detail("Alanine", "Ala", "A"));
		usage.put("GCA", new Detail("Alanine", "Ala", "A"));
		usage.put("GCT", new Detail("Alanine", "Ala", "A"));
		usage.put("GCC", new Detail("Alanine", "Ala", "A"));

		// A BASED TRIPLETS
		usage.put("AGG", new Detail("Arginine", "Arg", "R"));
		usage.put("AGA", new Detail("Arginine", "Arg", "R"));
		usage.put("AGT", new Detail("Serine", "Ser", "S"));
		usage.put("AGC", new Detail("Serine", "Ser", "S"));

		usage.put("AAG", new Detail("Lysine", "Lys", "K"));
		usage.put("AAA", new Detail("Lysine", "Lys", "K"));
		usage.put("AAT", new Detail("Asparagine", "Asn", "N"));
		usage.put("AAC", new Detail("Asparagine", "Asn", "N"));

		usage.put("ATG", new Detail("Methionine", "Met", "M"));
		usage.put("ATA", new Detail("Isoleucine", "Ile", "I"));
		usage.put("ATT", new Detail("Isoleucine", "Ile", "I"));
		usage.put("ATC", new Detail("Isoleucine", "Ile", "I"));

		usage.put("ACG", new Detail("Threonine", "Thr", "T"));
		usage.put("ACA", new Detail("Threonine", "Thr", "T"));
		usage.put("ACT", new Detail("Threonine", "Thr", "T"));
		usage.put("ACC", new Detail("Threonine", "Thr", "T"));

		// T BASED TRIPLETS
		usage.put("TGG", new Detail("Tryptophan", "Trp", "W"));
		usage.put("TGA", new Detail("Stop", "Stp", "*"));
		usage.put("TGT", new Detail("Cysteine", "Cys", "C"));
		usage.put("TGC", new Detail("Cysteine", "Cys", "C"));

		usage.put("TAG", new Detail("Stop", "Stp", "*"));
		usage.put("TAA", new Detail("Stop", "Stp", "*"));
		usage.put("TAT", new Detail("Tyrosine", "Tyr", "Y"));
		usage.put("TAC", new Detail("Tyrosine", "Tyr", "Y"));

		usage.put("TTG", new Detail("Leucine", "Leu", "L"));
		usage.put("TTA", new Detail("Leucine", "Leu", "L"));
		usage.put("TTT", new Detail("Phenylalanine", "Phe", "F"));
		usage.put("TTC", new Detail("Phenylalanine", "Phe", "F"));

		usage.put("TCG", new Detail("Serine", "Ser", "S"));
		usage.put("TCA", new Detail("Serine", "Ser", "S"));
		usage.put("TCT", new Detail("Serine", "Ser", "S"));
		usage.put("TCC", new Detail("Serine", "Ser", "S"));

		// C BASED TRIPLETS
		usage.put("CGG", new Detail("Arginine", "Arg", "R"));
		usage.put("CGA", new Detail("Arginine", "Arg", "R"));
		usage.put("CGT", new Detail("Arginine", "Arg", "R"));
		usage.put("CGC", new Detail("Arginine", "Arg", "R"));

		usage.put("CAG", new Detail("Glutamine", "Gln", "Q"));
		usage.put("CAA", new Detail("Glutamine", "Gln", "Q"));
		usage.put("CAT", new Detail("Histidine", "His", "H"));
		usage.put("CAC", new Detail("Histidine", "His", "H"));

		usage.put("CTG", new Detail("Leucine", "Leu", "L"));
		usage.put("CTA", new Detail("Leucine", "Leu", "L"));
		usage.put("CTT", new Detail("Leucine", "Leu", "L"));
		usage.put("CTC", new Detail("Leucine", "Leu", "L"));

		usage.put("CCG", new Detail("Proline", "Pro", "P"));
		usage.put("CCA", new Detail("Proline", "Pro", "P"));
		usage.put("CCT", new Detail("Proline", "Pro", "P"));
		usage.put("CCC", new Detail("Proline", "Pro", "P"));
	}

	public CodonUsage(String species)
	{
		this.species = species;
	}

	public String getSpecies()
	{
		return species;
	}

	public void setSpecies(String species)
	{
		this.species = species;
	}

	public LinkedHashMap<String, Detail> getUsage()
	{
		return usage;
	}

	public void setUsage(LinkedHashMap<String, Detail> usage)
	{
		this.usage = usage;
	}

	public class Detail
	{
		private String aminoacid;
		private String abbr;
		private String letterAbbr;
		private int codonNumber;
		private double frequency;

		public Detail()
		{

		}

		public Detail(String aminoacid, String abbr, String letterAbbr)
		{
			this.aminoacid = aminoacid;
			this.abbr = abbr;
			this.letterAbbr = letterAbbr;
		}

		public Detail(String aminoacid, String abbr, String letterAbbr, int codonNumber, double frequency)
		{
			this.aminoacid = aminoacid;
			this.abbr = abbr;
			this.letterAbbr = letterAbbr;
			this.frequency = frequency;
			this.codonNumber = codonNumber;
		}

		public String getAminoacid()
		{
			return aminoacid;
		}

		public void setAminoacid(String aminoacid)
		{
			this.aminoacid = aminoacid;
		}

		public String getAbbr()
		{
			return abbr;
		}

		public void setAbbr(String abbr)
		{
			this.abbr = abbr;
		}

		public String getLetterAbbr()
		{
			return letterAbbr;
		}

		public void setLetterAbbr(String letterAbbr)
		{
			this.letterAbbr = letterAbbr;
		}

		public double getFrequency()
		{
			return frequency;
		}

		public void setFrequency(double frequency)
		{
			this.frequency = frequency;
		}

		public int getCodonNumber()
		{
			return codonNumber;
		}

		public void setCodonNumber(int codonNumber)
		{
			this.codonNumber = codonNumber;
		}

		@Override
		public String toString()
		{
			return "Detail [aminoacid=" + aminoacid + ", abbr=" + abbr + ", letterAbbr=" + letterAbbr + ", codonNumber="
					+ codonNumber + ", frequency=" + frequency + "]";
		}
	}

	@Override
	public String toString()
	{
		return "CodonUsage [species=" + species + ", usage=" + usage + "]";
	}
}
