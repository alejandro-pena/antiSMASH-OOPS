package uk.ac.mib.antismashops.core.utils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.mib.antismashoops.AntiSmashOopsApplication;
import uk.ac.mib.antismashoops.MvcConfiguration;
import uk.ac.mib.antismashoops.core.domainobject.ClusterBlastLineage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { AntiSmashOopsApplication.class, MvcConfiguration.class })
@WebAppConfiguration
public class GenericTesting {

	private List<Node> leafNodes = new ArrayList<>();

	// @Test
	public void test() {
		String sequence = "gcgggacccctgtgacgctaccggcgtgcccctcgcggtccgcggccgccccgcaacgcgcaccgcgccggcaccaaggccggcgcgctgggcggtgtcaccaccctcggggtccggtcagtcctcgccccggacgatgttcgactcctgacccgattgcggtgcgcggacggtgctgccgtcctccacggcgggtacgcaggtgcgcatcacgcctctgctccgcaggcggcgggccttcgaccagcctgtctccagccgccggacggcgggtttgccgtcggtgtccgtggtcacggggcatcatcttccttctggttccgcttctggtgcgctcgcccctgcgggtccccggacggggaccggtgaaacccaggggccgtgcgggccgcgcggctcagccgccgcccaccgggatctcgctccacacctgcttgcccccgctgaccggcagcgtcccccaggtcgccgacatcgcctcgaccaggaggatgccgcgtccgccggtggcctcccagccgatgctggtcggcttgaccggcgtgcgcggcgaggcgtcggcgacggccacgcgcagccgtccgccgatcagggtgaggtcgagccgtaccttgccgtcggtgtgcaccagggcgttggtgaccagctcggagacgatcaggagcactccgtcgatgtcgtcgtccggtacgccccaggtgcgcagggtgcgccgggtgaagcggcgggcgtgccgcacggcctcgggcagccgccacaccgaccagctctcgcgcagcgggcgcagggccattccgtcgtagcgcaccagcagcagggccacgtcgtcgttgcggcgggcgttgccgagcagggcgtcggcgacgagtccgaggtggtcgggggcggagacggccagcgcgtgcgcgaaccggtccatgccctcgtcgatgtcggactccggcgtctccaccaggccgtccgtggtgagggcgatcagcgtgccgggccgcagccgcagcgggctcatcgggaagtcggcctgggtcaccacgccgagcggcgggccgccctccacctccgcgatctcggtgctgccgtcgggatggcgcagcaccggtggcaggtgccccgcgcgcacgcaccaggcggagccctcctccatgtcgaggtcgacgtagacgcaggtggcgaagaggtcggtctccatgcccgtcagcagccggttggcgtgggagaccacgacgtccggcgggtggccctcgaccgcgtaggcgcgcagggcggtgcgcatctggcccatgagggtggccgcgccggcgctgtggccctggacgtcgccgatgacgagggcgacgtggtggtcgggcagcgggatcacgtcgtaccagtcgccgcccagctccaggcccgccgtgctgggcaggtagcgggcgacggcgaccgcgccgggcagcttgggcaggcggcgcggcagcagctggcgctggagcatgccgaccagctcgtgctcggcgtcgaaggcgtgggcgcgcatcagggcctgcccggccaggcccgcggaggcggtgagcagggcccgttcgtcggggccgaagtcgtgcggggtgtcccagccgatcaggcaggcaccggccatgcgtccgccggcgggcagcggcagcacggcgaggccgccggggccgacgtcggcgagggcgggctccaggtccccggtgccggcgggccagatccgggcgcggccctcgcgcagcgcggcggccagggtcggcatggcccgtaccggcgcgtccggccactcggtgcgccactccaggcgccacagttcgggccaggactcggggtcgggcgggtcgaggacggtgacgacgagccggtcgttctccagctcggccagcgcgatccggtcggcccgcagcggcctgcgcagggcggcgacgacggcctggctgacgtcgcggaccgtcccggcggtggcgagcgcggcggccagccgctggacgcgggcgacgtcggtcacgtccggacgcagggtggaggcgtcggcgacggtgccgaccagaagggccggccggccctcgccgccgggcagcaggcgcccgctgagccgcagccacttcggcgggccggtgggctggagcacccggaactccagctcgcgctcgccgatggtcatgtggtcggactcggtcaccgacatcagggacggcaggtcctccggcacggtgaggccgagcagcgtctcgaccttgccgtcgaactcgccgggggccagtccgaacagccgcaggatgccgtcgccgacctccacccggccggagtccatggtcagggtgaaggcgtccggctggaggtcaccggtctcggcacgggcggcggtctcggccggggcggggcaggtcatcgcctccgcgatcgcctccaggcacgtacggtcgtcggtgtcgaacccgtccgggcgctcggtcagggcgagaaggcagccgccgccgtccccgcgcacgggcaggacggcgagcgagaagccgctcgacggcacatgccgtgactccgccgatccggcgaactcctccggcccgatccacaccgcctcgccggcgcggtgggcgtcggcgaccggggaaccgccggacgcggggtagctgtcgcgtacgccgtacagggagcggggcactccggccgactcgaccaggcagagcagctcgcgatcgtcgcccggcgcgtagagcgcggcgaaggaggcgcccgcgaagaccagcgcctgctcgaggattacgcgcagccgctcctgcggcaccggtccggccacgatcgcgttcagggcgccctcggcacgcagcgtcctcggtccgcgttccgcagcgtcctctccggccacctcgccattacagcgcttgtgaggcatccgcgcagcccctggggaccggggagcggcgatcaggggcactccggggctcgcggacgggcgcttcgcgcctccggacgggtgcgggccgggccgggacctgccggcggccaggaggtggtggcggacacgcaccgctgcccgcgggccgtgaaccagtgcggcgggcggtgcggccggggcgcagaccggccgcaccgagccgacgtggcgtcagcccgtcagcccgtcagccccgagccgtccccagcgccgacgggtgcaccgtcacccgcgtcacccgcgtcacctgtgtcacctttgtcacccgcaccagcgccgccgtcgcccgcgtcgcccgcacctcccgcatcaccctcctcgccggcacctgcgccggcaccgcccgcgtctccggcacccgcgccgccgtcctcgcccccgagggtggcgccgaccgccgccagggccgtggtgaccggctggaagaaggtctcgccgccgacggtgcagtcaccgctgccgccggaggtcaggccgatcgcgaggccgtcccgcgtgaagagggagccgccgctgtcgccgggctcggcgcagacgtcggtctgtatgaggccggtgaccatgccctcgggatagttgacggtggcgtccaggccgagcacctgcccgtcggccagtccggtcgtactgcccatgcggaagacctcctggccgaccgccgcctcggcggccccgctgatcggcagggtctggtcgccgaggtcgacctcgctcggcgcctcggtcgccgggtcgtcgtacctgacgagcgcgaagtcgccctcgccggggaagaccgcctggtcgacggtggcgatcggctgtccgccctgtgcgtcggaccactggtcggccgccaccccgcagtgaccggccgtcaggaaggcgggcgagccgtcgcccgcggtgacgttgaacccgagtgagcagcgcgctccgccgccgaagatcgcgtcgccgcccgacgcgaaggtcttgaaggtgccggcggacttcctgaccgtcgccatgccggacccgaggccgtcgacggtggactccagccggtcccaccggtcgccggtgacggtgctgtcggcggtcaccaggatcttgttggtccgcgggtcgatcgcccacgcggtcccggggatgccggcctcggccttcagggtctgcgcgccgccgcgcagctcgcccatgctgttgtcgacctcgcggaccttcgcacccgccttctcggcctgcacgaccacctggttgttgtcgccggggacgacgttgacgaccagttgccgggcgccggagtcgtagtaggagccggcgaagctgtcgccgagcaggccggcgagccgggcggcgaggtccgacgcgtccgccgccttgagcgtcctgggtgccgcggcggcatcgtccgaggagccgccgtcctgcgaggcgttcgcgttcggcagcaggatcgcggccgcgccgagcgccaccacaccgcccgccgccatcgcggccctgcgcttggggattcgcttgtgactcaacctgctcgacctcctgggaccggggttgatgcccgccggtccggcagctgttgggggcgcctggacggctgttggtacgcacggaccgggcggggtgttcagccgggccctcgcgccgcccccgatcggcaccctccgcatccggagcgcgacgcaccgttgcgatcacggagcggaatgactgcttcagcgaatctgcacatcgaatgcccaacccggtcactcccgttggcgcatctgcacaagcccgcggcggcccgtacgcctttggggccgtagtgccccaatcgcctcggccggggcaaggggccgcacgagccgatctcgcgcaccgtgtgaagaagtggccgcgaaggagtgcgcgcccctgcacgcttgggcccgcaggcctcgcaagtgtgctggtgagaggggtgtttgattggaagcccggaccggccgcgtgctttgatccatcgcaccgcgggggccgcggagggctccggaaaagggcgcccggcgcccgcggtcgcggccgtcatctgtccccagagggggccgctccccccttgtgaaatggctatacgaagggaagttccatcatgaactccaccccccaggttgagaccgtcgagatctccgacgccgacctcgacaacgtctccggcggcctgaacgtgaacgccgtcggcaccgtcaccggcctggtggacggcatcgccccggtctccggcctgctcaacaccgccgtcggcaccgtcgagggtgtcaccggcctgaacacggccccggtcacgggcctggtcgccggtctctgatcgcgcccttgcgtcgctgagtcccggagccgtccaggctccgggacttttcggccgttctccctccggccgtgatcgtcccccggccgttctcaccaagccctggatacgtcagccgtgcagttccgccaacaggccctcgcaaaactccagtcgccggaggagctcgatctcccggtgcgtttcgcccgtccccagggctggctggtgctgtccgtgacgctgatcgccatggccgccgcgtccgtgtgggcggtgggcggctcggtgacctccacggtgtccgcgcccgccgtcctcacccacggccagggcagttacctcctgcagagccccgtctccgggcaggtcaccgcggtcctcgcccgtccggggcagcggctggccgccgacgccccggtgctgaaggtccgcaccgcgaagggcgagacggtggtccgcacggtggacgccggccgggtcagcgcactcgccgccaccgtcgggcagatcatcgccaccggcgcgaacgtcgcgtccgtcgagaaggtcgcccacgccggcgacccgctctacgccaccgtctacgtccccgcggagaacgccgccgccatccccgcccacgcctcggtcgacctgaccgtccagtcggtgccgacgcagcagtacggcgtcctgcacggggaggtgaagtccgtggaccgctcggcccagtcggcgcagacgatcggcgcgttcctcggggacagcgcgctcggcgagcagttcaccgaggacggcaggccggtggccgtgacggtgcggctggcaacctcgaagtccacgaagagcggctacgcgtggtcctcggccgacgggccgccgttcgaactgacctccatgaccctggcctcgggctcgatccggctggccgaccagcgtcccgtcgattggctgctgccgtgagcaccgcacaggagacccggggcaggcgccgtgccgccccgccgaggcgcagcgtgccgaagagccgcgcgaggaccgtccgcacgcccacggtgctccagatggaggccgtggagtgcggtgccgcctccctcgccatggtgctgggacactacggccgccatgtccccctggaggagctgcggatcgcctgcggtgtctcccgcgacgggtcccgggcgagcaacctgctgaaggccgcgcggagttacgggttcaccgccaagggcatgcagatggacctggccgccctcgccgaggtgacggcaccggccatcctcttctgggagttcaaccactacgtcgtctacgacggcatgggccgccggttcgggcggcgcggggtgttcatcaacgacccgggcaagggacgcaggttcgtgtccctggaggacttcgacgggtccttcaccggtgtcgtgctgaccatggagcccggtgacggcttcacccgcggcggccgcaagcccggtgtgctcggtgcgatgcccgcccggctgcgcggaaccgcgggcacgctgcccgccgcggtgctggcgagcctgctcctggtggcggtcggcgcggcggtgcccgcgctcagccgcacctacatcgacatgttcctcatcggtgggcagacctcgctgctcggcgtgctgttcgcgtcgatgggcgcctgtgtgctgctcacggtggtgctgacctggctccagcaggccaacctgctgcacgggcgcatcatctcctccaccctctccagcgcccgcttcctgcggcacctgctgcggctgccggtgaccttcttctcccagcgcagccccgccgacctggtgcagcggctccagtccaacgacgccgtcgccgagacgctggcccgcgacctcgccgcggcgggcgtggacgcgatcgtggtcgtcctgtacgccgtgctgctgtacacgtacgacccgcagctcaccttcgtcggcatcggtgtggcgctgctcaacatcctggcgatgcgggtcgtcatccggctgcgcgcgacccgtacggccaagctgcgggcggacacggcgcggctgaccaacaccgcctacaccggcctccagctgatcgagacgatgaaggcgaccggcggcgaggacggctacttccgcaagtgggccggccagcacgccaccacgctggaggagcagcagcggctcggggtgcccagtgcctggctgggcgtggtcgcgccgacgctggcgacgctgaacagcgggctgatcctgtggatcggcggcatgcgcgcggtcgagggccacatctcggtcgggctgctggtcgccttccaggcgctggtggtccgcttcaccgcgccgctgacccggctgaacggtgtcgcgggccgcatccaggacttcgcggccgacgtggcccgcctcaaggacgtggagaacttccgggcggatccgctctacgaccgccccggtgacggcgactccacccgccggctgcgcgggcacgtggagctgcagaacgtctccttcggctacaacccgctcgacaagccgctgctgtccggtttcgacctgaccgtggggccggggcagcaggtggccctggtcggcggttcgggcagcggcaagtcgacggtgtcgcggctgatctcgggcctgtacgcgtcctgggacggcgtgatccgcatcgacgggcagcggctcgacgacatcccgcgcggggcgctggcgtcctccgtctccttcgtcgaccaggacgtgttcctgttcgagggctcggtgcgcgacaacgtggccctgtgggacccgtcgatccccgacgacgccgtggtggaggcgctgaaggacgccgcgctgtacgacgtggtgacgcgcaggccgggcgggctccacagcccggtggagcaggacggccgcaacttctccggcgggcagcgccagcgcctggagatcgcgcgggccctggtgcgccgccccagcatcctcgtcctggacgaggtgaccagcgcgctggacgcggagaccgagctggtggtgatggacaacctgcgccggcgcggctgcgcctgcgtggtgatcgcgcaccggctcagcacggtgcgggacagcgacgagatcgtcgtcctcgaccacggcacggtcgtggagcgcgggcggcacgaggagctgctggcgcgcggtggcgcgtacgcggcgctggtcagggagcggtgagatgacaaccgtgcacgaggggcagggcgacctggtcctgggggcgctcggctcgctgggcacgcgcgtcgactgcgccgggttcaaccgcctcgacctggaggggccgcaggtgctgtggctcgtggtgtccggcgcggtggacctgttcgcggtcgacgccggggagcagggccactggcaccacctgggacgcctggaggcgggctcggtactgctgggaccggtcgccgggccgcagcacacgctggtggcacgcccgctgcgggactgcgtggtgcaccggatcgggctgcgcgagctgtaccagccggcgcccacccagacgtggtcgtacgacgcgtacggcaacccccagtacgtgccgccgacgacgagtccgctggagtacgccctggccctcggcgtgggacgcggcctgaccgtcctcttccaggcgccgatggccacggagcgggccggcgcacccacggacgacgacgtgttctggatgcaggtgccgccgggcagcgtgcagtacggggcggtgtacggcgaggaggcggccgccgacctgttgatggacccggcgctgtggcagagcatggtcgaccagcagtaccggctgctgaccacccttgaccggtggatcgagcagctggagcgcacccacgagacccgcacggcggccggtatcaaggccggtgaggcggtgcgcgcgcgggccgaccggacgctgctggcctccatcggcaagcgctcggcgcagcgcaccacggccgccgacgccgacgccacgtacgcggcgtgcgggctggtcgcccgcgccgccggtattccgctcgccgagccgtccccgggcggcaccgagagcgaccggctggacccggtggagcggatcgcgctcgcctcgcgggtgcggaccaggtcggtgcggctggcggaccgctggtggcgggaggacgtggggccgctggtcgggcaccgcaccctgtcgggggcgccggtggctctgctgtggcggcgcggcggctatgtggccgtgcatccggcgaccggtcgggagacgcccgtggagcgggcgaacgccggggagttcgaaccgcgcgcggtgatgttctaccgcccgctgccggagcggcggccgagtccgctcgggctgctccggttctgtctggcgggcacccgccgggatctgacgtcgctgctgctcgccgggctggtgacggtggtgctcggcgcgctggtgccgatcgcgacgggccgggtgctcggcgagttcgtgccgaaggcgcagaccgggctgatcgtgcaggtgtgtctcgcggtgatgctgagcagcgtcgtcgcggcggccttcatgctgctgcagaacctcaccatcctgcgtctggagggccggatcgaggccaccctccagcccgccgtgtgggaccgcctgctgcggctgccgacgaagttcttcaccgagcgctccaccggcgaactggcgagcgcggccatgggcatcagcgcgatccgcagactgctcgcgggggtcggcccgaccgtggcccagtcggtgaccgtcggggcgatgaacctcggtctgctgctctggtacagcgtgccgatggcgttcgccgcgatcggcatgctggtggtcgtcgccggggtgttcctcgggctcgggctgtggcaggtgcgctggcagcggcggctggtcaaactcaccaacaagctgaacaaccaggcgttccagacgctgcgcgggctgccgaagctgcgggtggcggcagccgagaactacgcctacgcggcgtgggcggagcggttcgcgcacagccgggagctgcagcagcgggcgggccggatcaagaacctgaacgcggtgctcg";
		// String sequence =
		// "atggcggacatggacgcgttccgggacgagatcgcccgctgggcggcgggcggcgccggggacacggcggcggagctggcggcaggcctcggactgcggacggcggtgctgctggagggccccagcgacctcgccgccgtccaggccctggccgcccgcgaggaccgcgacctggccgccgagggcgtggccgtggtgtccatgggcggcgcgatgagcgtcggccgctacgccggcctgctcggcccgcccggcctcggcctgcgcctgaccggcctctgcgacgcccgggaggagcccttctacgtccgcggctggcagcgggcgggcgccgcgcgcgacttccacgtctgcgtcgccgacctggaggacgagatgatccgggcgctgggcccggcgcgggtcgaggaggtcatacggtccgaggacgaactccgcccctggcagaccttcctggcccagcccgcccagcacggccgcccccgggaacagcagctgcgccgcttcctcagcacgaagaagggccgcaagatccgctacggcagcctcctggtggaagccctcgacggcggacggccaccggccccgctggcggatctcctcgcggggttgtga";
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = null;
		Set<String> seqSections = new HashSet<>();

		for (int i = 0; i < sequence.length(); i += 3) {
			sb1.append(sequence.substring(i, i + 3));
			sb2 = new StringBuilder();
			for (int j = i + 3; j < sequence.length(); j += 3) {
				sb2.append(sequence.substring(j, j + 3));
				seqSections.add(sb2.toString());
			}
			seqSections.add(sb1.toString());
		}

		LinkedHashMap<String, Integer> frequencyMap = new LinkedHashMap<>();

		for (String s : seqSections) {
			frequencyMap.put(s, 0);
		}

		for (String s : seqSections) {
			frequencyMap.put(s, StringUtils.countOccurrencesOf(sequence, s));
		}

		// for (Entry<String, Integer> entry : frequencyMap.entrySet())
		// {
		// if (entry.getValue() > 1 && entry.getKey().length() > 3)
		// System.out.println("Value: " + entry.getValue() + " Key: " +
		// entry.getKey());
		// }

		frequencyMap = frequencyMap.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		System.out.println("Separation!!!");

		for (Entry<String, Integer> entry : frequencyMap.entrySet()) {
			if (entry.getValue() > 1 && entry.getKey().length() > 3)
				System.out.println("Value: " + entry.getValue() + " Key: " + entry.getKey());
		}

		// System.out.println("Separation!!!");
		//
		// CodonUsage cu = new CodonUsage();
		//
		// for (Entry<String, CodonUsage.Detail> entry :
		// cu.getUsage().entrySet())
		// {
		// System.out.println("Key: " + entry.getKey() + " Value: " +
		// entry.getValue().getAminoacid());
		// }
		//
		// LinkedHashMap<String, CodonUsage.Detail> usage =
		// cu.getUsage().entrySet().stream()
		// .sorted(Entry.comparingByValue())
		// .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) ->
		// e1, LinkedHashMap::new));
		//
		// System.out.println("Separation!!!");
		//
		// for (Entry<String, CodonUsage.Detail> entry : usage.entrySet())
		// {
		// System.out.println("Key: " + entry.getKey() + " Value: " +
		// entry.getValue().getAminoacid());
		// }
	}

