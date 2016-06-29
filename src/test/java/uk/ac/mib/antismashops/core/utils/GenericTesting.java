package uk.ac.mib.antismashops.core.utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.MvcConfiguration;
import uk.ac.mib.antismashoops.core.model.CodonUsage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { AntiSmashOopsApplication.class, MvcConfiguration.class })
@WebAppConfiguration
public class GenericTesting
{
	@Test
	public void test()
	{
		String sequence = "atggcggacatggacgcgttccgggacgagatcgcccgctgggcggcgggcggcgccggggacacggcggcggagctggcggcaggcctcggactgcggacggcggtgctgctggagggccccagcgacctcgccgccgtccaggccctggccgcccgcgaggaccgcgacctggccgccgagggcgtggccgtggtgtccatgggcggcgcgatgagcgtcggccgctacgccggcctgctcggcccgcccggcctcggcctgcgcctgaccggcctctgcgacgcccgggaggagcccttctacgtccgcggctggcagcgggcgggcgccgcgcgcgacttccacgtctgcgtcgccgacctggaggacgagatgatccgggcgctgggcccggcgcgggtcgaggaggtcatacggtccgaggacgaactccgcccctggcagaccttcctggcccagcccgcccagcacggccgcccccgggaacagcagctgcgccgcttcctcagcacgaagaagggccgcaagatccgctacggcagcctcctggtggaagccctcgacggcggacggccaccggccccgctggcggatctcctcgcggggttgtga";
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = null;
		Set<String> seqSections = new HashSet<>();

		for (int i = 0; i < sequence.length(); i += 3)
		{
			sb1.append(sequence.substring(i, i + 3));
			sb2 = new StringBuilder();
			for (int j = i + 3; j < sequence.length(); j += 3)
			{
				sb2.append(sequence.substring(j, j + 3));
				seqSections.add(sb2.toString());
			}
			seqSections.add(sb1.toString());
		}

		LinkedHashMap<String, Integer> frequencyMap = new LinkedHashMap<>();

		for (String s : seqSections)
		{
			frequencyMap.put(s, 0);
		}

		for (String s : seqSections)
		{
			frequencyMap.put(s, StringUtils.countOccurrencesOf(sequence, s));
		}

		// for (Entry<String, Integer> entry : frequencyMap.entrySet())
		// {
		// if (entry.getValue() > 1 && entry.getKey().length() > 3)
		// System.out.println("Value: " + entry.getValue() + " Key: " +
		// entry.getKey());
		// }
		//
		// frequencyMap =
		// frequencyMap.entrySet().stream().sorted(Entry.comparingByValue())
		// .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) ->
		// e1, LinkedHashMap::new));
		//
		// System.out.println("Separation!!!");
		//
		// for (Entry<String, Integer> entry : frequencyMap.entrySet())
		// {
		// if (entry.getValue() > 1 && entry.getKey().length() > 3)
		// System.out.println("Value: " + entry.getValue() + " Key: " +
		// entry.getKey());
		// }

		System.out.println("Separation!!!");

		CodonUsage cu = new CodonUsage();

		for (Entry<String, CodonUsage.Detail> entry : cu.getUsage().entrySet())
		{
			System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue().getAminoacid());
		}

		LinkedHashMap<String, CodonUsage.Detail> usage = cu.getUsage().entrySet().stream()
				.sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		System.out.println("Separation!!!");

		for (Entry<String, CodonUsage.Detail> entry : usage.entrySet())
		{
			System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue().getAminoacid());
		}

	}
}