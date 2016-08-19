package uk.ac.mib.antismashoops.core.domainobject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.ac.mib.antismashoops.core.domainobject.CodonUsage.Detail;

@Component
public class BiosyntheticGeneCluster
{
	private File file;
	private String name;
	private String parent;
	private String recordName;
	private String clusterNumber;
	private String clusterType;
	private int basePairs;
	private List<Gene> genes;
	private int numberOfGenes;
	private double gcContent;
	private double gcContentDiff = -1.0;
	private String clusterSequence;
	private CodonUsage codonUsage;
	private double cuScoreRef = -1.0;
	private double kcScore = 0.0;
	private int selfHomologyScore = -1;
	private Map<Integer, Integer> selfHomologyScores = new HashMap<>();
	private int pdScore = -1;
	private double score = 0.0;

	private static final Logger logger = LoggerFactory.getLogger(BiosyntheticGeneCluster.class);

	public BiosyntheticGeneCluster()
	{

	}

	public BiosyntheticGeneCluster(File file)
	{
		this.file = file;
		this.name = removeExtension(file.getName());
		this.parent = file.getParentFile().getName();
		String[] qualifiedName = this.name.split(".c");
		this.recordName = qualifiedName[0];
		Integer clusterNo = Integer.parseInt(qualifiedName[1].replaceAll("\\D+", ""));
		this.clusterNumber = clusterNo.toString();
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getParent()
	{
		return parent;
	}

	public void setParent(String parent)
	{
		this.parent = parent;
	}

	public String getRecordName()
	{
		return recordName;
	}

	public void setRecordName(String recordName)
	{
		this.recordName = recordName;
	}

	public String getClusterNumber()
	{
		return clusterNumber;
	}

	public void setClusterNumber(String clusterNumber)
	{
		this.clusterNumber = clusterNumber;
	}

	public String getClusterType()
	{
		return clusterType;
	}

	public void setClusterType(String clusterType)
	{
		this.clusterType = clusterType;
	}

	public int getBasePairs()
	{
		return basePairs;
	}

	public void setBasePairs(int basePairs)
	{
		this.basePairs = basePairs;
	}

	public List<Gene> getGenes()
	{
		return genes;
	}

	public void setGenes(List<Gene> genes)
	{
		this.genes = genes;
	}

	public int getNumberOfGenes()
	{
		return numberOfGenes;
	}

	public void setNumberOfGenes(int numberOfGenes)
	{
		this.numberOfGenes = numberOfGenes;
	}

	public double getGcContent()
	{
		return gcContent;
	}

	public void setGcContent(double gcContent)
	{
		this.gcContent = gcContent;
	}

	public String getClusterSequence()
	{
		return clusterSequence;
	}

	public void setClusterSequence(String clusterSequence)
	{
		this.clusterSequence = clusterSequence;
	}

	@Override
	public String toString()
	{
		return "Cluster [Cluster=" + recordName + clusterNumber + ", kcScore=" + kcScore + "]";
	}

	private static String removeExtension(String name)
	{
		return name.substring(0, name.lastIndexOf('.'));
	}

	public CodonUsage getCodonUsage()
	{
		return codonUsage;
	}

	public void setCodonUsage(CodonUsage codonUsage)
	{
		this.codonUsage = codonUsage;
	}

	public double getScore()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	public double getGcContentDiff()
	{
		return gcContentDiff;
	}

	public void setGcContentDiff(double gcContentRef)
	{
		this.gcContentDiff = Math.abs(this.gcContent - gcContentRef);
	}

	public double getCuScoreRef()
	{
		return cuScoreRef;
	}

	public void setCuScoreRef(CodonUsage cuRef)
	{
		double score = 0.0;
		this.computeCodonUsage();

		for (Entry<String, Detail> cdn : codonUsage.getUsage().entrySet())
		{
			Detail dCluster = cdn.getValue();
			Detail dRef = cuRef.getUsage().get(cdn.getKey());
			score += Math.abs(dCluster.getScorePerAminoacid() - dRef.getScorePerAminoacid());
		}

		this.cuScoreRef = score;
	}

	public double getKcScore()
	{
		return kcScore;
	}

	public void setKcScore(double kcScore)
	{
		this.kcScore = kcScore;
	}

	public int getSelfHomologyScore()
	{
		return selfHomologyScore;
	}

	public void setSelfHomologyScore(int selfHomologyScore)
	{
		this.selfHomologyScore = selfHomologyScore;
	}

	public Map<Integer, Integer> getSelfHomologyScores()
	{
		return selfHomologyScores;
	}

	public void setSelfHomologyScores(Map<Integer, Integer> selfHomologyScores)
	{
		this.selfHomologyScores = selfHomologyScores;
	}

	public int getPdScore()
	{
		return pdScore;
	}

	public void setPdScore(int pdScore)
	{
		this.pdScore = pdScore;
	}

	public void computeCodonUsage()
	{
		String sequence;
		int codonTotal;
		CodonUsage cu = new CodonUsage();
		int cdsLength = this.getCodingSequenceLength();

		for (Gene g : genes)
		{
			sequence = clusterSequence.substring(g.getStartBase() - 1, g.getStopBase());
			if (g.isComplement())
				sequence = getComplementSequence(sequence);

			codonTotal = sequence.length() / 3;

			// SETS NUMBER OF CODONS FOR CODON TABLE

			for (int i = 1; i <= codonTotal; i++)
			{
				CodonUsage.Detail d = cu.getUsage().get(sequence.substring(i * 3 - 3, i * 3));
				d.setCodonNumber(d.getCodonNumber() + 1);
			}
		}

		// SETS /1000 FOR CODON TABLE
		// SETS SCORE PER AMINOACID
		Map<String, Integer> aMap = CodonUsage.getAminoacidMap(cu.getUsage());

		for (Entry<String, Detail> codon : cu.getUsage().entrySet())
		{
			Detail d = codon.getValue();
			d.setFrequency((d.getCodonNumber() * 1000.0) / cdsLength);
			d.setScorePerAminoacid(d.getCodonNumber() * 100.0 / aMap.get(d.getAminoacid()));
		}

		this.setCodonUsage(cu);
	}

	public String getComplementSequence(String sequence)
	{
		StringBuilder sb = new StringBuilder();
		String reversedSequence = new StringBuilder(sequence).reverse().toString();

		for (int i = 0; i < reversedSequence.length(); i++)
		{
			switch (reversedSequence.charAt(i))
			{
			case 'A':
				sb.append("T");
				break;
			case 'C':
				sb.append("G");
				break;
			case 'G':
				sb.append("C");
				break;
			case 'T':
				sb.append("A");
				break;
			}
		}

		return sb.toString();
	}

	public int getCodingSequenceLength()
	{
		int count = 0;

		for (Gene g : this.genes)
		{
			count += g.getStopBase() - g.getStartBase();
			count++;
		}

		return count;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BiosyntheticGeneCluster other = (BiosyntheticGeneCluster) obj;
		if (clusterNumber == null)
		{
			if (other.clusterNumber != null)
				return false;
		} else if (!clusterNumber.equals(other.clusterNumber))
			return false;
		if (recordName == null)
		{
			if (other.recordName != null)
				return false;
		} else if (!recordName.equals(other.recordName))
			return false;
		return true;
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
}