	@Test
	public void test2() {
		// edp.setKnownClusterData(KnownClusterData.getKnownClusterList());
	}

	// @Test
	public void printMatrix() {
		String sequence = "gcgggacccctgtgacgctaccggcgtgcccctcgcggtccgcggccgccccgcaacgcgcaccgcgccggcaccaaggccggcgcgctgggcggtgtcaccaccctcggggtccggtcagtcctcgccccggacgatgttcgactcctgacccgattgcggtgcgcggacggtgctgccgtcctccacggcgggtacgcaggtgcgcatcacgcctctgctccgcaggcggcgggccttcgaccagcctgtctccagccgccggacggcgggtttgccgtcggtgtccgtggtcacggggcatcatcttccttctggttccgcttctggtgcgctcgcccctgcgggtccccggacggggaccggtgaaacccaggggccgtgcgggccgcgcggctcagccgccgcccaccgggatctcgctccacacctgcttgcccccgctgaccggcagcgtcccccaggtcgccgacatcgcctcgaccaggaggatgccgcgtccgccggtggcctcccagccgatgctggtcggcttgaccggcgtgcgcggcgaggcgtcggcgacggccacgcgcagccgtccgccgatcagggtgaggtcgagccgtaccttgccgtcggtgtgcaccagggcgttggtgaccagctcggagacgatcaggagcactccgtcgatgtcgtcgtccggtacgccccaggtgcgcagggtgcgccgggtgaagcggcgggcgtgccgcacggcctcgggcagccgccacaccgaccagctctcgcgcagcgggcgcagggccattccgtcgtagcgcaccagcagcagggccacgtcgtcgttgcggcgggcgttgccgagcagggcgtcggcgacgagtccgaggtggtcgggggcggagacggccagcgcgtgcgcgaaccggtccatgccctcgtcgatgtcggactccggcgtctccaccaggccgtccgtggtgagggcgatcagcgtgccgggccgcagccgcagcgggctcatcgggaagtcggcctgggtcaccacgccgagcggcgggccgccctccacctccgcgatctcggtgctgccgtcgggatggcgcagcaccggtggcaggtgccccgcgcgcacgcaccaggcggagccctcctccatgtcgaggtcgacgtagacgcaggtggcgaagaggtcggtctccatgcccgtcagcagccggttggcgtgggagaccacgacgtccggcgggtggccctcgaccgcgtaggcgcgcagggcggtgcgcatctggcccatgagggtggccgcgccggcgctgtggccctggacgtcgccgatgacgagggcgacgtggtggtcgggcagcgggatcacgtcgtaccagtcgccgcccagctccaggcccgccgtgctgggcaggtagcgggcgacggcgaccgcgccgggcagcttgggcaggcggcgcggcagcagctggcgctggagcatgccgaccagctcgtgctcggcgtcgaaggcgtgggcgcgcatcagggcctgcccggccaggcccgcggaggcggtgagcagggcccgttcgtcggggccgaagtcgtgcggggtgtcccagccgatcaggcaggcaccggccatgcgtccgccggcgggcagcggcagcacggcgaggccgccggggccgacgtcggcgagggcgggctccaggtccccggtgccggcgggccagatccgggcgcggccctcgcgcagcgcggcggccagggtcggcatggcccgtaccggcgcgtccggccactcggtgcgccactccaggcgccacagttcgggccaggactcggggtcgggcgggtcgaggacggtgacgacgagccggtcgttctccagctcggccagcgcgatccggtcggcccgcagcggcctgcgcagggcggcgacgacggcctggctgacgtcgcggaccgtcccggcggtggcgagcgcggcggccagccgctggacgcgggcgacgtcggtcacgtccggacgcagggtggaggcgtcggcgacggtgccgaccagaagggccggccggccctcgccgccgggcagcaggcgcccgctgagccgcagccacttcggcgggccggtgggctggagcacccggaactccagctcgcgctcgccgatggtcatgtggtcggactcggtcaccgacatcagggacggcaggtcctccggcacggtgaggccgagcagcgtctcgaccttgccgtcgaactcgccgggggccagtccgaacagccgcaggatgccgtcgccgacctccacccggccggagtccatggtcagggtgaaggcgtccggctggaggtcaccggtctcggcacgggcggcggtctcggccggggcggggcaggtcatcgcctccgcgatcgcctccaggcacgtacggtcgtcggtgtcgaacccgtccgggcgctcggtcagggcgagaaggcagccgccgccgtccccgcgcacgggcaggacggcgagcgagaagccgctcgacggcacatgccgtgactccgccgatccggcgaactcctccggcccgatccacaccgcctcgccggcgcggtgggcgtcggcgaccggggaaccgccggacgcggggtagctgtcgcgtacgccgtacagggagcggggcactccggccgactcgaccaggcagagcagctcgcgatcgtcgcccggcgcgtagagcgcggcgaaggaggcgcccgcgaagaccagcgcctgctcgaggattacgcgcagccgctcctgcggcaccggtccggccacgatcgcgttcagggcgccctcggcacgcagcgtcctcggtccgcgttccgcagcgtcctctccggccacctcgccattacagcgcttgtgaggcatccgcgcagcccctggggaccggggagcggcgatcaggggcactccggggctcgcggacgggcgcttcgcgcctccggacgggtgcgggccgggccgggacctgccggcggccaggaggtggtggcggacacgcaccgctgcccgcgggccgtgaaccagtgcggcgggcggtgcggccggggcgcagaccggccgcaccgagccgacgtggcgtcagcccgtcagcccgtcagccccgagccgtccccagcgccgacgggtgcaccgtcacccgcgtcacccgcgtcacctgtgtcacctttgtcacccgcaccagcgccgccgtcgcccgcgtcgcccgcacctcccgcatcaccctcctcgccggcacctgcgccggcaccgcccgcgtctccggcacccgcgccgccgtcctcgcccccgagggtggcgccgaccgccgccagggccgtggtgaccggctggaagaaggtctcgccgccgacggtgcagtcaccgctgccgccggaggtcaggccgatcgcgaggccgtcccgcgtgaagagggagccgccgctgtcgccgggctcggcgcagacgtcggtctgtatgaggccggtgaccatgccctcgggatagttgacggtggcgtccaggccgagcacctgcccgtcggccagtccggtcgtactgcccatgcggaagacctcctggccgaccgccgcctcggcggccccgctgatcggcagggtctggtcgccgaggtcgacctcgctcggcgcctcggtcgccgggtcgtcgtacctgacgagcgcgaagtcgccctcgccggggaagaccgcctggtcgacggtggcgatcggctgtccgccctgtgcgtcggaccactggtcggccgccaccccgcagtgaccggccgtcaggaaggcgggcgagccgtcgcccgcggtgacgttgaacccgagtgagcagcgcgctccgccgccgaagatcgcgtcgccgcccgacgcgaaggtcttgaaggtgccggcggacttcctgaccgtcgccatgccggacccgaggccgtcgacggtggactccagccggtcccaccggtcgccggtgacggtgctgtcggcggtcaccaggatcttgttggtccgcgggtcgatcgcccacgcggtcccggggatgccggcctcggccttcagggtctgcgcgccgccgcgcagctcgcccatgctgttgtcgacctcgcggaccttcgcacccgccttctcggcctgcacgaccacctggttgttgtcgccggggacgacgttgacgaccagttgccgggcgccggagtcgtagtaggagccggcgaagctgtcgccgagcaggccggcgagccgggcggcgaggtccgacgcgtccgccgccttgagcgtcctgggtgccgcggcggcatcgtccgaggagccgccgtcctgcgaggcgttcgcgttcggcagcaggatcgcggccgcgccgagcgccaccacaccgcccgccgccatcgcggccctgcgcttggggattcgcttgtgactcaacctgctcgacctcctgggaccggggttgatgcccgccggtccggcagctgttgggggcgcctggacggctgttggtacgcacggaccgggcggggtgttcagccgggccctcgcgccgcccccgatcggcaccctccgcatccggagcgcgacgcaccgttgcgatcacggagcggaatgactgcttcagcgaatctgcacatcgaatgcccaacccggtcactcccgttggcgcatctgcacaagcccgcggcggcccgtacgcctttggggccgtagtgccccaatcgcctcggccggggcaaggggccgcacgagccgatctcgcgcaccgtgtgaagaagtggccgcgaaggagtgcgcgcccctgcacgcttgggcccgcaggcctcgcaagtgtgctggtgagaggggtgtttgattggaagcccggaccggccgcgtgctttgatccatcgcaccgcgggggccgcggagggctccggaaaagggcgcccggcgcccgcggtcgcggccgtcatctgtccccagagggggccgctccccccttgtgaaatggctatacgaagggaagttccatcatgaactccaccccccaggttgagaccgtcgagatctccgacgccgacctcgacaacgtctccggcggcctgaacgtgaacgccgtcggcaccgtcaccggcctggtggacggcatcgccccggtctccggcctgctcaacaccgccgtcggcaccgtcgagggtgtcaccggcctgaacacggccccggtcacgggcctggtcgccggtctctgatcgcgcccttgcgtcgctgagtcccggagccgtccaggctccgggacttttcggccgttctccctccggccgtgatcgtcccccggccgttctcaccaagccctggatacgtcagccgtgcagttccgccaacaggccctcgcaaaactccagtcgccggaggagctcgatctcccggtgcgtttcgcccgtccccagggctggctggtgctgtccgtgacgctgatcgccatggccgccgcgtccgtgtgggcggtgggcggctcggtgacctccacggtgtccgcgcccgccgtcctcacccacggccagggcagttacctcctgcagagccccgtctccgggcaggtcaccgcggtcctcgcccgtccggggcagcggctggccgccgacgccccggtgctgaaggtccgcaccgcgaagggcgagacggtggtccgcacggtggacgccggccgggtcagcgcactcgccgccaccgtcgggcagatcatcgccaccggcgcgaacgtcgcgtccgtcgagaaggtcgcccacgccggcgacccgctctacgccaccgtctacgtccccgcggagaacgccgccgccatccccgcccacgcctcggtcgacctgaccgtccagtcggtgccgacgcagcagtacggcgtcctgcacggggaggtgaagtccgtggaccgctcggcccagtcggcgcagacgatcggcgcgttcctcggggacagcgcgctcggcgagcagttcaccgaggacggcaggccggtggccgtgacggtgcggctggcaacctcgaagtccacgaagagcggctacgcgtggtcctcggccgacgggccgccgttcgaactgacctccatgaccctggcctcgggctcgatccggctggccgaccagcgtcccgtcgattggctgctgccgtgagcaccgcacaggagacccggggcaggcgccgtgccgccccgccgaggcgcagcgtgccgaagagccgcgcgaggaccgtccgcacgcccacggtgctccagatggaggccgtggagtgcggtgccgcctccctcgccatggtgctgggacactacggccgccatgtccccctggaggagctgcggatcgcctgcggtgtctcccgcgacgggtcccgggcgagcaacctgctgaaggccgcgcggagttacgggttcaccgccaagggcatgcagatggacctggccgccctcgccgaggtgacggcaccggccatcctcttctgggagttcaaccactacgtcgtctacgacggcatgggccgccggttcgggcggcgcggggtgttcatcaacgacccgggcaagggacgcaggttcgtgtccctggaggacttcgacgggtccttcaccggtgtcgtgctgaccatggagcccggtgacggcttcacccgcggcggccgcaagcccggtgtgctcggtgcgatgcccgcccggctgcgcggaaccgcgggcacgctgcccgccgcggtgctggcgagcctgctcctggtggcggtcggcgcggcggtgcccgcgctcagccgcacctacatcgacatgttcctcatcggtgggcagacctcgctgctcggcgtgctgttcgcgtcgatgggcgcctgtgtgctgctcacggtggtgctgacctggctccagcaggccaacctgctgcacgggcgcatcatctcctccaccctctccagcgcccgcttcctgcggcacctgctgcggctgccggtgaccttcttctcccagcgcagccccgccgacctggtgcagcggctccagtccaacgacgccgtcgccgagacgctggcccgcgacctcgccgcggcgggcgtggacgcgatcgtggtcgtcctgtacgccgtgctgctgtacacgtacgacccgcagctcaccttcgtcggcatcggtgtggcgctgctcaacatcctggcgatgcgggtcgtcatccggctgcgcgcgacccgtacggccaagctgcgggcggacacggcgcggctgaccaacaccgcctacaccggcctccagctgatcgagacgatgaaggcgaccggcggcgaggacggctacttccgcaagtgggccggccagcacgccaccacgctggaggagcagcagcggctcggggtgcccagtgcctggctgggcgtggtcgcgccgacgctggcgacgctgaacagcgggctgatcctgtggatcggcggcatgcgcgcggtcgagggccacatctcggtcgggctgctggtcgccttccaggcgctggtggtccgcttcaccgcgccgctgacccggctgaacggtgtcgcgggccgcatccaggacttcgcggccgacgtggcccgcctcaaggacgtggagaacttccgggcggatccgctctacgaccgccccggtgacggcgactccacccgccggctgcgcgggcacgtggagctgcagaacgtctccttcggctacaacccgctcgacaagccgctgctgtccggtttcgacctgaccgtggggccggggcagcaggtggccctggtcggcggttcgggcagcggcaagtcgacggtgtcgcggctgatctcgggcctgtacgcgtcctgggacggcgtgatccgcatcgacgggcagcggctcgacgacatcccgcgcggggcgctggcgtcctccgtctccttcgtcgaccaggacgtgttcctgttcgagggctcggtgcgcgacaacgtggccctgtgggacccgtcgatccccgacgacgccgtggtggaggcgctgaaggacgccgcgctgtacgacgtggtgacgcgcaggccgggcgggctccacagcccggtggagcaggacggccgcaacttctccggcgggcagcgccagcgcctggagatcgcgcgggccctggtgcgccgccccagcatcctcgtcctggacgaggtgaccagcgcgctggacgcggagaccgagctggtggtgatggacaacctgcgccggcgcggctgcgcctgcgtggtgatcgcgcaccggctcagcacggtgcgggacagcgacgagatcgtcgtcctcgaccacggcacggtcgtggagcgcgggcggcacgaggagctgctggcgcgcggtggcgcgtacgcggcgctggtcagggagcggtgagatgacaaccgtgcacgaggggcagggcgacctggtcctgggggcgctcggctcgctgggcacgcgcgtcgactgcgccgggttcaaccgcctcgacctggaggggccgcaggtgctgtggctcgtggtgtccggcgcggtggacctgttcgcggtcgacgccggggagcagggccactggcaccacctgggacgcctggaggcgggctcggtactgctgggaccggtcgccgggccgcagcacacgctggtggcacgcccgctgcgggactgcgtggtgcaccggatcgggctgcgcgagctgtaccagccggcgcccacccagacgtggtcgtacgacgcgtacggcaacccccagtacgtgccgccgacgacgagtccgctggagtacgccctggccctcggcgtgggacgcggcctgaccgtcctcttccaggcgccgatggccacggagcgggccggcgcacccacggacgacgacgtgttctggatgcaggtgccgccgggcagcgtgcagtacggggcggtgtacggcgaggaggcggccgccgacctgttgatggacccggcgctgtggcagagcatggtcgaccagcagtaccggctgctgaccacccttgaccggtggatcgagcagctggagcgcacccacgagacccgcacggcggccggtatcaaggccggtgaggcggtgcgcgcgcgggccgaccggacgctgctggcctccatcggcaagcgctcggcgcagcgcaccacggccgccgacgccgacgccacgtacgcggcgtgcgggctggtcgcccgcgccgccggtattccgctcgccgagccgtccccgggcggcaccgagagcgaccggctggacccggtggagcggatcgcgctcgcctcgcgggtgcggaccaggtcggtgcggctggcggaccgctggtggcgggaggacgtggggccgctggtcgggcaccgcaccctgtcgggggcgccggtggctctgctgtggcggcgcggcggctatgtggccgtgcatccggcgaccggtcgggagacgcccgtggagcgggcgaacgccggggagttcgaaccgcgcgcggtgatgttctaccgcccgctgccggagcggcggccgagtccgctcgggctgctccggttctgtctggcgggcacccgccgggatctgacgtcgctgctgctcgccgggctggtgacggtggtgctcggcgcgctggtgccgatcgcgacgggccgggtgctcggcgagttcgtgccgaaggcgcagaccgggctgatcgtgcaggtgtgtctcgcggtgatgctgagcagcgtcgtcgcggcggccttcatgctgctgcagaacctcaccatcctgcgtctggagggccggatcgaggccaccctccagcccgccgtgtgggaccgcctgctgcggctgccgacgaagttcttcaccgagcgctccaccggcgaactggcgagcgcggccatgggcatcagcgcgatccgcagactgctcgcgggggtcggcccgaccgtggcccagtcggtgaccgtcggggcgatgaacctcggtctgctgctctggtacagcgtgccgatggcgttcgccgcgatcggcatgctggtggtcgtcgccggggtgttcctcgggctcgggctgtggcaggtgcgctggcagcggcggctggtcaaactcaccaacaagctgaacaaccaggcgttccagacgctgcgcgggctgccgaagctgcgggtggcggcagccgagaactacgcctacgcggcgtgggcggagcggttcgcgcacagccgggagctgcagcagcgggcgggccggatcaagaacctgaacgcggtgctcgg";
		String[][] matrix = new String[sequence.length()][sequence.length()];
		for (int i = 0; i < sequence.length(); i++) {
			for (int j = 0; j < sequence.length(); j++) {
				if (sequence.charAt(i) == sequence.charAt(j))
					matrix[i][j] = "o";
			}
		}

		for (int i = 0; i < sequence.length(); i++) {
			for (int j = 0; j < sequence.length(); j++) {
				if (matrix[i][j] != null && !matrix[i][j].equalsIgnoreCase(""))
					System.out.print(" " + matrix[i][j]);
				else
					System.out.print(" -");
			}
			System.out.println("");
		}
	}

