package uk.ac.mib.antismashoops.core.datatransferobject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SelfHomologyDTO
{
    private Integer selfHomologyScore = 0;
    private Integer maximumMatchScore = 0;
}
