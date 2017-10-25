package cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.PfamDomain;
import org.cbioportal.genome_nexus.persistence.PfamDomainRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContextConfiguration.xml")
@Configurable
public class PfamDomainRepositoryTest
{
    @Autowired
    private PfamDomainRepository pfamDomainRepository;

    @Test
    public void getAllPfamDomains()
    {
        List<PfamDomain> domains = pfamDomainRepository.findAll();
        Assert.assertEquals(15, domains.size());
    }

    @Test
    public void getPfamDomainsByTranscript()
    {
        List<PfamDomain> domains = pfamDomainRepository.findByTranscriptId("ENST00000288602");

        Assert.assertEquals(3, domains.size());
        Assert.assertEquals("ENSG00000157764", domains.get(0).getGeneId());
    }

    @Test
    public void getPfamDomainsByGene()
    {
        List<PfamDomain> domains = pfamDomainRepository.findByGeneId("ENSG00000184047");

        Assert.assertEquals(5, domains.size());
        Assert.assertEquals("DIABLO", domains.get(0).getGeneSymbol());
    }

    @Test
    public void getPfamDomainsByProtein()
    {
        List<PfamDomain> domains = pfamDomainRepository.findByProteinId("ENSP00000320343");

        Assert.assertEquals(2, domains.size());
        Assert.assertEquals("ENST00000353548", domains.get(0).getTranscriptId());
    }

    @Test
    public void getPfamDomainsByPfam()
    {
        List<PfamDomain> domains = pfamDomainRepository.findByPfamDomainId("PF07714");

        Assert.assertEquals(3, domains.size());
        Assert.assertEquals("Pkinase_Tyr", domains.get(0).getPfamDomainName());
        Assert.assertEquals("Protein tyrosine kinase", domains.get(0).getPfamDomainDescription());

        // we have PF00666 in the pfam file, but there is no protein matching to PF00666 in the biomart file
        domains = pfamDomainRepository.findByPfamDomainId("PF00666");
        Assert.assertEquals(0, domains.size());
    }
}