	// @Test
	public void getOrganism() throws Exception {
		List<ClusterBlastLineage> cbl = new ArrayList<>();

		// {CP003209=[Corynebacterium diphtheriae BH8, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium],
		// CP003217=[Corynebacterium diphtheriae VA01, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium],
		// CP003207=[Corynebacterium diphtheriae 241, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium],
		// CP003215=[Corynebacterium diphtheriae HC04, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium],
		// BX248355=[Corynebacterium diphtheriae, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium],
		// CP003213=[Corynebacterium diphtheriae HC02, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium],
		// CP003214=[Corynebacterium diphtheriae HC03, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium],
		// CP003211=[Corynebacterium diphtheriae CDCE 8392, Bacteria,
		// Actinobacteria, Corynebacteriales, Corynebacteriaceae,
		// Corynebacterium], CP003212=[Corynebacterium diphtheriae HC01,
		// Bacteria, Actinobacteria, Corynebacteriales, Corynebacteriaceae,
		// Corynebacterium], AJVH01000008=[Corynebacterium diphtheriae bv.
		// intermedius str. NCTC 5011, Bacteria, Actinobacteria,
		// Corynebacteriales, Corynebacteriaceae, Corynebacterium]}

		List<String> lineage = new ArrayList<>();

		lineage.add("Corynebacterium-diphtheriae-BH8");
		lineage.add("Bacteria");
		lineage.add("Actinobacteria");
		lineage.add("Corynebacteriales");
		lineage.add("Corynebacteriaceae");
		lineage.add("Corynebacterium");
		lineage.add(lineage.remove(0));
		ClusterBlastLineage lin = new ClusterBlastLineage();
		lin.setLineage(lineage);
		cbl.add(lin);

		lineage = new ArrayList<>();

		lineage.add("Corynebacterium diphtheriae VA01");
		lineage.add("Bacteria");
		lineage.add("Actinobacteria");
		lineage.add("Corynebacteriales");
		lineage.add("Corynebacteriaceae");
		lineage.add("Corynebacterium");
		lineage.add(lineage.remove(0));
		lin = new ClusterBlastLineage();
		lin.setLineage(lineage);
		cbl.add(lin);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element element = doc.createElement("root");
		doc.appendChild(element);

		Node root = doc.getFirstChild();

		for (ClusterBlastLineage item : cbl) {
			Node current = root;
			for (String species : item.getLineage()) {
				NodeList children = current.getChildNodes();
				boolean found = false;
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeName() == species) {
						current = children.item(i);
						found = true;
						break;
					}
				}
				if (!found) {
					Element e = doc.createElement(species);
					current.appendChild(e);
					current = e;
				}
			}
		}

		prettyPrint(doc);

	}

	public static final void prettyPrint(Document doc) throws Exception {

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
	}

	@Test
	public void testing() {
		List<Dog> a = new ArrayList<>();
		a.add(new Dog());
		a.get(0).age = 10;

		List<Dog> b = new ArrayList<>(a);
		b.get(0).age = 12;

		System.out.println(a.get(0).age);

	}

	public int getTreeSize(Node root) {
		if (root == null)
			return 0;

		if (!root.hasChildNodes())
			return 1;

		int count = 0;
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			count += getTreeSize(children.item(i));
		}
		return count + 1;
	}

	class Dog {
		public int age = 10;
	}

}
