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

	GCCSORT
	{
		@Override
		public int compare(Cluster c1, Cluster c2)
		{
			return c1.getGcContent() > c2.getGcContent() ? 1 : -1;
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
