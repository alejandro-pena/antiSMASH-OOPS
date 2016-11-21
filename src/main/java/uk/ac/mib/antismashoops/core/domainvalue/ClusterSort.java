package uk.ac.mib.antismashoops.core.domainvalue;

import java.util.Comparator;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;

public enum ClusterSort implements Comparator<BiosyntheticGeneCluster> {

	/**
	 * 
	 * Compares two BGCs according to the number of genes in descending order.
	 * 
	 */
	NOGSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getNumberOfGenes() == c2.getNumberOfGenes())
				return 0;
			return c1.getNumberOfGenes() > c2.getNumberOfGenes() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the CDS Length in descending order.
	 * 
	 */
	SLSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCdsLength() == c2.getCdsLength())
				return 0;
			return c1.getCdsLength() > c2.getCdsLength() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the GC Content in descending order.
	 * 
	 */
	GCCSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContent() == c2.getGcContent())
				return 0;
			return c1.getGcContent() > c2.getGcContent() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the GC Content compared to a reference
	 * species in ascending order.
	 * 
	 */
	GCCREFSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContentS() == c2.getGcContentS())
				return 0;
			return c1.getGcContentS() < c2.getGcContentS() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the codon bias score in ascending order.
	 * 
	 */
	CBSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCodonBiasS() == c2.getCodonBiasS())
				return 0;
			return c1.getCodonBiasS() < c2.getCodonBiasS() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the known cluster similarity score in
	 * ascending order.
	 * 
	 */
	KCSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getKcScore() == c2.getKcScore())
				return 0;
			return c1.getKcScore() < c2.getKcScore() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the self homology score in descending
	 * order.
	 * 
	 */
	SHSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getSelfHomologyScore() == c2.getSelfHomologyScore())
				return 0;
			return c1.getSelfHomologyScore() > c2.getSelfHomologyScore() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the phylogenetic diversity score in
	 * descending order.
	 * 
	 */
	PDSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getDiversityScore() == c2.getDiversityScore())
				return 0;
			return c1.getDiversityScore() > c2.getDiversityScore() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the number of genes in ascending order.
	 * 
	 */
	NOGSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getNumberOfGenes() == c2.getNumberOfGenes())
				return 0;
			return c1.getNumberOfGenes() < c2.getNumberOfGenes() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the CDS Length in ascending order.
	 * 
	 */
	SLSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCdsLength() == c2.getCdsLength())
				return 0;
			return c1.getCdsLength() < c2.getCdsLength() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the GC Content in ascending order.
	 * 
	 */
	GCCSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContent() == c2.getGcContent())
				return 0;
			return c1.getGcContent() < c2.getGcContent() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the GC Content compared to a reference
	 * species in descending order.
	 * 
	 */
	GCCREFSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContentS() == c2.getGcContentS())
				return 0;
			return c1.getGcContentS() > c2.getGcContentS() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the codon bias score in descending order.
	 * 
	 */
	CBSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCodonBiasS() == c2.getCodonBiasS())
				return 0;
			return c1.getCodonBiasS() > c2.getCodonBiasS() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the known cluster similarity score in
	 * descending order.
	 * 
	 */
	KCSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getKcScore() == c2.getKcScore())
				return 0;
			return c1.getKcScore() > c2.getKcScore() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the self homology score in ascending
	 * order.
	 * 
	 */
	SHSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getSelfHomologyScore() == c2.getSelfHomologyScore())
				return 0;
			return c1.getSelfHomologyScore() < c2.getSelfHomologyScore() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the phylogenetic diversity score in
	 * ascending order.
	 * 
	 */
	PDSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getDiversityScore() == c2.getDiversityScore())
				return 0;
			return c1.getDiversityScore() < c2.getDiversityScore() ? 1 : -1;
		}
	},

	/**
	 * 
	 * Compares two BGCs according to the final score in descending order.
	 * 
	 */
	SCORESORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getScore() == c2.getScore())
				return 0;
			return c1.getScore() < c2.getScore() ? 1 : -1;
		}
	}

}
