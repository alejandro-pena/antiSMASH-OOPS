package uk.ac.mib.antismashoops.core.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ClusterBlastLineage
{
	private String accNumber;
	private List<String> lineage = new ArrayList<>();

	public ClusterBlastLineage()
	{
	}

	public ClusterBlastLineage(String accNumber)
	{
		this.accNumber = formatAccNumber(accNumber);
	}

	private String formatAccNumber(String accNumber)
	{
		if (!accNumber.contains("_"))
			return accNumber;
		else
		{
			String[] tokens = accNumber.split("_");
			if (tokens.length == 2)
				return tokens[0];
			if (tokens.length == 3)
				return tokens[1];
		}
		return "";
	}

	public String getAccNumber()
	{
		return accNumber;
	}

	public void setAccNumber(String accNumber)
	{
		this.accNumber = accNumber;
	}

	public List<String> getLineage()
	{
		return lineage;
	}

	public void setLineage(List<String> lineage)
	{
		this.lineage = lineage;
	}
}
