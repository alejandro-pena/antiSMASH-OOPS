package uk.ac.mib.antismashoops.core.domainvalue;

import java.util.Comparator;

import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;

public enum ClusterSort implements Comparator<BiosyntheticGeneCluster> {
	NOGSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getNumberOfGenes() == c2.getNumberOfGenes())
				return 0;
			return c1.getNumberOfGenes() > c2.getNumberOfGenes() ? 1 : -1;
		}
	},

	SLSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCdsLength() == c2.getCdsLength())
				return 0;
			return c1.getCdsLength() > c2.getCdsLength() ? 1 : -1;
		}
	},

	GCCSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContent() == c2.getGcContent())
				return 0;
			return c1.getGcContent() > c2.getGcContent() ? 1 : -1;
		}
	},

	GCCREFSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContentS() == c2.getGcContentS())
				return 0;
			return c1.getGcContentS() < c2.getGcContentS() ? 1 : -1;
		}
	},

	CBSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCodonBiasS() == c2.getCodonBiasS())
				return 0;
			return c1.getCodonBiasS() < c2.getCodonBiasS() ? 1 : -1;
		}
	},

	KCSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getKcScore() == c2.getKcScore())
				return 0;
			return c1.getKcScore() < c2.getKcScore() ? 1 : -1;
		}
	},

	SHSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getSelfHomologyScore() == c2.getSelfHomologyScore())
				return 0;
			return c1.getSelfHomologyScore() > c2.getSelfHomologyScore() ? 1 : -1;
		}
	},

	PDSORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getPdScore() == c2.getPdScore())
				return 0;
			return c1.getPdScore() > c2.getPdScore() ? 1 : -1;
		}
	},

	NOGSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getNumberOfGenes() == c2.getNumberOfGenes())
				return 0;
			return c1.getNumberOfGenes() < c2.getNumberOfGenes() ? 1 : -1;
		}
	},

	SLSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCdsLength() == c2.getCdsLength())
				return 0;
			return c1.getCdsLength() < c2.getCdsLength() ? 1 : -1;
		}
	},

	GCCSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContent() == c2.getGcContent())
				return 0;
			return c1.getGcContent() < c2.getGcContent() ? 1 : -1;
		}
	},

	GCCREFSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getGcContentS() == c2.getGcContentS())
				return 0;
			return c1.getGcContentS() > c2.getGcContentS() ? 1 : -1;
		}
	},

	CBSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getCodonBiasS() == c2.getCodonBiasS())
				return 0;
			return c1.getCodonBiasS() > c2.getCodonBiasS() ? 1 : -1;
		}
	},

	KCSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getKcScore() == c2.getKcScore())
				return 0;
			return c1.getKcScore() > c2.getKcScore() ? 1 : -1;
		}
	},

	SHSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getSelfHomologyScore() == c2.getSelfHomologyScore())
				return 0;
			return c1.getSelfHomologyScore() < c2.getSelfHomologyScore() ? 1 : -1;
		}
	},

	PDSORTREV {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getPdScore() == c2.getPdScore())
				return 0;
			return c1.getPdScore() < c2.getPdScore() ? 1 : -1;
		}
	},

	SCORESORT {
		@Override
		public int compare(BiosyntheticGeneCluster c1, BiosyntheticGeneCluster c2) {
			if (c1.getScore() == c2.getScore())
				return 0;
			return c1.getScore() < c2.getScore() ? 1 : -1;
		}
	}

}
