package uk.ac.mib.antismashoops.core.service.params;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.ac.mib.antismashoops.core.domainobject.BiosyntheticGeneCluster;

@Service
public class GcContentService
{
    public void setGcContentData(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList)
    {
        biosyntheticGeneClusterList.forEach(BiosyntheticGeneCluster::setGcContent);
    }


    public void setGcContentDataWithReferenceSpecies(List<BiosyntheticGeneCluster> biosyntheticGeneClusterList, double refSpecies)
    {
        biosyntheticGeneClusterList.forEach(bgc -> bgc.setGcContentS(refSpecies));
    }

}
