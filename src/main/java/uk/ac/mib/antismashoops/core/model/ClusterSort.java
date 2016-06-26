package uk.ac.mib.antismashoops.core.model;

import java.util.Comparator;

public enum ClusterSort implements Comparator<Cluster>
{
	NOGSORT
	{
		@Override
		public int compare(Cluster c1, Cluster c2)
		{
			return c1.getNumberOfGenes() > c2.getNumberOfGenes() ? 1 : -1;
		}
	},

	SLSORT
	{
		@Override
		public int compare(Cluster c1, Cluster c2)
		{
			return c1.getCodingSequenceLength() > c2.getCodingSequenceLength() ? 1 : -1;
		}
	},

	GCCSORT
	{
		@Override
		public int compare(Cluster c1, Cluster c2)
		{
			return c1.getGcContent() > c2.getGcContent() ? 1 : -1;
		}
	},

	GCCREFSORT
	{
		@Override
		public int compare(Cluster c1, Cluster c2)
		{
			return c1.getGcContentDiff() < c2.getGcContentDiff() ? 1 : -1;
		}
	},

	CBSORT
	{
		@Override
		public int compare(Cluster c1, Cluster c2)
		{
			return c1.getCuScoreRef() < c2.getCuScoreRef() ? 1 : -1;
		}
	},

	SCORESORT
	{
		@Override
		public int compare(Cluster c1, Cluster c2)
		{
			return c1.getScore() < c2.getScore() ? 1 : -1;
		}
	}

}
