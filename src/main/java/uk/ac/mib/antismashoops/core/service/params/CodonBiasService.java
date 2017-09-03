package uk.ac.mib.antismashoops.core.service.params;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;
import uk.ac.mib.antismashoops.core.domainobject.CodonUsageTable;

@Service
public class CodonBiasService
{
    public void setCodonBiasData(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList)
    {
        biosyntheticGeneClusterList.forEach(BiosyntheticGeneCluster::setCodonUsageTable);
    }


    public void setCodonBiasDataWithReferenceSpecies(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList, CodonUsageTable codonUsageTable)
    {
        biosyntheticGeneClusterList.forEach(bgc -> bgc.setCodonBiasS(codonUsageTable));
    }

}
