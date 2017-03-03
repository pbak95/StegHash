package patryk.Bak.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HashTagChain {

	private static List<List<String>> permutationsList(List<String> list) {

	    if (list.size() == 0) {
	        List<List<String>> result = new ArrayList<List<String>>();
	        result.add(new ArrayList<String>());
	        return result;
	    }

	    List<List<String>> returnThis = new ArrayList<List<String>>();

	    String firstElement = list.remove(0);

	    List<List<String>> recursiveReturn = permutationsList(list);
	    for (List<String> li : recursiveReturn) {

	        for (int index = 0; index <= li.size(); index++) {
	            List<String> temp = new ArrayList<String>(li);
	            temp.add(index, firstElement);
	            returnThis.add(temp);
	        }

	    }
	    return returnThis;
	}
	
	private static Map<Integer, List<String>> getChain(List<List<String>> permutations){
		Map<Integer, List<String>> result = new LinkedHashMap<Integer, List<String>>();
		Map<String, Map<Integer, List<String>>> buckets = new HashMap<String, Map<Integer, List<String>>>();
		List<String> bucketNames = new ArrayList<String>(); 
		String previousBucket = "";
		
		for (int i=0; i< permutations.size(); i++){
			if(buckets.containsKey(permutations.get(i).get(permutations.get(i).size()-1))){
				buckets.get(permutations.get(i).get(permutations.get(i).size()-1)).put(i, permutations.get(i));
			}else{				
				Map<Integer, List<String>> tmpMap = new LinkedHashMap<Integer, List<String>>();
				tmpMap.put(i, permutations.get(i));
				buckets.put(permutations.get(i).get(permutations.get(i).size()-1), tmpMap);
				bucketNames.add(permutations.get(i).get(permutations.get(i).size()-1));
			}
		}
		
//		buckets.forEach((a,b) -> {
//			System.out.println("Bucket: "+ a);
//			b.forEach((c,d) -> {
//				System.out.println("Address: "+ c);
//				d.forEach(System.out::println);
//			});
//		});
		
		 Random rnd = new Random();
		
		while(!bucketNames.isEmpty()){
			if(previousBucket.equals("")){
				
				String currentBucket = bucketNames.get(rnd.nextInt(bucketNames.size()));
				Map<Integer, List<String>> pseudorandomBucket = buckets.get(currentBucket);
				List<Integer> addressesFromCurrentBucket = new ArrayList<Integer>();
				pseudorandomBucket.forEach((a,b)-> addressesFromCurrentBucket.add(a));
				int currentAddress  = addressesFromCurrentBucket.get(rnd.nextInt( addressesFromCurrentBucket.size()));
				result.put(currentAddress, buckets.get(currentBucket).remove(currentAddress));
				if(buckets.get(currentBucket).isEmpty()){
					bucketNames.remove(currentBucket);
					previousBucket = "";
				}else{
					if(bucketNames.size() != 1){
						bucketNames.remove(currentBucket);
						previousBucket = currentBucket;
					}else{
						previousBucket = "";
					}
					
				}
				
			}else{
				String currentBucket = bucketNames.get(rnd.nextInt(bucketNames.size()));
				bucketNames.add(previousBucket);
				Map<Integer, List<String>> pseudorandomBucket = buckets.get(currentBucket);
				List<Integer> addressesFromCurrentBucket = new ArrayList<Integer>();
				pseudorandomBucket.forEach((a,b)-> addressesFromCurrentBucket.add(a));
				int currentAddress  = addressesFromCurrentBucket.get(rnd.nextInt( addressesFromCurrentBucket.size()));
				result.put(currentAddress, buckets.get(currentBucket).remove(currentAddress));
				if(buckets.get(currentBucket).isEmpty()){
					bucketNames.remove(currentBucket);
					previousBucket = "";
				}else{
					bucketNames.remove(currentBucket);
					previousBucket = currentBucket;
				}
				
			}
			
		}
		
		
		return result;
	}
	

	public static Map<Integer, List<String>> generateChainOfHashtags(List<String> list){
		return getChain(permutationsList(list));
	}

	
	public static void main(String[] args) {
		
		List<String> test = new ArrayList<String>(Arrays.asList("#Warsaw", "#home", "#Poland", "#test123"));
		Map<Integer, List<String>> resultChain = generateChainOfHashtags(test);
		resultChain.forEach((a,b)->{
			System.out.println("Address: " + a);
			b.forEach(System.out::println);
		});


	}

}
