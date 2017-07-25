package uk.ac.mib.antismashoops.core.domainobject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Species
{
    private String speciesId;
    private String speciesName;
    private String url;


    public Species()
    {
    }


    /**
     * Class constructor.
     *
     * @param speciesId   According to the Kazusa website
     * @param speciesName Scientific name of the species
     * @param url         Associated url in the Kazusa website
     */

    public Species(String speciesId, String speciesName, String url)
    {
        this.speciesId = speciesId;
        this.speciesName = speciesName;
        this.url = url;
    }
}
